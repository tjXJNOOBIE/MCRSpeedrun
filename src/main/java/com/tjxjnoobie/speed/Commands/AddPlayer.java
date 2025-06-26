package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.speed.managers.GameManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddPlayer implements CommandExecutor {
    private final Utils utils;
    private final GameManager gameManager;
    private final RankCache rankCache;

    public AddPlayer(Utils utils, GameManager gameManager, RankCache rankCache) {
        this.utils = utils;
        this.gameManager = gameManager;
        this.rankCache = rankCache;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command,String s, String [] args) {
        int length = args.length;
        Player player = (Player) commandSender;
        UUID uuid = UUID.randomUUID();
        String name = player.getName();
        if(rankCache.getPowerLevel(player.getUniqueId()) >=10000
          || rankCache.hasPermission(player.getUniqueId(), "speedrun.fakeplayer")) {
            if (length == 0) {
                gameManager.addInGame(uuid, name);
                player.sendMessage(utils.staffPrefix + "Added to game hash");
            } else {
                player.sendMessage(utils.staffPrefix + "Usage: /addplayer");
            }
        }else{
            player.sendMessage(utils.prefix+" §cNo permission.");
        }
        return false;
    }
}
