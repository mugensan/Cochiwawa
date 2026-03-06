import express, { Express } from "express";
import dotenv from "dotenv";
import bodyParser from "body-parser";

// Import services
import * as identityService from "./services/identityService";
import * as emergencyService from "./services/emergencyService";
import * as vehicleService from "./services/vehicleService";
import * as matchingService from "./services/matchingService";

// Import GraphQL schema
import { safetySystemTypeDefs, safetyResolvers } from "./graphql";

dotenv.config();

const app: Express = express();
const PORT = process.env.PORT || 5000;

// Middleware
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

/**
 * ============================================
 * HEALTH & STATUS ENDPOINTS
 * ============================================
 */
app.get("/", (req, res) => {
    res.json({
        message: "Cochiwawa Backend - Identity, Safety & Verification System",
        version: "2.0.0",
        features: [
            "User Identity Verification (RUT, Passport, National ID)",
            "Facial Photo Verification",
            "Driver Vehicle Verification",
            "Gender-based Ride Preferences",
            "Emergency Alert System",
            "Legal Terms Acceptance",
            "Real-time Safety Matching"
        ]
    });
});

app.get("/health", (req, res) => {
    res.json({ status: "ok", timestamp: new Date().toISOString() });
});

/**
 * ============================================
 * IDENTITY VERIFICATION ROUTES
 * ============================================
 */
app.post("/api/identity/update", async (req, res) => {
    try {
        const {
            userId,
            userType,
            fullName,
            gender,
            rut,
            passport,
            nationalId,
            profilePhotoUrl
        } = req.body;

        const result = await identityService.updateUserIdentity({
            userId,
            userType,
            fullName,
            gender,
            rut,
            passport,
            nationalId,
            profilePhotoUrl
        });

        res.json({
            success: result,
            message: result ? "Identity updated successfully" : "Failed to update identity"
        });
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

app.get("/api/identity/:userId/:userType", async (req, res) => {
    try {
        const { userId, userType } = req.params;
        const identity = await identityService.getUserIdentity(
            parseInt(userId),
            userType as "driver" | "passenger"
        );
        res.json(identity);
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

/**
 * ============================================
 * EMERGENCY ALERT ROUTES
 * ============================================
 */
app.post("/api/emergency/alert", async (req, res) => {
    try {
        const { userId, userType, latitude, longitude } = req.body;

        const alert = await emergencyService.createEmergencyAlert({
            userId,
            userType,
            latitude,
            longitude
        });

        // Broadcast alert
        emergencyService.broadcastEmergencyAlert(alert);

        res.json({
            success: true,
            alertId: alert.id,
            message: "Emergency alert created"
        });
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

app.get("/api/emergency/alerts/active", async (req, res) => {
    try {
        const alerts = await emergencyService.getActiveAlerts();
        res.json(alerts);
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

app.get("/api/emergency/alerts/:userId/:userType", async (req, res) => {
    try {
        const { userId, userType } = req.params;
        const alerts = await emergencyService.getUserAlerts(parseInt(userId), userType);
        res.json(alerts);
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

app.patch("/api/emergency/alert/:alertId/resolve", async (req, res) => {
    try {
        const { alertId } = req.params;
        const result = await emergencyService.resolveAlert(alertId);
        res.json({ success: result });
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

/**
 * ============================================
 * VEHICLE VERIFICATION ROUTES
 * ============================================
 */
app.post("/api/vehicle/register", async (req, res) => {
    try {
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
        } = req.body;

        const vehicle = await vehicleService.registerVehicle({
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
        });

        res.json({
            success: true,
            vehicleId: vehicle.id,
            message: "Vehicle registered successfully"
        });
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

app.get("/api/vehicle/:vehicleId", async (req, res) => {
    try {
        const { vehicleId } = req.params;
        const vehicle = await vehicleService.getVehicleById(vehicleId);
        res.json(vehicle);
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

app.get("/api/vehicle/driver/:driverId", async (req, res) => {
    try {
        const { driverId } = req.params;
        const vehicles = await vehicleService.getDriverVehicles(parseInt(driverId));
        res.json(vehicles);
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

app.patch("/api/vehicle/:vehicleId/verify", async (req, res) => {
    try {
        const { vehicleId } = req.params;
        const result = await vehicleService.verifyVehicle(vehicleId);
        res.json({ success: result });
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

/**
 * ============================================
 * RIDE MATCHING ROUTES
 * ============================================
 */
app.post("/api/rides/find-matches", async (req, res) => {
    try {
        const { passengerId, latitude, longitude, maxDistance } = req.body;

        const matches = await matchingService.findMatchingRides(
            passengerId,
            latitude,
            longitude,
            maxDistance
        );

        res.json({
            success: true,
            matches,
            count: matches.length
        });
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

app.post("/api/rides/validate-passenger", async (req, res) => {
    try {
        const { rideId, passengerId } = req.body;
        const isValid = await matchingService.validatePassengerAssignment(rideId, passengerId);
        res.json({ valid: isValid });
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

app.get("/api/rides/driver/:driverId/can-create", async (req, res) => {
    try {
        const { driverId } = req.params;
        const canCreate = await matchingService.canDriverCreateRide(parseInt(driverId));
        res.json({ canCreate, reason: canCreate ? "Verified" : "Driver or vehicle not verified" });
    } catch (error) {
        res.status(500).json({ error: (error as any).message });
    }
});

/**
 * ============================================
 * GRAPHQL SCHEMA ENDPOINT (documentation)
 * ============================================
 */
app.get("/api/graphql/schema", (req, res) => {
    res.json({
        message: "GraphQL Schema available",
        types: safetySystemTypeDefs.length,
        resolvers: Object.keys(safetyResolvers),
        note: "To use GraphQL, integrate with Apollo Server or other GraphQL server"
    });
});

/**
 * ============================================
 * SERVER STARTUP
 * ============================================
 */
app.listen(PORT, () => {
    console.log(`Cochiwawa backend is running on http://localhost:${PORT}`);
    console.log(`Health check: http://localhost:${PORT}/health`);
    console.log(`
╔════════════════════════════════════════════════════════════╗
║    Cochiwawa Backend - Safety & Verification System       ║
║                      v2.0.0                                ║
╚════════════════════════════════════════════════════════════╝

API Endpoints:
  Identity:     POST   /api/identity/update
  Identity:     GET    /api/identity/:userId/:userType
  Emergency:    POST   /api/emergency/alert
  Emergency:    GET    /api/emergency/alerts/active
  Emergency:    GET    /api/emergency/alerts/:userId/:userType
  Emergency:    PATCH  /api/emergency/alert/:alertId/resolve
  Vehicle:      POST   /api/vehicle/register
  Vehicle:      GET    /api/vehicle/:vehicleId
  Vehicle:      GET    /api/vehicle/driver/:driverId
  Vehicle:      PATCH  /api/vehicle/:vehicleId/verify
  Matching:     POST   /api/rides/find-matches
  Matching:     POST   /api/rides/validate-passenger
  Matching:     GET    /api/rides/driver/:driverId/can-create

GraphQL:
  Schema available at: /api/graphql/schema
  To integrate: Use Apollo Server or PostGraphile
    `);
});

