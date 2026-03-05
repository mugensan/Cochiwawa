export interface Ride {
    id: string;
    driverId: string;
    origin: string;
    destination: string;
    price: number;
    availableSeats: number;
    departureTime: Date;
    createdAt: Date;
}