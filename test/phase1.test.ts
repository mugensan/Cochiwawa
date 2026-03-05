import { PassengerController } from "../src/passengers/passenger.controller";
import { DriverController } from "../src/domain/driver/driver.controller";
import { RideController } from "../src/domain/ride/ride.controller";
import { PaymentController } from "../src/domain/payment/payment.controller";
import { FareCalculator } from "../src/utils/calculator";

async function runPhase1Tests() {
    try {
        const passenger = await PassengerController.createPassenger({
            first_name: "John",
            last_name: "Smith",
            email: "john.smith@example.com",
            phone: "1234567890",
        });
        console.log("Passenger created:", passenger);

        const driver = await DriverController.createDriver({
            first_name: "Jane",
            last_name: "Doe",
            email: "jane@doe.com",
            phone: "0987654321",
            vehicle_model: "Toyota Camry",
            vehicle_plate: "ABC123",
        });
        console.log("Driver created:", driver);

        const fare = FareCalculator.calculatePrice(10, 1.5, true);
        const ride = await RideController.createRide({
            passenger_id: passenger.id!,
            driver_id: driver.id!,
            origin: "123 Main St",
            destination: "456 Elm St",
            departure_time: new Date(),
        });
        console.log("Ride created:", ride);

        const payment = await PaymentController.createPayment({
            ride_id: ride.id!,
            amount: fare,
            method: "credit_card",
        });
        console.log("Payment created:", payment);
    } catch (err) {
        console.error("Error during tests:", err);
    }
}

runPhase1Tests();