-- Create driver vehicles table
CREATE TABLE IF NOT EXISTS driver_vehicles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    driver_id INT NOT NULL REFERENCES drivers(id) ON DELETE CASCADE,
    driver_license_number TEXT NOT NULL,
    vehicle_plate TEXT NOT NULL UNIQUE,
    vehicle_model TEXT NOT NULL,
    vehicle_color TEXT NOT NULL,
    insurance_document_url TEXT,
    car_photo_front TEXT,
    car_photo_back TEXT,
    car_photo_left TEXT,
    car_photo_driver_seat TEXT,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_driver_vehicles_driver_id ON driver_vehicles(driver_id);
CREATE INDEX IF NOT EXISTS idx_driver_vehicles_verified ON driver_vehicles(verified);
CREATE INDEX IF NOT EXISTS idx_driver_vehicles_vehicle_plate ON driver_vehicles(vehicle_plate);
