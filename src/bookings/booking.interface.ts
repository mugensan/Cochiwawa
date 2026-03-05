export interface Booking{
    id: string;
    rideId: string;
    passengerId: string;
    seatsReserved: number;
    totalAmount: number;
    commissionAmount: number;
    createdAt: Date;
}