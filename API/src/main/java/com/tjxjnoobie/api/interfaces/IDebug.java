package com.tjxjnoobie.api.interfaces;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Interface for debug command operations
 */
public interface IDebug {
    
    /**
     * Executes debug command
     * @param sender The command sender
     * @param args Command arguments
     * @return true if command was handled successfully
     */
    boolean executeDebugCommand(CommandSender sender, String[] args);
    
    /**
     * Shows debug information to sender
     * @param sender The command sender
     */
    void showDebugInfo(CommandSender sender);
    
    /**
     * Shows player debug information
     * @param sender The command sender
     * @param targetPlayer The target player
     */
    void showPlayerDebugInfo(CommandSender sender, Player targetPlayer);
    
    /**
     * Shows server debug information
     * @param sender The command sender
     */
    void showServerDebugInfo(CommandSender sender);
    
    /**
     * Shows game debug information
     * @param sender The command sender
     */
    void showGameDebugInfo(CommandSender sender);
    
    /**
     * Toggles debug mode for a player
     * @param player The player to toggle debug for
     * @return true if debug is now enabled
     */
    boolean toggleDebugMode(Player player);
    
    /**
     * Enables debug mode for a player
     * @param player The player to enable debug for
     */
    void enableDebugMode(Player player);
    
    /**
     * Disables debug mode for a player
     * @param player The player to disable debug for
     */
    void disableDebugMode(Player player);
    
    /**
     * Checks if debug mode is enabled for a player
     * @param player The player to check
     * @return true if debug mode is enabled
     */
    boolean isDebugModeEnabled(Player player);
    
    /**
     * Sends debug message to all debug users
     * @param message The debug message
     */
    void sendDebugMessage(String message);
    
    /**
     * Sends debug message to specific player
     * @param player The player to send message to
     * @param message The debug message
     */
    void sendDebugMessage(Player player, String message);
    
    /**
     * Logs debug information to console
     * @param message The debug message
     */
    void logDebugInfo(String message);
    
    /**
     * Dumps system state to debug
     * @param sender The command sender
     */
    void dumpSystemState(CommandSender sender);
    
    /**
     * Performs debug test
     * @param sender The command sender
     * @param testName The test name
     */
    void performDebugTest(CommandSender sender, String testName);
    
    /**
     * Reloads debug configuration
     * @param sender The command sender
     */
    void reloadDebugConfig(CommandSender sender);
    
    /**
     * Shows available debug commands
     * @param sender The command sender
     */
    void showDebugHelp(CommandSender sender);
    
    /**
     * Exports debug data
     * @param sender The command sender
     * @param format The export format
     */
    void exportDebugData(CommandSender sender, String format);
    
    /**
     * Clears debug logs
     * @param sender The command sender
     */
    void clearDebugLogs(CommandSender sender);
    
    /**
     * Shows debug statistics
     * @param sender The command sender
     */
    void showDebugStatistics(CommandSender sender);
    
    /**
     * Sets debug level
     * @param sender The command sender
     * @param level The debug level
     */
    void setDebugLevel(CommandSender sender, String level);
    
    /**
     * Gets current debug level
     * @return The debug level
     */
    String getDebugLevel();
}