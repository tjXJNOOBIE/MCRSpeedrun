package com.tjxjnoobie.proxy.Events;

import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.minecraft.velocity.logs.PunishLog;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class VelocityLoginEvent {

    private final IGlobalContext globalContext;

    public VelocityLoginEvent(IGlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    
    @Subscribe
    public void onLogin(LoginEvent e) throws SQLException {
        Player player = e.getPlayer();
        UUID uuid = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getUsername();
        IPunishManager punishManager = globalContext.getPunishManager();
        IPlayerProfile playerProfile = globalContext.getPlayerProfile();
        IProxyUtils proxyUtils = globalContext.getProxyUtils();
        IRankCache rankCache = globalContext.getRankCache();
        boolean isBanned = punishManager.isPunished(uuid,name,"BANNED");
        if(isBanned){
            PunishLog punishLog = punishManager.getActivePunishment(uuid,name,"BANS");
            Timestamp banStart = punishLog.getStartDate();
            Timestamp banEnd = punishLog.getEndDate();
            String formattedDuration;
            if (banEnd == null) {
                formattedDuration = "Permanent";
            } else {
                // Calculate the remaining ban duration (banEnd - now)
                Duration remainingDuration = Duration.between(Instant.now(), banEnd.toInstant());
                // If the ban has expired (shouldn't happen here), you can set it accordingly
                if (remainingDuration.isNegative() || remainingDuration.isZero()) {
                    e.setResult(ResultedEvent.ComponentResult.allowed());

                    punishManager.setPunished(uuid,"BANS",0);
                    System.out.println(name+" ban has expired, allowing join");
                    return;
                } else {
                    formattedDuration = proxyUtils.formatDuration(remainingDuration);
                }
            }
            String sender = punishLog.getSender();;
            String reason = punishLog.getReason();
            player.disconnect(proxyUtils.colorzie("&4You were banned by &b&l"+ sender +"\n " +
                    "&eDuration&7: &c"+ formattedDuration +"\n" +
                    "&eReason&7: &b " + reason+"\n" +
                    "&cYou may appeal on Discord @ " +proxyUtils.getDiscordString()+ " or on the website @ " + proxyUtils.getWebsiteString()));
            return;
        }
        rankCache.addRankCache(uuid);
        playerProfile.createProfile(uuid,name);

    }
}
