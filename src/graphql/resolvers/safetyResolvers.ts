import * as identityService from '../../services/identityService';
import * as emergencyService from '../../services/emergencyService';
import * as vehicleService from '../../services/vehicleService';
import * as matchingService from '../../services/matchingService';
import { pool } from '../../config/database';

/**
 * GraphQL Resolvers for Identity and Safety System
 */

export const safetyResolvers = {
    Mutation: {
        /**
         * Update user identity information
         */
        updateUserIdentity: async (_: any, args: any) => {
            try {
                const result = await identityService.updateUserIdentity({
                    userId: args.userId,
                    userType: args.userType,
                    fullName: args.fullName,
                    gender: args.gender,
                    rut: args.rut,
                    passport: args.passport,
                    nationalId: args.nationalId,
                    profilePhotoUrl: args.profilePhotoUrl
                });

                if (result) {
                    const identity = await identityService.getUserIdentity(args.userId, args.userType);
                    return {
                        success: true,
                        message: 'Identity updated successfully',
                        userIdentity: {
                            id: identity.id,
                            fullName: identity.full_name,
                            gender: identity.gender,
                            rut: identity.rut,
                            passport: identity.passport,
                            nationalId: identity.national_id,
                            profilePhotoUrl: identity.profile_photo_url,
                            facialVerification: identity.facial_verification,
                            verified: identity.verified
                        }
                    };
                } else {
                    return {
                        success: false,
                        message: 'Failed to update identity'
                    };
                }
            } catch (error) {
                console.error('Error updating identity:', error);
                return {
                    success: false,
                    message: (error as any).message
                };
            }
        },

        /**
         * Verify document
         */
        verifyDocument: async (_: any, args: any) => {
            try {
                const isValid = await identityService.verifyDocument(
                    args.documentType.toLowerCase(),
                    args.documentValue
                );
                return isValid;
            } catch (error) {
                console.error('Error verifying document:', error);
                return false;
            }
        },

        /**
         * Trigger emergency alert
         */
        triggerEmergencyAlert: async (_: any, args: any) => {
            try {
                const alert = await emergencyService.createEmergencyAlert({
                    userId: args.userId,
                    userType: args.userType,
                    latitude: args.latitude,
                    longitude: args.longitude
                });

                // Broadcast via WebSocket
                emergencyService.broadcastEmergencyAlert(alert);

                return {
                    success: true,
                    alertId: alert.id,
                    message: 'Emergency alert triggered'
                };
            } catch (error) {
                console.error('Error triggering emergency alert:', error);
                return {
                    success: false,
                    message: (error as any).message
                };
            }
        },

        /**
         * Dismiss emergency alert
         */
        dismissEmergencyAlert: async (_: any, args: any) => {
            try {
                const result = await emergencyService.dismissAlert(args.alertId);
                return result;
            } catch (error) {
                console.error('Error dismissing alert:', error);
                return false;
            }
        },

        /**
         * Accept terms and conditions
         */
        acceptTerms: async (_: any, args: any) => {
            try {
                const result = await pool.query(
                    `INSERT INTO terms_acceptance (user_id, user_type, terms_version)
                     VALUES ($1, $2, '1.0')
                     ON CONFLICT DO NOTHING`,
                    [args.userId, args.userType]
                );
                return true;
            } catch (error) {
                console.error('Error accepting terms:', error);
                return false;
            }
        },

        /**
         * Register vehicle
         */
        registerVehicle: async (_: any, args: any) => {
            try {
                const vehicle = await vehicleService.registerVehicle({
                    driverId: args.driverId,
                    driverLicenseNumber: args.driverLicenseNumber,
                    vehiclePlate: args.vehiclePlate,
                    vehicleModel: args.vehicleModel,
                    vehicleColor: args.vehicleColor,
                    insuranceDocumentUrl: args.insuranceDocumentUrl,
                    carPhotoFront: args.carPhotoFront,
                    carPhotoBack: args.carPhotoBack,
                    carPhotoLeft: args.carPhotoLeft,
                    carPhotoDriverSeat: args.carPhotoDriverSeat
                });

                // Create admin review record
                await pool.query(
                    `INSERT INTO admin_reviews (driver_id, vehicle_id, review_type, status)
                     VALUES ($1, $2, 'VEHICLE_VERIFICATION', 'PENDING')`,
                    [args.driverId, vehicle.id]
                );

                return {
                    success: true,
                    vehicleId: vehicle.id,
                    message: 'Vehicle registered successfully'
                };
            } catch (error) {
                console.error('Error registering vehicle:', error);
                return {
                    success: false,
                    message: (error as any).message
                };
            }
        },

        /**
         * Update vehicle images
         */
        updateVehicleImages: async (_: any, args: any) => {
            try {
                const result = await vehicleService.updateVehicle(args.vehicleId, {
                    carPhotoFront: args.carPhotoFront,
                    carPhotoBack: args.carPhotoBack,
                    carPhotoLeft: args.carPhotoLeft,
                    carPhotoDriverSeat: args.carPhotoDriverSeat
                });
                return result;
            } catch (error) {
                console.error('Error updating vehicle images:', error);
                return false;
            }
        },

        /**
         * Verify vehicle (admin action)
         */
        verifyVehicle: async (_: any, args: any) => {
            try {
                const result = await vehicleService.verifyVehicle(args.vehicleId);

                if (result) {
                    // Get vehicle to get driver ID
                    const vehicle = await vehicleService.getVehicleById(args.vehicleId);
                    if (vehicle) {
                        // Broadcast approval
                        vehicleService.broadcastVehicleApproval(vehicle.driverId);
                    }
                }

                return result;
            } catch (error) {
                console.error('Error verifying vehicle:', error);
                return false;
            }
        },

        /**
         * Approve vehicle review
         */
        approveVehicleReview: async (_: any, args: any) => {
            try {
                const result = await pool.query(
                    `UPDATE admin_reviews
                     SET status = 'APPROVED', notes = $1, updated_at = NOW()
                     WHERE id = $2
                     RETURNING vehicle_id`,
                    [args.notes || '', args.reviewId]
                );

                if (result.rows.length > 0) {
                    const vehicleId = result.rows[0].vehicle_id;
                    await vehicleService.verifyVehicle(vehicleId);
                    return true;
                }
                return false;
            } catch (error) {
                console.error('Error approving review:', error);
                return false;
            }
        },

        /**
         * Reject vehicle review
         */
        rejectVehicleReview: async (_: any, args: any) => {
            try {
                await pool.query(
                    `UPDATE admin_reviews
                     SET status = 'REJECTED', notes = $1, updated_at = NOW()
                     WHERE id = $2`,
                    [args.notes, args.reviewId]
                );
                return true;
            } catch (error) {
                console.error('Error rejecting review:', error);
                return false;
            }
        },

        /**
         * Create ride with preferences
         */
        createRideWithPreferences: async (_: any, args: any) => {
            try {
                // Check if driver can create rides
                const canCreate = await matchingService.canDriverCreateRide(args.driverId);
                if (!canCreate) {
                    throw new Error('Driver must be verified with a verified vehicle to create rides');
                }

                const result = await pool.query(
                    `INSERT INTO rides (driver_id, origin, destination, departure_time, available_seats, price_per_seat, gender_preference)
                     VALUES ($1, $2, $3, $4, $5, $6, $7)
                     RETURNING *`,
                    [
                        args.driverId,
                        args.origin,
                        args.destination,
                        args.departureTime,
                        args.availableSeats,
                        args.pricePerSeat,
                        args.genderPreference || 'ANY'
                    ]
                );

                const ride = result.rows[0];
                return {
                    id: ride.id,
                    driverId: ride.driver_id,
                    origin: ride.origin,
                    destination: ride.destination,
                    departureTime: ride.departure_time,
                    availableSeats: ride.available_seats,
                    pricePerSeat: ride.price_per_seat,
                    genderPreference: ride.gender_preference,
                    createdAt: ride.created_at,
                    updatedAt: ride.updated_at
                };
            } catch (error) {
                console.error('Error creating ride:', error);
                throw error;
            }
        }
    },

    Query: {
        /**
         * Get active emergency alerts
         */
        getEmergencyAlerts: async () => {
            try {
                const alerts = await emergencyService.getActiveAlerts();
                return alerts.map((alert: any) => ({
                    id: alert.id,
                    userId: alert.user_id,
                    userType: alert.user_type,
                    latitude: alert.latitude,
                    longitude: alert.longitude,
                    status: alert.status,
                    createdAt: alert.created_at
                }));
            } catch (error) {
                console.error('Error getting emergency alerts:', error);
                return [];
            }
        },

        /**
         * Get user's emergency alerts
         */
        getUserEmergencyAlerts: async (_: any, args: any) => {
            try {
                const alerts = await emergencyService.getUserAlerts(args.userId, args.userType);
                return alerts.map((alert: any) => ({
                    id: alert.id,
                    userId: alert.user_id,
                    userType: alert.user_type,
                    latitude: alert.latitude,
                    longitude: alert.longitude,
                    status: alert.status,
                    createdAt: alert.created_at
                }));
            } catch (error) {
                console.error('Error getting user alerts:', error);
                return [];
            }
        },

        /**
         * Get driver vehicles
         */
        getDriverVehicles: async (_: any, args: any) => {
            try {
                const vehicles = await vehicleService.getDriverVehicles(args.driverId);
                return vehicles.map((v: any) => ({
                    id: v.id,
                    driverId: v.driver_id,
                    driverLicenseNumber: v.driver_license_number,
                    vehiclePlate: v.vehicle_plate,
                    vehicleModel: v.vehicle_model,
                    vehicleColor: v.vehicle_color,
                    insuranceDocumentUrl: v.insurance_document_url,
                    carPhotoFront: v.car_photo_front,
                    carPhotoBack: v.car_photo_back,
                    carPhotoLeft: v.car_photo_left,
                    carPhotoDriverSeat: v.car_photo_driver_seat,
                    verified: v.verified,
                    createdAt: v.created_at,
                    updatedAt: v.updated_at
                }));
            } catch (error) {
                console.error('Error getting driver vehicles:', error);
                return [];
            }
        },

        /**
         * Get vehicle details
         */
        getVehicleDetails: async (_: any, args: any) => {
            try {
                const vehicle = await vehicleService.getVehicleById(args.vehicleId);
                if (!vehicle) return null;

                return {
                    id: vehicle.id,
                    driverId: vehicle.driverId,
                    driverLicenseNumber: vehicle.driverLicenseNumber,
                    vehiclePlate: vehicle.vehiclePlate,
                    vehicleModel: vehicle.vehicleModel,
                    vehicleColor: vehicle.vehicleColor,
                    insuranceDocumentUrl: vehicle.insuranceDocumentUrl,
                    carPhotoFront: vehicle.carPhotoFront,
                    carPhotoBack: vehicle.carPhotoBack,
                    carPhotoLeft: vehicle.carPhotoLeft,
                    carPhotoDriverSeat: vehicle.carPhotoDriverSeat,
                    verified: vehicle.verified,
                    createdAt: vehicle.createdAt,
                    updatedAt: vehicle.updatedAt
                };
            } catch (error) {
                console.error('Error getting vehicle details:', error);
                return null;
            }
        },

        /**
         * Get all unverified vehicles for admin
         */
        getAllUnverifiedVehicles: async (_: any, args: any) => {
            try {
                const limit = args.limit || 50;
                const offset = args.offset || 0;
                const vehicles = await vehicleService.getUnverifiedVehicles(limit, offset);
                return vehicles.map((v: any) => ({
                    id: v.id,
                    driverId: v.driver_id,
                    driverLicenseNumber: v.driver_license_number,
                    vehiclePlate: v.vehicle_plate,
                    vehicleModel: v.vehicle_model,
                    vehicleColor: v.vehicle_color,
                    insuranceDocumentUrl: v.insurance_document_url,
                    carPhotoFront: v.car_photo_front,
                    carPhotoBack: v.car_photo_back,
                    carPhotoLeft: v.car_photo_left,
                    carPhotoDriverSeat: v.car_photo_driver_seat,
                    verified: v.verified,
                    createdAt: v.created_at,
                    updatedAt: v.updated_at
                }));
            } catch (error) {
                console.error('Error getting unverified vehicles:', error);
                return [];
            }
        },

        /**
         * Get admin reviews
         */
        getAdminReviews: async (_: any, args: any) => {
            try {
                const query = args.driverId
                    ? `SELECT * FROM admin_reviews WHERE driver_id = $1 ORDER BY created_at DESC`
                    : `SELECT * FROM admin_reviews ORDER BY created_at DESC LIMIT 100`;

                const result = await pool.query(query, args.driverId ? [args.driverId] : []);

                return result.rows.map((r: any) => ({
                    id: r.id,
                    driverId: r.driver_id,
                    vehicleId: r.vehicle_id,
                    reviewType: r.review_type,
                    status: r.status,
                    notes: r.notes,
                    reviewedBy: r.reviewed_by,
                    createdAt: r.created_at,
                    updatedAt: r.updated_at
                }));
            } catch (error) {
                console.error('Error getting admin reviews:', error);
                return [];
            }
        },

        /**
         * Find matching rides for a passenger
         */
        findMatchingRides: async (_: any, args: any) => {
            try {
                const matches = await matchingService.findMatchingRides(
                    args.passengerId,
                    args.lat,
                    args.lng,
                    args.maxDistance || 50
                );

                return matches.map((match: any) => ({
                    rideId: match.rideId,
                    distance: match.distance,
                    matchScore: match.matchScore,
                    driver: null // Can be populated with driver details if needed
                }));
            } catch (error) {
                console.error('Error finding matching rides:', error);
                return [];
            }
        }
    }
};

export default safetyResolvers;
