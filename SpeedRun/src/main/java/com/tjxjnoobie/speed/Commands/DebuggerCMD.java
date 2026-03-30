package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.IDebugger;
import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
import com.tjxjnoobie.api.interfaces.IRankCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DebuggerCMD implements CommandExecutor, IMCUtils, IDebugger, IRankCache {





    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        boolean isDebugger = isDebugger(uuid);
        int length = args.length;
        if(getCachedPowerLevel(uuid) <= 10000 || hasCachedPermission(uuid,"server.debugger")) {
            if (length == 0 && !isDebugger) {
                setDebugger(uuid, true);
                player.sendMessage(getMinecraftStaffInGamePrefix() + "You are now a server debugger");
                loadDebuggersCache();

            } else if(length==0&&isDebugger){
                getDebuggerHash(uuid).remove(uuid.toString());
                setDebugger(uuid, false);
                player.sendMessage(getMinecraftStaffInGamePrefix() + "You are no longer a server debugger");
                loadDebuggersCache();
            }
            if (length == 1) {
                String debuggerName = args[0];
                Player targetPlayer = Bukkit.getPlayer(debuggerName);

                if (targetPlayer == null) {
                    player.sendMessage(getMinecraftStaffInGamePrefix() + args[0] + " does not exist");
                    return false;
                }
                UUID targetPlayerUUID = targetPlayer.getUniqueId();
                boolean isDebuggerTarget = isDebugger(targetPlayerUUID);

                if (!isDebuggerTarget) {

                    setDebugger(uuid, true);
                    targetPlayer.sendMessage(getMinecraftStaffInGamePrefix() + "You are now a network debugger");
                    player.sendMessage(getMinecraftStaffInGamePrefix()+"Made " + targetPlayer.getName() + " a server debugger");
                    loadDebuggersCache();
                }else{
                    getDebuggerHash(uuid).remove(uuid.toString());
                    setDebugger(uuid,false);
                    player.sendMessage(getMinecraftStaffInGamePrefix()+targetPlayer.getName()+" is no longer a server debugger");
                    loadDebuggersCache();
                }
            }
        }else{
            player.sendMessage(getMinecraftPrefix()+"§cNo permission.");
        }
        return false;
    }
}