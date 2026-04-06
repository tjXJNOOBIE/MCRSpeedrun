/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.machine.data.interfaces;

public interface ILocalServerMetaData extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {

    /**
     * Retrieves the unique identifier for the current server instance.
     *
     * @return a string representing the server ID; empty string if not set or unavailable
     */
    default String getLocalServerID(){
        return "";
    }

    /**
     * Sets the unique identifier for the server, typically used to distinguish between different instances of a server.
     *
     * @param serverID the unique string identifier for the server; must not be null or empty
     */
    default void setServerID(String serverID) {
    }

    /**
     * Retrieves the in-game prefix used for Minecraft staff members in the server.
     * This prefix is typically displayed before staff player names during gameplay and can be customized per server configuration.
     *
     * @return The in-game prefix string for staff members, or an empty string if not defined
     */
    default String getMinecraftStaffInGamePrefix(){
        return "";
    }

    /**
     * Retrieves the unique identifier for the game associated with this server metadata.
     *
     * @return the game ID string, which uniquely identifies the game configuration or instance
     */
    default String getGameID() {
        return "";
    }

    /**
     * Sets the unique game identifier for the current server or game instance.
     *
     * @param gameID a string representing the unique identifier for the game or server session
     */
    default void setGameID(String gameID) {
    }


    /**
     * Retrieves the Minecraft staff prefix used in server messages.
     * This prefix is typically displayed before staff-related commands or notifications
     * to identify the message as originating from a staff member. The prefix value is
     * determined by the server's configuration and may vary based on permissions or roles.
     *
     * @return the Minecraft staff prefix string, which is generally empty by default
     */
    default String getLocalServerPrefix(){
        return "";
    }
}
