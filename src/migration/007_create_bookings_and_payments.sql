CREATE TABLE bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    passenger_id UUID REFERENCES users(id),
    ride_id INT REFERENCES rides(id),
    seats INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE driver_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    passenger_id UUID REFERENCES users(id),
    driver_id UUID REFERENCES users(id),
    ride_id INT REFERENCES rides(id),
    amount NUMERIC NOT NULL,
    platform_fee NUMERIC NOT NULL,
    driver_amount NUMERIC NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
