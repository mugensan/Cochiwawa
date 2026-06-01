import { pool } from '../../config/database';

export interface Driver {
    id?: number;
    first_name: string;
    last_name: string;
    email: string;
    phone: string;
    vehicle_model: string;
    vehicle_plate: string;
    created_at?: Date;
    updated_at?: Date;
}

export const DriverModel = {
    async create(driver: Driver) {
        const { first_name, last_name, email, phone, vehicle_model, vehicle_plate } = driver;
        const result = await pool.query(
            'INSERT INTO drivers (first_name, last_name, email, phone, vehicle_model, vehicle_plate) VALUES ($1,$2,$3,$4,$5,$6) RETURNING *',
            [first_name, last_name, email, phone, vehicle_model, vehicle_plate]
        );
        return result.rows[0] as Driver;
    },

    async findById(id: number) {
        const result = await pool.query('SELECT * FROM drivers WHERE id = $1', [id]);
        return result.rows[0] as Driver | undefined;
    },
};