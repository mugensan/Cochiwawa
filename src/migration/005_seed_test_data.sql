--Sample passengers for testing purposes
INSERT INTO passengers (first_name, last_name, email, phone) VALUES
('John', 'Doe', 'john@doe.com', '+56900000000'),
('Jane', 'Smith', 'jane@smith', '+56900000001'),

--Sample drivers for testing purposes
INSERT INTO drivers (first_name, last_name, email, phone, vehicle_model, vehicle_plate) VALUES
('John', 'Doe', 'john@doe.com', '+56900000000'),
('Jane', 'Smith', 'jane@smith', '+56900000001');

--Sample rides for testing purposes
INSERT INTO rides (driver_id, origin, destination, departure_time, available_seats, price_per_seat) VALUES
(1, 'Santiago', 'Valparaiso', '2024-07-01 08:00:00', 3, 10.00),
(2, 'Valparaiso', 'Santiago', '2024-07-01 09:00:00', 2, 12.00);

