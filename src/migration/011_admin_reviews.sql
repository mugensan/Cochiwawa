-- Create admin reviews table
CREATE TABLE IF NOT EXISTS admin_reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    driver_id INT NOT NULL REFERENCES drivers(id) ON DELETE CASCADE,
    vehicle_id UUID REFERENCES driver_vehicles(id) ON DELETE CASCADE,
    review_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    reviewed_by INT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Add constraint for status values
ALTER TABLE admin_reviews ADD CONSTRAINT admin_reviews_status_check 
CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'));

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_admin_reviews_driver_id ON admin_reviews(driver_id);
CREATE INDEX IF NOT EXISTS idx_admin_reviews_vehicle_id ON admin_reviews(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_admin_reviews_status ON admin_reviews(status);
CREATE INDEX IF NOT EXISTS idx_admin_reviews_created_at ON admin_reviews(created_at);
