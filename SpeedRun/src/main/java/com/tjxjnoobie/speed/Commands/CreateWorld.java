package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IUtils;
import com.tjxjnoobie.api.interfaces.IWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateWorld implements CommandExecutor, IUtils {

    private final IGlobalContext globalContext;

    public CreateWorld(IGlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        IWorldManager worldManager = globalContext.getWorldManager();
        Player player = (Player) commandSender;
        World.Environment environment;

        try {
            environment = World.Environment.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            player.sendMessage(staffPrefix+"§fInvalid world type§e! Use §cNORMAL§f, §cNETHER§f,§c §for§c THE_END§f.");
            return true;
        }
        if(args.length != 2 && player.isOp()){
            player.sendMessage(staffPrefix+ "Usage: /createworld <name> <type>");

        }else if(args.length == 2){
            String worldName = args[0];
            player.sendMessage(staffPrefix+"Creating world §f" + args[1] + "§c with name §f" +args[0]);
            worldManager.createWorld(worldName, environment);
            World world = Bukkit.getWorld(args[1]);
            player.teleport(world.getSpawnLocation());
        }
        return false;
    }
}
