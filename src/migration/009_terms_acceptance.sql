-- Create terms acceptance table
CREATE TABLE IF NOT EXISTS terms_acceptance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id INT NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    accepted_at TIMESTAMP DEFAULT NOW(),
    terms_version VARCHAR(50) DEFAULT '1.0'
);

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_terms_acceptance_user_id ON terms_acceptance(user_id);
CREATE INDEX IF NOT EXISTS idx_terms_acceptance_accepted_at ON terms_acceptance(accepted_at);
