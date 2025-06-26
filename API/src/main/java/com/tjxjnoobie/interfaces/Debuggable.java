package com.tjxjnoobie.interfaces;

import com.tjxjnoobie.API.GlobalContext;
import org.bukkit.entity.Player;

/**
 * Interface for classes that need debug messaging capabilities
 */
public interface Debuggable {
    
    /**
     * Gets the GlobalContext for accessing debug utilities
     * @return The GlobalContext instance
     */
    GlobalContext getGlobalContext();
    
    /**
     * Sends a debug message to a player if they are a debugger
     * @param player The player to send the message to
     * @param message The debug message
     */
    default void sendDebugMessage(Player player, String message) {
        if (player != null) {
            GlobalContext context = getGlobalContext();
            if (context != null) {
                boolean isDebugger = context.getDebugger().isDebugger(player.getUniqueId());
                if (isDebugger) {
                    String staffPrefix = context.getUtils().getStaffPrefix();
                    player.sendMessage(staffPrefix + "§c" + message);
                }
            }
        }
    }
    
    /**
     * Sends a debug message with a custom prefix
     * @param player The player to send the message to
     * @param prefix The custom prefix (e.g., "[BUILDER]", "[MANAGER]")
     * @param message The debug message
     */
    default void sendDebugMessage(Player player, String prefix, String message) {
        if (player != null) {
            GlobalContext context = getGlobalContext();
            if (context != null) {
                boolean isDebugger = context.getDebugger().isDebugger(player.getUniqueId());
                if (isDebugger) {
                    String staffPrefix = context.getUtils().getStaffPrefix();
                    player.sendMessage(staffPrefix + "§c" + prefix + " " + message);
                }
            }
        }
    }
}