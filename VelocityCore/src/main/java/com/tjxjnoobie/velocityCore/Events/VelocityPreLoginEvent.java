package com.tjxjnoobie.velocityCore.Events;

import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.API.managers.PlayerProfile;
import com.tjxjnoobie.API.utils.ProxyUtils;
import com.tjxjnoobie.API.velocity.managers.PunishManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;

import java.sql.SQLException;

public class VelocityPreLoginEvent {
    private final GlobalContext globalContext;

    public VelocityPreLoginEvent(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Subscribe
    public void onPreLogin(PreLoginEvent e) throws SQLException {
        PunishManager punishManager = globalContext.getPunishManager();
        PlayerProfile playerProfile = globalContext.getPlayerProfile();
        ProxyUtils proxyUtils = globalContext.getProxyUtils();
        RankCache rankCache = globalContext.getRankCache();
        String name = e.getUsername();




    }



}
