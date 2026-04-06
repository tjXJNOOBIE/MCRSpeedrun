package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.enums.GameTypeEnum;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface for game type management with default implementations
 */
public interface IGameType extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {
    
    /**
     * Gets the game type hash map for tracking game types
     * Override this to provide custom storage
     * @return HashMap for game type tracking
     */
    default HashMap<String, Boolean> getGameTypeHash() {
        return new HashMap<>();
    }


    /**
     * Gets the current game type enum
     * Override this to provide custom storage
     * @return The current game type enum
     */
    default GameTypeEnum getGameTypeEnum() {
        return null;
    }


    /**
     * Sets the game type enum internally
     * Override this to provide custom storage
     * @param gameType The game type to set
     */
    default void setGameTypeEnum(GameTypeEnum gameType) {}


    /**
     * Gets the current game type
     * @return The current game type enum
     */
    default GameTypeEnum getGameType() {
        return null;
    }


    /**
     * Sets the game type locally
     * @param gametype The game type to set
     */
    default void setType(GameTypeEnum gametype) {}


    /**
     * Sets the game type in the database and locally
     * @param gameType The game type to set
     * @param serverid The server ID
     * @throws SQLException if database error occurs
     */
    default void setGameType(GameTypeEnum gameType, String serverid) throws SQLException {}


    /**
     * Gets the game type from the database for a specific server
     * @param serverid The server ID
     * @param database The database name (unused in current implementation)
     * @return The game type as a string
     */
    default String getType(String serverid, String database) {
        return "";
    }


    /**
     * Gets all game types from the database
     * @return ArrayList of game type strings
     */
    default ArrayList<String> getGameTypes() {
        return new ArrayList<>();
    }
}
