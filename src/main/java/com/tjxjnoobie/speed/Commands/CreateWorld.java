package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.API.minecraft.managers.WorldManager;
import com.tjxjnoobie.API.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateWorld implements CommandExecutor {

    private final WorldManager worldManager;
    private final Utils utils;

    public CreateWorld(WorldManager worldManager, Utils utils) {
        this.worldManager = worldManager;
        this.utils = utils;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        World.Environment environment;

        try {
            environment = World.Environment.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            player.sendMessage(utils.staffPrefix+"Invalid world type! Use NORMAL, NETHER, or THE_END.");
            return true;
        }
        if(args.length != 2 && player.isOp()){
            player.sendMessage(utils.staffPrefix + "Usage: /createworld <name> <type>");

        }else if(args.length == 2){
            String worldName = args[0];
            player.sendMessage(utils.staffPrefix+"Creating world " + args[1] + " with name §f" +args[0]);
            worldManager.createWorld(worldName, environment);
            World world = Bukkit.getWorld(args[1]);
            player.teleport(world.getSpawnLocation());
        }
        return false;
    }
}
