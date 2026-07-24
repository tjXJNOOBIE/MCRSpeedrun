package com.tjxjnoobie.api.interfaces;

import org.tavall.dependency.composition.domains.InfrastructureDomain;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Interface for rank management operations
 */
public interface IRank extends IRankCache, InfrastructureDomain {

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

    default void setRank(UUID uuid, String rankName) throws SQLException {
        // no-op
    }

    default void revokeRank(UUID uuid, String fallbackRankName) throws SQLException {
        // no-op
    }

    default List<String> getRanks() {
        return Collections.emptyList();
    }
}
