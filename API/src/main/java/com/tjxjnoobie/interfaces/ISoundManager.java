package com.tjxjnoobie.interfaces;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Interface for sound management operations
 */
public interface ISoundManager {
    
    /**
     * Plays a sound for a specific player
     * @param player The player to play the sound for
     * @param sound The sound to play
     * @param volume The volume level
     * @param pitch The pitch level
     */
    void playSound(Player player, Sound sound, float volume, float pitch);
    
    /**
     * Plays a sound at a specific location
     * @param location The location to play the sound
     * @param sound The sound to play
     * @param volume The volume level
     * @param pitch The pitch level
     */
    void playSoundAtLocation(Location location, Sound sound, float volume, float pitch);
    
    /**
     * Plays a sound for all players
     * @param sound The sound to play
     * @param volume The volume level
     * @param pitch The pitch level
     */
    void playSoundForAll(Sound sound, float volume, float pitch);
    
    /**
     * Plays a sound for all players at a location
     * @param location The location to play the sound
     * @param sound The sound to play
     * @param volume The volume level
     * @param pitch The pitch level
     */
    void playSoundForAllAtLocation(Location location, Sound sound, float volume, float pitch);
    
    /**
     * Plays a custom sound effect
     * @param player The player to play the sound for
     * @param soundName The custom sound name
     * @param volume The volume level
     * @param pitch The pitch level
     */
    void playCustomSound(Player player, String soundName, float volume, float pitch);
    
    /**
     * Stops all sounds for a player
     * @param player The player to stop sounds for
     */
    void stopAllSounds(Player player);
    
    /**
     * Stops a specific sound for a player
     * @param player The player to stop the sound for
     * @param sound The sound to stop
     */
    void stopSound(Player player, Sound sound);
    
    /**
     * Plays a sound sequence
     * @param player The player to play the sequence for
     * @param sounds Array of sounds to play
     * @param delays Array of delays between sounds (in ticks)
     */
    void playSoundSequence(Player player, Sound[] sounds, int[] delays);
    
    /**
     * Plays a dramatic effect sound
     * @param player The player to play the effect for
     */
    void playDramaticEffect(Player player);
    
    /**
     * Plays a victory sound
     * @param player The player to play the sound for
     */
    void playVictorySound(Player player);
    
    /**
     * Plays a defeat sound
     * @param player The player to play the sound for
     */
    void playDefeatSound(Player player);
    
    /**
     * Plays a notification sound
     * @param player The player to play the sound for
     */
    void playNotificationSound(Player player);
    
    /**
     * Plays a warning sound
     * @param player The player to play the sound for
     */
    void playWarningSound(Player player);
    
    /**
     * Sets the master volume for a player
     * @param player The player to set volume for
     * @param volume The master volume (0.0 to 1.0)
     */
    void setMasterVolume(Player player, float volume);
    
    /**
     * Gets the master volume for a player
     * @param player The player to get volume for
     * @return The master volume
     */
    float getMasterVolume(Player player);
    void playVictoryWithDragonDeath(Player killer);
}