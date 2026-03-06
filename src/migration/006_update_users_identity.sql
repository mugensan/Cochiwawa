-- Add identity verification fields to drivers table
ALTER TABLE drivers
ADD COLUMN IF NOT EXISTS full_name TEXT,
ADD COLUMN IF NOT EXISTS gender TEXT,
ADD COLUMN IF NOT EXISTS rut TEXT,
ADD COLUMN IF NOT EXISTS passport TEXT,
ADD COLUMN IF NOT EXISTS national_id TEXT,
ADD COLUMN IF NOT EXISTS profile_photo_url TEXT,
ADD COLUMN IF NOT EXISTS facial_verification BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT FALSE;

-- Add identity verification fields to passengers table
ALTER TABLE passengers
ADD COLUMN IF NOT EXISTS full_name TEXT,
ADD COLUMN IF NOT EXISTS gender TEXT,
ADD COLUMN IF NOT EXISTS rut TEXT,
ADD COLUMN IF NOT EXISTS passport TEXT,
ADD COLUMN IF NOT EXISTS national_id TEXT,
ADD COLUMN IF NOT EXISTS profile_photo_url TEXT,
ADD COLUMN IF NOT EXISTS facial_verification BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT FALSE;

-- Add constraint for gender values
ALTER TABLE drivers ADD CONSTRAINT drivers_gender_check 
CHECK (gender IS NULL OR gender IN ('MALE', 'FEMALE', 'OTHER'));

ALTER TABLE passengers ADD CONSTRAINT passengers_gender_check 
CHECK (gender IS NULL OR gender IN ('MALE', 'FEMALE', 'OTHER'));
