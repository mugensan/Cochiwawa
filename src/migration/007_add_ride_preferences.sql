-- Add gender preferences to rides table
ALTER TABLE rides
ADD COLUMN IF NOT EXISTS gender_preference TEXT DEFAULT 'ANY';

-- Add constraint for gender_preference values
ALTER TABLE rides ADD CONSTRAINT rides_gender_preference_check 
CHECK (gender_preference IN ('ANY', 'FEMALE_ONLY'));
