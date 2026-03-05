import { RideModel, Ride } from './ride.model';

export class RideController {
    static async createRide(data: Ride) {
        return RideModel.create(data);
    }

    static async getRide(id: number) {
        return RideModel.findById(id);
    }
}