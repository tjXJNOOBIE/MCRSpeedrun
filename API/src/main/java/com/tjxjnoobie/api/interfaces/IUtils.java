package com.tjxjnoobie.api.interfaces;


import com.tjxjnoobie.api.dependency.contexts.GlobalContext;
import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.platform.global.console.Log;
import org.bukkit.Bukkit;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Interface for utility functions providing common server operations and formatting
 * This interface defines essential utility methods for server management, messaging,
 * time formatting, and configuration handling.
 * 
 * @param <T> The type of context this utility interface works with
 */

public interface IUtils<T> {

    String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    String prefix = "§6§lNovus§8§l »»§f ";
    String staffPrefix = "��4§lNovus §8§l»»§c ";
    String serverID = null;
    String gameID = "";

    //TODO Update methods to not use globalContext in methods

    /**
     * Gets the context instance
     * Uses the default implementation from ContextAccess with a fallback
     * 
     * @return The context instance
     */

    default IGlobalContext getGlobalContext() {
        //TODO: Remove concrete class from here

        IGlobalContext globalContext = new GlobalContext();

        // Type-safe cast only if T matches the actual context type
            if (globalContext != null) {
                return globalContext;
            }


        Log.warn("[IUtils] Failed to resolve IContext<T> for generic type T.");
        return null;
    }
    


    /**
     * Gets the server prefix for messages
     * This prefix is used for general server messages to players
     *
     * @return The server prefix string with color codes (default: "§6§lNovus »§c ")
     */
    default String getPrefix() {
        return prefix;
    }

    /**
     * Gets the staff prefix for messages
     * This prefix is used for staff-related messages and announcements
     *
     * @return The staff prefix string with color codes (default: "§4§lNovus »§c ")
     */
    default String getStaffPrefix() {
        return staffPrefix;
    }

    default String getGameID() {
        return getGlobalContext().getLocalServerMetaData().getGameID();
    }

    /**
     * Gets the unique server identifier
     * This ID is used to distinguish between different server instances
     *
     * @return The server ID string, typically a randomly generated alphanumeric code
     */
    default String getServerID() {
        String serverID = getGlobalContext().getLocalServerMetaData().getServerID();
    Log.info("[ID] Retrieving server ID... " + serverID);

        return serverID;

    }

    default void createServerID() {
        Log.info("[ID] Creating new server ID...");
        getGlobalContext().getLocalServerMetaData().setServerID(generateRandomID(5));
    }

    default void createGameID(IGlobalContext globalContext) {
       globalContext.getLocalServerMetaData().setGameID(generateRandomID(6));
    }

    default String generateRandomID(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder id = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            id.append(CHARACTERS.charAt(index));
        }
        Log.info("[ID] Generated ID: " + id.toString());


        return id.toString();
    }

    default boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            return false;
        }
    }
    default String formatTimestamp(Timestamp timestamp, DateTimeFormatter formatter) {
        return (timestamp != null) ? timestamp.toLocalDateTime().format(formatter) : null;
    }
    default String formatTime(long milliseconds) {
        long hours = milliseconds / (1000 * 60 * 60);
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long seconds = (milliseconds / 1000) % 60;
        long millis = milliseconds % 1000;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
        } else {
            return String.format("%02d:%02d.%03d", minutes, seconds, millis);
        }
    }

    default Map<String, Object> getConfigValues(IGlobalContext globalContext) {
        return globalContext.getUtils().getConfigValues(globalContext);
    }

    default void broadcastMessage(IGlobalContext globalContext, String message) {
        globalContext.getUtils().broadcastMessage(globalContext, message);
        if (message != null && !message.trim().isEmpty()) {
            Bukkit.broadcastMessage(prefix + message);
        }
    }


    /**
     * Gets a configuration value
     * @param key The configuration key
     * @return The configuration value
     */

    default Object getConfigValue(IGlobalContext globalContext, String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }
        return globalContext.getUtils().getConfigValues(globalContext).get(key);
    }
    default void setGameType(IGlobalContext globalContext, GameTypeEnum gameTypeEnum) throws SQLException {
        globalContext.getGameType().setGameType(gameTypeEnum, serverID);
    }


    default void setConfigValue(String key, Object value, IGlobalContext globalContext){

    }
}
