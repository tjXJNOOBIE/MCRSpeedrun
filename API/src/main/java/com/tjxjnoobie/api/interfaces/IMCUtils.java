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


    //TODO: Replace class methods parameters for custom DI system

    default ISpeedRunContext getSpeedRunContext() {
        return null;
    }

    /**
     * Plays a dramatic boom effect
     *
     * @param player The player to play the effect for
     */
    default void playDramaticBoom(Player player, Plugin plugin) {
    }

    /**
     * Hides a player from all other players
     *
     * @param player The player to hide
     */


    default void sendDebugMessage(ISpeedRunContext speedRunContext, Player player, String message) {
        IRankCache rankCache = speedRunContext.getRankCache();
        IUtils utils = speedRunContext.getUtils();
        UUID uuid = player.getUniqueId();
        IDebugger debugger = speedRunContext.getDebugger();
        if (rankCache.getPowerLevel(uuid) <= 10000 ||
                rankCache.hasPermission(uuid, "network.debug")
                        && debugger.isDebugger(uuid)) {
            player.sendMessage(utils.getStaffPrefix() + message);
        }
    }

    default void hidePlayerFromAll(Player toHide, ISpeedRunContext speedRunContext) {
        Plugin plugin = speedRunContext.getPlugin();
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            viewer.hidePlayer(plugin, toHide);
        }
    }

    default String getMinecraftPrefix() {
        return "";
    }

    default String getMinecraftStaffPrefix() {
        return "";
    }



    default String getServerID() {
        return "";
    }

    void hidePlayerFromAll(Player toHide);

    default Player getAllPlayers() {
        for (Player ap : Bukkit.getOnlinePlayers()) {
            if (ap != null) {
                return ap;
            } else {
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


    void playDramaticBoom(Player player);

    default void cancelBukkitTask(BukkitTask task) {
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

    default void sendDebugMessage(Player player, String message){

    }

    default boolean isDebugger(String name){
        return false;
    }
}

