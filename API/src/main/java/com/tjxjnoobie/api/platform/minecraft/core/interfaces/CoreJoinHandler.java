package com.tjxjnoobie.api.platform.minecraft.core.interfaces;

import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public interface CoreJoinHandler {


    void onCoreJoin(PlayerJoinEvent e) throws SQLException;
}