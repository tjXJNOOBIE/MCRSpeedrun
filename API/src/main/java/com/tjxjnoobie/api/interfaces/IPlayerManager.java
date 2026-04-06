package com.tjxjnoobie.api.interfaces;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Interface for player management operations
 */
public interface IPlayerManager extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {
    
    /**
     * Makes a player a spectator
     * @param uuid The player's UUID
     * @param name The player's name
     * @param player The player object
     */
    default void makeSpectator(UUID uuid, String name, Player player) {

    }
    
    /**
     * Eliminates a player from the game
     * @param uuid The player's UUID
     * @param name The player's name
     * @param player The player object
     */
    default void eliminatePlayer(UUID uuid, String name, Player player){

    }
    

}
