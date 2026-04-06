package com.tjxjnoobie.api.interfaces;

import org.bukkit.entity.Player;

/**
 * Interface for Minecraft rank management
 */
public interface IRankMC extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {
    


    void setDisplayName(Player player);
}
