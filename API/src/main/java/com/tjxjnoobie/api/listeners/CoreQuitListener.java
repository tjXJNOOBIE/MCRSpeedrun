package com.tjxjnoobie.api.listeners;

import com.tjxjnoobie.api.interfaces.CoreQuitHandler;
import com.tjxjnoobie.api.interfaces.InterfaceManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class CoreQuitListener implements CoreQuitHandler, Listener {



    @EventHandler
    public void onCoreQuit(PlayerQuitEvent e) throws SQLException {
    CoreQuitHandler coreQuitHandler = InterfaceManager.getCoreQuitHandler();
    coreQuitHandler.onCoreQuit(e);
    }
}
