package com.tjxjnoobie.interfaces;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Interface for player management operations
 */
public interface IPlayerManager {
    
    /**
     * Makes a player a spectator
     * @param uuid The player's UUID
     * @param name The player's name
     * @param player The player object
     */
    void makeSpectator(UUID uuid, String name, Player player);
    
    /**
     * Eliminates a player from the game
     * @param uuid The player's UUID
     * @param name The player's name
     * @param player The player object
     */
    void eliminatePlayer(UUID uuid, String name, Player player);
    
    /**
     * Adds a player to the game
     * @param uuid The player's UUID
     * @param name The player's name
     * @param player The player object
     */
    void addPlayerToGame(UUID uuid, String name, Player player);
    
    /**
     * Removes a player from the game
     * @param uuid The player's UUID
     * @param name The player's name
     * @param player The player object
     */
    void removePlayerFromGame(UUID uuid, String name, Player player);
    
    /**
     * Checks if a player is in the game
     * @param uuid The player's UUID
     * @return true if player is in game
     */
    boolean isPlayerInGame(UUID uuid);
    
    /**
     * Checks if a player is spectating
     * @param uuid The player's UUID
     * @return true if player is spectating
     */
    boolean isPlayerSpectating(UUID uuid);
    
    /**
     * Gets the number of active players
     * @return Number of active players
     */
    int getActivePlayerCount();
    
    /**
     * Gets the number of spectating players
     * @return Number of spectating players
     */
    int getSpectatorCount();
    
    /**
     * Teleports a player to spawn
     * @param player The player to teleport
     */
    void teleportToSpawn(Player player);
    
    /**
     * Resets a player's state
     * @param player The player to reset
     */
    void resetPlayerState(Player player);
    
    /**
     * Gives starting items to a player
     * @param player The player to give items to
     */
    void giveStartingItems(Player player);
    
    /**
     * Clears a player's inventory
     * @param player The player whose inventory to clear
     */
    void clearPlayerInventory(Player player);
}