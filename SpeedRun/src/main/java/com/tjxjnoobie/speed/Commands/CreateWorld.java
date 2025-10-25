package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IMCUtils;
import com.tjxjnoobie.api.interfaces.IWorldManager;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateWorld implements CommandExecutor, IMCUtils {

    @Inject private IGlobalContext globalContext;
    @Inject IWorldManager worldManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        World.Environment environment;

        try {
            environment = World.Environment.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            player.sendMessage(getMinecraftStaffInGamePrefix()+"§fInvalid world type§e! Use §cNORMAL§f, §cNETHER§f,§c §for§c THE_END§f.");
            return true;
        }
        if(args.length != 2 && player.isOp()){
            player.sendMessage(getMinecraftStaffInGamePrefix()+ "Usage: /createworld <name> <type>");

        }else if(args.length == 2){
            String worldName = args[0];
            player.sendMessage(getMinecraftStaffInGamePrefix()+"Creating world §f" + args[1] + "§c with name §f" +args[0]);
            worldManager.createWorld(worldName, environment);
            World world = Bukkit.getWorld(args[1]);
            player.teleport(world.getSpawnLocation());
        }
        return false;
    }
}
