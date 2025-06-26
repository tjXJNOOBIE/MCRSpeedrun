package com.tjxjnoobie.core.Events;

import com.tjxjnoobie.API.cache.LobbyStatsCache;
import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.managers.Debugger;
import com.tjxjnoobie.API.managers.GameType;
import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.API.minecraft.RankMC;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.enums.GameTypeEnum;
import com.tjxjnoobie.interfaces.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.UUID;

public abstract class CoreJoin implements Listener, CoreJoinHandler, IUtils {


    private final GlobalContext globalContext;
    private IGameState iGameState;


    public CoreJoin(GlobalContext globalContext) {
        this.globalContext = globalContext;


    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCoreJoin(PlayerJoinEvent e) throws SQLException {
        IRankCache rankCache = globalContext.getRankCache();
        IRankMC rankMC = globalContext.getRankMC();
        IDebugger debugger = globalContext.getDebugger();
        IGameType gameType = globalContext.getGameType();
        IUtils utils = globalContext.getUtils();
        ILobbyStatsCache lobbyStatsCache = globalContext.getLobbyStatsCache();
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
            player.sendMessage(getStaffPrefix()+"You currently server debugging");
        }


    }
    }
