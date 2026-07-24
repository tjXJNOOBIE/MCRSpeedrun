package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.*;
import org.tavall.dependency.annotations.Inject;
import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LoadWorld implements CommandExecutor, IMCUtils, IUtils {


    @Inject
    private IRankCache rankCache;
    @Inject
    private IWorldManager worldManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        int length = args.length;
        String cmdName = command.getName();
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();

        if (rankCache.getCachedPowerLevel(uuid) >= 10000||
           rankCache.hasCachedPermission(uuid,"server.command.world")) {
            if (length <= 1) {
                player.sendMessage(getMinecraftPrefix() + "Usage: /world <load/unload> <worldname> [true/false]");
                return false;
            }
            String action = args[0].toLowerCase();

            if (action.equalsIgnoreCase("load") && length == 2) {
                String worldName = args[1];
                worldManager.loadWorld(worldName);
            }
            if (action.equalsIgnoreCase("unload") && length == 2) {
                boolean action2 = parseBoolean(args[2]);
                String worldName = args[1];
                worldManager.unloadWorld(worldName, action2);
                return false;
            }


        }else{
            player.sendMessage(getMinecraftPrefix()+"§cNo Permission.");
        }
        return true;
    }

    }