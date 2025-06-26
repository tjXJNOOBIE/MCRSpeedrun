package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.API.minecraft.managers.WorldManager;
import com.tjxjnoobie.API.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Seed implements CommandExecutor {

    private final Utils utils;
    private final WorldManager worldManager;

    public Seed(Utils utils, WorldManager worldManager) {
        this.utils = utils;
        this.worldManager = worldManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        int length = args.length;
        Player player = (Player) commandSender;
        if(length <1 && player.isOp()){
            player.sendMessage(utils.staffPrefix+"Usage: /loadseed <name> <seed>");
        }
        if(length == 2){
            String worldname = args[0];
            long seed = Long.parseLong(args[1]);
            worldManager.loadWorldFromSeed(worldname,seed);
            player.sendMessage(utils.staffPrefix+"Loading world " + worldname+" from seed " +seed);
        }


        return false;
    }
}
