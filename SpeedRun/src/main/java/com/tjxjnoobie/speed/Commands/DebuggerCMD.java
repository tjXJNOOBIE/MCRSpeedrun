package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.IDebugger;
import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IRankCache;
import com.tjxjnoobie.api.interfaces.IUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DebuggerCMD implements CommandExecutor, IUtils {



    private final IGlobalContext globalContext;


    public DebuggerCMD(IGlobalContext globalContext) {
        this.globalContext = globalContext;


    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        IDebugger debugger = globalContext.getDebugger();
        IRankCache rankCache = globalContext.getRankCache();
        boolean isDebugger = debugger.isDebugger(uuid);
        int length = args.length;
        if(rankCache.getPowerLevel(uuid) <= 10000 || rankCache.hasPermission(uuid,"server.debugger")) {
            if (length == 0 && !isDebugger) {
                debugger.setDebugger(uuid, true);
                player.sendMessage(staffPrefix + "You are now a server debugger");
                debugger.loadDebuggersCache();

            } else if(length==0&&isDebugger){
                debugger.getDebuggerHash(uuid).remove(uuid.toString());
                debugger.setDebugger(uuid, false);
                player.sendMessage(staffPrefix + "You are no longer a server debugger");
                debugger.loadDebuggersCache();
            }
            if (length == 1) {
                String debuggerName = args[0];
                Player targetPlayer = Bukkit.getPlayer(debuggerName);

                if (targetPlayer == null) {
                    player.sendMessage(staffPrefix + args[0] + " does not exist");
                    return false;
                }
                UUID targetPlayerUUID = targetPlayer.getUniqueId();
                boolean isDebuggerTarget = debugger.isDebugger(targetPlayerUUID);

                if (!isDebuggerTarget) {

                    debugger.setDebugger(uuid, true);
                    targetPlayer.sendMessage(staffPrefix + "You are now a network debugger");
                    player.sendMessage(staffPrefix+"Made " + targetPlayer.getName() + " a server debugger");
                    debugger.loadDebuggersCache();
                }else{
                    debugger.getDebuggerHash(uuid).remove(uuid.toString());
                    debugger.setDebugger(uuid,false);
                    player.sendMessage(staffPrefix+targetPlayer.getName()+" is no longer a server debugger");
                    debugger.loadDebuggersCache();
                }
            }
        }else{
            player.sendMessage(prefix+"§cNo permission.");
        }
        return false;
    }
}
