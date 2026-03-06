-- Add coordinates to rides table for route matching
ALTER TABLE rides
ADD COLUMN IF NOT EXISTS latitude_origin DOUBLE PRECISION,
ADD COLUMN IF NOT EXISTS longitude_origin DOUBLE PRECISION,
ADD COLUMN IF NOT EXISTS latitude_destination DOUBLE PRECISION,
ADD COLUMN IF NOT EXISTS longitude_destination DOUBLE PRECISION;

-- Add status column if not exists
ALTER TABLE rides
ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE';

-- Add constraint for status
ALTER TABLE rides ADD CONSTRAINT rides_status_check
CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED'));
