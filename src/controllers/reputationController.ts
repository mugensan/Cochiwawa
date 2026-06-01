import { Request, Response } from 'express';
import * as reputationService from '../services/reputationService';

/**
 * Reputation Controller - Handles user reputation and review endpoints
 */

/**
 * Get user reputation
 * GET /users/:id/reputation
 */
export async function getUserReputation(req: Request, res: Response) {
    try {
        const idParam = Array.isArray(req.params.id) ? req.params.id[0] : req.params.id;
        const userId = parseInt(idParam);

        if (isNaN(userId)) {
            return res.status(400).json({ error: 'Invalid user ID' });
        }

        const reputation = await reputationService.calculateUserRating(userId);

        res.json({
            userId: reputation.userId,
            averageRating: reputation.averageRating,
            totalReviews: reputation.totalReviews,
            completedTrips: reputation.completedTrips,
            ratingBreakdown: reputation.ratingBreakdown
        });
    } catch (error) {
        console.error('Error getting user reputation:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
}

/**
 * Add a review
 * POST /reviews
 */
export async function addReview(req: Request, res: Response) {
    try {
        const { tripId, reviewerId, reviewedUserId, rating, comment } = req.body;

        if (!tripId || !reviewerId || !reviewedUserId || !rating) {
            return res.status(400).json({ error: 'Missing required fields' });
        }

        const review = await reputationService.addReview(
            tripId,
            reviewerId,
            reviewedUserId,
            rating,
            comment
        );

        res.status(201).json(review);
    } catch (error: any) {
        console.error('Error adding review:', error);

        if (error.message.includes('Rating must be between') ||
            error.message.includes('Can only review completed trips') ||
            error.message.includes('did not participate') ||
            error.message.includes('Review already exists')) {
            return res.status(400).json({ error: error.message });
        }

        res.status(500).json({ error: 'Internal server error' });
    }
}

/**
 * Get reviews for a user
 * GET /users/:id/reviews
 */
export async function getUserReviews(req: Request, res: Response) {
    try {
        const idParam = Array.isArray(req.params.id) ? req.params.id[0] : req.params.id;
        const userId = parseInt(idParam);
        const limit = req.query.limit ? parseInt(req.query.limit as string) : 20;

        if (isNaN(userId)) {
            return res.status(400).json({ error: 'Invalid user ID' });
        }

        const reviews = await reputationService.getUserReviews(userId, limit);

        res.json(reviews);
    } catch (error) {
        console.error('Error getting user reviews:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
}

/**
 * Get reviews given by a user
 * GET /users/:id/reviews-given
 */
export async function getReviewsGivenByUser(req: Request, res: Response) {
    try {
        const idParam = Array.isArray(req.params.id) ? req.params.id[0] : req.params.id;
        const userId = parseInt(idParam);
        const limit = req.query.limit ? parseInt(req.query.limit as string) : 20;

        if (isNaN(userId)) {
            return res.status(400).json({ error: 'Invalid user ID' });
        }

        const reviews = await reputationService.getReviewsGivenByUser(userId, limit);

        res.json(reviews);
    } catch (error) {
        console.error('Error getting reviews given by user:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
}

/**
 * Check if user can review another user for a trip
 * GET /reviews/can-review/:tripId/:reviewerId/:reviewedUserId
 */
export async function canReviewUser(req: Request, res: Response) {
    try {
        const tripIdParam = Array.isArray(req.params.tripId) ? req.params.tripId[0] : req.params.tripId;
        const reviewerIdParam = Array.isArray(req.params.reviewerId) ? req.params.reviewerId[0] : req.params.reviewerId;
        const reviewedUserIdParam = Array.isArray(req.params.reviewedUserId) ? req.params.reviewedUserId[0] : req.params.reviewedUserId;
        const tripId = parseInt(tripIdParam);
        const reviewerId = parseInt(reviewerIdParam);
        const reviewedUserId = parseInt(reviewedUserIdParam);

        if (isNaN(tripId) || isNaN(reviewerId) || isNaN(reviewedUserId)) {
            return res.status(400).json({ error: 'Invalid IDs' });
        }

        const canReview = await reputationService.canReviewUser(reviewerId, reviewedUserId, tripId);

        res.json({ canReview });
    } catch (error) {
        console.error('Error checking review permission:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
}

/**
 * Get top rated users
 * GET /reviews/top-rated
 */
export async function getTopRatedUsers(req: Request, res: Response) {
    try {
        const limit = req.query.limit ? parseInt(req.query.limit as string) : 10;
        const minReviews = req.query.minReviews ? parseInt(req.query.minReviews as string) : 5;

        const topUsers = await reputationService.getTopRatedUsers(limit, minReviews);

        res.json(topUsers);
    } catch (error) {
        console.error('Error getting top rated users:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
}