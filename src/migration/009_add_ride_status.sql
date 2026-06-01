-- Add status column to rides table for chat functionality
ALTER TABLE rides
ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE';

-- Add constraint for status values
ALTER TABLE rides ADD CONSTRAINT rides_status_check
CHECK (status IN ('ACTIVE', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'));

-- Add index for status queries
CREATE INDEX IF NOT EXISTS idx_rides_status ON rides(status);