import { pool } from '../config/database';
import { calculateDistance, isWithinRadius } from '../utils/distance';

/**
 * Route Matching Service - Finds compatible trips based on geographic proximity
 * Extends the basic matching to include nearby routes, not just exact matches
 */

export interface TripMatch {
    tripId: number;
    driverId: number;
    origin: string;
    destination: string;
    departureTime: Date;
    availableSeats: number;
    pricePerSeat: number;
    distanceFromOrigin: number; // km
    distanceFromDestination: number; // km
    totalRouteDeviation: number; // km
    matchScore: number; // 0-100, higher is better
}

export interface Trip {
    id: number;
    driver_id: number;
    origin: string;
    destination: string;
    departure_time: Date;
    available_seats: number;
    price_per_seat: number;
    latitude_origin?: number;
    longitude_origin?: number;
    latitude_destination?: number;
    longitude_destination?: number;
    status?: string;
}

/**
 * Find matching trips based on geographic proximity
 * @param originLat Passenger's origin latitude
 * @param originLng Passenger's origin longitude
 * @param destLat Passenger's destination latitude
 * @param destLng Passenger's destination longitude
 * @param searchDate Optional date filter (YYYY-MM-DD format)
 * @param maxResults Maximum number of results to return
 * @returns Array of matching trips with scores
 */
export async function findMatchingTrips(
    originLat: number,
    originLng: number,
    destLat: number,
    destLng: number,
    searchDate?: string,
    maxResults: number = 20
): Promise<TripMatch[]> {
    try {
        // Build query with optional date filter
        let query = `
            SELECT id, driver_id, origin, destination, departure_time,
                   available_seats, price_per_seat, latitude_origin, longitude_origin,
                   latitude_destination, longitude_destination, status
            FROM rides
            WHERE available_seats > 0
            AND status = 'ACTIVE'
        `;

        const params: any[] = [];
        let paramIndex = 1;

        if (searchDate) {
            // Filter by date (departure_time date matches search date)
            query += ` AND DATE(departure_time) = $${paramIndex++}`;
            params.push(searchDate);
        }

        const result = await pool.query(query, params);
        const trips: Trip[] = result.rows;

        const matches: TripMatch[] = [];

        for (const trip of trips) {
            // Skip trips without coordinates
            if (!trip.latitude_origin || !trip.longitude_origin ||
                !trip.latitude_destination || !trip.longitude_destination) {
                continue;
            }

            // Calculate distances
            const originDistance = calculateDistance(
                originLat, originLng,
                trip.latitude_origin, trip.longitude_origin
            );

            const destDistance = calculateDistance(
                destLat, destLng,
                trip.latitude_destination, trip.longitude_destination
            );

            // Check if within acceptable radius
            const maxOriginRadius = 10; // 10km for pickup
            const maxDestRadius = 20; // 20km for dropoff

            if (originDistance > maxOriginRadius || destDistance > maxDestRadius) {
                continue;
            }

            // Calculate match score (0-100)
            // Lower distances = higher score
            const originScore = Math.max(0, 100 - (originDistance / maxOriginRadius) * 100);
            const destScore = Math.max(0, 100 - (destDistance / maxDestRadius) * 100);
            const matchScore = (originScore + destScore) / 2;

            matches.push({
                tripId: trip.id,
                driverId: trip.driver_id,
                origin: trip.origin,
                destination: trip.destination,
                departureTime: trip.departure_time,
                availableSeats: trip.available_seats,
                pricePerSeat: typeof trip.price_per_seat === 'number' ? trip.price_per_seat : parseFloat(trip.price_per_seat),
                distanceFromOrigin: originDistance,
                distanceFromDestination: destDistance,
                totalRouteDeviation: originDistance + destDistance,
                matchScore: Math.round(matchScore)
            });
        }

        // Sort by match score (descending) and then by total deviation (ascending)
        matches.sort((a, b) => {
            if (b.matchScore !== a.matchScore) {
                return b.matchScore - a.matchScore;
            }
            return a.totalRouteDeviation - b.totalRouteDeviation;
        });

        // Return top results
        return matches.slice(0, maxResults);
    } catch (error) {
        console.error('Error finding matching trips:', error);
        throw new Error('Failed to find matching trips');
    }
}

/**
 * Update trip coordinates (called when creating/updating trips)
 * @param tripId Trip ID
 * @param originLat Origin latitude
 * @param originLng Origin longitude
 * @param destLat Destination latitude
 * @param destLng Destination longitude
 */
export async function updateTripCoordinates(
    tripId: number,
    originLat: number,
    originLng: number,
    destLat: number,
    destLng: number
): Promise<void> {
    try {
        await pool.query(
            `UPDATE rides
             SET latitude_origin = $1, longitude_origin = $2,
                 latitude_destination = $3, longitude_destination = $4,
                 updated_at = NOW()
             WHERE id = $5`,
            [originLat, originLng, destLat, destLng, tripId]
        );
    } catch (error) {
        console.error('Error updating trip coordinates:', error);
        throw new Error('Failed to update trip coordinates');
    }
}

/**
 * Get trip details with coordinates
 * @param tripId Trip ID
 * @returns Trip details or null
 */
export async function getTripWithCoordinates(tripId: number): Promise<Trip | null> {
    try {
        const result = await pool.query(
            `SELECT id, driver_id, origin, destination, departure_time,
                    available_seats, price_per_seat, latitude_origin, longitude_origin,
                    latitude_destination, longitude_destination, status
             FROM rides WHERE id = $1`,
            [tripId]
        );

        return result.rows[0] || null;
    } catch (error) {
        console.error('Error getting trip with coordinates:', error);
        throw new Error('Failed to get trip details');
    }
}

/**
 * Find trips within a geographic area (for admin/dashboard purposes)
 * @param centerLat Center latitude
 * @param centerLng Center longitude
 * @param radiusKm Search radius in km
 * @param limit Maximum results
 */
export async function findTripsInArea(
    centerLat: number,
    centerLng: number,
    radiusKm: number,
    limit: number = 50
): Promise<Trip[]> {
    try {
        const result = await pool.query(
            `SELECT id, driver_id, origin, destination, departure_time,
                    available_seats, price_per_seat, latitude_origin, longitude_origin,
                    latitude_destination, longitude_destination, status
             FROM rides
             WHERE status = 'ACTIVE'
             AND latitude_origin IS NOT NULL
             AND longitude_origin IS NOT NULL
             ORDER BY
                 -- Use simple distance approximation for ordering
                 ((latitude_origin - $1)^2 + (longitude_origin - $2)^2) ASC
             LIMIT $3`,
            [centerLat, centerLng, limit]
        );

        // Filter by actual distance
        const trips = result.rows.filter((trip: Trip) => {
            if (!trip.latitude_origin || !trip.longitude_origin) return false;
            return isWithinRadius(centerLat, centerLng, trip.latitude_origin, trip.longitude_origin, radiusKm);
        });

        return trips;
    } catch (error) {
        console.error('Error finding trips in area:', error);
        throw new Error('Failed to find trips in area');
    }
}
