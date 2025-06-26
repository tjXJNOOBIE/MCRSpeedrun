package com.tjxjnoobie.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Interface for quit event handling
 */
public interface IQuitEvent {
    
    /**
     * Handles player quit event
     * @param event The player quit event
     */
    void onPlayerQuit(PlayerQuitEvent event);
    
    /**
     * Processes player quit logic
     * @param player The player who quit
     */
    void processPlayerQuit(Player player);
    
    /**
     * Saves player data on quit
     * @param player The player to save data for
     */
    void savePlayerData(Player player);
    
    /**
     * Handles quit during game
     * @param player The player who quit during game
     */
    void handleGameQuit(Player player);
    
    /**
     * Handles quit during lobby
     * @param player The player who quit during lobby
     */
    void handleLobbyQuit(Player player);
    
    /**
     * Cleans up player resources
     * @param player The player to clean up for
     */
    void cleanupPlayerResources(Player player);
    
    /**
     * Notifies other players of quit
     * @param player The player who quit
     */
    void notifyPlayersOfQuit(Player player);
    
    /**
     * Updates game state after quit
     * @param player The player who quit
     */
    void updateGameStateAfterQuit(Player player);
    
    /**
     * Handles spectator quit
     * @param player The spectator who quit
     */
    void handleSpectatorQuit(Player player);
    
    /**
     * Records quit statistics
     * @param player The player who quit
     */
    void recordQuitStatistics(Player player);
    
    /**
     * Handles emergency quit (server shutdown, etc.)
     * @param player The player in emergency quit
     */
    void handleEmergencyQuit(Player player);
    
    /**
     * Gets quit event statistics
     * @return Quit statistics object
     */
    Object getQuitStatistics();
    
    /**
     * Enables quit event handling
     */
    void enable();
    
    /**
     * Disables quit event handling
     */
    void disable();
    
    /**
     * Checks if quit event handling is enabled
     * @return true if enabled
     */
    boolean isEnabled();
    
    /**
     * Sets quit message format
     * @param format The message format
     */
    void setQuitMessageFormat(String format);
    
    /**
     * Gets quit message format
     * @return The message format
     */
    String getQuitMessageFormat();
    
    /**
     * Handles timeout quit
     * @param player The player who timed out
     */
    void handleTimeoutQuit(Player player);
    
    /**
     * Handles kick quit
     * @param player The player who was kicked
     * @param reason The kick reason
     */
    void handleKickQuit(Player player, String reason);
}