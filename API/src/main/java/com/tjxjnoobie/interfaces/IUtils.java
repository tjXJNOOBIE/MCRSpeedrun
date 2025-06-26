package com.tjxjnoobie.interfaces;

import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.API.utils.Utils;

import java.sql.SQLException;

/**
 * Interface for utility functions providing common server operations and formatting
 * This interface defines essential utility methods for server management, messaging,
 * time formatting, and configuration handling.
 */
public interface IUtils {

    /**
     * Gets the server prefix for messages
     * This prefix is used for general server messages to players
     * @return The server prefix string with color codes (default: "§6§lNovus »§c ")
     */
    default String getPrefix(){
        return "§6§lNovus »§c ";
    }
    
    /**
     * Gets the staff prefix for messages
     * This prefix is used for staff-related messages and announcements
     * @return The staff prefix string with color codes (default: "§4§lNovus »§c ")
     */
    default String getStaffPrefix(){
        return "§4§lNovus »§c ";
    }
    
    /**
     * Gets the unique server identifier
     * This ID is used to distinguish between different server instances
     * @return The server ID string, typically a randomly generated alphanumeric code
     */
    default String getServerID(IGlobalContext context) {
        IUtils utils = context.getUtils();
        return utils.getServerID(context);

    }
    
    /**
     * Formats a time duration from milliseconds to a human-readable format
     * Converts milliseconds to hours:minutes:seconds.milliseconds format
     * @param milliseconds The time duration in milliseconds
     * @return Formatted time string (e.g., "01:23:45.678" or "23:45.678")
     */
    String formatTime(long milliseconds);
    
    /**
     * Formats a player name with appropriate color codes
     * Applies consistent color formatting to player names for display
     * @param playerName The raw player name to format
     * @return Formatted player name with color codes, or "§7Unknown" if null/empty
     */
    String formatPlayerName(String playerName);
    
    /**
     * Sends a message to all online players on the server
     * Broadcasts the message with the server prefix to all connected players
     * @param message The message content to broadcast (without prefix)
     */
    void broadcastMessage(String message);
    
    /**
     * Logs a message to the server console
     * Writes the message to the server log with appropriate formatting
     * @param message The message to log to console
     */
    void logMessage(String message);
    
    /**
     * Retrieves a configuration value by key
     * Gets a stored configuration value from the internal configuration map
     * @param key The configuration key to look up
     * @return The configuration value, or null if key doesn't exist
     */
    Object getConfigValue(String key);
    
    /**
     * Sets a configuration value for the given key
     * Stores a configuration value in the internal configuration map
     * @param key The configuration key to set
     * @param value The value to associate with the key
     */
    void setConfigValue(String key, Object value);

    void createServerID();

    void createGameID();

    String getGameID();

    void setGameType() throws SQLException;
}