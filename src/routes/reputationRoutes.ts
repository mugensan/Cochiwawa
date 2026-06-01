import express from 'express';
import {
    getUserReputation,
    addReview,
    getUserReviews,
    getReviewsGivenByUser,
    canReviewUser,
    getTopRatedUsers
} from '../controllers/reputationController';

const router = express.Router();

/**
 * Reputation and Review Routes
 * All routes require proper authentication (middleware should be added)
 */

// Get user reputation
router.get('/users/:id/reputation', getUserReputation);

// Add a review
router.post('/reviews', addReview);

// Get reviews for a user
router.get('/users/:id/reviews', getUserReviews);

// Get reviews given by a user
router.get('/users/:id/reviews-given', getReviewsGivenByUser);

// Check if user can review another user for a trip
router.get('/reviews/can-review/:tripId/:reviewerId/:reviewedUserId', canReviewUser);

// Get top rated users
router.get('/reviews/top-rated', getTopRatedUsers);

export default router;