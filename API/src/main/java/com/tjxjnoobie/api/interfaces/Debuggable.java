package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import org.bukkit.entity.Player;

/**
 * Interface for classes that need debug messaging capabilities
 */
public interface Debuggable {
    
    /**
     * Gets the GlobalContext for accessing debug utilities
     * @return The GlobalContext instance
     */


    @Inject
    default IGlobalContext getGlobalContext() {
        return null;
    }
    /**
     * Sends a debug message to a player if they are a debugger
     * @param player The player to send the message to
     * @param message The debug message
     */
    default void sendDebugMessage(Player player, String message) {
        if (player != null) {
                boolean isDebugger = getGlobalContext().getDebugger().isDebugger(player.getUniqueId());
                if (isDebugger) {
                    String staffPrefix = getGlobalContext().getUtils().getStaffPrefix();
                    player.sendMessage(staffPrefix + "§c" + message);

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
                boolean isDebugger = getGlobalContext().getDebugger().isDebugger(player.getUniqueId());
                if (isDebugger) {
                    String staffPrefix = getGlobalContext().getUtils().getStaffPrefix();
                    player.sendMessage(staffPrefix + "§c" + prefix + " " + message);
                }
            }
        }
    }
