package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DebuggerCMD implements CommandExecutor, IMCUtils, IDebugger {



   @Inject
   private IGlobalContext globalContext;




    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        IRankCache rankCache = globalContext.getRankCache();
        boolean isDebugger = isDebugger(uuid);
        int length = args.length;
        if(rankCache.getPowerLevel(uuid) <= 10000 || rankCache.hasPermission(uuid,"server.debugger")) {
            if (length == 0 && !isDebugger) {
                setDebugger(uuid, true);
                player.sendMessage(getMinecraftStaffPrefix() + "You are now a server debugger");
                loadDebuggersCache();

            } else if(length==0&&isDebugger){
                getDebuggerHash(uuid).remove(uuid.toString());
                setDebugger(uuid, false);
                player.sendMessage(getMinecraftStaffPrefix() + "You are no longer a server debugger");
                loadDebuggersCache();
            }
            if (length == 1) {
                String debuggerName = args[0];
                Player targetPlayer = Bukkit.getPlayer(debuggerName);

                if (targetPlayer == null) {
                    player.sendMessage(getMinecraftStaffPrefix() + args[0] + " does not exist");
                    return false;
                }
                UUID targetPlayerUUID = targetPlayer.getUniqueId();
                boolean isDebuggerTarget = isDebugger(targetPlayerUUID);

                if (!isDebuggerTarget) {

                    setDebugger(uuid, true);
                    targetPlayer.sendMessage(getMinecraftStaffPrefix() + "You are now a network debugger");
                    player.sendMessage(getMinecraftStaffPrefix()+"Made " + targetPlayer.getName() + " a server debugger");
                    loadDebuggersCache();
                }else{
                    getDebuggerHash(uuid).remove(uuid.toString());
                    setDebugger(uuid,false);
                    player.sendMessage(getMinecraftStaffPrefix()+targetPlayer.getName()+" is no longer a server debugger");
                    loadDebuggersCache();
                }
            }
        }else{
            player.sendMessage(getMinecraftPrefix()+"§cNo permission.");
        }
        return false;
    }
}
