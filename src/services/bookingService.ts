import { pool } from "../config/database";

export async function createBooking(passengerId: string, rideId: number, seats: number) {
    const result = await pool.query(
        "INSERT INTO bookings (passenger_id, ride_id, seats) VALUES ($1, $2, $3) RETURNING *",
        [passengerId, rideId, seats]
    );
    return result.rows[0];
}

export async function getPassengerBookings(passengerId: string) {
    const result = await pool.query(
        `SELECT b.*, r.origin, r.destination, r.departure_time, r.price_per_seat
         FROM bookings b
         JOIN rides r ON b.ride_id = r.id
         WHERE b.passenger_id = $1`,
        [passengerId]
    );
    return result.rows;
}
