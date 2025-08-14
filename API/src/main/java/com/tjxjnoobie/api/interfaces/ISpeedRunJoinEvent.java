package com.tjxjnoobie.api.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Interface for join event handling
 */
public interface ISpeedRunJoinEvent {
    
    /**
     * Handles player join event
     * @param event The player join event
     */
    void onPlayerJoin(PlayerJoinEvent event);
    
    /**
     * Processes player join logic
     * @param player The player who joined
     */
    void processPlayerJoin(Player player);
    
    /**
     * Sets up player for the game
     * @param player The player to set up
     */
    void setupPlayer(Player player);
    
    /**
     * Sends welcome message to player
     * @param player The player to send message to
     */
    void sendWelcomeMessage(Player player);
    
    /**
     * Applies join effects to player
     * @param player The player to apply effects to
     */
    void applyJoinEffects(Player player);
    
    /**
     * Checks if player can join the game
     * @param player The player to check
     * @return true if player can join
     */
    boolean canPlayerJoin(Player player);
    
    /**
     * Handles first-time player join
     * @param player The new player
     */
    void handleFirstTimeJoin(Player player);
    
    /**
     * Handles returning player join
     * @param player The returning player
     */
    void handleReturningPlayerJoin(Player player);
    
    /**
     * Updates player data on join
     * @param player The player to update
     */
    void updatePlayerData(Player player);
    
    /**
     * Notifies other players of join
     * @param player The player who joined
     */
    void notifyPlayersOfJoin(Player player);
    
    /**
     * Handles join during different game states
     * @param player The player who joined
     */
    void handleGameStateJoin(Player player);
    
    /**
     * Cleans up join event resources
     */
    void cleanup();
    
    /**
     * Gets join event statistics
     * @return Join statistics object
     */
    Object getJoinStatistics();
    
    /**
     * Enables join event handling
     */
    void enable();
    
    /**
     * Disables join event handling
     */
    void disable();
    
    /**
     * Checks if join event handling is enabled
     * @return true if enabled
     */
    boolean isEnabled();
}