package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IRankCache;
import com.tjxjnoobie.api.interfaces.IUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChangeWorldCMD implements CommandExecutor, IUtils {

    private final IGlobalContext globalContext;
    public ChangeWorldCMD(IGlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();
        IRankCache rankCache = globalContext.getRankCache();
        if(!rankCache.hasPermission(uuid,"core.world.change")||
                rankCache.isAdmin(uuid)){
            player.sendMessage(prefix+"No permission.");
            return false;
        }
        if ((commandSender instanceof ConsoleCommandSender)) {
            commandSender.sendMessage(staffPrefix+"Only players can issue this command.");
            return false;
        }
        if(args.length != 1) {
            player.sendMessage(staffPrefix+"Usage: /changeworld §f<world>");
        } else {
            String worldName = args[0];
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                player.sendMessage("World '§f" + worldName + "'§c does not exist.");
                return false;
            } else {
                Location spawnLocation = world.getSpawnLocation();
                player.teleport(spawnLocation);
                player.sendMessage(staffPrefix+"You have been teleported to world: §f" + world.getName());
            }
        }
        return true;
    }
    }

