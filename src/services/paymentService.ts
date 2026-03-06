import { pool } from "../config/database";

export async function createPayment(
    passengerId: string,
    driverId: string,
    rideId: number,
    amount: number
) {
    const platformFee = amount * 0.10; // 10% fee
    const driverAmount = amount - platformFee;

    const result = await pool.query(
        `INSERT INTO driver_payments (passenger_id, driver_id, ride_id, amount, platform_fee, driver_amount)
         VALUES ($1, $2, $3, $4, $5, $6) RETURNING *`,
        [passengerId, driverId, rideId, amount, platformFee, driverAmount]
    );
    return result.rows[0];
}

export async function getDriverEarnings(driverId: string) {
    const result = await pool.query(
        "SELECT SUM(driver_amount) as total_earnings, COUNT(*) as total_rides FROM driver_payments WHERE driver_id = $1",
        [driverId]
    );
    return result.rows[0];
}
