import { PassengerModel, Passenger } from './passengers.model';

export class PassengerController {
    static async createPassenger(data: Passenger) {
        return PassengerModel.create(data);
    }

    static async listPassengers() {
        return PassengerModel.findAll();
    }

    static async getPassenger(id: number) {
        return PassengerModel.findById(id);
    }
}