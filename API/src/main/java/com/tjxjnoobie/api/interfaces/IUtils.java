package com.tjxjnoobie.api.interfaces;


import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
import com.tjxjnoobie.api.dependency.composition.domains.IInfrastructureDomain;
import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;

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
@ComposesToInterface(IInfrastructureDomain.class)
public interface IUtils extends ILocalServerMetaData {






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
     * @return Map of configuration values
     */
    default Map<String, Object> getConfigValues() {
        return new java.util.HashMap<>();
    }



    /**
     * Sets the game type for the server
     * @param gameTypeEnum The game type to set
     * @throws SQLException if database operation fails
     */
    default void setGameType(GameTypeEnum gameTypeEnum) throws SQLException {
        // Default empty implementation - override in concrete class
    }

    /**
     * Sets a configuration value
     * @param key The configuration key
     * @param value The value to set
     * @param globalContext The global context
     */
    default void setConfigValue(String key, Object value){
        // Default empty implementation - override in concrete class
    }
}
