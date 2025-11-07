package com.tjxjnoobie.proxy.Events;

import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
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

public class VelocityLoginEvent implements IPunishManager, IPlayerProfile, IProxyUtils, IRankCache {

   @Inject private IGlobalContext globalContext;




    @Subscribe
    public void onLogin(LoginEvent e) throws SQLException {
        Player player = e.getPlayer();
        UUID uuid = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getUsername();
        boolean isBanned = isPunished(uuid,name,"BANNED");
        if(isBanned){
            PunishLog punishLog = getActivePunishment(uuid,name,"BANS");
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

                    setPunished(uuid,"BANS",0);
                    System.out.println(name+" ban has expired, allowing join");
                    return;
                } else {
                    formattedDuration = formatDuration(remainingDuration);
                }
            }
            String sender = punishLog.getSender();;
            String reason = punishLog.getReason();
            player.disconnect(colorzie("&4You were banned by &b&l"+ sender +"\n " +
                    "&eDuration&7: &c"+ formattedDuration +"\n" +
                    "&eReason&7: &b " + reason+"\n" +
                    "&cYou may appeal on Discord @ " +getDiscordString()+ " or on the website @ " + getWebsiteString()));
            return;
        }
        addRankCache(uuid);
        createProfile(uuid,name);

    }
}
