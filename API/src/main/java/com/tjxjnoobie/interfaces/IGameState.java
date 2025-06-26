package com.tjxjnoobie.interfaces;

import com.tjxjnoobie.enums.GameStateEnum;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Interface for game state management
 */
public interface IGameState {

    void GameStateEnum();

    /**
     * Gets the current game state
     * @return The current game state enum
     */
    GameStateEnum getCurrentState();

    void setState(GameStateEnum gamestate);

    /**
     * Sets the game state
     * @param gameState The game state to set
     * @param serverId The server ID for distributed systems
     */
    void setGameState(GameStateEnum gameState, String serverId) throws SQLException;
    
    /**
     * Sets the game state without server ID
     * @param gameState The game state to set
     */
    void setGameState(GameStateEnum gameState);
    
    /**
     * Checks if the current state matches the given state
     * @param gameState The game state to check
     * @return true if current state matches
     */
    boolean isGameState(GameStateEnum gameState);
    
    /**
     * Gets the game state as a string
     * @return The game state string representation
     */
    String getGameStateString();
    
    /**
     * Gets the previous game state
     * @return The previous game state or null if none
     */
    GameStateEnum getPreviousState();

    String getState(String serverid, String database);

    void createServerID(String serverId, String gameID, String table, String gameType) throws SQLException;

    void removeServerID(String serverID);

    ArrayList<String> getAllGameIDs();

    ArrayList<String> getServerIDs();
}