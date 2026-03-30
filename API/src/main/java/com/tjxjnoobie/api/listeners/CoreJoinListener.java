package com.tjxjnoobie.api.listeners;

import com.tjxjnoobie.api.platform.minecraft.core.interfaces.CoreJoinHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class CoreJoinListener implements Listener, CoreJoinHandler {


    @Override
    @EventHandler
    public void onCoreJoin(PlayerJoinEvent e) throws SQLException {
//        CoreJoinHandler coreJoinHandler = InterfaceManager.getCoreJoinHandler();
//        coreJoinHandler.onCoreJoin(e);

    }
}