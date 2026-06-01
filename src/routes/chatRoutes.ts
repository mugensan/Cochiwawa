import express from 'express';
import {
    getUserChatRooms,
    getMessages,
    sendMessage,
    getChatRoomDetails,
    closeChatRoom
} from '../controllers/chatController';

const router = express.Router();

/**
 * Chat Routes - REST API endpoints for chat functionality
 * All routes require proper authentication (middleware should be added)
 */

// Get chat rooms for a user
router.get('/rooms/:userId', getUserChatRooms);

// Get messages for a chat room
router.get('/messages/:chatRoomId', getMessages);

// Send a message to a chat room
router.post('/send', sendMessage);

// Get chat room details (optional endpoint)
router.get('/room/:chatRoomId', getChatRoomDetails);

// Close a chat room
router.post('/room/:chatRoomId/close', closeChatRoom);

export default router;