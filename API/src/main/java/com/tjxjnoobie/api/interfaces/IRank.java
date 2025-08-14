package com.tjxjnoobie.api.interfaces;

import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Interface for rank management operations
 */
public interface IRank {
    
    /**
     * Gets a player's rank
     * @param playerId The player's UUID
     * @return The player's rank name
     */
    String getPlayerRank(UUID playerId);
    
    /**
     * Sets a player's rank
     * @param playerId The player's UUID
     * @param rankName The rank name to set
     */
    void setPlayerRank(UUID playerId, String rankName);
    
    /**
     * Gets rank display name
     * @param rankName The rank name
     * @return The formatted display name
     */
    String getRankDisplayName(String rankName);
    
    /**
     * Gets rank prefix
     * @param rankName The rank name
     * @return The rank prefix
     */
    String getRankPrefix(String rankName);
    
    /**
     * Gets rank suffix
     * @param rankName The rank name
     * @return The rank suffix
     */
    String getRankSuffix(String rankName);
    
    /**
     * Gets rank color
     * @param rankName The rank name
     * @return The rank color code
     */
    String getRankColor(String rankName);
    
    /**
     * Gets rank weight/priority
     * @param rankName The rank name
     * @return The rank weight (higher = more important)
     */
    int getRankWeight(String rankName);
    
    /**
     * Gets all available ranks
     * @return Array of rank names
     */
    String[] getAllRanks();
    
    /**
     * Gets ranks ordered by weight
     * @return Array of rank names ordered by weight (highest first)
     */
    String[] getRanksByWeight();
    
    /**
     * Checks if a rank exists
     * @param rankName The rank name
     * @return true if rank exists
     */
    boolean rankExists(String rankName);
    
    /**
     * Creates a new rank
     * @param rankName The rank name
     * @param displayName The display name
     * @param prefix The prefix
     * @param suffix The suffix
     * @param color The color code
     * @param weight The rank weight
     * @return true if rank was created successfully
     */
    boolean createRank(String rankName, String displayName, String prefix, String suffix, String color, int weight);
    
    /**
     * Deletes a rank
     * @param rankName The rank name
     * @return true if rank was deleted successfully
     */
    boolean deleteRank(String rankName);
    
    /**
     * Updates rank properties
     * @param rankName The rank name
     * @param property The property to update
     * @param value The new value
     * @return true if update was successful
     */
    boolean updateRankProperty(String rankName, String property, Object value);
    
    /**
     * Gets the default rank
     * @return The default rank name
     */
    String getDefaultRank();
    
    /**
     * Sets the default rank
     * @param rankName The rank name to set as default
     */
    void setDefaultRank(String rankName);
    
    /**
     * Compares two ranks by weight
     * @param rank1 First rank name
     * @param rank2 Second rank name
     * @return Comparison result (-1, 0, 1)
     */
    int compareRanks(String rank1, String rank2);
    
    /**
     * Gets the highest rank a player has
     * @param playerId The player's UUID
     * @return The highest rank name
     */
    String getHighestRank(UUID playerId);
    
    /**
     * Checks if a player has a specific rank
     * @param playerId The player's UUID
     * @param rankName The rank name to check
     * @return true if player has the rank
     */
    boolean hasRank(UUID playerId, String rankName);
    
    /**
     * Adds a rank to a player
     * @param playerId The player's UUID
     * @param rankName The rank name to add
     */
    void addRank(UUID playerId, String rankName);
    
    /**
     * Removes a rank from a player
     * @param playerId The player's UUID
     * @param rankName The rank name to remove
     */
    void removeRank(UUID playerId, String rankName);
    
    /**
     * Gets all ranks a player has
     * @param playerId The player's UUID
     * @return Array of rank names
     */
    String[] getPlayerRanks(UUID playerId);

    void setDisplayName(Player player) throws SQLException;

    void setRankFromUsername(String userName, String rankName);
}