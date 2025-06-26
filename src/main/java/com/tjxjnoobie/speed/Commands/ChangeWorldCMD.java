package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.API.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ChangeWorldCMD implements CommandExecutor {

    private final Utils utils;

    public ChangeWorldCMD(Utils utils) {
        this.utils = utils;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player player = (Player) commandSender;
        if ((commandSender instanceof ConsoleCommandSender)) {
            commandSender.sendMessage(utils.staffPrefix+"Only players can issue this command.");
        }
        if(args.length != 1) {
            player.sendMessage(utils.staffPrefix+"Usage: /changeworld §f<world>");
        } else {
            String worldName = args[0];
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                player.sendMessage("World '§f" + worldName + "'§c does not exist.");
                return true;
            } else {
                Location spawnLocation = world.getSpawnLocation();
                player.teleport(spawnLocation);
                player.sendMessage(utils.staffPrefix+"You have been teleported to world: §f" + world.getName());
            }
        }
        return true;
    }
    }

