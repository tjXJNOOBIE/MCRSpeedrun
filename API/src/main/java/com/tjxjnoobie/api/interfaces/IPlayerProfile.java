package com.tjxjnoobie.api.interfaces;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Interface for player profile management
 */
public interface IPlayerProfile extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {


    default String getUUIDFromUsername(String table, String username, String redisKey, String punishment) throws SQLException {
        return "";
    }

    default String getUUIDFromName(String table, String username, String redisKey, String punishment) throws SQLException {
        return "";
    }

    default String getUUIDFromName(String table, String name, String redisKey) throws SQLException {
        return "";
    }

    /**
     * Creates a new player profile
     *
     * @param playerId   The player's UUID
     * @param playerName The player's name
     */
    default void createProfile(UUID playerId, String playerName) throws SQLException {
    }

    default boolean playerExistsFromUsername(String username, String table, String redisKey, String punishment) throws SQLException {
        return false;
    }

    default boolean playerExistsByUsername(String username, String table, String redisKey, String punishment) throws SQLException{
        return false;
    }

    default String getUUIDFromUsername(String punishTable, String targetName, String punishTable1) throws SQLException {
        return "";
    }

    default boolean playerExistsByUUID(UUID uuid, String table, String redisKey) throws SQLException {
        return false;
    }

    default boolean playerExistsFromUsername(String targetName, String punish, String punish1) throws SQLException {
        return false;
    }
}
