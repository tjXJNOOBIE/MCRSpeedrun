package com.tjxjnoobie.interfaces;

import java.util.UUID;

/**
 * Interface for punishment management operations
 */
public interface IPunishManager {
    
    /**
     * Bans a player
     * @param playerId The player's UUID
     * @param reason The ban reason
     * @param duration Duration in milliseconds (0 for permanent)
     * @param staffId The staff member's UUID who issued the ban
     * @return The punishment ID
     */
    String banPlayer(UUID playerId, String reason, long duration, UUID staffId);
    
    /**
     * Kicks a player
     * @param playerId The player's UUID
     * @param reason The kick reason
     * @param staffId The staff member's UUID who issued the kick
     * @return The punishment ID
     */
    String kickPlayer(UUID playerId, String reason, UUID staffId);
    
    /**
     * Mutes a player
     * @param playerId The player's UUID
     * @param reason The mute reason
     * @param duration Duration in milliseconds (0 for permanent)
     * @param staffId The staff member's UUID who issued the mute
     * @return The punishment ID
     */
    String mutePlayer(UUID playerId, String reason, long duration, UUID staffId);
    
    /**
     * Warns a player
     * @param playerId The player's UUID
     * @param reason The warning reason
     * @param staffId The staff member's UUID who issued the warning
     * @return The punishment ID
     */
    String warnPlayer(UUID playerId, String reason, UUID staffId);
    
    /**
     * Unbans a player
     * @param playerId The player's UUID
     * @param staffId The staff member's UUID who removed the ban
     * @return true if unban was successful
     */
    boolean unbanPlayer(UUID playerId, UUID staffId);
    
    /**
     * Unmutes a player
     * @param playerId The player's UUID
     * @param staffId The staff member's UUID who removed the mute
     * @return true if unmute was successful
     */
    boolean unmutePlayer(UUID playerId, UUID staffId);
    
    /**
     * Checks if a player is banned
     * @param playerId The player's UUID
     * @return true if player is banned
     */
    boolean isBanned(UUID playerId);
    
    /**
     * Checks if a player is muted
     * @param playerId The player's UUID
     * @return true if player is muted
     */
    boolean isMuted(UUID playerId);
    
    /**
     * Gets active ban information
     * @param playerId The player's UUID
     * @return Ban information object or null if not banned
     */
    Object getActiveBan(UUID playerId);
    
    /**
     * Gets active mute information
     * @param playerId The player's UUID
     * @return Mute information object or null if not muted
     */
    Object getActiveMute(UUID playerId);
    
    /**
     * Gets punishment history for a player
     * @param playerId The player's UUID
     * @param limit Maximum number of entries
     * @return Array of punishment records
     */
    Object[] getPunishmentHistory(UUID playerId, int limit);
    
    /**
     * Gets all active punishments
     * @param punishmentType The punishment type filter (null for all)
     * @return Array of active punishments
     */
    Object[] getActivePunishments(String punishmentType);
    
    /**
     * Removes a punishment
     * @param punishmentId The punishment ID
     * @param staffId The staff member's UUID who removed it
     * @return true if removal was successful
     */
    boolean removePunishment(String punishmentId, UUID staffId);
    
    /**
     * Gets punishment by ID
     * @param punishmentId The punishment ID
     * @return Punishment object or null if not found
     */
    Object getPunishment(String punishmentId);
    
    /**
     * Updates punishment reason
     * @param punishmentId The punishment ID
     * @param newReason The new reason
     * @param staffId The staff member's UUID who updated it
     * @return true if update was successful
     */
    boolean updatePunishmentReason(String punishmentId, String newReason, UUID staffId);
    
    /**
     * Extends punishment duration
     * @param punishmentId The punishment ID
     * @param additionalTime Additional time in milliseconds
     * @param staffId The staff member's UUID who extended it
     * @return true if extension was successful
     */
    boolean extendPunishment(String punishmentId, long additionalTime, UUID staffId);
    
    /**
     * Gets punishment statistics
     * @return Punishment statistics object
     */
    Object getPunishmentStatistics();
    
    /**
     * Gets staff punishment statistics
     * @param staffId The staff member's UUID
     * @return Staff punishment statistics
     */
    Object getStaffStatistics(UUID staffId);
    
    /**
     * Checks if a punishment has expired
     * @param punishmentId The punishment ID
     * @return true if punishment has expired
     */
    boolean isPunishmentExpired(String punishmentId);
    
    /**
     * Cleans up expired punishments
     * @return Number of punishments cleaned up
     */
    int cleanupExpiredPunishments();
}