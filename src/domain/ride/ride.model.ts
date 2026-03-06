import { pool } from '../../config/database';

export interface Ride {
    id?: number;
    driver_id: number;
    origin: string;
    destination: string;
    departure_time: Date;
    available_seats: number;
    price_per_seat: number;
    created_at?: Date;
    updated_at?: Date;
}

export const RideModel = {
    async create(ride: Ride) {
        const { driver_id, origin, destination, departure_time, available_seats, price_per_seat } = ride;
        const result = await pool.query(
            'INSERT INTO rides (driver_id, origin, destination, departure_time, available_seats, price_per_seat) VALUES ($1,$2,$3,$4,$5,$6) RETURNING *',
            [driver_id, origin, destination, departure_time, available_seats, price_per_seat]
        );
        return result.rows[0] as Ride;
    },

    async findById(id: number) {
        const result = await pool.query('SELECT * FROM rides WHERE id = $1', [id]);
        return result.rows[0] as Ride | undefined;
    },

    async search(origin: string, destination: string) {
        const result = await pool.query(
            'SELECT * FROM rides WHERE origin ILIKE $1 AND destination ILIKE $2',
            [`%${origin}%`, `%${destination}%`]
        );
        return result.rows as Ride[];
    }
};