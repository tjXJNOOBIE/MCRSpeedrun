package com.tjxjnoobie.api.interfaces;


import com.tjxjnoobie.api.enums.GameTypeEnum;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Interface for utility functions providing common server operations and formatting
 * This interface defines essential utility methods for server management, messaging,
 * time formatting, and configuration handling.
 * 
 */
public interface IUtils {



    /**
     * Gets the context instance
     * 
     * @return The context instance
     */
    default IGlobalContext getGlobalContext() {
        return null;
    }

    /**
     * Gets the server prefix for messages
     * This prefix is used for general server messages to players
     *
     * @return The server prefix string with color codes (default: "§6§lNovus »§c ")
     */
    default String getPrefix() {
        return " ";
    }

    /**
     * Gets the staff prefix for messages
     * This prefix is used for staff-related messages and announcements
     *
     * @return The staff prefix string with color codes (default: "§4§lNovus »§c ")
     */
    default String getStaffPrefix() {
        return " ";
    }

    /**
     * Gets the game ID from the local server metadata
     * @return The game ID string
     */
    default String getGameID() {
        return "";
    }

    /**
     * Gets the unique server identifier
     * This ID is used to distinguish between different server instances
     *
     * @return The server ID string, typically a randomly generated alphanumeric code
     */
    default String getServerID() {
        return "";
    }

    /**
     * Creates a new server ID and stores it in the local server metadata
     */
    default void createServerID() {
        // Default empty implementation - override in concrete class
    }

    /**
     * Creates a new game ID and stores it in the local server metadata
     */
    default void createGameID() {
        // Default empty implementation - override in concrete class
    }

    /**
     * Generates a random ID of the specified length
     * @param length The length of the ID to generate
     * @return A randomly generated alphanumeric ID
     */
    default String generateRandomID(int length) {
        return "";
    }

    /**
     * Gets configuration values map
     * @return Map of configuration values
     */
    default Map<String, Object> getConfigValues() {
        return new java.util.HashMap<>();
    }

    /**
     * Sets the game type based on the current directory
     * @throws SQLException if database operation fails
     */
    default void setGameType() throws SQLException {
        // Default empty implementation - override in concrete class
    }

    /**
     * Gets the current time in format "dd.MM HH:mm"
     * @return Formatted time string
     */
    default String getTime() {
        return "";
    }

    /**
     * Gets the current date in format "d MMM yyyy"
     * @return Formatted date string
     */
    default String getDate() {
        return "";
    }

    /**
     * Gets the advanced time with timezone information
     * @return Formatted time with timezone
     */
    default String getAdvancedTime() {
        return "";
    }

    /**
     * Parses a string value to boolean
     * @param value The string value to parse
     * @return The parsed boolean value
     */
    default boolean parseBoolean(String value) {
        return false;
    }

    /**
     * Formats a timestamp with the given formatter
     * @param timestamp The timestamp to format
     * @param formatter The date time formatter
     * @return The formatted timestamp string
     */
    default String formatTimestamp(Timestamp timestamp, DateTimeFormatter formatter) {
        return "";
    }

    /**
     * Formats milliseconds into a time string
     * @param milliseconds The milliseconds to format
     * @return The formatted time string
     */
    default String formatTime(long milliseconds) {
        return "";
    }

    /**
     * Gets configuration values from the global context
     * @param globalContext The global context
     * @return Map of configuration values
     */
    default Map<String, Object> getConfigValues(IGlobalContext globalContext) {
        return new java.util.HashMap<>();
    }

    /**
     * Broadcasts a message to all players
     * @param globalContext The global context
     * @param message The message to broadcast
     */
    default void broadcastMessage(IGlobalContext globalContext, String message) {
        // Default empty implementation - override in concrete class
    }

    /**
     * Gets a configuration value by key
     * @param globalContext The global context
     * @param key The configuration key
     * @return The configuration value
     */
    default Object getConfigValue(IGlobalContext globalContext, String key) {
        return null;
    }

    /**
     * Sets the game type for the server
     * @param globalContext The global context
     * @param gameTypeEnum The game type to set
     * @throws SQLException if database operation fails
     */
    default void setGameType(IGlobalContext globalContext, GameTypeEnum gameTypeEnum) throws SQLException {
        // Default empty implementation - override in concrete class
    }

    /**
     * Sets a configuration value
     * @param key The configuration key
     * @param value The value to set
     * @param globalContext The global context
     */
    default void setConfigValue(String key, Object value, IGlobalContext globalContext){
        // Default empty implementation - override in concrete class
    }
}
