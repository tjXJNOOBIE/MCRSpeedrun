package com.tjxjnoobie.core.Events;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.minecraft.core.interfaces.CoreJoinHandler;
import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.UUID;

public class CoreJoin implements Listener, CoreJoinHandler, IMCUtils {


    @Inject private IGameState iGameState;
    @Inject private IRankCache rankCache;
    @Inject private IRankMC rankMC;
    @Inject private IDebugger debugger ;
    @Inject private IGameType gameType;
    @Inject private ILobbyStatsCache lobbyStatsCache;



    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCoreJoin(PlayerJoinEvent e) throws SQLException {

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String uuidString = uuid.toString();
        String name = player.getName();
        GameTypeEnum currentType = gameType.getGameType();
        if(currentType == GameTypeEnum.LOBBY){
            lobbyStatsCache.loadLobbyStats(uuid);
        }
        lobbyStatsCache.loadLobbyStats(uuid);
        rankCache.addRankCache(uuid);
        rankMC.setDisplayName(player);
        if(debugger.isDebuggerSQL(uuid)){
            debugger.setDebuggerHash(uuid,name);
            player.sendMessage(getMinecraftStaffInGamePrefix()+"You currently server debugging");
        }


    }
    }