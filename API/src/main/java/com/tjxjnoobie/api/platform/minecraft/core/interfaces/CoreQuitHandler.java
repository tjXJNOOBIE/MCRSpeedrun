package com.tjxjnoobie.api.platform.minecraft.core.interfaces;

import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public interface CoreQuitHandler {

    void onCoreQuit(PlayerQuitEvent e) throws SQLException;
}