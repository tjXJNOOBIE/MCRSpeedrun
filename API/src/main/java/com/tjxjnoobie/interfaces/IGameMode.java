package com.tjxjnoobie.interfaces;

import com.tjxjnoobie.enums.GameModeEnum;

/**
 * Interface for game mode management
 */
public interface IGameMode {
    
    /**
     * Gets the current game mode
     * @return The current game mode enum
     */
    GameModeEnum getCurrentGameMode();
    
    /**
     * Sets the game mode
     * @param gameMode The game mode to set
     */
    void setGameMode(GameModeEnum gameMode);
    
    /**
     * Checks if the current game mode matches the given mode
     * @param gameMode The game mode to check
     * @return true if current mode matches
     */
    boolean isGameMode(GameModeEnum gameMode);
    
    /**
     * Gets the game mode as a string
     * @return The game mode string representation
     */
    String getGameModeString();
}