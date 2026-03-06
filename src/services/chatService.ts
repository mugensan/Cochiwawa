import { pool } from '../config/database';

/**
 * Chat Service - Handles chat room management for confirmed bookings
 * Manages chat rooms, participants, and messages for trip-based communication
 */

export interface ChatRoom {
    id: string;
    rideId: string;
    createdAt: Date;
}

export interface ChatParticipant {
    id: string;
    chatRoomId: string;
    userType: 'driver' | 'passenger';
    userId: number;
    joinedAt: Date;
}

export interface Message {
    id: string;
    chatRoomId: string;
    userType: 'driver' | 'passenger';
    userId: number;
    message: string;
    createdAt: Date;
}

export interface ChatRoomSummary {
    id: string;
    rideId: string;
    rideRoute: string;
    driverName: string;
    lastMessage?: string;
    lastMessageTime?: Date;
    participantCount: number;
}

/**
 * Create a chat room for a ride
 * Should be called after successful payment confirmation
 */
export async function createChatRoom(rideId: string): Promise<ChatRoom> {
    try {
        // Check if chat room already exists for this ride
        const existing = await pool.query(
            'SELECT id FROM chat_rooms WHERE ride_id = $1',
            [rideId]
        );

        if (existing.rows.length > 0) {
            throw new Error('Chat room already exists for this ride');
        }

        const result = await pool.query(
            'INSERT INTO chat_rooms (ride_id) VALUES ($1) RETURNING *',
            [rideId]
        );

        console.log(`Chat room created for ride ${rideId}`);
        return result.rows[0];
    } catch (error) {
        console.error('Error creating chat room:', error);
        throw new Error('Failed to create chat room');
    }
}

/**
 * Add a participant to a chat room
 * Should be called for driver and confirmed passengers
 */
export async function addParticipant(chatRoomId: string, userType: 'driver' | 'passenger', userId: number): Promise<boolean> {
    try {
        // Check if already a participant
        const existing = await pool.query(
            'SELECT id FROM chat_participants WHERE chat_room_id = $1 AND user_type = $2 AND user_id = $3',
            [chatRoomId, userType, userId]
        );

        if (existing.rows.length > 0) {
            return true; // Already a participant
        }

        await pool.query(
            'INSERT INTO chat_participants (chat_room_id, user_type, user_id) VALUES ($1, $2, $3)',
            [chatRoomId, userType, userId]
        );

        console.log(`User ${userType} ${userId} added to chat room ${chatRoomId}`);
        return true;
    } catch (error) {
        console.error('Error adding participant:', error);
        throw new Error('Failed to add participant');
    }
}

/**
 * Send a message to a chat room
 * Includes access control and trip completion checks
 */
export async function sendMessage(chatRoomId: string, userType: 'driver' | 'passenger', userId: number, message: string): Promise<Message> {
    try {
        // Verify sender is a participant
        const participantCheck = await pool.query(
            'SELECT cp.id FROM chat_participants cp WHERE cp.chat_room_id = $1 AND cp.user_type = $2 AND cp.user_id = $3',
            [chatRoomId, userType, userId]
        );

        if (participantCheck.rows.length === 0) {
            throw new Error('Access denied: User is not a participant in this chat room');
        }

        // Check if ride is completed (chat should be read-only)
        const rideCheck = await pool.query(
            `SELECT r.status, cr.is_active FROM chat_rooms cr
             JOIN rides r ON cr.ride_id = r.id
             WHERE cr.id = $1`,
            [chatRoomId]
        );

        if (rideCheck.rows.length > 0) {
            if (rideCheck.rows[0].status === 'COMPLETED') {
                throw new Error('Ride completed. Chat closed.');
            }
            if (!rideCheck.rows[0].is_active) {
                throw new Error('Chat room is closed.');
            }
        }

        // Insert message
        const result = await pool.query(
            'INSERT INTO messages (chat_room_id, user_type, user_id, message) VALUES ($1, $2, $3, $4) RETURNING *',
            [chatRoomId, userType, userId, message.trim()]
        );

        console.log(`Message sent in chat room ${chatRoomId} by ${userType} ${userId}`);
        return result.rows[0];
    } catch (error) {
        console.error('Error sending message:', error);
        throw error;
    }
}

/**
 * Get messages for a chat room
 * Returns last 200 messages ordered by creation time
 */
export async function getMessages(chatRoomId: string): Promise<Message[]> {
    try {
        const result = await pool.query(
            `SELECT m.id, m.chat_room_id, m.user_type, m.user_id, m.message, m.created_at
             FROM messages m
             WHERE m.chat_room_id = $1
             ORDER BY m.created_at DESC
             LIMIT 200`,
            [chatRoomId]
        );

        // Return in chronological order (oldest first)
        return result.rows.reverse();
    } catch (error) {
        console.error('Error getting messages:', error);
        throw new Error('Failed to get messages');
    }
}

/**
 * Get chat rooms for a user
 * Returns summary information for each chat room the user participates in
 */
export async function getUserChatRooms(userId: string): Promise<ChatRoomSummary[]> {
    try {
        const result = await pool.query(
            `SELECT
                cr.id,
                cr.ride_id,
                r.origin || ' to ' || r.destination as ride_route,
                u.first_name || ' ' || u.last_name as driver_name,
                COUNT(cp2.id) as participant_count,
                (
                    SELECT m.message
                    FROM messages m
                    WHERE m.chat_room_id = cr.id
                    ORDER BY m.created_at DESC
                    LIMIT 1
                ) as last_message,
                (
                    SELECT m.created_at
                    FROM messages m
                    WHERE m.chat_room_id = cr.id
                    ORDER BY m.created_at DESC
                    LIMIT 1
                ) as last_message_time
             FROM chat_participants cp
             JOIN chat_rooms cr ON cp.chat_room_id = cr.id
             JOIN rides r ON cr.ride_id = r.id
             JOIN users u ON r.driver_id = u.id
             LEFT JOIN chat_participants cp2 ON cp2.chat_room_id = cr.id
             WHERE cp.user_id = $1 AND cr.is_active = true
             GROUP BY cr.id, cr.ride_id, r.origin, r.destination, u.first_name, u.last_name
             ORDER BY COALESCE(last_message_time, cr.created_at) DESC`,
            [userId]
        );

        return result.rows.map(row => ({
            id: row.id,
            rideId: row.ride_id,
            rideRoute: row.ride_route,
            driverName: row.driver_name,
            lastMessage: row.last_message,
            lastMessageTime: row.last_message_time,
            participantCount: parseInt(row.participant_count)
        }));
    } catch (error) {
        console.error('Error getting user chat rooms:', error);
        throw new Error('Failed to get user chat rooms');
    }
}

/**
 * Check if a user is a participant in a chat room
 */
export async function isUserParticipant(chatRoomId: string, userId: string): Promise<boolean> {
    try {
        const result = await pool.query(
            'SELECT id FROM chat_participants WHERE chat_room_id = $1 AND user_id = $2',
            [chatRoomId, userId]
        );

        return result.rows.length > 0;
    } catch (error) {
        console.error('Error checking participant status:', error);
        return false;
    }
}

/**
 * Get chat room by ride ID
 */
export async function getChatRoomByRideId(rideId: string): Promise<ChatRoom | null> {
    try {
        const result = await pool.query(
            'SELECT * FROM chat_rooms WHERE ride_id = $1',
            [rideId]
        );

        return result.rows[0] || null;
    } catch (error) {
        console.error('Error getting chat room by ride ID:', error);
        throw new Error('Failed to get chat room');
    }
}

/**
 * Get chat room details by ID
 */
export async function getChatRoomDetails(chatRoomId: string): Promise<{ id: string; rideId: string; isActive: boolean } | null> {
    try {
        const result = await pool.query(
            'SELECT id, ride_id, is_active FROM chat_rooms WHERE id = $1',
            [chatRoomId]
        );

        if (result.rows.length === 0) {
            return null;
        }

        return {
            id: result.rows[0].id,
            rideId: result.rows[0].ride_id,
            isActive: result.rows[0].is_active
        };
    } catch (error) {
        console.error('Error getting chat room details:', error);
        throw new Error('Failed to get chat room details');
    }
}

/**
 * Close a chat room (mark as inactive)
 */
export async function closeChatRoom(chatRoomId: string): Promise<void> {
    try {
        await pool.query(
            'UPDATE chat_rooms SET is_active = false WHERE id = $1',
            [chatRoomId]
        );
    } catch (error) {
        console.error('Error closing chat room:', error);
        throw new Error('Failed to close chat room');
    }
}

/**
 * Initialize chat room after successful payment
 * This is the main integration point called from payment service
 */
export async function initializeRideChat(rideId: string, passengerId: string): Promise<string> {
    try {
        // Create chat room if it doesn't exist
        let chatRoom = await getChatRoomByRideId(rideId);
        if (!chatRoom) {
            chatRoom = await createChatRoom(rideId);

            // Add driver as participant
            const rideResult = await pool.query('SELECT driver_id FROM rides WHERE id = $1', [rideId]);
            if (rideResult.rows.length > 0) {
                await addParticipant(chatRoom.id, 'driver', rideResult.rows[0].driver_id);
            }
        }

        // Add passenger as participant
        await addParticipant(chatRoom.id, 'passenger', parseInt(passengerId));

        console.log(`Chat initialized for ride ${rideId}, passenger ${passengerId}`);
        return chatRoom.id;
    } catch (error) {
        console.error('Error initializing ride chat:', error);
        throw new Error('Failed to initialize ride chat');
    }
}