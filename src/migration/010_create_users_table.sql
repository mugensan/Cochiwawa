-- Create users table to unify passengers and drivers
-- This provides a common reference for chat system and other features

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'driver' or 'passenger'
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Add constraint for role values
ALTER TABLE users ADD CONSTRAINT users_role_check
CHECK (role IN ('driver', 'passenger'));

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Migrate existing drivers to users table
INSERT INTO users (email, password_hash, role, first_name, last_name, phone)
SELECT email, 'temp_hash', 'driver', first_name, last_name, phone
FROM drivers
ON CONFLICT (email) DO NOTHING;

-- Migrate existing passengers to users table
INSERT INTO users (email, password_hash, role, first_name, last_name, phone)
SELECT email, 'temp_hash', 'passenger', first_name, last_name, phone
FROM passengers
ON CONFLICT (email) DO NOTHING;

-- Note: Password hashes should be properly migrated in production
-- This is a placeholder migration