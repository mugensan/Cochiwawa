import { pool } from '../../config/database';

export interface Ride {
    id?: number;
    passenger_id: number;
    driver_id: number;
    origin: string;
    destination: string;
    departure_time: Date;
    created_at?: Date;
    updated_at?: Date;
}

export const RideModel = {
    async create(ride: Ride) {
        const { passenger_id, driver_id, origin, destination, departure_time } = ride;
        const result = await pool.query(
            'INSERT INTO rides (passenger_id, driver_id, origin, destination, departure_time) VALUES ($1,$2,$3,$4,$5) RETURNING *',
            [passenger_id, driver_id, origin, destination, departure_time]
        );
        return result.rows[0] as Ride;
    },

    async findById(id: number) {
        const result = await pool.query('SELECT * FROM rides WHERE id = $1', [id]);
        return result.rows[0] as Ride | undefined;
    },
};