package com.tjxjnoobie.interfaces;

import java.util.UUID;

/**
 * Interface for statistics management operations
 */
public interface IStatsManager {
    
    /**
     * Gets player statistics
     * @param playerId The player's UUID
     * @param gameMode The game mode for which to get statistics
     * @param statFor The statistic to retrieve (e.g., "WINS", "LOSSES", "PLAYED")
     * @return Player statistics object
     */
    int getStat(String gameMode, UUID playerId, String statFor);
    
    /**
     * Updates player statistics
     * @param playerId The player's UUID
     * @param stats The statistics to update
     */
    void updatePlayerStats(UUID playerId, Object stats);
    
    /**
     * Increments a statistic
     * @param playerId The player's UUID
     * @param statName The statistic name
     * @param amount The amount to increment
     */
    void incrementStat(UUID playerId, String statName, int amount);
    
    /**
     * Sets a statistic value
     * @param playerId The player's UUID
     * @param statName The statistic name
     * @param value The value to set
     */
    void setStat(String table, String coloum, UUID uuid, int statfor);
    void setStat(String table, String coloum, UUID uuid, long statfor);

    void setBestSRTime(String bestTime, UUID uuid );

    /**
     * Gets global statistics
     * @return Global statistics object
     */
    Object getGlobalStats();
    
    /**
     * Gets leaderboard for a statistic
     * @param statName The statistic name
     * @param limit Maximum number of entries
     * @return Leaderboard entries
     */
    Object[] getLeaderboard(String statName, int limit);
    
    /**
     * Gets player rank for a statistic
     * @param playerId The player's UUID
     * @param statName The statistic name
     * @return Player's rank (1-based)
     */
    int getPlayerRank(UUID playerId, String statName);
    
    /**
     * Resets player statistics
     * @param playerId The player's UUID
     */
    void resetPlayerStats(UUID playerId);
    
    /**
     * Resets a specific statistic
     * @param playerId The player's UUID
     * @param statName The statistic name
     */
    void resetStat(UUID playerId, String statName);
    
    /**
     * Saves statistics to database
     * @param playerId The player's UUID
     */
    void saveStats(UUID playerId);
    
    /**
     * Loads statistics from database
     * @param playerId The player's UUID
     */
    void loadStats(UUID playerId);
    
    /**
     * Gets all available statistics
     * @return Array of statistic names
     */
    String[] getAvailableStats();
    
    /**
     * Checks if a statistic exists
     * @param statName The statistic name
     * @return true if statistic exists
     */
    boolean statExists(String statName);
    
    /**
     * Registers a new statistic
     * @param statName The statistic name
     * @param description The statistic description
     */
    void registerStat(String statName, String description);
    
    /**
     * Unregisters a statistic
     * @param statName The statistic name
     */
    void unregisterStat(String statName);
    
    /**
     * Gets statistics summary for a player
     * @param playerId The player's UUID
     * @return Statistics summary
     */
    String getStatsSummary(UUID playerId);
    
    /**
     * Exports player statistics
     * @param playerId The player's UUID
     * @param format The export format
     * @return Exported statistics data
     */
    String exportStats(UUID playerId, String format);
    
    /**
     * Imports player statistics
     * @param playerId The player's UUID
     * @param data The statistics data
     * @param format The data format
     * @return true if import was successful
     */
    boolean importStats(UUID playerId, String data, String format);

    String getBestSRTime(UUID uuid);

    String getGrade(String gameMode, UUID uuid);
}