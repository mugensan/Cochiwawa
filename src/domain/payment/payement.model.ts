import {pool} from "../../config/database";

export interface Payment {
    id?: number;
    ride_id: number;
    passenger_id: number;
    amount: number;
    fee_percentage: number;
    total_amount?: number;
    status?: string,
    created_at?: Date;
    updated_at?: Date;
}

export const PaymentModel = {
    async create(payment: Payment) {
        const { ride_id, passenger_id, amount, fee_percentage = 0.8 } = payment;
        const total_amount = amount + (amount * fee_percentage / 100);
        const result = await pool.query(
            "INSERT INTO payments (ride_id, passenger_id, amount, fee_percentage, total_amount) VALUES ($1, $2, $3, $4, $5) RETURNING *",
            [ride_id, passenger_id, amount, fee_percentage, total_amount]
        );
        return result.rows[0];
    },

    async findAll() {
        const result = await pool.query('SELECT * FROM payments');
        return result.rows;
    },

    async findById(id: number) {
        const result = await pool.query('SELECT * FROM payments WHERE id = $1', [id]);
        return result.rows[0];
    }
};