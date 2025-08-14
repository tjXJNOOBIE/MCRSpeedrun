package com.tjxjnoobie.api.interfaces;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

/**
 * Interface for Minecraft utility functions
 */
public interface IMCUtils {

    /**
     * Gets all online players
     *
     * @return A player representing all online players (for bulk operations)
     */

    /**
     * Plays a sound for all online players
     *
     * @param location The location to play the sound
     * @param sound    The sound to play
     * @param volume   The volume level
     * @param pitch    The pitch level
     */

    /**
     * Plays a dramatic boom effect
     *
     * @param player The player to play the effect for
     */
    default void playDramaticBoom(Player player, Plugin plugin) {
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 0.5f); // Low-pitched cave sound
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 1.0f, 0.8f), 10L);
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f), 20L);
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.6f), 30L);
    }
    /**
     * Hides a player from all other players
     *
     * @param player The player to hide
     */


    default void sendDebugMessage(IGlobalContext globalContext, Player player, String message) {
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
    default void hidePlayerFromAll(Player toHide, IGlobalContext globalContext) {
        Plugin plugin = globalContext.getPlugin();
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            viewer.hidePlayer(plugin, toHide);
        }
    }

    default Player getAllPlayers() {
        for (Player ap : Bukkit.getOnlinePlayers()) {
            if (ap != null) {
                return ap;
            } else{
                System.out.println("Can't get All Players, No players online");
            }
        }
        return null;
    }

    default void playSoundForAll(Location location, Sound sound, float volume, float pitch) {
        for (Player ap : Bukkit.getOnlinePlayers()) {
            ap.playSound(location, sound, volume, pitch);
        }
    }

    default void sendMessageToAll(String message) {
        for (Player ap : Bukkit.getOnlinePlayers()) {
            ap.sendMessage(message);
        }
    }



    default void cancelTask(BukkitTask task) {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            Bukkit.getLogger().info(task.toString() + " Task has been cancelled.");
        }
    }



    /**
     * Broadcast a plain text message to all players using Adventure API
     */
    default void broadcast(Component message) {
        getAllPlayers().sendMessage(message);
    }

    /**
     * Broadcast a message with hover text
     */
    default void broadcast(Component message, Component hoverText) {
        Component withHover = message.hoverEvent(HoverEvent.showText(hoverText));
        broadcast(withHover);
    }

    /**
     * Broadcast a message with a click event
     */
    default void broadcast(Component message, ClickEvent clickEvent) {
        Component withClick = message.clickEvent(clickEvent);
        broadcast(withClick);
    }

    /**
     * Broadcast a message with hover and click events
     */
    default void broadcast(Component message, HoverEvent<?> hoverEvent, ClickEvent clickEvent) {
        Component withEvents = message.hoverEvent(hoverEvent).clickEvent(clickEvent);
        broadcast(withEvents);
    }

    // Helper factory methods for convenience (optional)

    default HoverEvent<Component> createHoverText(String text) {
        return HoverEvent.showText(Component.text(text));
    }

    default ClickEvent createClickRunCommand(String command) {
        return ClickEvent.runCommand(command);
    }

    default ClickEvent createClickOpenUrl(String url) {
        return ClickEvent.openUrl(url);
    }
}

