package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddPlayer implements CommandExecutor, IUtils {

    private final IGlobalContext globalContext;
    private final ISpeedRunContext speedRunContext;
    public AddPlayer(IGlobalContext globalContext, ISpeedRunContext speedRunContext) {
        this.globalContext = globalContext;

        this.speedRunContext = speedRunContext;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command,String s, String [] args) {
        IGameManager gameManager = speedRunContext.getGameManager(); ;
        IRankCache rankCache = globalContext.getRankCache();
        int length = args.length;
        Player player = (Player) commandSender;
        UUID uuid = UUID.randomUUID();
        String name = player.getName();
        if(rankCache.getPowerLevel(player.getUniqueId()) >=10000
          || rankCache.hasPermission(player.getUniqueId(), "speedrun.fakeplayer")) {
            if (length == 0) {
                gameManager.addInGame(uuid, name);
                player.sendMessage(getStaffPrefix() + "Added to game hash");
            } else {
                player.sendMessage(getPrefix() + "Usage: /addplayer");
            }
        }else{
            player.sendMessage(getPrefix()+" §cNo permission.");
        }
        return false;
    }
}
