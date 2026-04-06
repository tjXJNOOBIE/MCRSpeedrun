package com.tjxjnoobie.api.interfaces;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for debugging operations
 */
public interface IDebugger extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {
    
    /**
     * Checks if a player is a debugger
     * @param playerId The player's UUID
     * @return true if player is a debugger
     */
    default boolean isDebugger(UUID playerId) {
        return false;
    }

    /**
     * Sets the debugger status for a specified player by UUID.
     *
     * @param uuid The unique identifier of the player to modify debugger status for
     * @param debuggerStatus True if the player should be designated as a debugger, false otherwise
     */
    default void setDebugger(UUID uuid, boolean debuggerStatus) { }

    /**
     * Generates a SQL query string that retrieves the list of debuggers from the database.
     * The returned SQL is intended to be used in database queries to fetch debugger information.
     *
     * @return A SQL query string that selects debugger data from the database; returns an empty string if no query is defined
     */
    default String getDebuggersSQL() {
        return "";
    }

    /**
     * Retrieves a string representation of the current list of debuggers.
     *
     * @return A string containing information about the debuggers, typically formatted for display or logging purposes.
     */
    default String getDebuggers() {
        return "";
    }

    /**
     * Checks if a player with the given UUID is identified as a debugger using SQL-based debugging rules.
     *
     * @param uuid The UUID of the player to check for debugger status
     * @return true if the player is recognized as a debugger according to SQL-defined criteria, false otherwise
     */
    default boolean isDebuggerSQL(UUID uuid) {
        return false;
    }

    /**
     * Retrieves a map of debugger-related data associated with the given UUID.
     * This method returns a hash map containing debugger-specific information, such as debug session details or metadata.
     *
     * @param uuid The unique identifier (UUID) of the player for whom to retrieve debugger hash data
     * @return A map of string-keyed debugger data, where keys represent data fields and values represent their corresponding values
     */
    default Map<String,String> getDebuggerHash(UUID uuid) {
        return new HashMap<>();
    }

    /**
     * Sets the debug hash for a player, associating a UUID with a given name.
     * This method is used to store or update the mapping between a player's UUID and a human-readable name
     * in the debugger system, which may be used for identification purposes during debugging sessions.
     *
     * @param uuid The unique identifier (UUID) of the player
     * @param name The display name or identifier associated with the UUID
     */
    default void setDebuggerHash(UUID uuid, String name) { }

    /**
     * Loads a cached representation of debugger data, typically used to improve performance by avoiding repeated database queries.
     * This method should be implemented to fetch and store debugger-related information (such as active debuggers or their status)
     * in memory for faster access during runtime. The exact data source and storage mechanism are left to implementation specifics.
     */
    default void loadDebuggersCache() { }

    /**
     * Sends a debug message to the specified player using the provided global context.
     * The message is typically used for internal debugging purposes and may be logged or displayed
     * only to players who have been designated as debuggers.
     *
     * @param playerId The UUID of the player to whom the debug message should be sent
     * @param message The debug message content to send to the player
     */
    default void sendDebugMessage(UUID playerId, String message) { }


    /**
     * Sends a debug message to the specified player with an optional prefix.
     * The message is sent using the provided global context, which may include game state and other runtime dependencies.
     * This method is intended for internal debugging purposes and does not affect gameplay or player experience.
     *
     * @param player The Player object to send the debug message to
     * @param prefix An optional prefix string to prepend to the message (e.g., 'DEBUG: ')
     * @param message The actual debug message content to be sent
     */
    default void sendDebugMessage(Player player, String prefix, String message) { }
}
