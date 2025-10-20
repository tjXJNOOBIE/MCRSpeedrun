package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class Debug implements CommandExecutor, IUtils, IDebug {


    @Inject private IGlobalContext globalContext;
    @Inject private ISpeedRunContext speedRunContext;
    @Inject private IGameState gameState;
    @Inject private IGameManager gameManager;
    @Inject private ILocationCache locationCache;
    @Inject private IWorldManager worldManager;
    @Inject private IRankCache rankCache;
    @Inject private IRetentionManager retentionManager;
    @Inject private IDebugger debugger;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();
        String worldName = player.getWorld().getName();
        if(args.length== 0 && player.isOp()){
            player.sendMessage("Rank: "+ rankCache.getRank(uuid));
            player.sendMessage("Power Level: " + rankCache.getPowerLevel(uuid));
            player.sendMessage("ServerID: "+getServerID());
            player.sendMessage("GameID: "+getGameID());
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




