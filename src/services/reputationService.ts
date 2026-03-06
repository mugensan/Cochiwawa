import { pool } from '../config/database';

/**
 * Reputation Service - Manages user ratings and trust scores
 * Calculates reputation based on reviews from completed trips
 */

export interface UserReputation {
    userId: number;
    averageRating: number;
    totalReviews: number;
    completedTrips: number;
    ratingBreakdown: {
        1: number;
        2: number;
        3: number;
        4: number;
        5: number;
    };
}

export interface Review {
    id: string;
    tripId: number;
    reviewerId: number;
    reviewedUserId: number;
    rating: number;
    comment?: string;
    createdAt: Date;
}

/**
 * Calculate user reputation score
 * @param userId User ID to calculate reputation for
 * @returns User reputation data
 */
export async function calculateUserRating(userId: number): Promise<UserReputation> {
    try {
        // Get all reviews for the user
        const reviewsResult = await pool.query(
            `SELECT rating FROM reviews WHERE reviewed_user_id = $1`,
            [userId]
        );

        const ratings = reviewsResult.rows.map((row: any) => row.rating);
        const totalReviews = ratings.length;

        // Calculate average rating
        const averageRating = totalReviews > 0
            ? ratings.reduce((sum: number, rating: number) => sum + rating, 0) / totalReviews
            : 0;

        // Count completed trips (trips where user was driver or passenger)
        const completedTripsResult = await pool.query(
            `SELECT COUNT(DISTINCT r.id) as count
             FROM rides r
             WHERE r.status = 'COMPLETED'
             AND (r.driver_id = $1 OR EXISTS (
                 SELECT 1 FROM bookings b WHERE b.ride_id = r.id AND b.passenger_id = $1
             ))`,
            [userId]
        );

        const completedTrips = parseInt(completedTripsResult.rows[0].count);

        // Calculate rating breakdown
        const ratingBreakdown = {
            1: 0,
            2: 0,
            3: 0,
            4: 0,
            5: 0
        };

        ratings.forEach((rating: number) => {
            if (rating >= 1 && rating <= 5) {
                ratingBreakdown[rating as keyof typeof ratingBreakdown]++;
            }
        });

        return {
            userId,
            averageRating: Math.round(averageRating * 10) / 10, // Round to 1 decimal
            totalReviews,
            completedTrips,
            ratingBreakdown
        };
    } catch (error) {
        console.error('Error calculating user rating:', error);
        throw new Error('Failed to calculate user reputation');
    }
}

/**
 * Add a review for a completed trip
 * @param tripId Trip ID
 * @param reviewerId User ID of reviewer
 * @param reviewedUserId User ID being reviewed
 * @param rating Rating (1-5)
 * @param comment Optional comment
 * @returns Created review
 */
export async function addReview(
    tripId: number,
    reviewerId: number,
    reviewedUserId: number,
    rating: number,
    comment?: string
): Promise<Review> {
    try {
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new Error('Rating must be between 1 and 5');
        }

        // Check if trip is completed
        const tripResult = await pool.query(
            `SELECT status FROM rides WHERE id = $1`,
            [tripId]
        );

        if (tripResult.rows.length === 0) {
            throw new Error('Trip not found');
        }

        if (tripResult.rows[0].status !== 'COMPLETED') {
            throw new Error('Can only review completed trips');
        }

        // Check if reviewer participated in the trip
        const participationResult = await pool.query(
            `SELECT 1 FROM rides r
             LEFT JOIN bookings b ON b.ride_id = r.id
             WHERE r.id = $1
             AND (r.driver_id = $2 OR b.passenger_id = $2)
             LIMIT 1`,
            [tripId, reviewerId]
        );

        if (participationResult.rows.length === 0) {
            throw new Error('User did not participate in this trip');
        }

        // Check if reviewer already reviewed this user for this trip
        const existingReviewResult = await pool.query(
            `SELECT id FROM reviews
             WHERE trip_id = $1 AND reviewer_id = $2 AND reviewed_user_id = $3`,
            [tripId, reviewerId, reviewedUserId]
        );

        if (existingReviewResult.rows.length > 0) {
            throw new Error('Review already exists for this trip and user');
        }

        // Create review
        const result = await pool.query(
            `INSERT INTO reviews (trip_id, reviewer_id, reviewed_user_id, rating, comment)
             VALUES ($1, $2, $3, $4, $5)
             RETURNING *`,
            [tripId, reviewerId, reviewedUserId, rating, comment || null]
        );

        const review = result.rows[0];
        return {
            id: review.id,
            tripId: review.trip_id,
            reviewerId: review.reviewer_id,
            reviewedUserId: review.reviewed_user_id,
            rating: review.rating,
            comment: review.comment,
            createdAt: review.created_at
        };
    } catch (error) {
        console.error('Error adding review:', error);
        throw error;
    }
}

/**
 * Get reviews for a user
 * @param userId User ID
 * @param limit Maximum number of reviews to return
 * @returns Array of reviews
 */
export async function getUserReviews(userId: number, limit: number = 20): Promise<Review[]> {
    try {
        const result = await pool.query(
            `SELECT r.*, t.origin, t.destination
             FROM reviews r
             JOIN rides t ON r.trip_id = t.id
             WHERE r.reviewed_user_id = $1
             ORDER BY r.created_at DESC
             LIMIT $2`,
            [userId, limit]
        );

        return result.rows.map((row: any) => ({
            id: row.id,
            tripId: row.trip_id,
            reviewerId: row.reviewer_id,
            reviewedUserId: row.reviewed_user_id,
            rating: row.rating,
            comment: row.comment,
            createdAt: row.created_at,
            tripRoute: `${row.origin} → ${row.destination}`
        }));
    } catch (error) {
        console.error('Error getting user reviews:', error);
        throw new Error('Failed to get user reviews');
    }
}

/**
 * Get reviews given by a user
 * @param userId User ID who gave the reviews
 * @param limit Maximum number of reviews to return
 * @returns Array of reviews
 */
export async function getReviewsGivenByUser(userId: number, limit: number = 20): Promise<Review[]> {
    try {
        const result = await pool.query(
            `SELECT r.*, t.origin, t.destination
             FROM reviews r
             JOIN rides t ON r.trip_id = t.id
             WHERE r.reviewer_id = $1
             ORDER BY r.created_at DESC
             LIMIT $2`,
            [userId, limit]
        );

        return result.rows.map((row: any) => ({
            id: row.id,
            tripId: row.trip_id,
            reviewerId: row.reviewer_id,
            reviewedUserId: row.reviewed_user_id,
            rating: row.rating,
            comment: row.comment,
            createdAt: row.created_at,
            tripRoute: `${row.origin} → ${row.destination}`
        }));
    } catch (error) {
        console.error('Error getting reviews given by user:', error);
        throw new Error('Failed to get reviews given by user');
    }
}

/**
 * Check if user can review another user for a trip
 * @param reviewerId Reviewer user ID
 * @param reviewedUserId User being reviewed
 * @param tripId Trip ID
 * @returns True if review is allowed
 */
export async function canReviewUser(
    reviewerId: number,
    reviewedUserId: number,
    tripId: number
): Promise<boolean> {
    try {
        // Check if trip is completed
        const tripResult = await pool.query(
            `SELECT status FROM rides WHERE id = $1`,
            [tripId]
        );

        if (tripResult.rows.length === 0 || tripResult.rows[0].status !== 'COMPLETED') {
            return false;
        }

        // Check if reviewer participated
        const participationResult = await pool.query(
            `SELECT 1 FROM rides r
             LEFT JOIN bookings b ON b.ride_id = r.id
             WHERE r.id = $1
             AND (r.driver_id = $2 OR b.passenger_id = $2)
             LIMIT 1`,
            [tripId, reviewerId]
        );

        if (participationResult.rows.length === 0) {
            return false;
        }

        // Check if reviewed user participated
        const reviewedParticipationResult = await pool.query(
            `SELECT 1 FROM rides r
             LEFT JOIN bookings b ON b.ride_id = r.id
             WHERE r.id = $1
             AND (r.driver_id = $3 OR b.passenger_id = $3)
             LIMIT 1`,
            [tripId, reviewerId, reviewedUserId]
        );

        if (reviewedParticipationResult.rows.length === 0) {
            return false;
        }

        // Check if review already exists
        const existingReviewResult = await pool.query(
            `SELECT id FROM reviews
             WHERE trip_id = $1 AND reviewer_id = $2 AND reviewed_user_id = $3`,
            [tripId, reviewerId, reviewedUserId]
        );

        return existingReviewResult.rows.length === 0;
    } catch (error) {
        console.error('Error checking review permission:', error);
        return false;
    }
}

/**
 * Get top-rated users (for leaderboard or recommendations)
 * @param limit Maximum number of users to return
 * @param minReviews Minimum reviews required
 * @returns Array of user reputations
 */
export async function getTopRatedUsers(limit: number = 10, minReviews: number = 5): Promise<UserReputation[]> {
    try {
        // This is a simplified implementation
        // In production, you might want to cache these calculations
        const usersResult = await pool.query(
            `SELECT DISTINCT reviewed_user_id
             FROM reviews
             GROUP BY reviewed_user_id
             HAVING COUNT(*) >= $1
             ORDER BY AVG(rating) DESC
             LIMIT $2`,
            [minReviews, limit]
        );

        const reputations: UserReputation[] = [];
        for (const row of usersResult.rows) {
            const reputation = await calculateUserRating(row.reviewed_user_id);
            reputations.push(reputation);
        }

        return reputations;
    } catch (error) {
        console.error('Error getting top rated users:', error);
        throw new Error('Failed to get top rated users');
    }
}
