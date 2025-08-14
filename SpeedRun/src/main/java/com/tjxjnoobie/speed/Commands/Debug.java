package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class Debug implements CommandExecutor, IUtils {


    private final IGlobalContext globalContext;
    private final ISpeedRunContext speedRunContext;
    public Debug(IGlobalContext globalContext, ISpeedRunContext speedRunContext) {
        this.globalContext = globalContext;
        this.speedRunContext = speedRunContext;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        IGameState gameState = globalContext.getGameState();
        IGameManager gameManager = speedRunContext.getGameManager();
        IUtils utils = globalContext.getUtils();
        ILocationCache locationCache = speedRunContext.getLocationCache();
        IWorldManager worldManager = globalContext.getWorldManager();
        IRankCache rankCache = globalContext.getRankCache();
        IRetentionManager retentionManager = globalContext.getRetentionManager();
        IDebugger debugger = globalContext.getDebugger();
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();
        String worldName = player.getWorld().getName();
        if(args.length== 0 && player.isOp()){
            player.sendMessage("Rank: "+ rankCache.getRank(uuid));
            player.sendMessage("Power Level: " + rankCache.getPowerLevel(uuid));
            player.sendMessage("ServerID: "+getServerID(globalContext));
            player.sendMessage("GameID: "+utils.getGameID());
            player.sendMessage("Playing: "+gameManager.getPlaying());
            player.sendMessage("Watching: "+gameManager.getWatching());
            player.sendMessage("Gamestate: "+ gameState.getCurrentState());
            player.sendMessage("Spawn: "+locationCache.getSpawn());
            player.sendMessage("Spawn World: " +worldManager.getSpawnWorld());
            player.sendMessage("Permissions: " + rankCache.getPermissions(uuid));
            player.sendMessage(InterfaceManager.getBlockPlaceHandler().toString());
            player.sendMessage("Network debuggers: "+ debugger.getDebuggers());
            try {
                retentionManager.updateRetentionRatings();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            retentionManager.getCachedRetentionDataSorted();
            retentionManager.calculate30DayRetention();

        }else{
            player.sendMessage(getStaffPrefix()+"Usage: /debug");
        }
        return false;
    }
}




