package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
import com.tjxjnoobie.api.interfaces.IWorldManager;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Seed implements CommandExecutor, IMCUtils {



    @Inject IWorldManager worldManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        int length = args.length;
        Player player = (Player) commandSender;
        //TODO Add Permissions Check
        if(length <1 && player.isOp()){
            player.sendMessage(getMinecraftStaffInGamePrefix()+"Usage: /loadseed <name> <seed>");
        }
        if(length == 2){
            String worldname = args[0];
            long seed = Long.parseLong(args[1]);
            worldManager.loadWorldFromSeed(worldname,seed);
            player.sendMessage(getMinecraftStaffInGamePrefix()+"Loading world " + worldname+" from seed " +seed);
        }


        return false;
    }
}