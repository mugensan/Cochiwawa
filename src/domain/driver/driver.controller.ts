import { DriverModel, Driver } from './driver.model';

export class DriverController {
    static async createDriver(data: Driver) {
        return DriverModel.create(data);
    }

    static async getDriver(id: number) {
        return DriverModel.findById(id);
    }
}