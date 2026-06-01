CREATE TABLE NOT EXISTS payments (
    id SERIAL PRIMARY KEY,
    ride_id INT REFERENCES rides(id) ON DELETE CASCADE,
    amount DECIMAL(10, 2) NOT NULL,
    fee_percent DECIMAL(10, 2) NOT NULL,
    total DECIMAL (10, 2) GENERATED ALWAYS AS (amount + (amount * fee_percent / 100)) STORED,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);