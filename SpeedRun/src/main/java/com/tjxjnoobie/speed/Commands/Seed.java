package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IMCUtils;
import com.tjxjnoobie.api.interfaces.IUtils;
import com.tjxjnoobie.api.interfaces.IWorldManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Seed implements CommandExecutor, IMCUtils {


    private final IGlobalContext globalContext;

    public Seed(IGlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        int length = args.length;
        Player player = (Player) commandSender;
        IWorldManager worldManager = globalContext.getWorldManager();
        //TODO Add Permissions Check
        if(length <1 && player.isOp()){
            player.sendMessage(staffPrefix+"Usage: /loadseed <name> <seed>");
        }
        if(length == 2){
            String worldname = args[0];
            long seed = Long.parseLong(args[1]);
            worldManager.loadWorldFromSeed(worldname,seed);
            player.sendMessage(staffPrefix+"Loading world " + worldname+" from seed " +seed);
        }


        return false;
    }
}
