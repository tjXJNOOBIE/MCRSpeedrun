package com.tjxjnoobie.api.interfaces;

import org.bukkit.entity.Player;

/**
 * Interface for sound management operations
 */
public interface ISoundManager {


    void playVictoryJingle(Player player);

    void playEpicEnderDragonDeath(Player player);

    void playCustomDragonDeath(Player player);

    void playVictoryWithDragonDeath(Player killer);
}