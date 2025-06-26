package com.tjxjnoobie.interfaces;

import java.util.UUID;

/**
 * Interface for player profile management
 */
public interface IPlayerProfile {
    
    /**
     * Gets a player's profile
     * @param playerId The player's UUID
     * @return The player profile object
     */
    Object getProfile(UUID playerId);
    
    /**
     * Creates a new player profile
     * @param playerId The player's UUID
     * @param playerName The player's name
     * @return The created profile
     */
    Object createProfile(UUID playerId, String playerName);
    
    /**
     * Updates a player's profile
     * @param playerId The player's UUID
     * @param profile The profile data to update
     */
    void updateProfile(UUID playerId, Object profile);
    
    /**
     * Deletes a player's profile
     * @param playerId The player's UUID
     * @return true if deletion was successful
     */
    boolean deleteProfile(UUID playerId);
    
    /**
     * Gets a player's statistics
     * @param playerId The player's UUID
     * @return The player's statistics
     */
    Object getPlayerStatistics(UUID playerId);
    
    /**
     * Updates player statistics
     * @param playerId The player's UUID
     * @param stats The statistics to update
     */
    void updateStatistics(UUID playerId, Object stats);
    
    /**
     * Gets a player's achievements
     * @param playerId The player's UUID
     * @return Array of achievements
     */
    Object[] getAchievements(UUID playerId);
    
    /**
     * Adds an achievement to a player
     * @param playerId The player's UUID
     * @param achievement The achievement to add
     */
    void addAchievement(UUID playerId, Object achievement);
    
    /**
     * Gets a player's preferences
     * @param playerId The player's UUID
     * @return The player's preferences
     */
    Object getPreferences(UUID playerId);
    
    /**
     * Updates player preferences
     * @param playerId The player's UUID
     * @param preferences The preferences to update
     */
    void updatePreferences(UUID playerId, Object preferences);
    
    /**
     * Gets a player's friends list
     * @param playerId The player's UUID
     * @return Array of friend UUIDs
     */
    UUID[] getFriends(UUID playerId);
    
    /**
     * Adds a friend to a player's list
     * @param playerId The player's UUID
     * @param friendId The friend's UUID
     */
    void addFriend(UUID playerId, UUID friendId);
    
    /**
     * Removes a friend from a player's list
     * @param playerId The player's UUID
     * @param friendId The friend's UUID
     */
    void removeFriend(UUID playerId, UUID friendId);
    
    /**
     * Checks if a profile exists
     * @param playerId The player's UUID
     * @return true if profile exists
     */
    boolean profileExists(UUID playerId);
    
    /**
     * Gets the last login time
     * @param playerId The player's UUID
     * @return Last login timestamp
     */
    long getLastLogin(UUID playerId);
    
    /**
     * Updates the last login time
     * @param playerId The player's UUID
     */
    void updateLastLogin(UUID playerId);
}