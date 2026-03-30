package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Vote implements CommandExecutor, IMCUtils {

    @Inject private IVoting voting;
    @Inject private IGameState gameState;


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        int length = args.length;
        int choice = 0;
        if(length != 1){
        player.sendMessage(getMinecraftPrefix()+"Usage: /vote <number>");
        }else{
            if(voting.getHasVotedHash().contains(uuid)){
                player.sendMessage(getMinecraftPrefix()+"You have already voted!");
                return false;
            }
            if(gameState.getCurrentState() != GameStateEnum.LOBBY){
                player.sendMessage(getMinecraftPrefix()+"You may only vote in lobby");
                return false;
            }
            try {
                choice = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(getMinecraftPrefix()+"Please Enter a number!");

            }
            List<String> gamemodes = new ArrayList<>(voting.getGameModes().keySet());

            if (choice < 1 || choice > gamemodes.size()) {
                player.sendMessage(getMinecraftPrefix()+ "Invalid choice. Please select a number between 1 and " + gamemodes.size() + ".");
                return false;
            }
            String selectedGamemode = gamemodes.get(choice - 1);
            voting.vote(player,selectedGamemode);
            voting.getHasVotedHash().add(uuid);
            player.sendMessage("You voted for " + selectedGamemode + "!");
        }


        return false;
    }
}