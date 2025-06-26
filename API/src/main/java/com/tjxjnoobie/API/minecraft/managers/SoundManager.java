package com.tjxjnoobie.API.minecraft.managers;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SoundManager {

    public final Plugin plugin;

    public SoundManager(Plugin plugin) {
        this.plugin = plugin;
    }


    public void playVictoryJingle(Player player) {
        // 1. Start with a loud, uplifting level-up sound to kick off the celebration
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 1.2f);

        // 2. Quickly follow up with a second "power-up" style jingle (adds more energy)
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f), 10L); // 0.5 sec later

        // 3. Play an energetic bell chime for extra grandeur
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1.2f, 1.5f), 20L); // 1 sec later

        // 4. Layer in another success sound to make it feel like a final crescendo
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.6f, 1.3f), 30L); // 1.5 sec later

        // 5. Finish with a bold, echoing bell chime to make the moment feel complete
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1.5f, 1.0f), 40L); // 2 sec later

        // 6. (Optional) Add a subtle, mystical portal hum as a fade-out effect
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.8f, 0.5f), 50L); // 2.5 sec later
    }
    public void playEpicEnderDragonDeath(Player player) {
        // 1. Start with an eerie hum to create a sense of anticipation and dread (a low-pitched, distant sound)
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 0.3f);

        // 2. Suddenly, a deep growl of the dragon (in the distance) to intensify the atmosphere
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.2f, 0.7f), 10L);

        // 3. The first crack of thunder as the dragon's health reaches its final moments
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 1.0f), 20L);

        // 4. A powerful sonic boom as the dragon begins to crumble (quick but impactful)
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f), 30L);

        // 5. The sound of dragon wings flapping as the dragon falls, as if it’s still struggling
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.6f, 1.5f), 35L);

        // 6. A powerful portal sound, signifying the opening of the gateway and the transition to the next phase
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 1.0f, 1.3f), 40L);

        // 7. A victory celebration (horns, fanfare, or cheering) as the dragon finally dies
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f), 50L);

        // 8. Finally, a mysterious "echo" or "reverberating" sound like the dragon’s spirit leaving the world
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1.2f, 1.0f), 60L);
    }
    public void playCustomDragonDeath(Player player) {
        // 1. Initial low roar at t=0 (0 ticks)
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.7f);

        // 2. Second, slightly louder roar at t=15 (0.75 sec)
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.2f, 0.9f), 15L);

        // 3. A third roar ("howl") to build tension at t=30 (1.5 sec)
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.3f, 1.0f), 30L);

        // 4. Add the sound of wing flaps to simulate the dragon’s struggle at t=45 (2.25 sec)
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.2f), 45L);

        // 5. Finally, play a heavy thud sound (using BLOCK_ANVIL_LAND) at t=60 (3 sec)
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 0.8f), 60L);
    }
    public void playVictoryWithDragonDeath(Player player) {
        // 1. Start with an uplifting victory sound (celebratory level-up)
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.2f, 1.2f); // Level up sound to signify success

        // 2. Follow up with a powerful, uplifting theme (another level-up)
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 1.5f), 10L); // Victory bell-like sound

        // 3. Add a soft background sound, like the faint wind of wings flapping
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5f, 1.0f), 20L); // Dragon wings subtly in the background

        // 4. Play a distant roar/howl hinting at the dragon's fading power
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 0.7f), 30L); // Dragon growl, distant but present

        // 5. Add a louder success sound for increased energy
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.3f, 1.4f), 40L); // Another success sound, but louder

        // 6. Burst of energy with a controlled explosion effect (victory flair)
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f), 50L); // A controlled explosion

        // 7. Subtle portal activation sound, symbolizing transition
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 1.0f, 1.3f), 60L); // Portal opening in the background

        // 8. End with a triumphant victory jingle (final celebratory note)
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.6f, 1.0f), 70L); // Big victory sound
    }
}
