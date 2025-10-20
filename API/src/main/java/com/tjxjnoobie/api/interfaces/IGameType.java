package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.managers.MySQL;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface for game type management with default implementations
 */
public interface IGameType {
    
    /**
     * Gets the game type hash map for tracking game types
     * Override this to provide custom storage
     * @return HashMap for game type tracking
     */
    default HashMap<String, Boolean> getGameTypeHash() {
        // Default implementation - can be overridden for custom storage
        return new HashMap<>();
    }
    
    /**
     * Gets the current game type enum
     * Override this to provide custom storage
     * @return The current game type enum
     */
    default GameTypeEnum getGameTypeEnum() {
        // Default to LOBBY - override for custom behavior
        return GameTypeEnum.LOBBY;
    }
    
    /**
     * Sets the game type enum internally
     * Override this to provide custom storage
     * @param gameType The game type to set
     */
    default void setGameTypeEnum(GameTypeEnum gameType) {
        // Default implementation does nothing - override for custom storage
        // This is intentionally empty as it's meant to be overridden
    }

    /**
     * Initializes the game type to LOBBY
     * @deprecated Use setGameTypeEnum directly
     */
    @Deprecated
    default void GameTypEnum() {
        setGameTypeEnum(GameTypeEnum.LOBBY);
    }
    
    /**
     * Gets the current game type
     * @return The current game type enum
     */
    default GameTypeEnum getGameType() {
        return getGameTypeEnum();
    }

    /**
     * Sets the game type locally
     * @param gametype The game type to set
     */
    default void setType(GameTypeEnum gametype) {
        setGameTypeEnum(gametype);
    }

    /**
     * Sets the game type in the database and locally
     * @param gameType The game type to set
     * @param serverid The server ID
     * @throws SQLException if database error occurs
     */
    default void setGameType(GameTypeEnum gameType, String serverid) throws SQLException {
        MySQL.executePreparedStatement("UPDATE servers SET GAMETYPE= ? WHERE SERVERID= ?", gameType.name(), serverid);
        Bukkit.getLogger().info("Game Type set to " + gameType);
        setType(gameType);
    }

    /**
     * Gets the game type from the database for a specific server
     * @param serverid The server ID
     * @param database The database name (unused in current implementation)
     * @return The game type as a string
     */
    default String getType(String serverid, String database) {
        String gametype = "";
        try {
            ResultSet rs = MySQL.getResult("SELECT * FROM servers WHERE SERVERID= ?", serverid);
            if (rs.next()) {
                gametype = rs.getString("GAMETYPE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gametype;
    }

    /**
     * Gets all game types from the database
     * @return ArrayList of game type strings
     */
    default ArrayList<String> getGameTypes() {
        ArrayList<String> gameTypes = new ArrayList<>();
        String query = "SELECT GAMETYPE FROM servers";
        ResultSet rs = MySQL.getResult(query);
        try {
            while (rs.next()) {
                String type = rs.getString("GAMETYPE");
                gameTypes.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gameTypes;
    }
}
