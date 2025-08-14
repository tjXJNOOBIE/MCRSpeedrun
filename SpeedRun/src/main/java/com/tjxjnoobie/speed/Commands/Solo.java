package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.internal.utils.Utils;
import com.tjxjnoobie.speed.managers.GameManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class Solo implements CommandExecutor {

    private final Utils utils;
    private final GameManager gameManager;
    public Solo(Utils utils, GameManager gameManager) {
        this.utils = utils;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int length = args.length;
        Player player = (Player) sender;
        Location location = player.getLocation();
        boolean canSolo = gameManager.canSolo();
        if(length > 0){
            player.sendMessage(utils.prefix+"Usage: /solo");
        }else if(canSolo){
            player.sendMessage(utils.prefix+"Starting game solo");
            player.playSound(location, Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f,1.0f);
            try {
                gameManager.startGame();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            player.sendMessage(utils.prefix+"Can't start game solo!");
        }

        return false;
    }
}
