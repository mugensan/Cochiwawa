export class FareCalculator {
    static calculatePrice(distanceKm: number, ratePerKm: number, isPeak: boolean): number {
        let fare = distanceKm * ratePerKm;
        if (isPeak) fare *= 1.2;
        return fare;
    }
}