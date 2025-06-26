package com.tjxjnoobie.interfaces;

import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.abstracts.AbstractContext;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Interface for Minecraft utility functions
 */
public interface IMCUtils extends IGlobalContext {

    /**
     * Gets all online players
     *
     * @return A player representing all online players (for bulk operations)
     */
     Player getAllPlayers();

    /**
     * Plays a sound for all online players
     *
     * @param location The location to play the sound
     * @param sound    The sound to play
     * @param volume   The volume level
     * @param pitch    The pitch level
     */
     void playSoundForAll(Location location, Sound sound, float volume, float pitch);

    /**
     * Plays a dramatic boom effect
     *
     * @param player The player to play the effect for
     */
     void playDramaticBoom(Player player);

    /**
     * Hides a player from all other players
     *
     * @param player The player to hide
     */
     void hidePlayerFromAll(Player player);

    /**
     * Shows a player to all other players
     *
     * @param player The player to show
     */
     void showPlayerToAll(Player player);

    /**
     * Sends a title to all players
     *
     * @param title    The main title
     * @param subtitle The subtitle
     * @param fadeIn   Fade in time in ticks
     * @param stay     Stay time in ticks
     * @param fadeOut  Fade out time in ticks
     */
     void sendTitleToAll(String title, String subtitle, int fadeIn, int stay, int fadeOut);

    /**
     * Teleports all players to a location
     *
     * @param location The location to teleport to
     */
     void teleportAllPlayers(Location location);

    /**
     * Gets all online player objects as a list
     *
     * @return List of all online players
     */
    List<Player> getAllPlayersList();

    /**
     * Sends a message to all players with a specific permission
     *
     * @param permission The permission to check
     * @param message    The message to send
     */
    void sendMessageToPermission(String permission, String message);

    /**
     * Clears inventory for all players
     */
    void clearAllInventories();

    /**
     * Sets game mode for all players
     *
     * @param gameMode The game mode to set
     */
    void setGameModeForAll(org.bukkit.GameMode gameMode);

    default void sendDebugMessage(GlobalContext globalContext, Player player, String message) {
        IRankCache rankCache = globalContext.getRankCache();
        IUtils utils = globalContext.getUtils();
        UUID uuid = player.getUniqueId();
        IDebugger debugger = globalContext.getDebugger();
        if (rankCache.getPowerLevel(uuid) <= 10000 ||
                rankCache.hasPermission(uuid, "network.debug")
                        && debugger.isDebugger(uuid)) {
            player.sendMessage(utils.getStaffPrefix() + message);
        }
    }
}

