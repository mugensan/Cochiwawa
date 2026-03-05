import { PaymentModel, Payment } from './payment.model';

export class PaymentController {
    static async createPayment(data: Payment) {
        return PaymentModel.create(data);
    }

    static async getPayment(id: number) {
        return PaymentModel.findById(id);
    }
}