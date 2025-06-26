package com.tjxjnoobie.core.Events;

import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.managers.GameType;
import com.tjxjnoobie.API.managers.RetentionManager;
import com.tjxjnoobie.enums.GameTypeEnum;
import com.tjxjnoobie.interfaces.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.UUID;


public class CoreQuit implements Listener, CoreQuitHandler {


    private final IRetentionManager iRetentionManager;
    private final IRankCache iRankCache;


    public CoreQuit(IRetentionManager iRetentionManager, IRankCache iRankCache) {
        this.iRetentionManager = iRetentionManager;
        this.iRankCache = iRankCache;
    }

    
    @EventHandler
    public void onCoreQuit(PlayerQuitEvent e) throws SQLException {

        UUID uuid = e.getPlayer().getUniqueId();
        iRetentionManager.updateLastActiveDate(uuid);
        iRankCache.removeRankCache(uuid);

    }


}
