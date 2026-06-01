CREATE TABLE corridors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    origin TEXT NOT NULL,
    destination TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE recurring_rides (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    driver_id UUID REFERENCES users(id),
    corridor_id UUID REFERENCES corridors(id),
    departure_time TIME NOT NULL,
    days_of_week TEXT[] NOT NULL,
    seats INTEGER NOT NULL,
    price_per_seat NUMERIC NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) UNIQUE,
    plan_type TEXT NOT NULL, -- 'WEEKLY', 'MONTHLY'
    valid_until TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Seed some corridors
INSERT INTO corridors (name, origin, destination) VALUES
('Maipú → Providencia', 'Maipú', 'Providencia'),
('Puente Alto → Las Condes', 'Puente Alto', 'Las Condes'),
('Ñuñoa → Santiago Centro', 'Ñuñoa', 'Santiago Centro');
