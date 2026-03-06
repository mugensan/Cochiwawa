import { pool } from '../config/database';

/**
 * Emergency Service - Handles emergency alert system
 * Manages emergency alerts and broadcasts via WebSocket
 */

export interface EmergencyAlert {
    id?: string;
    userId: number;
    userType: 'driver' | 'passenger';
    latitude: number;
    longitude: number;
    status?: 'ACTIVE' | 'RESOLVED' | 'DISMISSED';
    createdAt?: Date;
}

/**
 * Create emergency alert in database
 */
export async function createEmergencyAlert(alert: EmergencyAlert): Promise<EmergencyAlert> {
    const { userId, userType, latitude, longitude } = alert;

    try {
        const result = await pool.query(
            `INSERT INTO emergency_alerts (user_id, user_type, latitude, longitude, status)
             VALUES ($1, $2, $3, $4, 'ACTIVE')
             RETURNING *`,
            [userId, userType, latitude, longitude]
        );

        console.log(`Emergency alert created for ${userType} ${userId} at [${latitude}, ${longitude}]`);
        return result.rows[0];
    } catch (error) {
        console.error('Error creating emergency alert:', error);
        throw new Error('Failed to create emergency alert');
    }
}

/**
 * Get active emergency alerts
 */
export async function getActiveAlerts(): Promise<EmergencyAlert[]> {
    try {
        const result = await pool.query(
            `SELECT * FROM emergency_alerts
             WHERE status = 'ACTIVE'
             ORDER BY created_at DESC`
        );

        return result.rows;
    } catch (error) {
        console.error('Error getting active alerts:', error);
        throw new Error('Failed to get active alerts');
    }
}

/**
 * Get emergency alert by ID
 */
export async function getAlertById(alertId: string): Promise<EmergencyAlert | null> {
    try {
        const result = await pool.query(
            `SELECT * FROM emergency_alerts WHERE id = $1`,
            [alertId]
        );

        return result.rows[0] || null;
    } catch (error) {
        console.error('Error getting alert:', error);
        throw new Error('Failed to get alert');
    }
}

/**
 * Get alerts for a specific user
 */
export async function getUserAlerts(userId: number, userType: string): Promise<EmergencyAlert[]> {
    try {
        const result = await pool.query(
            `SELECT * FROM emergency_alerts
             WHERE user_id = $1 AND user_type = $2
             ORDER BY created_at DESC
             LIMIT 50`,
            [userId, userType]
        );

        return result.rows;
    } catch (error) {
        console.error('Error getting user alerts:', error);
        throw new Error('Failed to get user alerts');
    }
}

/**
 * Update emergency alert status
 */
export async function updateAlertStatus(
    alertId: string,
    status: 'ACTIVE' | 'RESOLVED' | 'DISMISSED'
): Promise<boolean> {
    try {
        const result = await pool.query(
            `UPDATE emergency_alerts
             SET status = $1, updated_at = NOW()
             WHERE id = $2
             RETURNING id`,
            [status, alertId]
        );

        return result.rows.length > 0;
    } catch (error) {
        console.error('Error updating alert status:', error);
        throw new Error('Failed to update alert status');
    }
}

/**
 * Resolve/dismiss emergency alert
 */
export async function resolveAlert(alertId: string): Promise<boolean> {
    return updateAlertStatus(alertId, 'RESOLVED');
}

/**
 * Dismiss emergency alert
 */
export async function dismissAlert(alertId: string): Promise<boolean> {
    return updateAlertStatus(alertId, 'DISMISSED');
}

/**
 * Get alerts within a geographic area (for admin dashboard)
 */
export async function getAlertsInArea(
    centerLat: number,
    centerLng: number,
    radiusKm: number
): Promise<EmergencyAlert[]> {
    try {
        // Using PostgreSQL earth distance operator (~)
        // This requires the earthdistance extension
        const result = await pool.query(
            `SELECT * FROM emergency_alerts
             WHERE status = 'ACTIVE'
             AND earth_distance(
                 ll_to_earth($1, $2),
                 ll_to_earth(latitude, longitude)
             ) < ($3 * 1000)
             ORDER BY created_at DESC`,
            [centerLat, centerLng, radiusKm]
        );

        return result.rows;
    } catch (error) {
        // If earthdistance extension is not available, use simplified distance calculation
        console.log('Using simplified distance calculation');
        return getActiveAlerts();
    }
}

/**
 * Broadcast emergency alert via WebSocket
 * (Implementation depends on your WebSocket server setup)
 */
export function broadcastEmergencyAlert(alert: EmergencyAlert): void {
    // TODO: Implement WebSocket broadcast
    // Example: io.emit('emergency_alert', alert)
    console.log('Broadcasting emergency alert:', {
        type: 'EMERGENCY_ALERT',
        userId: alert.userId,
        lat: alert.latitude,
        lng: alert.longitude
    });
}
