package com.tjxjnoobie.api.interfaces;

import java.util.UUID;

/**
 * Interface for lobby statistics cache management
 */
public interface ILobbyStatsCache {
    
    /**
     * Gets lobby statistics for a player
     * @param playerId The player's UUID
     * @return Lobby statistics object
     */
    Object getLobbyStats(UUID playerId);
    
    /**
     * Updates lobby statistics for a player
     * @param playerId The player's UUID
     * @param stats The statistics to update
     */
    void updateLobbyStats(UUID playerId, Object stats);
    
    /**
     * Gets player's lobby join count
     * @param playerId The player's UUID
     * @return Number of lobby joins
     */
    int getLobbyJoinCount(UUID playerId);
    
    /**
     * Increments lobby join count
     * @param playerId The player's UUID
     */
    void incrementLobbyJoinCount(UUID playerId);
    
    /**
     * Gets player's total lobby time
     * @param playerId The player's UUID
     * @return Total time spent in lobby (milliseconds)
     */
    long getTotalLobbyTime(UUID playerId);
    
    /**
     * Adds lobby time for a player
     * @param playerId The player's UUID
     * @param timeMillis Time to add in milliseconds
     */
    void addLobbyTime(UUID playerId, long timeMillis);
    
    /**
     * Gets player's average lobby time
     * @param playerId The player's UUID
     * @return Average lobby time in milliseconds
     */
    long getAverageLobbyTime(UUID playerId);
    
    /**
     * Gets player's lobby preferences
     * @param playerId The player's UUID
     * @return Lobby preferences object
     */
    Object getLobbyPreferences(UUID playerId);
    
    /**
     * Updates lobby preferences
     * @param playerId The player's UUID
     * @param preferences The preferences to update
     */
    void updateLobbyPreferences(UUID playerId, Object preferences);
    
    /**
     * Gets global lobby statistics
     * @return Global lobby statistics
     */
    Object getGlobalLobbyStats();
    
    /**
     * Gets lobby leaderboard
     * @param statType The statistic type for leaderboard
     * @param limit Maximum number of entries
     * @return Leaderboard entries
     */
    Object[] getLobbyLeaderboard(String statType, int limit);
    
    /**
     * Clears lobby statistics for a player
     * @param playerId The player's UUID
     */
    void clearLobbyStats(UUID playerId);
    
    /**
     * Clears all lobby statistics
     */
    void clearAllLobbyStats();
    
    /**
     * Refreshes lobby statistics from database
     * @param playerId The player's UUID
     */
    void refreshLobbyStats(UUID playerId);
    
    /**
     * Saves lobby statistics to database
     * @param playerId The player's UUID
     */
    void saveLobbyStats(UUID playerId);
    
    /**
     * Gets lobby session data
     * @param playerId The player's UUID
     * @return Current session data
     */
    Object getLobbySession(UUID playerId);
    
    /**
     * Starts a lobby session
     * @param playerId The player's UUID
     */
    void startLobbySession(UUID playerId);
    
    /**
     * Ends a lobby session
     * @param playerId The player's UUID
     */
    void endLobbySession(UUID playerId);
    
    /**
     * Gets cache size
     * @return Number of cached entries
     */
    int getCacheSize();
    
    /**
     * Clears expired cache entries
     * @return Number of entries cleared
     */
    int clearExpiredEntries();

    String getGlobalGrade(UUID uuid);

    void loadLobbyStats(UUID uuid);
}