package com.tjxjnoobie.interfaces;

import java.util.UUID;

/**
 * Interface for speedrun statistics cache management
 */
public interface ISpeedrunStatsCache {
    
    /**
     * Gets player statistics
     * @param playerId The player UUID
     * @return Player statistics object
     */
    Object getPlayerStats(UUID playerId);
    
    /**
     * Updates player statistics
     * @param playerId The player UUID
     * @param stats The statistics to update
     */
    void updatePlayerStats(UUID playerId, Object stats);
    
    /**
     * Gets player best time
     * @param playerId The player UUID
     * @return Best time in milliseconds
     */
    long getBestTime(UUID playerId);
    
    /**
     * Sets player best time
     * @param playerId The player UUID
     * @param time The time in milliseconds
     */
    void setBestTime(UUID playerId, long time);
    
    /**
     * Gets player completion count
     * @param playerId The player UUID
     * @return Number of completions
     */
    int getCompletionCount(UUID playerId);
    
    /**
     * Increments player completion count
     * @param playerId The player UUID
     */
    void incrementCompletionCount(UUID playerId);
    
    /**
     * Gets player rank
     * @param playerId The player UUID
     * @return Player rank
     */
    int getPlayerRank(UUID playerId);
    
    /**
     * Clears all cached statistics
     */
    void clearCache();
    
    /**
     * Refreshes statistics from database
     * @param playerId The player UUID
     */
    void refreshStats(UUID playerId);
    
    /**
     * Saves statistics to database
     * @param playerId The player UUID
     */
    void saveStats(UUID playerId);

    void addBestTime(UUID uuid, String finalTime);
}