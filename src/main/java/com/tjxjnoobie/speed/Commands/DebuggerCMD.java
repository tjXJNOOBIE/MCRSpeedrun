package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.managers.Debugger;
import com.tjxjnoobie.API.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DebuggerCMD implements CommandExecutor {

        private final Debugger debugger;
        private final Utils utils;
        private final RankCache rankCache;

    public DebuggerCMD(Debugger debugger, Utils utils, RankCache rankCache) {
        this.debugger = debugger;
        this.utils = utils;
        this.rankCache = rankCache;

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        boolean isDebugger = debugger.isDebugger(uuid);
        int length = args.length;
        if(rankCache.getPowerLevel(uuid) <= 10000 || rankCache.hasPermission(uuid,"server.debugger")) {
            if (length == 0 && !isDebugger) {
                debugger.setDebugger(uuid, true);
                player.sendMessage(utils.staffPrefix + "You are now a server debugger");
                debugger.loadDebuggersCache();

            } else if(length==0&&isDebugger){
                debugger.getDebuggerHash(uuid).remove(uuid.toString());
                debugger.setDebugger(uuid, false);
                player.sendMessage(utils.staffPrefix + "You are no longer a server debugger");
                debugger.loadDebuggersCache();
            }
            if (length == 1) {
                String debuggerName = args[0];
                Player targetPlayer = Bukkit.getPlayer(debuggerName);

                if (targetPlayer == null) {
                    player.sendMessage(utils.staffPrefix + args[0] + " does not exist");
                    return false;
                }
                UUID targetPlayerUUID = targetPlayer.getUniqueId();
                boolean isDebuggerTarget = debugger.isDebugger(targetPlayerUUID);

                if (!isDebuggerTarget) {

                    debugger.setDebugger(uuid, true);
                    targetPlayer.sendMessage(utils.prefix + "You are now a network debugger");
                    player.sendMessage(utils.staffPrefix+"Made " + targetPlayer.getName() + " a server debugger");
                    debugger.loadDebuggersCache();
                }else{
                    debugger.getDebuggerHash(uuid).remove(uuid.toString());
                    debugger.setDebugger(uuid,false);
                    player.sendMessage(utils.staffPrefix+targetPlayer.getName()+" is no longer a server debugger");
                    debugger.loadDebuggersCache();
                }
            }
        }else{
            player.sendMessage(utils.prefix+"§cNo permission.");
        }
        return false;
    }
}
