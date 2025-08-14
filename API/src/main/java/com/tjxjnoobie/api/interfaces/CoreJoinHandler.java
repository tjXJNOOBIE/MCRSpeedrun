package com.tjxjnoobie.api.interfaces;

import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public interface CoreJoinHandler {


    void onCoreJoin(PlayerJoinEvent e) throws SQLException;
}
