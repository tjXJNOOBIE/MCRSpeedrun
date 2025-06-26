package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.minecraft.managers.WorldManager;
import com.tjxjnoobie.API.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LoadWorld implements CommandExecutor {

    private final Utils utils;
    private final WorldManager worldManager;
    private RankCache rankCache;

    public LoadWorld(Utils utils, WorldManager worldManager) {
        this.utils = utils;
        this.worldManager = worldManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        int length = args.length;
        String cmdName = command.getName();
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();
        if (rankCache.getPowerLevel(uuid) >= 10000||
           rankCache.hasPermission(uuid,"server.command.world")) {
            if (length <= 1) {
                player.sendMessage(utils.staffPrefix + "Usage: /world <load/unload> <worldname> [true/false]");
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
            player.sendMessage(utils.prefix+"§cNo Permission.");
        }
        return true;
    }
    private boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            return false;
        }
    }
        }





