package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.interfaces.InterfaceManager;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import com.tjxjnoobie.speed.managers.Voting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Vote implements CommandExecutor {

    private final SpeedRunContext speedRunContext;


    public Vote(SpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        Voting voting = speedRunContext.getVoting();
        Utils utils = speedRunContext.getUtils();
        GameState gameState = speedRunContext.getGameState();
        int length = args.length;
        int choice = 0;
        if(length != 1){
        player.sendMessage(utils.prefix+"Usage: /vote <number>");
        }else{
            if(voting.getHasVotedHash().contains(uuid)){
                player.sendMessage(utils.prefix+"You have already voted!");
                return false;
            }
            if(gameState.getCurrentState() != GameStateEnum.LOBBY){
                player.sendMessage(utils.prefix+"You may only vote in lobby");
                return false;
            }
            try {
                choice = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(utils.prefix+"Please Enter a number!");

            }
            List<String> gamemodes = new ArrayList<>(voting.getGameModes().keySet());

            if (choice < 1 || choice > gamemodes.size()) {
                player.sendMessage(utils.prefix+ "Invalid choice. Please select a number between 1 and " + gamemodes.size() + ".");
                return false;
            }
            String selectedGamemode = gamemodes.get(choice - 1);
            try {
                voting.vote(player,selectedGamemode);
                voting.getHasVotedHash().add(uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            player.sendMessage("You voted for " + selectedGamemode + "!");
        }


        return false;
    }
}
