package com.tjxjnoobie.api.interfaces;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Interface for rank management operations
 */
public interface IRank  {

    default String getRank(UUID uuid) throws SQLException {
        return null;
    }

    default int getPowerLevel(UUID uuid) throws SQLException {
        return 0;
    }

    default Set<String> getPermissions(UUID uuid) throws SQLException {
        return Collections.emptySet();
    }

    // Checks if a given player UUID has a specific permission
    default boolean hasPermission(UUID uuid, String permission) throws SQLException {
        return false;
    }

    /**
     * Gets all available ranks
     * @return Array of rank names
     */
    default String getAllRanks() {
        return "";
    }

    default void setRankFromUsername(String userName, String rankName) throws SQLException {
        // no-op
    }

    default List<String> getRanks() {
        return Collections.emptyList();
    }
}