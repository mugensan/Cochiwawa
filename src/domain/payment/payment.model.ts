import { pool } from '../../config/database';

export interface Payment {
    id?: number;
    ride_id: number;
    amount: number;
    method: string;
    created_at?: Date;
    updated_at?: Date;
}

export const PaymentModel = {
    async create(payment: Payment) {
        const { ride_id, amount, method } = payment;
        const result = await pool.query(
            'INSERT INTO payments (ride_id, amount, method) VALUES ($1,$2,$3) RETURNING *',
            [ride_id, amount, method]
        );
        return result.rows[0] as Payment;
    },

    async findById(id: number) {
        const result = await pool.query('SELECT * FROM payments WHERE id = $1', [id]);
        return result.rows[0] as Payment | undefined;
    },
};