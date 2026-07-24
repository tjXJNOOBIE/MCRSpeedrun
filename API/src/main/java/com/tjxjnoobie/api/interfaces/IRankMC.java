package com.tjxjnoobie.api.interfaces;

import org.bukkit.entity.Player;

/**
 * Interface for Minecraft rank management
 */
public interface IRankMC extends org.tavall.dependency.IDependencyInjectableInterface {
    


    void setDisplayName(Player player);
}
