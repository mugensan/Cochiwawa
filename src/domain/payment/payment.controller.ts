import { PaymentModel, Payment } from './payment.model';
import * as chatService from '../../services/chatService';

export class PaymentController {
    static async createPayment(data: Payment) {
        return PaymentModel.create(data);
    }

    static async getPayment(id: number) {
        return PaymentModel.findById(id);
    }

    /**
     * Process successful payment and initialize chat
     * Called after payment confirmation from payment gateway
     */
    static async processSuccessfulPayment(rideId: string, passengerId: string, paymentData: Payment) {
        try {
            // Create the payment record
            const payment = await PaymentModel.create(paymentData);

            // Initialize chat room for the ride
            const chatRoomId = await chatService.initializeRideChat(rideId, passengerId);

            console.log(`Payment processed and chat initialized for ride ${rideId}, passenger ${passengerId}`);

            return {
                payment,
                chatRoomId,
                success: true
            };
        } catch (error) {
            console.error('Error processing successful payment:', error);
            throw new Error('Failed to process payment and initialize chat');
        }
    }
}