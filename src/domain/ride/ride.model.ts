import { pool } from '../../config/database';
import { geocodeAddress } from '../../utils/distance';

export interface Ride {
    id?: number;
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
    status?: 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
    created_at?: Date;
    updated_at?: Date;
}

export const RideModel = {
    async create(ride: Ride) {
        const {
            driver_id,
            origin,
            destination,
            departure_time,
            available_seats,
            price_per_seat
        } = ride;

        // Geocode addresses to get coordinates
        const originCoords = await geocodeAddress(origin);
        const destCoords = await geocodeAddress(destination);

        const result = await pool.query(
            `INSERT INTO rides (
                driver_id, origin, destination, departure_time, available_seats, price_per_seat,
                latitude_origin, longitude_origin, latitude_destination, longitude_destination, status
            ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, 'ACTIVE') RETURNING *`,
            [
                driver_id, origin, destination, departure_time, available_seats, price_per_seat,
                originCoords.lat, originCoords.lng, destCoords.lat, destCoords.lng
            ]
        );
        return result.rows[0] as Ride;
    },

    async findById(id: number) {
        const result = await pool.query('SELECT * FROM rides WHERE id = $1', [id]);
        return result.rows[0] as Ride | undefined;
    },

    async search(origin: string, destination: string) {
        const result = await pool.query(
            'SELECT * FROM rides WHERE origin ILIKE $1 AND destination ILIKE $2 AND status = $3',
            [`%${origin}%`, `%${destination}%`, 'ACTIVE']
        );
        return result.rows as Ride[];
    },

    /**
     * Advanced search with route matching
     */
    async searchWithRouteMatching(
        originLat: number,
        originLng: number,
        destLat: number,
        destLng: number,
        searchDate?: string,
        maxResults: number = 20
    ) {
        let query = `
            SELECT id, driver_id, origin, destination, departure_time,
                   available_seats, price_per_seat, latitude_origin, longitude_origin,
                   latitude_destination, longitude_destination, status
            FROM rides
            WHERE available_seats > 0
            AND status = 'ACTIVE'
            AND latitude_origin IS NOT NULL
            AND longitude_origin IS NOT NULL
            AND latitude_destination IS NOT NULL
            AND longitude_destination IS NOT NULL
        `;

        const params: any[] = [];
        let paramIndex = 1;

        if (searchDate) {
            query += ` AND DATE(departure_time) = $${paramIndex++}`;
            params.push(searchDate);
        }

        query += ` ORDER BY departure_time ASC LIMIT $${paramIndex}`;
        params.push(maxResults);

        const result = await pool.query(query, params);
        return result.rows as Ride[];
    },

    async updateStatus(id: number, status: 'ACTIVE' | 'COMPLETED' | 'CANCELLED') {
        const result = await pool.query(
            `UPDATE rides SET status = $1, updated_at = NOW() WHERE id = $2 RETURNING *`,
            [status, id]
        );
        return result.rows[0] as Ride | undefined;
    },

    async completeTrip(id: number) {
        // Update trip status
        const trip = await this.updateStatus(id, 'COMPLETED');

        if (trip) {
            // TODO: Close chat room (set to read-only)
            // TODO: Unlock review system
            console.log(`Trip ${id} completed`);
        }

        return trip;
    },

    async getDriverTrips(driverId: number) {
        const result = await pool.query(
            `SELECT * FROM rides WHERE driver_id = $1 ORDER BY departure_time DESC`,
            [driverId]
        );
        return result.rows as Ride[];
    },

    async getActiveTrips() {
        const result = await pool.query(
            `SELECT * FROM rides WHERE status = 'ACTIVE' ORDER BY departure_time ASC`
        );
        return result.rows as Ride[];
    }
};