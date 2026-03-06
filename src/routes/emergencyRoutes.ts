import express from 'express';
import {
    createEmergencyAlert,
    getActiveAlerts,
    getUserAlerts,
    updateAlertStatus,
    getAlertsInArea
} from '../controllers/emergencyController';

const router = express.Router();

/**
 * Emergency Alert Routes
 * Critical safety endpoints - should have appropriate rate limiting
 */

// Create emergency alert
router.post('/emergency-alert', createEmergencyAlert);

// Get active emergency alerts (admin/police access)
router.get('/emergency-alerts/active', getActiveAlerts);

// Get emergency alerts for a specific user
router.get('/emergency-alerts/user/:userId', getUserAlerts);

// Update emergency alert status
router.patch('/emergency-alerts/:alertId/status', updateAlertStatus);

// Get emergency alerts in geographic area
router.get('/emergency-alerts/area', getAlertsInArea);

export default router;