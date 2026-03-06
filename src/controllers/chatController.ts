import { Request, Response } from 'express';
import * as chatService from '../services/chatService';

/**
 * Chat Controller - Handles HTTP requests for chat functionality
 * Manages chat rooms, messages, and participant access
 */

/**
 * Get chat rooms for a user
 * Returns list of chat rooms with trip details and last messages
 */
export const getUserChatRooms = async (req: Request, res: Response) => {
    try {
        const userId = Array.isArray(req.params.userId) ? req.params.userId[0] : req.params.userId;

        if (!userId) {
            return res.status(400).json({ error: 'User ID is required' });
        }

        const chatRooms = await chatService.getUserChatRooms(userId);
        res.json({
            success: true,
            chatRooms,
            count: chatRooms.length
        });
    } catch (error) {
        console.error('Error getting user chat rooms:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
};

/**
 * Get messages for a chat room
 * Returns chat history ordered by creation time
 */
export const getMessages = async (req: Request, res: Response) => {
    try {
        const chatRoomId = Array.isArray(req.params.chatRoomId) ? req.params.chatRoomId[0] : req.params.chatRoomId;
        const userIdQuery = Array.isArray(req.query.userId) ? req.query.userId[0] : req.query.userId;
        const userIdParam = typeof userIdQuery === 'string' ? userIdQuery : undefined;

        if (!chatRoomId) {
            return res.status(400).json({ error: 'Chat room ID is required' });
        }

        // Verify user is a participant
        if (userIdParam) {
            const isParticipant = await chatService.isUserParticipant(chatRoomId, userIdParam);
            if (!isParticipant) {
                return res.status(403).json({ error: 'Access denied: Not a participant in this chat room' });
            }
        }

        const messages = await chatService.getMessages(chatRoomId);
        res.json({
            success: true,
            messages,
            count: messages.length
        });
    } catch (error) {
        console.error('Error getting messages:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
};

/**
 * Send a message to a chat room
 * Validates participant access and trip status
 */
export const sendMessage = async (req: Request, res: Response) => {
    try {
        const { chatRoomId, userType, userId, message } = req.body;

        if (!chatRoomId || !userType || !userId || !message) {
            return res.status(400).json({
                error: 'Chat room ID, user type, user ID, and message are required'
            });
        }

        if (message.trim().length === 0) {
            return res.status(400).json({ error: 'Message cannot be empty' });
        }

        if (message.length > 1000) {
            return res.status(400).json({ error: 'Message too long (max 1000 characters)' });
        }

        const sentMessage = await chatService.sendMessage(chatRoomId, userType, userId, message);

        res.json({
            success: true,
            message: sentMessage
        });
    } catch (error: any) {
        console.error('Error sending message:', error);

        if (error.message.includes('Access denied') || error.message.includes('Trip completed')) {
            return res.status(403).json({ error: error.message });
        }

        res.status(500).json({ error: 'Internal server error' });
    }
};

/**
 * Get chat room details
 * Returns basic information about a chat room
 */
export const getChatRoomDetails = async (req: Request, res: Response) => {
    try {
        const chatRoomId = Array.isArray(req.params.chatRoomId) ? req.params.chatRoomId[0] : req.params.chatRoomId;
        const userIdQuery = Array.isArray(req.query.userId) ? req.query.userId[0] : req.query.userId;
        const userIdParam = typeof userIdQuery === 'string' ? userIdQuery : undefined;

        if (!chatRoomId) {
            return res.status(400).json({ error: 'Chat room ID is required' });
        }

        // Verify user is a participant
        if (userIdParam) {
            const isParticipant = await chatService.isUserParticipant(chatRoomId, userIdParam);
            if (!isParticipant) {
                return res.status(403).json({ error: 'Access denied: Not a participant in this chat room' });
            }
        }

        const chatRoom = await chatService.getChatRoomDetails(chatRoomId);
        if (!chatRoom) {
            return res.status(404).json({ error: 'Chat room not found' });
        }

        res.json({
            success: true,
            chatRoom: {
                id: chatRoom.id,
                rideId: chatRoom.rideId,
                status: chatRoom.isActive ? 'active' : 'closed'
            }
        });
    } catch (error) {
        console.error('Error getting chat room details:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
};

/**
 * Close a chat room
 * Marks the chat room as inactive (typically when trip is completed)
 */
export const closeChatRoom = async (req: Request, res: Response) => {
    try {
        const chatRoomId = Array.isArray(req.params.chatRoomId) ? req.params.chatRoomId[0] : req.params.chatRoomId;
        const { userId } = req.body; // For authorization

        if (!chatRoomId) {
            return res.status(400).json({ error: 'Chat room ID is required' });
        }

        // Verify user is a participant (could add driver-only restriction later)
        if (userId) {
            const isParticipant = await chatService.isUserParticipant(chatRoomId, userId);
            if (!isParticipant) {
                return res.status(403).json({ error: 'Access denied: Not a participant in this chat room' });
            }
        }

        await chatService.closeChatRoom(chatRoomId);

        res.json({
            success: true,
            message: 'Chat room closed successfully'
        });
    } catch (error) {
        console.error('Error closing chat room:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
};