-- Create chat system tables for confirmed bookings
-- Chat rooms are created automatically after successful payment

-- Chat rooms table
CREATE TABLE IF NOT EXISTS chat_rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ride_id INT NOT NULL REFERENCES rides(id), -- References rides(id)
    created_at TIMESTAMP DEFAULT NOW()
);

-- Add index for ride_id lookups
CREATE INDEX IF NOT EXISTS idx_chat_rooms_ride_id ON chat_rooms(ride_id);

-- Chat participants table
CREATE TABLE IF NOT EXISTS chat_participants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chat_room_id UUID NOT NULL REFERENCES chat_rooms(id) ON DELETE CASCADE,
    user_type VARCHAR(20) NOT NULL, -- 'driver' or 'passenger'
    user_id INT NOT NULL, -- References drivers(id) or passengers(id)
    joined_at TIMESTAMP DEFAULT NOW()
);

-- Add indexes for performance
CREATE INDEX IF NOT EXISTS idx_chat_participants_chat_room_id ON chat_participants(chat_room_id);
CREATE INDEX IF NOT EXISTS idx_chat_participants_user_type_id ON chat_participants(user_type, user_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_chat_participants_unique ON chat_participants(chat_room_id, user_type, user_id);

-- Messages table
CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chat_room_id UUID NOT NULL REFERENCES chat_rooms(id) ON DELETE CASCADE,
    user_type VARCHAR(20) NOT NULL, -- 'driver' or 'passenger'
    user_id INT NOT NULL, -- References drivers(id) or passengers(id)
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Add indexes for message queries
CREATE INDEX IF NOT EXISTS idx_messages_chat_room_id ON messages(chat_room_id);
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at);
CREATE INDEX IF NOT EXISTS idx_messages_user_type_id ON messages(user_type, user_id);

-- Add constraint to ensure message is not empty
ALTER TABLE messages ADD CONSTRAINT messages_message_not_empty CHECK (length(trim(message)) > 0);