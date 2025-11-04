package com.tjxjnoobie.api.interfaces;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Interface for player profile management
 */
public interface IPlayerProfile {


    /**
     * Creates a new player profile
     *
     * @param playerId   The player's UUID
     * @param playerName The player's name
     */
    default void createProfile(UUID playerId, String playerName) throws SQLException {
    }


    default String getUUIDFromUsername(String punishTable, String targetName, String punishTable1) throws SQLException {
        return "";
    }

    default boolean playerExistsFromUsername(String targetName, String punish, String punish1) throws SQLException {
        return false;
    }
}