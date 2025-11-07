package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Interface for Minecraft utility functions
 */
public interface IMCUtils extends ILocalServerMetaData, IDebugger {


    //TODO: Replace class methods parameters for custom DI system

    //TODO: Make a more concerned context for IMCutils instead of using SpeedRunContext
    default ISpeedRunContext getSpeedRunContext() {
        return null;
    }


    default String getMinecraftPrefix(){
        return "";
    }

    /**
     * Retrieves the unique identifier for the current Minecraft server.
     * This method provides a string representation of the server's ID, which can
     * be used for distinguishing the server in a multi-server environment or for
     * logging and configuration purposes.
     *
     * @return A string representing the unique identifier of the Minecraft server
     */
    default String getMinecraftServerID(){
        return "";
    }

    /**
     * Hides a specified player from all other players in the game. This method ensures that the given player is no longer visible to any other active players, typically used for
     *  privacy or security purposes during gameplay.
     *
     * @param toHide The player entity to hide from all other players
     */
    default void hidePlayerFromAll(Player toHide){

    }

    /**
     * Retrieves a list of all players currently in the game.
     * This method returns a Player object representing the collection of all active players.
     *
     * @return A Player instance containing all current players in the game
     */
    default Player getAllMinecraftPlayers() {
        return null;
    }

    /**
     * Plays a specified sound at a given location for all connected players in the game.
     * The sound is played with the specified volume and pitch settings, ensuring consistent audio experience across all players.
     *
     * @param location The location in the world where the sound should be played
     * @param sound The type of sound to play (e.g., SFX.BLOCK_BREAK, SFX.ENTITY_PLAYER_STEP)
     * @param volume The volume level of the sound, ranging from 0.0 (silent) to 1.0 (max volume)
     * @param pitch The pitch level of the sound, affecting its tone; values below 1.0 make it higher-pitched and above 1.0 lower-pitched
     */
    default void playSoundForAll(Location location, Sound sound, float volume, float pitch) {

    }

    /**
     * Broadcasts a message to all connected players in the game.
     * This method sends the specified message to every active player using the Adventure API, ensuring visibility across the entire server.
     *
     * @param message The text message to be sent to all players
     */
    default void sendMessageToAll(String message) {

    }


    /**
     * Plays a dramatic boom effect for the specified player.
     * This method triggers a visual and auditory effect to simulate a dramatic explosion or boom, typically used for game events such as major milestones or in-game victories.
     *
     * @param player The player to play the dramatic boom effect for
     */
    default void playDramaticBoom(Player player){

    }

    /**
     * Cancels a Bukkit task associated with the provided task object.
     * This method is used to stop an ongoing background task scheduled by Bukkit, preventing further execution of the task's logic.
     *
     * @param task The BukkitTask instance representing the task to be cancelled
     */
    default void cancelBukkitTask(BukkitTask task) {

    }


    /**
     * Broadcast a plain text message to all players using Adventure API
     */
    default void broadcast(Component message) {
        getAllMinecraftPlayers().sendMessage(message);
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


    /**
     * Sends a debug message to the specified player using the Minecraft chat system.
     * This method is intended for internal debugging purposes and is not meant for end-user interaction.
     * The message will be displayed directly in the player's chat interface.
     *
     * @param player The player to whom the debug message should be sent
     * @param message The debug message content to display to the player
     */
    default void sendDebugMessage(Player player, String message){

    }

    /**
     * Checks if a given player name matches a known debugger identifier.
     * This method is used to determine whether a specific player should be considered a debugger based on their name.
     *
     * @param name the name of the player to check for debugger status
     * @return true if the player name is recognized as a debugger, false otherwise
     */
    default boolean isDebugger(String name){
        return false;
    }
}

