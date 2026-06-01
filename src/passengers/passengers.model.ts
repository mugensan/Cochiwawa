import { pool } from '../config/database';

export interface Passenger {
    id?: number;
    first_name: string;
    last_name: string;
    email: string;
    phone: string;
    created_at?: Date;
    updated_at?: Date;
}

export const PassengerModel = {
    async create(passenger: Passenger) {
        // Implement database logic to create a passenger
        const { first_name, last_name, email, phone } = passenger;
        // Example SQL query using parameterized queries to prevent SQL injection
        const result = await pool.query(
            "INSERT INTO passengers (first_name, last_name, email, phone) VALUES ($1, $2, $3, $4) RETURNING *",
            [first_name, last_name, email, phone]
        );
        return result.rows[0];
    },
    async findAll(){
        const result = await pool.query('SELECT*FROM passengers');
        return result.rows;
    },

    async findById(id: number) {
        const result = await pool.query('SELECT * FROM passengers WHERE id = $1', [id]);
        return result.rows[0];
    }
};