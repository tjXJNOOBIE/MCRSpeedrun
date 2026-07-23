package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.*;
import org.tavall.dependency.annotations.Inject;
import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddPlayer implements CommandExecutor, IMCUtils {

    @Inject private IGameManager gameManager;
    @Inject IRankCache rankCache;


    @Override
    public boolean onCommand(CommandSender commandSender, Command command,String s, String [] args) {

        int length = args.length;
        Player player = (Player) commandSender;
        UUID uuid = UUID.randomUUID();
        String name = player.getName();
        if(rankCache.getCachedPowerLevel(player.getUniqueId()) >=10000
          || rankCache.hasCachedPermission(player.getUniqueId(), "speedrun.fakeplayer")) {
            if (length == 0) {
                gameManager.addInGame(uuid, name);
                player.sendMessage(getMinecraftStaffInGamePrefix() + "Added to game hash");
            } else {
                player.sendMessage(getMinecraftStaffInGamePrefix() + "Usage: /addplayer");
            }
        }else{
            player.sendMessage(getMinecraftStaffInGamePrefix()+" §cNo permission.");
        }
        return false;
    }
}