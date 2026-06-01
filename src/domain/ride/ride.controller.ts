import { RideModel, Ride } from './ride.model';
import * as routeMatchingService from '../../services/routeMatchingService';
import { Request, Response } from 'express';

export class RideController {
    static async createRide(data: Ride) {
        return RideModel.create(data);
    }

    static async getRide(id: number) {
        return RideModel.findById(id);
    }

    static async searchRides(req: Request, res: Response) {
        try {
            const { origin, destination, date } = req.query;

            if (!origin || !destination) {
                return res.status(400).json({ error: 'Origin and destination are required' });
            }

            // Try route matching if coordinates are provided
            const originLat = req.query.originLat ? parseFloat(req.query.originLat as string) : null;
            const originLng = req.query.originLng ? parseFloat(req.query.originLng as string) : null;
            const destLat = req.query.destLat ? parseFloat(req.query.destLat as string) : null;
            const destLng = req.query.destLng ? parseFloat(req.query.destLng as string) : null;

            let rides: any[];

            if (originLat && originLng && destLat && destLng) {
                // Use advanced route matching
                rides = await routeMatchingService.findMatchingTrips(
                    originLat,
                    originLng,
                    destLat,
                    destLng,
                    date as string
                );
            } else {
                // Fallback to basic text search
                rides = await RideModel.search(origin as string, destination as string);
            }

            res.json(rides);
        } catch (error) {
            console.error('Error searching rides:', error);
            res.status(500).json({ error: 'Internal server error' });
        }
    }

    static async completeTrip(req: Request, res: Response) {
        try {
            const tripIdStr = Array.isArray(req.params.tripId) ? req.params.tripId[0] : req.params.tripId;
            const tripId = parseInt(tripIdStr);

            if (isNaN(tripId)) {
                return res.status(400).json({ error: 'Invalid trip ID' });
            }

            // TODO: Verify that the requester is the driver of the trip
            // For now, allow any authenticated user (add auth middleware)

            const trip = await RideModel.completeTrip(tripId);

            if (!trip) {
                return res.status(404).json({ error: 'Trip not found' });
            }

            res.json({
                message: 'Trip completed successfully',
                trip: {
                    id: trip.id,
                    status: trip.status,
                    completedAt: trip.updated_at
                }
            });
        } catch (error) {
            console.error('Error completing trip:', error);
            res.status(500).json({ error: 'Internal server error' });
        }
    }

    static async getDriverTrips(req: Request, res: Response) {
        try {
            const driverIdStr = Array.isArray(req.params.driverId) ? req.params.driverId[0] : req.params.driverId;
            const driverId = parseInt(driverIdStr);

            if (isNaN(driverId)) {
                return res.status(400).json({ error: 'Invalid driver ID' });
            }

            const trips = await RideModel.getDriverTrips(driverId);
            res.json(trips);
        } catch (error) {
            console.error('Error getting driver trips:', error);
            res.status(500).json({ error: 'Internal server error' });
        }
    }

    static async getActiveTrips(req: Request, res: Response) {
        try {
            const trips = await RideModel.getActiveTrips();
            res.json(trips);
        } catch (error) {
            console.error('Error getting active trips:', error);
            res.status(500).json({ error: 'Internal server error' });
        }
    }
}