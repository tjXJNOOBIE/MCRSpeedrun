package com.tjxjnoobie.api.interfaces;

import org.bukkit.entity.Player;

/**
 * Interface for sound management operations
 */
public interface ISoundManager extends org.tavall.dependency.IDependencyInjectableInterface {


    default void playVictoryJingle(Player player){

    }

    default void playEpicEnderDragonDeath(Player player){

    }

    default void playCustomDragonDeath(Player player){

    }

    default void playVictoryWithDragonDeath(Player killer){

    }
}
