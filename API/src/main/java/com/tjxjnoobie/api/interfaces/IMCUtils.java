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
     * Sends a debug message to the specified player using the provided context.
     * This method is intended for internal debugging purposes and may not be visible or accessible to end users.
     * The message will be sent directly to the player via the Minecraft chat system.
     *
     * @param speedRunContext The SpeedRun context containing dependencies such as plugins, game state, and utilities
     * @param player The player to whom the debug message should be sent
     * @param message The debug message content to display to the player
     */

    default void sendDebugMessage(ISpeedRunContext speedRunContext, Player player, String message) {

    }

    /**
     * Hides a specified player from all other players in the game. This method ensures that the given player is no longer visible to any other active players, typically used for
     *  privacy or security purposes during gameplay.
     *
     * @param toHide The player entity to hide from all other players
     * @param speedRunContext The context containing necessary services and dependencies for the game logic; used to access game-specific functionality and ensure proper execution
     *  environment
     */
    default void hidePlayerFromAll(Player toHide, ISpeedRunContext speedRunContext) {

    }

    /**
     * Retrieves the Minecraft prefix used in chat messages or commands.
     *
     * @return The Minecraft prefix string, typically used to denote server-specific or role-based identifiers (e.g., "§6[Admin]").
     */
    default String getMinecraftPrefix() {
        return "";
    }

    /**
     * Retrieves the Minecraft staff prefix used to identify staff members in chat or commands.
     * This method returns a predefined string that represents the official prefix for staff roles within the server's messaging system.
     *
     * @return The Minecraft staff prefix, typically used in chat formatting or player identification
     */
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

    }

    default void sendMessageToAll(String message) {

    }


    void playDramaticBoom(Player player);

    default void cancelBukkitTask(BukkitTask task) {

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

    }

    /**
     * Broadcast a message with a click event
     */
    default void broadcast(Component message, ClickEvent clickEvent) {

    }

    /**
     * Broadcast a message with hover and click events
     */
    default void broadcast(Component message, HoverEvent<?> hoverEvent, ClickEvent clickEvent) {

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

