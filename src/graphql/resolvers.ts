import { registerUser, loginUser } from "../services/authService";
import { pool } from "../config/database";
import { createBooking, getPassengerBookings } from "../services/bookingService";
import { getDriverEarnings } from "../services/paymentService";
import { broadcast } from "../websocket/WebSocketServer";

export const resolvers = {
  Query: {
    me: (parent: any, args: any, context: any) => {
      return context.user;
    },
    getDriverRides: async (parent: any, { driverId }: any) => {
      const result = await pool.query("SELECT * FROM rides WHERE driver_id = $1", [driverId]);
      return result.rows.map(r => ({
        ...r,
        driverId: r.driver_id,
        departureTime: r.departure_time.toISOString(),
        availableSeats: r.available_seats,
        pricePerSeat: r.price_per_seat
      }));
    },
    getPassengerBookings: async (parent: any, { passengerId }: any) => {
      const bookings = await getPassengerBookings(passengerId);
      return bookings.map(b => ({
        ...b,
        passengerId: b.passenger_id,
        rideId: b.ride_id,
        createdAt: b.created_at.toISOString(),
        ride: {
            id: b.ride_id,
            origin: b.origin,
            destination: b.destination,
            departureTime: b.departure_time.toISOString(),
            pricePerSeat: b.price_per_seat
        }
      }));
    },
    getDriverEarnings: async (parent: any, { driverId }: any) => {
      const earnings = await getDriverEarnings(driverId);
      return {
        totalEarnings: parseFloat(earnings.total_earnings || "0"),
        totalRides: parseInt(earnings.total_rides || "0")
      };
    }
  },
  Mutation: {
    signUp: async (parent: any, { email, password, role, fullName, gender, nationalId, profilePhotoUrl }: any) => {
      const user = await registerUser(email, password, role, fullName, gender, nationalId, profilePhotoUrl);
      const auth = await loginUser(email, password);
      if (!auth) throw new Error("Failed to login after registration");
      return auth;
    },
    signIn: async (parent: any, { email, password }: any) => {
      const auth = await loginUser(email, password);
      if (!auth) throw new Error("Invalid credentials");
      return auth;
    },
    registerVehicle: async (parent: any, args: any) => {
      const {
        driverId, driverLicenseNumber, vehiclePlate, vehicleModel,
        vehicleColor, insuranceDocumentUrl, carPhotoFront,
        carPhotoBack, carPhotoLeft, carPhotoDriverSeat
      } = args;

      try {
        await pool.query(
          `INSERT INTO vehicles (
            driver_id, license_number, plate, model, color,
            insurance_url, photo_front, photo_back, photo_left, photo_driver_seat
          ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)`,
          [
            driverId, driverLicenseNumber, vehiclePlate, vehicleModel,
            vehicleColor, insuranceDocumentUrl, carPhotoFront,
            carPhotoBack, carPhotoLeft, carPhotoDriverSeat
          ]
        );
        broadcast({ type: "VEHICLE_APPROVED", driverId }); // Simplified broadcast
        return true;
      } catch (e) {
        console.error(e);
        return false;
      }
    },
    triggerEmergencyAlert: async (parent: any, { userId, lat, lng }: any) => {
        console.log(`EMERGENCY ALERT from user ${userId} at ${lat}, ${lng}`);
        broadcast({ type: "EMERGENCY_ALERT", userId, lat, lng });
        return true;
    },
    bookRide: async (parent: any, { rideId, seats }: any, context: any) => {
        if (!context.user) throw new Error("Unauthorized");
        const booking = await createBooking(context.user.userId, rideId, seats);
        broadcast({ type: "RIDE_UPDATED", rideId });
        return booking;
    }
  },
};
