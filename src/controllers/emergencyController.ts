import { Request, Response } from 'express';
import { pool } from '../config/database';

/**
 * Emergency Controller - Handles emergency alerts and safety features
 */

/**
 * Create emergency alert
 * POST /emergency-alert
 */
export async function createEmergencyAlert(req: Request, res: Response) {
    try {
        const { userId, tripId, latitude, longitude } = req.body;

        if (!userId || !latitude || !longitude) {
            return res.status(400).json({ error: 'Missing required fields: userId, latitude, longitude' });
        }

        // Validate coordinates
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return res.status(400).json({ error: 'Invalid coordinates' });
        }

        const result = await pool.query(
            `INSERT INTO emergency_alerts (user_id, trip_id, latitude, longitude, status)
             VALUES ($1, $2, $3, $4, 'ACTIVE')
             RETURNING *`,
            [userId, tripId || null, latitude, longitude]
        );

        const alert = result.rows[0];

        console.log(`Emergency alert created for user ${userId} at [${latitude}, ${longitude}]`);

        // TODO: In production, trigger additional actions:
        // - Send notifications to emergency contacts
        // - Alert authorities if configured
        // - Send push notifications to trip participants
        // - Log for admin dashboard

        res.status(201).json({
            id: alert.id,
            userId: alert.user_id,
            tripId: alert.trip_id,
            latitude: alert.latitude,
            longitude: alert.longitude,
            status: alert.status,
            createdAt: alert.created_at
        });
    } catch (error) {
        console.error('Error creating emergency alert:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
}

/**
 * Get active emergency alerts
 * GET /emergency-alerts/active
 */
export async function getActiveAlerts(req: Request, res: Response) {
    try {
        const result = await pool.query(
            `SELECT ea.*, u.first_name, u.last_name, r.origin, r.destination
             FROM emergency_alerts ea
             LEFT JOIN users u ON ea.user_id = u.id
             LEFT JOIN rides r ON ea.trip_id = r.id
             WHERE ea.status = 'ACTIVE'
             ORDER BY ea.created_at DESC
             LIMIT 50`
        );

        const alerts = result.rows.map((row: any) => ({
            id: row.id,
            userId: row.user_id,
            userName: row.first_name && row.last_name ? `${row.first_name} ${row.last_name}` : 'Unknown',
            tripId: row.trip_id,
            tripRoute: row.origin && row.destination ? `${row.origin} → ${row.destination}` : null,
            latitude: row.latitude,
            longitude: row.longitude,
            status: row.status,
            createdAt: row.created_at
        }));

        res.json(alerts);
    } catch (error) {
        console.error('Error getting active alerts:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
}

/**
 * Get emergency alerts for a user
 * GET /emergency-alerts/user/:userId
 */
export async function getUserAlerts(req: Request, res: Response) {
    try {
        const userIdParam = Array.isArray(req.params.userId) ? req.params.userId[0] : req.params.userId;
        const userId = parseInt(userIdParam);

        if (isNaN(userId)) {
            return res.status(400).json({ error: 'Invalid user ID' });
        }

        const result = await pool.query(
            `SELECT ea.*, r.origin, r.destination
             FROM emergency_alerts ea
             LEFT JOIN rides r ON ea.trip_id = r.id
             WHERE ea.user_id = $1
             ORDER BY ea.created_at DESC
             LIMIT 20`,
            [userId]
        );

        const alerts = result.rows.map((row: any) => ({
            id: row.id,
            tripId: row.trip_id,
            tripRoute: row.origin && row.destination ? `${row.origin} → ${row.destination}` : null,
            latitude: row.latitude,
            longitude: row.longitude,
            status: row.status,
            createdAt: row.created_at
        }));

        res.json(alerts);
    } catch (error) {
        console.error('Error getting user alerts:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
}

/**
 * Update emergency alert status
 * PATCH /emergency-alerts/:alertId/status
 */
export async function updateAlertStatus(req: Request, res: Response) {
    try {
        const alertId = req.params.alertId;
        const { status } = req.body;

        if (!status || !['ACTIVE', 'RESOLVED', 'DISMISSED'].includes(status)) {
            return res.status(400).json({ error: 'Invalid status. Must be ACTIVE, RESOLVED, or DISMISSED' });
        }

        const result = await pool.query(
            `UPDATE emergency_alerts
             SET status = $1, updated_at = NOW()
             WHERE id = $2
             RETURNING *`,
            [status, alertId]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ error: 'Emergency alert not found' });
        }

        const alert = result.rows[0];

        console.log(`Emergency alert ${alertId} status updated to ${status}`);

        res.json({
            id: alert.id,
            status: alert.status,
            updatedAt: alert.updated_at
        });
    } catch (error) {
        console.error('Error updating alert status:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
}

/**
 * Get emergency alerts in area (for admin/police dashboard)
 * GET /emergency-alerts/area?lat=...&lng=...&radius=...
 */
export async function getAlertsInArea(req: Request, res: Response) {
    try {
        const centerLat = parseFloat(req.query.lat as string);
        const centerLng = parseFloat(req.query.lng as string);
        const radiusKm = parseFloat(req.query.radius as string) || 50;

        if (isNaN(centerLat) || isNaN(centerLng)) {
            return res.status(400).json({ error: 'Invalid coordinates' });
        }

        // Simple bounding box approximation for PostgreSQL
        // For production, use PostGIS or earthdistance extension
        const latDelta = radiusKm / 111; // ~111km per degree latitude
        const lngDelta = radiusKm / (111 * Math.cos(centerLat * Math.PI / 180));

        const result = await pool.query(
            `SELECT ea.*, u.first_name, u.last_name, r.origin, r.destination
             FROM emergency_alerts ea
             LEFT JOIN users u ON ea.user_id = u.id
             LEFT JOIN rides r ON ea.trip_id = r.id
             WHERE ea.status = 'ACTIVE'
             AND ea.latitude BETWEEN $1 AND $2
             AND ea.longitude BETWEEN $3 AND $4
             ORDER BY ea.created_at DESC`,
            [
                centerLat - latDelta,
                centerLat + latDelta,
                centerLng - lngDelta,
                centerLng + lngDelta
            ]
        );

        const alerts = result.rows.map((row: any) => ({
            id: row.id,
            userId: row.user_id,
            userName: row.first_name && row.last_name ? `${row.first_name} ${row.last_name}` : 'Unknown',
            tripId: row.trip_id,
            tripRoute: row.origin && row.destination ? `${row.origin} → ${row.destination}` : null,
            latitude: row.latitude,
            longitude: row.longitude,
            status: row.status,
            createdAt: row.created_at,
            distance: calculateDistance(centerLat, centerLng, row.latitude, row.longitude)
        }));

        // Filter by actual distance (simple approximation)
        const filteredAlerts = alerts.filter(alert => alert.distance <= radiusKm);

        res.json(filteredAlerts);
    } catch (error) {
        console.error('Error getting alerts in area:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
}

/**
 * Simple distance calculation helper
 */
function calculateDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
    const R = 6371; // Earth's radius in km
    const dLat = ((lat2 - lat1) * Math.PI) / 180;
    const dLon = ((lon2 - lon1) * Math.PI) / 180;
    const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos((lat1 * Math.PI) / 180) *
            Math.cos((lat2 * Math.PI) / 180) *
            Math.sin(dLon / 2) *
            Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
}