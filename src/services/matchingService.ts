import { pool } from '../config/database';

/**
 * Matching Service - Handles ride matching algorithm
 * Integrates safety preferences including gender-based filtering
 */

export interface RideMatch {
    rideId: number;
    driverId: number;
    passengerId: number;
    distance: number;
    matchScore: number;
}

export interface Ride {
    id: number;
    driver_id: number;
    origin: string;
    destination: string;
    departure_time: Date;
    available_seats: number;
    price_per_seat: number;
    gender_preference?: string;
    created_at?: Date;
    updated_at?: Date;
}

export interface PassengerInfo {
    id: number;
    gender?: string;
    verified?: boolean;
}

export interface DriverInfo {
    id: number;
    verified?: boolean;
}

/**
 * Check if passenger matches driver's gender preference
 */
async function checkGenderPreference(ride: Ride, passengerId: number): Promise<boolean> {
    try {
        if (!ride.gender_preference || ride.gender_preference === 'ANY') {
            return true;
        }

        // Get passenger gender
        const result = await pool.query(
            `SELECT gender FROM passengers WHERE id = $1`,
            [passengerId]
        );

        if (result.rows.length === 0) {
            return false;
        }

        const passengerGender = result.rows[0].gender;

        // Apply gender preference filtering
        if (ride.gender_preference === 'FEMALE_ONLY' && passengerGender !== 'FEMALE') {
            return false;
        }

        return true;
    } catch (error) {
        console.error('Error checking gender preference:', error);
        return false;
    }
}

/**
 * Check if driver is verified
 */
async function isDriverVerified(driverId: number): Promise<boolean> {
    try {
        const result = await pool.query(
            `SELECT verified FROM drivers WHERE id = $1`,
            [driverId]
        );

        if (result.rows.length === 0) {
            return false;
        }

        return result.rows[0].verified === true;
    } catch (error) {
        console.error('Error checking driver verification:', error);
        return false;
    }
}

/**
 * Check if driver's vehicle is verified
 */
async function isDriverVehicleVerified(driverId: number): Promise<boolean> {
    try {
        const result = await pool.query(
            `SELECT verified FROM driver_vehicles
             WHERE driver_id = $1 AND verified = true
             LIMIT 1`,
            [driverId]
        );

        return result.rows.length > 0;
    } catch (error) {
        console.error('Error checking vehicle verification:', error);
        return false;
    }
}

/**
 * Check if passenger is verified
 */
async function isPassengerVerified(passengerId: number): Promise<boolean> {
    try {
        const result = await pool.query(
            `SELECT verified FROM passengers WHERE id = $1`,
            [passengerId]
        );

        if (result.rows.length === 0) {
            return false;
        }

        return result.rows[0].verified === true;
    } catch (error) {
        console.error('Error checking passenger verification:', error);
        return false;
    }
}

/**
 * Calculate distance between two coordinates (simplified)
 */
function calculateDistance(lat1: number, lng1: number, lat2: number, lng2: number): number {
    const R = 6371; // Earth's radius in km
    const dLat = ((lat2 - lat1) * Math.PI) / 180;
    const dLng = ((lng2 - lng1) * Math.PI) / 180;
    const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos((lat1 * Math.PI) / 180) *
            Math.cos((lat2 * Math.PI) / 180) *
            Math.sin(dLng / 2) *
            Math.sin(dLng / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
}

/**
 * Find matching rides for a passenger
 * Priority: Gender preference -> Verification status -> Distance/Price/Time
 */
export async function findMatchingRides(
    passengerId: number,
    passengerLat: number,
    passengerLng: number,
    maxDistanceKm: number = 50
): Promise<RideMatch[]> {
    try {
        // Check if passenger is verified
        const isVerified = await isPassengerVerified(passengerId);
        if (!isVerified) {
            console.log('Passenger not verified');
            return [];
        }

        // Get available rides
        const ridesResult = await pool.query(
            `SELECT r.id, r.driver_id, r.available_seats, r.gender_preference, 
                    d.verified
             FROM rides r
             JOIN drivers d ON r.driver_id = d.id
             WHERE r.available_seats > 0
             AND r.departure_time > NOW()`,
        );

        const matches: RideMatch[] = [];

        for (const ride of ridesResult.rows) {
            // Step 1: Check gender preference (SAFETY FILTER - HIGHEST PRIORITY)
            const genderMatch = await checkGenderPreference(ride, passengerId);
            if (!genderMatch) {
                console.log(`Ride ${ride.id} filtered due to gender preference`);
                continue;
            }

            // Step 2: Check if driver is verified
            const driverVerified = ride.verified;
            if (!driverVerified) {
                console.log(`Ride ${ride.id} driver not verified`);
                continue;
            }

            // Step 3: Check if driver's vehicle is verified
            const vehicleVerified = await isDriverVehicleVerified(ride.driver_id);
            if (!vehicleVerified) {
                console.log(`Ride ${ride.id} vehicle not verified`);
                continue;
            }

            // Step 4: Calculate distance (if we had coordinates)
            // For now, using a mock distance
            const distance = Math.random() * maxDistanceKm;

            if (distance <= maxDistanceKm) {
                const matchScore = 100 - (distance / maxDistanceKm) * 50; // Score based on distance
                matches.push({
                    rideId: ride.id,
                    driverId: ride.driver_id,
                    passengerId,
                    distance,
                    matchScore
                });
            }
        }

        // Sort by match score (descending)
        matches.sort((a, b) => b.matchScore - a.matchScore);

        return matches;
    } catch (error) {
        console.error('Error finding matching rides:', error);
        throw new Error('Failed to find matching rides');
    }
}

/**
 * Validate if a passenger can be assigned to a ride
 */
export async function validatePassengerAssignment(rideId: number, passengerId: number): Promise<boolean> {
    try {
        // Get ride
        const rideResult = await pool.query(
            `SELECT id, driver_id, available_seats, gender_preference FROM rides WHERE id = $1`,
            [rideId]
        );

        if (rideResult.rows.length === 0) {
            return false;
        }

        const ride = rideResult.rows[0];

        // Check if passenger is verified
        const passengerVerified = await isPassengerVerified(passengerId);
        if (!passengerVerified) {
            console.log(`Passenger ${passengerId} not verified`);
            return false;
        }

        // Check gender preference
        const genderMatch = await checkGenderPreference(ride, passengerId);
        if (!genderMatch) {
            console.log(`Gender preference mismatch for race ${rideId}`);
            return false;
        }

        // Check driver verification
        const driverVerified = await isDriverVerified(ride.driver_id);
        if (!driverVerified) {
            console.log(`Driver ${ride.driver_id} not verified`);
            return false;
        }

        // Check vehicle verification
        const vehicleVerified = await isDriverVehicleVerified(ride.driver_id);
        if (!vehicleVerified) {
            console.log(`Driver ${ride.driver_id} vehicle not verified`);
            return false;
        }

        return true;
    } catch (error) {
        console.error('Error validating passenger assignment:', error);
        return false;
    }
}

/**
 * Check if a driver can create a ride
 */
export async function canDriverCreateRide(driverId: number): Promise<boolean> {
    try {
        // Check if driver is verified
        const driverVerified = await isDriverVerified(driverId);
        if (!driverVerified) {
            console.log(`Driver ${driverId} not verified`);
            return false;
        }

        // Check if driver has a verified vehicle
        const vehicleVerified = await isDriverVehicleVerified(driverId);
        if (!vehicleVerified) {
            console.log(`Driver ${driverId} vehicle not verified`);
            return false;
        }

        return true;
    } catch (error) {
        console.error('Error checking driver eligibility:', error);
        return false;
    }
}

/**
 * Get matching drivers for a ride (for rider requests)
 */
export async function findMatchingDrivers(
    rideId: number,
    maxResults: number = 10
): Promise<RideMatch[]> {
    try {
        // Implementation for finding drivers for a specific ride request
        // This is the reverse operation - riders can also request rides
        const result = await pool.query(
            `SELECT d.id, d.verified FROM drivers d
             WHERE d.verified = true
             AND EXISTS (
                 SELECT 1 FROM driver_vehicles dv
                 WHERE dv.driver_id = d.id AND dv.verified = true
             )
             LIMIT $1`,
            [maxResults]
        );

        const matches: RideMatch[] = result.rows.map((driver: any) => ({
            rideId,
            driverId: driver.id,
            passengerId: 0,
            distance: 0,
            matchScore: 90 // High score since driver is verified
        }));

        return matches;
    } catch (error) {
        console.error('Error finding matching drivers:', error);
        throw new Error('Failed to find matching drivers');
    }
}
