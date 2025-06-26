package com.tjxjnoobie.interfaces;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Interface for boss bar management operations
 */
public interface IBossBarManager {
    
    /**
     * Creates a new boss bar
     * @param title The boss bar title
     * @param color The boss bar color
     * @param style The boss bar style
     * @return The created boss bar
     */
    BossBar createBossBar(String title, BarColor color, BarStyle style);
    
    /**
     * Creates a boss bar with default settings
     * @param title The boss bar title
     * @return The created boss bar
     */
    BossBar createBossBar(String title);
    
    /**
     * Adds a player to a boss bar
     * @param bossBar The boss bar
     * @param player The player to add
     */
    void addPlayer(BossBar bossBar, Player player);
    
    /**
     * Removes a player from a boss bar
     * @param bossBar The boss bar
     * @param player The player to remove
     */
    void removePlayer(BossBar bossBar, Player player);
    
    /**
     * Adds a player to a boss bar by ID
     * @param bossBarId The boss bar ID
     * @param player The player to add
     */
    void addPlayer(String bossBarId, Player player);
    
    /**
     * Removes a player from a boss bar by ID
     * @param bossBarId The boss bar ID
     * @param player The player to remove
     */
    void removePlayer(String bossBarId, Player player);
    
    /**
     * Updates boss bar progress
     * @param bossBar The boss bar
     * @param progress The progress (0.0 to 1.0)
     */
    void updateProgress(BossBar bossBar, double progress);
    
    /**
     * Updates boss bar title
     * @param bossBar The boss bar
     * @param title The new title
     */
    void updateTitle(BossBar bossBar, String title);
    
    /**
     * Updates boss bar color
     * @param bossBar The boss bar
     * @param color The new color
     */
    void updateColor(BossBar bossBar, BarColor color);
    
    /**
     * Updates boss bar style
     * @param bossBar The boss bar
     * @param style The new style
     */
    void updateStyle(BossBar bossBar, BarStyle style);
    
    /**
     * Gets a boss bar by ID
     * @param bossBarId The boss bar ID
     * @return The boss bar or null if not found
     */
    BossBar getBossBar(String bossBarId);
    
    /**
     * Registers a boss bar with an ID
     * @param bossBarId The boss bar ID
     * @param bossBar The boss bar to register
     */
    void registerBossBar(String bossBarId, BossBar bossBar);
    
    /**
     * Unregisters a boss bar
     * @param bossBarId The boss bar ID
     */
    void unregisterBossBar(String bossBarId);
    
    /**
     * Shows a boss bar to all players
     * @param bossBar The boss bar to show
     */
    void showToAll(BossBar bossBar);
    
    /**
     * Hides a boss bar from all players
     * @param bossBar The boss bar to hide
     */
    void hideFromAll(BossBar bossBar);
    
    /**
     * Creates a timer boss bar
     * @param title The boss bar title
     * @param durationSeconds The timer duration in seconds
     * @return The timer boss bar
     */
    BossBar createTimerBossBar(String title, int durationSeconds);
    
    /**
     * Starts a timer boss bar
     * @param bossBarId The boss bar ID
     */
    void startTimer(String bossBarId);
    
    /**
     * Stops a timer boss bar
     * @param bossBarId The boss bar ID
     */
    void stopTimer(String bossBarId);
    
    /**
     * Removes all boss bars for a player
     * @param player The player
     */
    void removeAllBossBars(Player player);
    
    /**
     * Gets all active boss bars
     * @return Array of boss bar IDs
     */
    String[] getActiveBossBars();
    
    /**
     * Cleans up all boss bars
     */
    void cleanup();

    void removePlayer(Player player);
}