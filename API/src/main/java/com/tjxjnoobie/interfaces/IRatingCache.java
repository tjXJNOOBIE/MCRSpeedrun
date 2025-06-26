package com.tjxjnoobie.interfaces;

import java.util.UUID;

/**
 * Interface for rating cache management
 */
public interface IRatingCache {
    
    /**
     * Gets a player's rating
     * @param playerId The player's UUID
     * @return The player's rating
     */
    double getPlayerRating(UUID playerId);
    
    /**
     * Sets a player's rating
     * @param playerId The player's UUID
     * @param rating The rating to set
     */
    void setPlayerRating(UUID playerId, double rating);
    
    /**
     * Updates a player's rating
     * @param playerId The player's UUID
     * @param ratingChange The change in rating
     */
    void updatePlayerRating(UUID playerId, double ratingChange);
    
    /**
     * Gets a player's rating deviation
     * @param playerId The player's UUID
     * @return The rating deviation
     */
    double getRatingDeviation(UUID playerId);
    
    /**
     * Sets a player's rating deviation
     * @param playerId The player's UUID
     * @param deviation The deviation to set
     */
    void setRatingDeviation(UUID playerId, double deviation);
    
    /**
     * Gets a player's volatility
     * @param playerId The player's UUID
     * @return The volatility value
     */
    double getVolatility(UUID playerId);
    
    /**
     * Sets a player's volatility
     * @param playerId The player's UUID
     * @param volatility The volatility to set
     */
    void setVolatility(UUID playerId, double volatility);
    
    /**
     * Calculates new ratings after a match
     * @param player1Id First player's UUID
     * @param player2Id Second player's UUID
     * @param result Match result (1.0 = player1 wins, 0.0 = player2 wins, 0.5 = draw)
     */
    void calculateNewRatings(UUID player1Id, UUID player2Id, double result);
    
    /**
     * Gets the leaderboard rankings
     * @param limit Maximum number of entries to return
     * @return Array of player rankings
     */
    Object[] getLeaderboard(int limit);
    
    /**
     * Gets a player's rank position
     * @param playerId The player's UUID
     * @return The rank position (1-based)
     */
    int getPlayerRank(UUID playerId);
    
    /**
     * Refreshes ratings from database
     * @param playerId The player's UUID
     */
    void refreshRating(UUID playerId);
    
    /**
     * Saves ratings to database
     * @param playerId The player's UUID
     */
    void saveRating(UUID playerId);
    
    /**
     * Clears all cached ratings
     */
    void clearCache();
}