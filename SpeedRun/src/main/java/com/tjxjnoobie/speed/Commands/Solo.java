package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.speed.managers.GameManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class Solo implements CommandExecutor, IMCUtils {

    @Inject private GameManager gameManager;


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int length = args.length;
        Player player = (Player) sender;
        Location location = player.getLocation();
        boolean canSolo = gameManager.canSolo();
        if(length > 0){
            player.sendMessage(getMinecraftPrefix()+"Usage: /solo");
        }else if(canSolo){
            player.sendMessage(getMinecraftPrefix()+"Starting game solo");
            player.playSound(location, Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f,1.0f);
            try {
                gameManager.startGame();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            player.sendMessage(getMinecraftPrefix()+"Can't start game solo!");
        }

        return false;
    }
}