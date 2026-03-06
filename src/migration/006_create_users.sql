CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL,
    full_name TEXT,
    gender TEXT,
    national_id TEXT,
    profile_photo_url TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);
