package com.tjxjnoobie.interfaces;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * The interface Rank cache.
 */
public interface IRankCache {

    void addRankCache(UUID uuid) throws SQLException;

    void removeRankCache(UUID uuid);

    String getRank(UUID uuid);

    int getPowerLevel(UUID uuid);

    Set<String> getPermissions(UUID uuid);

    boolean hasPermission(UUID uuid, String permission);

    HashMap<UUID,String> getRankCache();

    HashMap<UUID,Integer> getPowerLevelCache();

    HashMap<UUID, Set<String>> getPermissionsCache();

    boolean rankExists(String rankName) throws SQLException;

    boolean isStaff(UUID uuid);

    boolean isAdmin(UUID uuid);

    boolean isDonor(UUID uuid);
}
