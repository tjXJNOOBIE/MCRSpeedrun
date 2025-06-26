package com.tjxjnoobie.interfaces;

import java.util.UUID;

/**
 * Interface for rating API operations
 */
public interface IRatingAPI {
    
    /**
     * Gets a player's current rating
     * @param playerId The player's UUID
     * @return The player's rating object
     */
    IRating getPlayerRating(UUID playerId);
    
    /**
     * Updates a player's rating
     * @param playerId The player's UUID
     * @param newRating The new rating object
     */
    void updatePlayerRating(UUID playerId, IRating newRating);
    
    /**
     * Calculates rating changes for a match
     * @param player1Id First player's UUID
     * @param player2Id Second player's UUID
     * @param result Match result (1.0 = player1 wins, 0.0 = player2 wins, 0.5 = draw)
     * @return Array containing [player1NewRating, player2NewRating]
     */
    IRating[] calculateMatchRatings(UUID player1Id, UUID player2Id, double result);
    
    /**
     * Processes a match result and updates ratings
     * @param player1Id First player's UUID
     * @param player2Id Second player's UUID
     * @param result Match result
     */
    void processMatchResult(UUID player1Id, UUID player2Id, double result);
    
    /**
     * Gets the top rated players
     * @param limit Maximum number of players to return
     * @return Array of top rated players
     */
    Object[] getTopPlayers(int limit);
    
    /**
     * Gets a player's rank
     * @param playerId The player's UUID
     * @return The player's rank (1-based)
     */
    int getPlayerRank(UUID playerId);
    
    /**
     * Gets rating statistics
     * @return Rating statistics object
     */
    Object getRatingStatistics();
    
    /**
     * Recalculates all ratings
     */
    void recalculateAllRatings();
    
    /**
     * Exports rating data
     * @param format The export format
     * @return Exported data
     */
    String exportRatings(String format);
    
    /**
     * Imports rating data
     * @param data The data to import
     * @param format The data format
     * @return true if import was successful
     */
    boolean importRatings(String data, String format);
    
    /**
     * Gets rating history for a player
     * @param playerId The player's UUID
     * @param limit Maximum number of entries
     * @return Rating history
     */
    Object[] getRatingHistory(UUID playerId, int limit);
    
    /**
     * Backs up rating data
     * @return Backup data
     */
    String backupRatings();
    
    /**
     * Restores rating data from backup
     * @param backupData The backup data
     * @return true if restore was successful
     */
    boolean restoreRatings(String backupData);
}