package com.tjxjnoobie.interfaces;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Interface for Minecraft rank management
 */
public interface IRankMC {
    
    /**
     * Gets a player's rank
     * @param playerId The player's UUID
     * @return The player's rank
     */
    String getPlayerRank(UUID playerId);
    
    /**
     * Sets a player's rank
     * @param playerId The player's UUID
     * @param rank The rank to set
     */
    void setPlayerRank(UUID playerId, String rank);
    
    /**
     * Gets a player's rank display name
     * @param playerId The player's UUID
     * @return The formatted rank display name
     */
    String getRankDisplayName(UUID playerId);
    
    /**
     * Gets a player's rank prefix
     * @param playerId The player's UUID
     * @return The rank prefix
     */
    String getRankPrefix(UUID playerId);
    
    /**
     * Gets a player's rank suffix
     * @param playerId The player's UUID
     * @return The rank suffix
     */
    String getRankSuffix(UUID playerId);
    
    /**
     * Checks if a player has a specific rank
     * @param playerId The player's UUID
     * @param rank The rank to check
     * @return true if player has the rank
     */
    boolean hasRank(UUID playerId, String rank);
    
    /**
     * Checks if a player has permission for a rank
     * @param playerId The player's UUID
     * @param permission The permission to check
     * @return true if player has permission
     */
    boolean hasRankPermission(UUID playerId, String permission);
    
    /**
     * Gets all available ranks
     * @return Array of available ranks
     */
    String[] getAvailableRanks();
    
    /**
     * Gets the default rank
     * @return The default rank name
     */
    String getDefaultRank();
    
    /**
     * Promotes a player to the next rank
     * @param playerId The player's UUID
     * @return true if promotion was successful
     */
    boolean promotePlayer(UUID playerId);
    
    /**
     * Demotes a player to the previous rank
     * @param playerId The player's UUID
     * @return true if demotion was successful
     */
    boolean demotePlayer(UUID playerId);
    
    /**
     * Gets the rank hierarchy level
     * @param rank The rank name
     * @return The hierarchy level (higher = more important)
     */
    int getRankLevel(String rank);
    
    /**
     * Compares two ranks
     * @param rank1 First rank
     * @param rank2 Second rank
     * @return Comparison result (-1, 0, 1)
     */
    int compareRanks(String rank1, String rank2);
    
    /**
     * Updates player's display name with rank formatting
     * @param player The player to update
     */
    void updatePlayerDisplayName(Player player);
    
    /**
     * Refreshes rank data from database
     * @param playerId The player's UUID
     */
    void refreshRankData(UUID playerId);

    void setDisplayName(Player player);
}