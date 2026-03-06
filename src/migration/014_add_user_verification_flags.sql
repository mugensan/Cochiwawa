-- Add verification flags to users table for trust badges
ALTER TABLE users
ADD COLUMN IF NOT EXISTS verified_identity BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS verified_driver BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS verified_vehicle BOOLEAN DEFAULT FALSE;

-- Also add to drivers and passengers if they exist separately
-- Assuming users table is the main one, but adding to both for consistency
ALTER TABLE drivers
ADD COLUMN IF NOT EXISTS verified_identity BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS verified_driver BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS verified_vehicle BOOLEAN DEFAULT FALSE;

ALTER TABLE passengers
ADD COLUMN IF NOT EXISTS verified_identity BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS verified_driver BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS verified_vehicle BOOLEAN DEFAULT FALSE;
