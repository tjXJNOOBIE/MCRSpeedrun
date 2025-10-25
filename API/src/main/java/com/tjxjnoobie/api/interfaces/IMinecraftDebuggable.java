package com.tjxjnoobie.api.interfaces;

import org.bukkit.entity.Player;

/**
 * Interface for classes that need debug messaging capabilities
 */
public interface IMinecraftDebuggable {


    default ISpeedRunContext getSpeedRunContext(){
        return null;
    }

    /**
     * Sends a debug message to a player if they are a debugger
     *
     * @param player  The player to send the message to
     * @param message The debug message
     */
    default void sendDebugMessage(Player player, String message) {
    }

    /**
     * Sends a debug message with a custom prefix
     *
     * @param player  The player to send the message to
     * @param prefix  The custom prefix (e.g., "[BUILDER]", "[MANAGER]")
     * @param message The debug message
     */
    default void sendDebugMessage(Player player, String prefix, String message) {
    }
}
