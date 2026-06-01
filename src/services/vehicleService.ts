import { pool } from '../config/database';

/**
 * Vehicle Service - Handles driver vehicle verification
 * Manages vehicle registration, image uploads, and verification
 */

export interface DriverVehicle {
    id?: string;
    driverId: number;
    driverLicenseNumber: string;
    vehiclePlate: string;
    vehicleModel: string;
    vehicleColor: string;
    insuranceDocumentUrl?: string;
    carPhotoFront?: string;
    carPhotoBack?: string;
    carPhotoLeft?: string;
    carPhotoDriverSeat?: string;
    verified?: boolean;
    createdAt?: Date;
    updatedAt?: Date;
}

/**
 * Upload vehicle image to storage
 * Returns the URL of the uploaded image
 */
export async function uploadVehicleImage(file: any, driverId: number, imageType: string): Promise<string> {
    try {
        // TODO: Implement actual cloud storage upload (S3, Cloudflare R2, etc.)
        const fileName = `${driverId}-${imageType}-${Date.now()}.jpg`;
        const url = `https://storage.example.com/drivers/${driverId}/vehicle/${fileName}`;
        console.log(`Vehicle image uploaded: ${url}`);
        return url;
    } catch (error) {
        console.error('Error uploading vehicle image:', error);
        throw new Error('Failed to upload vehicle image');
    }
}

/**
 * Register a new vehicle for a driver
 */
export async function registerVehicle(vehicle: DriverVehicle): Promise<DriverVehicle> {
    const {
        driverId,
        driverLicenseNumber,
        vehiclePlate,
        vehicleModel,
        vehicleColor,
        insuranceDocumentUrl,
        carPhotoFront,
        carPhotoBack,
        carPhotoLeft,
        carPhotoDriverSeat
    } = vehicle;

    try {
        const result = await pool.query(
            `INSERT INTO driver_vehicles (
                driver_id, driver_license_number, vehicle_plate,
                vehicle_model, vehicle_color, insurance_document_url,
                car_photo_front, car_photo_back, car_photo_left, car_photo_driver_seat,
                verified
            ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, false)
             RETURNING *`,
            [
                driverId,
                driverLicenseNumber,
                vehiclePlate,
                vehicleModel,
                vehicleColor,
                insuranceDocumentUrl || null,
                carPhotoFront || null,
                carPhotoBack || null,
                carPhotoLeft || null,
                carPhotoDriverSeat || null
            ]
        );

        console.log(`Vehicle registered for driver ${driverId}: ${vehiclePlate}`);
        return result.rows[0];
    } catch (error) {
        console.error('Error registering vehicle:', error);
        if ((error as any).code === '23505') {
            throw new Error('Vehicle plate already exists');
        }
        throw new Error('Failed to register vehicle');
    }
}

/**
 * Get vehicle by ID
 */
export async function getVehicleById(vehicleId: string): Promise<DriverVehicle | null> {
    try {
        const result = await pool.query(
            `SELECT * FROM driver_vehicles WHERE id = $1`,
            [vehicleId]
        );

        return result.rows[0] || null;
    } catch (error) {
        console.error('Error getting vehicle:', error);
        throw new Error('Failed to get vehicle');
    }
}

/**
 * Get vehicle by driver ID
 */
export async function getDriverVehicles(driverId: number): Promise<DriverVehicle[]> {
    try {
        const result = await pool.query(
            `SELECT * FROM driver_vehicles WHERE driver_id = $1 ORDER BY created_at DESC`,
            [driverId]
        );

        return result.rows;
    } catch (error) {
        console.error('Error getting driver vehicles:', error);
        throw new Error('Failed to get driver vehicles');
    }
}

/**
 * Get verified vehicle for driver
 */
export async function getVerifiedVehicleForDriver(driverId: number): Promise<DriverVehicle | null> {
    try {
        const result = await pool.query(
            `SELECT * FROM driver_vehicles
             WHERE driver_id = $1 AND verified = true
             ORDER BY updated_at DESC
             LIMIT 1`,
            [driverId]
        );

        return result.rows[0] || null;
    } catch (error) {
        console.error('Error getting verified vehicle:', error);
        throw new Error('Failed to get verified vehicle');
    }
}

/**
 * Update vehicle information
 */
export async function updateVehicle(vehicleId: string, updates: Partial<DriverVehicle>): Promise<boolean> {
    try {
        const fields = [];
        const values = [];
        let paramCount = 1;

        if (updates.insuranceDocumentUrl !== undefined) {
            fields.push(`insurance_document_url = $${paramCount++}`);
            values.push(updates.insuranceDocumentUrl);
        }
        if (updates.carPhotoFront !== undefined) {
            fields.push(`car_photo_front = $${paramCount++}`);
            values.push(updates.carPhotoFront);
        }
        if (updates.carPhotoBack !== undefined) {
            fields.push(`car_photo_back = $${paramCount++}`);
            values.push(updates.carPhotoBack);
        }
        if (updates.carPhotoLeft !== undefined) {
            fields.push(`car_photo_left = $${paramCount++}`);
            values.push(updates.carPhotoLeft);
        }
        if (updates.carPhotoDriverSeat !== undefined) {
            fields.push(`car_photo_driver_seat = $${paramCount++}`);
            values.push(updates.carPhotoDriverSeat);
        }

        if (fields.length === 0) return true;

        fields.push(`updated_at = NOW()`);
        values.push(vehicleId);

        const query = `UPDATE driver_vehicles
                      SET ${fields.join(', ')}
                      WHERE id = $${paramCount}
                      RETURNING id`;

        const result = await pool.query(query, values);
        return result.rows.length > 0;
    } catch (error) {
        console.error('Error updating vehicle:', error);
        throw new Error('Failed to update vehicle');
    }
}

/**
 * Verify vehicle (mark as verified)
 */
export async function verifyVehicle(vehicleId: string): Promise<boolean> {
    try {
        const result = await pool.query(
            `UPDATE driver_vehicles
             SET verified = true, updated_at = NOW()
             WHERE id = $1
             RETURNING id`,
            [vehicleId]
        );

        return result.rows.length > 0;
    } catch (error) {
        console.error('Error verifying vehicle:', error);
        throw new Error('Failed to verify vehicle');
    }
}

/**
 * Check if driver's vehicle is verified
 */
export async function isDriverVehicleVerified(driverId: number): Promise<boolean> {
    try {
        const vehicle = await getVerifiedVehicleForDriver(driverId);
        return !!vehicle;
    } catch (error) {
        console.error('Error checking vehicle verification:', error);
        return false;
    }
}

/**
 * Delete vehicle (soft delete by updating status)
 */
export async function deleteVehicle(vehicleId: string): Promise<boolean> {
    try {
        const result = await pool.query(
            `DELETE FROM driver_vehicles WHERE id = $1`,
            [vehicleId]
        );

        return result.rowCount ? result.rowCount > 0 : false;
    } catch (error) {
        console.error('Error deleting vehicle:', error);
        throw new Error('Failed to delete vehicle');
    }
}

/**
 * Get all unverified vehicles for admin review
 */
export async function getUnverifiedVehicles(limit: number = 50, offset: number = 0): Promise<DriverVehicle[]> {
    try {
        const result = await pool.query(
            `SELECT * FROM driver_vehicles
             WHERE verified = false
             ORDER BY created_at ASC
             LIMIT $1 OFFSET $2`,
            [limit, offset]
        );

        return result.rows;
    } catch (error) {
        console.error('Error getting unverified vehicles:', error);
        throw new Error('Failed to get unverified vehicles');
    }
}

/**
 * Broadcast vehicle approval via WebSocket
 */
export function broadcastVehicleApproval(driverId: number): void {
    // TODO: Implement WebSocket broadcast
    // Example: io.emit('vehicle_approved', { driverId })
    console.log('Broadcasting vehicle approval:', {
        type: 'VEHICLE_APPROVED',
        driverId
    });
}
