package com.tjxjnoobie.api.interfaces;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * The interface Rank cache.
 */
public interface IRankCache {

    default void addRankCache(UUID uuid) throws SQLException {
    }

    default void removeRankCache(UUID uuid) {
    }

    default String getCachedRank(UUID uuid) {
        return null;
    }

    default int getCachedPowerLevel(UUID uuid) {
        return 0;
    }

    default Set<String> getCachedPermissions(UUID uuid) {
        return null;
    }

    default boolean hasCachedPermission(UUID uuid, String permission) {
        return false;
    }

    default HashMap<UUID, String> getRankCache() {
        return null;
    }

    default HashMap<UUID, Integer> getPowerLevelCache() {
        return null;
    }

    default HashMap<UUID, Set<String>> getPermissionsCache() {
        return null;
    }

    default boolean rankExists(String rankName) throws SQLException {
        return false;
    }

    default boolean isStaff(UUID uuid) {
        return false;
    }

    default boolean isAdmin(UUID uuid) {
        return false;
    }

    default boolean isDonor(UUID uuid) {
        return false;
    }
}
