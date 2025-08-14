package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IRankCache;
import com.tjxjnoobie.api.interfaces.IUtils;
import com.tjxjnoobie.api.interfaces.IWorldManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LoadWorld implements CommandExecutor, IUtils {

    private final IGlobalContext globalContext;

    public LoadWorld(IGlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        int length = args.length;
        String cmdName = command.getName();
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();
        IRankCache rankCache = globalContext.getRankCache();
        IWorldManager worldManager = globalContext.getWorldManager();
        if (rankCache.getPowerLevel(uuid) >= 10000||
           rankCache.hasPermission(uuid,"server.command.world")) {
            if (length <= 1) {
                player.sendMessage(staffPrefix + "Usage: /world <load/unload> <worldname> [true/false]");
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
            player.sendMessage(prefix+"§cNo Permission.");
        }
        return true;
    }

    }






