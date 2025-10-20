package com.tjxjnoobie.api.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Interface for rank management operations
 */
public interface IRank {


    String getRank(UUID uuid) throws SQLException;

    int getPowerLevel(UUID uuid) throws SQLException;

    Set<String> getPermissions(UUID uuid) throws SQLException;

    // Checks if a given player UUID has a specific permission
    boolean hasPermission(UUID uuid, String permission) throws SQLException;

    /**
     * Gets all available ranks
     * @return Array of rank names
     */
    String getAllRanks();


    void setRankFromUsername(String userName, String rankName) throws SQLException;

    List<String> getRanks();
}