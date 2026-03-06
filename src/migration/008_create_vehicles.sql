CREATE TABLE vehicles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    driver_id UUID REFERENCES users(id) UNIQUE,
    license_number TEXT NOT NULL,
    plate TEXT NOT NULL,
    model TEXT NOT NULL,
    color TEXT NOT NULL,
    insurance_url TEXT NOT NULL,
    photo_front TEXT NOT NULL,
    photo_back TEXT NOT NULL,
    photo_left TEXT NOT NULL,
    photo_driver_seat TEXT NOT NULL,
    status TEXT DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT NOW()
);
