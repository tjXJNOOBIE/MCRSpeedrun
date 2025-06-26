package com.tjxjnoobie.interfaces;

import java.util.UUID;

/**
 * Interface for punishment logging operations
 */
public interface IPunishLog {
    
    /**
     * Logs a punishment action
     * @param punishmentId The punishment ID
     * @param playerId The punished player's UUID
     * @param staffId The staff member's UUID
     * @param action The punishment action
     * @param reason The punishment reason
     * @param duration Duration in milliseconds (0 for permanent)
     */
    void logPunishment(String punishmentId, UUID playerId, UUID staffId, String action, String reason, long duration);
    
    /**
     * Logs a punishment removal
     * @param punishmentId The punishment ID
     * @param staffId The staff member's UUID who removed it
     * @param reason The removal reason
     */
    void logPunishmentRemoval(String punishmentId, UUID staffId, String reason);
    
    /**
     * Logs a punishment modification
     * @param punishmentId The punishment ID
     * @param staffId The staff member's UUID who modified it
     * @param modification The modification description
     */
    void logPunishmentModification(String punishmentId, UUID staffId, String modification);
    
    /**
     * Gets punishment logs for a player
     * @param playerId The player's UUID
     * @param limit Maximum number of entries
     * @return Array of log entries
     */
    Object[] getPlayerLogs(UUID playerId, int limit);
    
    /**
     * Gets punishment logs by staff member
     * @param staffId The staff member's UUID
     * @param limit Maximum number of entries
     * @return Array of log entries
     */
    Object[] getStaffLogs(UUID staffId, int limit);
    
    /**
     * Gets all punishment logs
     * @param limit Maximum number of entries
     * @return Array of log entries
     */
    Object[] getAllLogs(int limit);
    
    /**
     * Gets logs for a specific punishment
     * @param punishmentId The punishment ID
     * @return Array of log entries for the punishment
     */
    Object[] getPunishmentLogs(String punishmentId);
    
    /**
     * Searches logs by criteria
     * @param criteria Search criteria object
     * @param limit Maximum number of entries
     * @return Array of matching log entries
     */
    Object[] searchLogs(Object criteria, int limit);
    
    /**
     * Gets logs within a time range
     * @param startTime Start time in milliseconds
     * @param endTime End time in milliseconds
     * @param limit Maximum number of entries
     * @return Array of log entries
     */
    Object[] getLogsByTimeRange(long startTime, long endTime, int limit);
    
    /**
     * Gets logs by action type
     * @param action The action type
     * @param limit Maximum number of entries
     * @return Array of log entries
     */
    Object[] getLogsByAction(String action, int limit);
    
    /**
     * Exports punishment logs
     * @param format The export format
     * @param criteria Export criteria
     * @return Exported log data
     */
    String exportLogs(String format, Object criteria);
    
    /**
     * Archives old logs
     * @param olderThanDays Archive logs older than specified days
     * @return Number of logs archived
     */
    int archiveOldLogs(int olderThanDays);
    
    /**
     * Deletes old logs
     * @param olderThanDays Delete logs older than specified days
     * @return Number of logs deleted
     */
    int deleteOldLogs(int olderThanDays);
    
    /**
     * Gets log statistics
     * @return Log statistics object
     */
    Object getLogStatistics();
    
    /**
     * Gets the total number of logs
     * @return Total log count
     */
    long getTotalLogCount();
    
    /**
     * Gets logs count by action type
     * @param action The action type
     * @return Number of logs for the action
     */
    long getLogCountByAction(String action);
    
    /**
     * Gets recent activity summary
     * @param hours Number of hours to look back
     * @return Activity summary object
     */
    Object getRecentActivity(int hours);
    
    /**
     * Validates log integrity
     * @return Validation report
     */
    Object validateLogIntegrity();
    
    /**
     * Backs up punishment logs
     * @return Backup data
     */
    String backupLogs();
    
    /**
     * Restores punishment logs from backup
     * @param backupData The backup data
     * @return true if restore was successful
     */
    boolean restoreLogs(String backupData);
}