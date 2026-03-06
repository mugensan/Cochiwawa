/**
 * Distance Utility - Helper functions for geographic calculations
 * Used for route matching and proximity calculations
 */

/**
 * Calculate distance between two coordinates using Haversine formula
 * @param lat1 Latitude of first point
 * @param lon1 Longitude of first point
 * @param lat2 Latitude of second point
 * @param lon2 Longitude of second point
 * @returns Distance in kilometers
 */
export function calculateDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
    const R = 6371; // Earth's radius in kilometers
    const dLat = ((lat2 - lat1) * Math.PI) / 180;
    const dLon = ((lon2 - lon1) * Math.PI) / 180;
    const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos((lat1 * Math.PI) / 180) *
            Math.cos((lat2 * Math.PI) / 180) *
            Math.sin(dLon / 2) *
            Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distance = R * c;
    return distance;
}

/**
 * Check if coordinates are within a certain radius
 * @param centerLat Center latitude
 * @param centerLng Center longitude
 * @param pointLat Point latitude
 * @param pointLng Point longitude
 * @param radiusKm Radius in kilometers
 * @returns True if point is within radius
 */
export function isWithinRadius(
    centerLat: number,
    centerLng: number,
    pointLat: number,
    pointLng: number,
    radiusKm: number
): boolean {
    const distance = calculateDistance(centerLat, centerLng, pointLat, pointLng);
    return distance <= radiusKm;
}

/**
 * Simple geocoding placeholder - in production, use Google Maps API or similar
 * @param address Address string
 * @returns Promise with lat/lng coordinates
 */
export async function geocodeAddress(address: string): Promise<{ lat: number; lng: number }> {
    // TODO: Implement actual geocoding service
    // For now, return mock coordinates based on Chilean cities
    const mockCoordinates: { [key: string]: { lat: number; lng: number } } = {
        'Santiago': { lat: -33.4489, lng: -70.6693 },
        'Viña del Mar': { lat: -33.0246, lng: -71.5518 },
        'Valparaíso': { lat: -33.0472, lng: -71.6127 },
        'Concepción': { lat: -36.8269, lng: -73.0498 },
        'Antofagasta': { lat: -23.6509, lng: -70.3975 },
        'Temuco': { lat: -38.7359, lng: -72.5904 },
        'Puerto Montt': { lat: -41.4689, lng: -72.9411 },
        'Punta Arenas': { lat: -53.1638, lng: -70.9171 }
    };

    const normalizedAddress = address.toLowerCase();
    for (const [city, coords] of Object.entries(mockCoordinates)) {
        if (normalizedAddress.includes(city.toLowerCase())) {
            return coords;
        }
    }

    // Default fallback
    return { lat: -33.4489, lng: -70.6693 }; // Santiago
}

/**
 * Reverse geocoding placeholder
 * @param lat Latitude
 * @param lng Longitude
 * @returns Promise with address string
 */
export async function reverseGeocode(lat: number, lng: number): Promise<string> {
    // TODO: Implement actual reverse geocoding
    // Simple mock based on coordinates
    if (Math.abs(lat - (-33.4489)) < 0.1 && Math.abs(lng - (-70.6693)) < 0.1) {
        return 'Santiago, Chile';
    }
    if (Math.abs(lat - (-33.0246)) < 0.1 && Math.abs(lng - (-71.5518)) < 0.1) {
        return 'Viña del Mar, Chile';
    }
    // Add more as needed
    return `${lat.toFixed(4)}, ${lng.toFixed(4)}`;
}
