package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.enums.GameModeEnum;

/**
 * Interface for game mode management
 */
public interface IGameMode extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {
    
    /**
     * Gets the current game mode
     * @return The current game mode enum
     */
    default GameModeEnum getCurrentGameMode(){
        return null;
    }
    
    /**
     * Sets the game mode
     * @param gameMode The game mode to set
     */
    default void setGameMode(GameModeEnum gameMode){

    }
    
    /**
     * Checks if the current game mode matches the given mode
     * @param gameMode The game mode to check
     * @return true if current mode matches
     */
    default boolean isGameMode(GameModeEnum gameMode){
        return false;
    }
    
    /**
     * Gets the game mode as a string
     * @return The game mode string representation
     */
    default String getGameModeString(){
        return null;
    }
}
