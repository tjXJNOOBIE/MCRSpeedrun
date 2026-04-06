package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.platform.global.annotations.Injectable;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.managers.MySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface for game state management
 */
@Injectable("Manages game state transitions and persistence")
public interface IGameState extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {

    // Backing fields
    HashMap<String, Boolean> gamestate = new HashMap<>();
    GameStateEnum[] CURRENT_STATE_BOX = new GameStateEnum[1]; // index 0 holds current state

    default void GameStateEnum() {
        CURRENT_STATE_BOX[0] = GameStateEnum.LOBBY;    }

    /**
     * Gets the current game state
     * @return The current game state enum
     */
    default GameStateEnum getCurrentState() {
        return CURRENT_STATE_BOX[0];
    }

    default void setState(GameStateEnum gamestate) {
        CURRENT_STATE_BOX[0] = gamestate;
    }

    /**
     * Sets the game state
     * @param gameState The game state to set
     * @param serverId The server ID for distributed systems
     */
    default void setGameState(GameStateEnum gameState, String serverId) throws SQLException {
        Log.info("[STATE] Setting game state to " + gameState);
        MySQL.executePreparedStatement("UPDATE servers SET GAMESTATE= ? WHERE SERVERID= ?", gameState.name(), serverId);
        setState(gameState);
        Log.info("[STATE] Game state set to " + gameState);
    }

    /**
     * Sets the game state without server ID
     * @param gameState The game state to set
     */
    default void setGameState(GameStateEnum gameState) {
        setState(gameState);
    }

    /**
     * Checks if the current state matches the given state
     * @param gameState The game state to check
     * @return true if current state matches
     */
    default boolean isGameState(GameStateEnum gameState) {
        return getCurrentState() == gameState;
    }

    /**
     * Gets the game state as a string
     * @return The game state string representation
     */
    default String getGameStateString() {
        GameStateEnum cs = getCurrentState();
        return cs != null ? cs.name() : "";
    }

    /**
     * Gets the previous game state
     * @return The previous game state or null if none
     */
    default GameStateEnum getPreviousState() {
        return null;
    }

    default String getState(String serverid, String database) {
        String gamestateStr = "";
        try {
            ResultSet rs = MySQL.getResult("SELECT * FROM servers WHERE SERVERID= ?", serverid);
            if ((rs.next())) {
                rs.getString("GAMESTATE");
            }
            gamestateStr = rs.getString("GAMESTATE");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gamestateStr;
    }

    default void createServerID(String serverId, String gameID, String table, String gameType) throws SQLException {
        Log.info("[ID] Creating new server ID in database...");
        MySQL.executePreparedStatement(
            "INSERT INTO " + table + " (SERVERID, GAMETYPE, GAMESTATE, SPAWNWORLD, GAMEID) VALUES (?,?,?,?,?)",
            serverId, gameType, "LOBBY", "lobby", gameID
        );
        Log.info("[ID] Created new server ID in database...");

    }

    default void removeServerID(String serverID) {
        try {
            PreparedStatement statement = MySQL.connection.prepareStatement(
                "DELETE FROM servers WHERE SERVERID ='" + serverID + "'"
            );
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default ArrayList<String> getAllGameIDs() {
        ArrayList<String> IDs = new ArrayList<>();
        String query = "SELECT ID FROM servers";
        ResultSet rs = MySQL.getResult(query);
        try {
            while (rs.next()) {
                String id = rs.getString("ID");
                IDs.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return IDs;
    }

    default ArrayList<String> getServerIDs() {
        ArrayList<String> IDs = new ArrayList<>();
        String query = "SELECT ID FROM servers";
        ResultSet rs = MySQL.getResult(query);
        try {
            while (rs.next()) {
                String id = rs.getString("ID");
                IDs.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return IDs;
    }
}
