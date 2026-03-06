import { pool } from "../config/database";
import * as chatService from "./chatService";

/**
 * Booking Service - Handles seat reservations with protection against overselling
 * Uses database transactions and locking to ensure data consistency
 */

export interface Booking {
    id?: number;
    passenger_id: number;
    ride_id: number;
    seats: number;
    status: 'PENDING' | 'CONFIRMED' | 'CANCELLED';
    created_at?: Date;
    updated_at?: Date;
}

/**
 * Create booking with seat protection
 * Uses transaction and SELECT FOR UPDATE to prevent race conditions
 * @param passengerId Passenger ID
 * @param rideId Ride ID
 * @param seats Number of seats to book
 * @returns Created booking or throws error
 */
export async function createBooking(passengerId: number, rideId: number, seats: number): Promise<Booking> {
    const client = await pool.connect();

    try {
        await client.query('BEGIN');

        // Lock the ride row to prevent concurrent modifications
        const rideResult = await client.query(
            `SELECT id, available_seats, driver_id FROM rides
             WHERE id = $1 FOR UPDATE`,
            [rideId]
        );

        if (rideResult.rows.length === 0) {
            throw new Error('Ride not found');
        }

        const ride = rideResult.rows[0];

        // Check if enough seats are available
        if (ride.available_seats < seats) {
            throw new Error('No seats available');
        }

        // Create booking
        const bookingResult = await client.query(
            `INSERT INTO bookings (passenger_id, ride_id, seats, status)
             VALUES ($1, $2, $3, 'PENDING')
             RETURNING *`,
            [passengerId, rideId, seats]
        );

        // Update available seats
        await client.query(
            `UPDATE rides
             SET available_seats = available_seats - $1, updated_at = NOW()
             WHERE id = $2`,
            [seats, rideId]
        );

        await client.query('COMMIT');

        console.log(`Booking created: ${seats} seats for passenger ${passengerId} on ride ${rideId}`);
        return bookingResult.rows[0];

    } catch (error) {
        await client.query('ROLLBACK');
        console.error('Error creating booking:', error);
        throw error;
    } finally {
        client.release();
    }
}

/**
 * Confirm booking after successful payment
 * @param bookingId Booking ID
 * @returns Updated booking
 */
export async function confirmBooking(bookingId: number): Promise<Booking> {
    const client = await pool.connect();

    try {
        await client.query('BEGIN');

        // Get booking details
        const bookingResult = await client.query(
            `SELECT b.*, r.driver_id FROM bookings b
             JOIN rides r ON b.ride_id = r.id
             WHERE b.id = $1 FOR UPDATE`,
            [bookingId]
        );

        if (bookingResult.rows.length === 0) {
            throw new Error('Booking not found');
        }

        const booking = bookingResult.rows[0];

        if (booking.status !== 'PENDING') {
            throw new Error('Booking is not in pending status');
        }

        // Update booking status
        const updatedBookingResult = await client.query(
            `UPDATE bookings
             SET status = 'CONFIRMED', updated_at = NOW()
             WHERE id = $1
             RETURNING *`,
            [bookingId]
        );

        // Create or update chat room for the trip
        await createChatRoomForTrip(booking.ride_id, booking.passenger_id, booking.driver_id);

        await client.query('COMMIT');

        console.log(`Booking confirmed: ${bookingId}`);
        return updatedBookingResult.rows[0];

    } catch (error) {
        await client.query('ROLLBACK');
        console.error('Error confirming booking:', error);
        throw error;
    } finally {
        client.release();
    }
}

/**
 * Cancel booking and restore seats
 * @param bookingId Booking ID
 * @param passengerId Passenger ID (for verification)
 * @returns Updated booking
 */
export async function cancelBooking(bookingId: number, passengerId: number): Promise<Booking> {
    const client = await pool.connect();

    try {
        await client.query('BEGIN');

        // Get booking details with lock
        const bookingResult = await client.query(
            `SELECT b.*, r.driver_id FROM bookings b
             JOIN rides r ON b.ride_id = r.id
             WHERE b.id = $1 AND b.passenger_id = $2 FOR UPDATE`,
            [bookingId, passengerId]
        );

        if (bookingResult.rows.length === 0) {
            throw new Error('Booking not found or access denied');
        }

        const booking = bookingResult.rows[0];

        if (booking.status === 'CANCELLED') {
            throw new Error('Booking is already cancelled');
        }

        // Update booking status
        const updatedBookingResult = await client.query(
            `UPDATE bookings
             SET status = 'CANCELLED', updated_at = NOW()
             WHERE id = $1
             RETURNING *`,
            [bookingId]
        );

        // Restore seats if booking was confirmed
        if (booking.status === 'CONFIRMED') {
            await client.query(
                `UPDATE rides
                 SET available_seats = available_seats + $1, updated_at = NOW()
                 WHERE id = $2`,
                [booking.seats, booking.ride_id]
            );
        }

        await client.query('COMMIT');

        console.log(`Booking cancelled: ${bookingId}`);
        return updatedBookingResult.rows[0];

    } catch (error) {
        await client.query('ROLLBACK');
        console.error('Error cancelling booking:', error);
        throw error;
    } finally {
        client.release();
    }
}

/**
 * Create chat room for trip if it doesn't exist and add participants
 * @param tripId Trip ID
 * @param passengerId Passenger ID
 * @param driverId Driver ID
 */
async function createChatRoomForTrip(tripId: number, passengerId: number, driverId: number): Promise<void> {
    try {
        // Check if chat room exists for this trip
        const chatRoomResult = await pool.query(
            `SELECT id FROM chat_rooms WHERE trip_id = $1`,
            [tripId]
        );

        let chatRoomId: string;

        if (chatRoomResult.rows.length === 0) {
            // Create chat room
            const newChatRoom = await chatService.createChatRoom(tripId.toString());
            chatRoomId = newChatRoom.id;

            // Add driver as participant
            await chatService.addParticipant(chatRoomId, 'driver', driverId);
        } else {
            chatRoomId = chatRoomResult.rows[0].id;
        }

        // Add passenger as participant (if not already)
        const existingParticipant = await pool.query(
            `SELECT id FROM chat_participants
             WHERE chat_room_id = $1 AND user_id = $2`,
            [chatRoomId, passengerId]
        );

        if (existingParticipant.rows.length === 0) {
            await chatService.addParticipant(chatRoomId, 'passenger', passengerId);
        }

    } catch (error) {
        console.error('Error creating chat room for trip:', error);
        // Don't throw - booking should succeed even if chat creation fails
    }
}

/**
 * Get passenger bookings
 * @param passengerId Passenger ID
 * @returns Array of bookings
 */
export async function getPassengerBookings(passengerId: number) {
    const result = await pool.query(
        `SELECT b.*, r.origin, r.destination, r.departure_time, r.price_per_seat, r.status as trip_status
         FROM bookings b
         JOIN rides r ON b.ride_id = r.id
         WHERE b.passenger_id = $1
         ORDER BY b.created_at DESC`,
        [passengerId]
    );
    return result.rows;
}

/**
 * Get driver bookings for a ride
 * @param rideId Ride ID
 * @returns Array of bookings for the ride
 */
export async function getRideBookings(rideId: number) {
    const result = await pool.query(
        `SELECT b.*, u.first_name, u.last_name, u.email
         FROM bookings b
         JOIN users u ON b.passenger_id = u.id
         WHERE b.ride_id = $1 AND b.status = 'CONFIRMED'
         ORDER BY b.created_at ASC`,
        [rideId]
    );
    return result.rows;
}

/**
 * Check if passenger has confirmed booking for a ride
 * @param passengerId Passenger ID
 * @param rideId Ride ID
 * @returns True if confirmed booking exists
 */
export async function hasConfirmedBooking(passengerId: number, rideId: number): Promise<boolean> {
    const result = await pool.query(
        `SELECT id FROM bookings
         WHERE passenger_id = $1 AND ride_id = $2 AND status = 'CONFIRMED'`,
        [passengerId, rideId]
    );
    return result.rows.length > 0;
}
