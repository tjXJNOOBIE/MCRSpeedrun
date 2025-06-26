package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.managers.Debugger;
import com.tjxjnoobie.API.managers.RetentionManager;
import com.tjxjnoobie.speed.cache.LocationCache;
import com.tjxjnoobie.API.minecraft.managers.SoundManager;
import com.tjxjnoobie.API.minecraft.managers.WorldManager;
import com.tjxjnoobie.interfaces.InterfaceManager;
import com.tjxjnoobie.speed.managers.GameManager;
import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.API.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.UUID;

public class Debug implements CommandExecutor {

    private final GameState gameState;
    private final GameManager gameManager;
    private final Utils utils;
    private final LocationCache locationCache;
    private final WorldManager worldManager;
    private final RankCache rankCache;
    private final SoundManager sound;
    private final RetentionManager retentionManager;
    private final Debugger debugger;
    private final Plugin plugin;

    public Debug(GameState gameState, GameManager gameManager, Utils utils, LocationCache locationCache, WorldManager worldManager, RankCache rankCache, SoundManager sound, RetentionManager retentionManager, Debugger debugger, Plugin plugin) {
        this.gameState = gameState;
        this.gameManager = gameManager;
        this.utils = utils;
        this.locationCache = locationCache;
        this.worldManager = worldManager;
        this.rankCache = rankCache;
        this.sound = sound;
        this.retentionManager = retentionManager;
        this.debugger = debugger;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();
        String worldName = player.getWorld().getName();
        if(args.length== 0 && player.isOp()){
            player.sendMessage("Rank: "+ rankCache.getRank(uuid));
            player.sendMessage("Power Level: " + rankCache.getPowerLevel(uuid));
            player.sendMessage("ServerID: "+utils.getServerID());
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
            player.sendMessage(utils.staffPrefix+"Usage: /debug");
        }
        return false;
    }
}




