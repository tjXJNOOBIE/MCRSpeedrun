package com.tjxjnoobie.proxy.Commands;

import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Unban implements SimpleCommand, IProxyUtils, IRankCache, IPlayerProfile, IPunishManager {

   @Inject private IGlobalContext globalContext;
   @com.google.inject.Inject private ProxyServer proxyServer;



    /**
     * Executes the unban command for a specified player.
     * <p>
     * This method processes the unban command by verifying the command source
     * and arguments. It checks if the command source has the necessary permissions
     * and then calls the processUnban method to handle the unbanning logic.
     * If the command is issued from the console, it uses "Console" as the sender name.
     *
     * @param invocation The command invocation containing the source and arguments.
     */
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        int length = args.length;
        if (length != 1) {
            source.sendMessage(withStaffPrefix("Usage: /unban <player>"));
            return;
        }
        String targetName = args[0];

        if (source instanceof Player sender) {
            UUID senderUUID = sender.getUniqueId();
            String senderName = sender.getUsername();
            if (!isStaff(senderUUID) && !hasPermission(senderUUID, "network.unban")) {
                source.sendMessage(withPrefix("No permission."));
                return;
            }
            processUnban(source, targetName, senderName);
        } else if (source instanceof ConsoleCommandSource) {
            processUnban(source, targetName, "Console");
        }
    }
        /**
     * Processes the unban of a player by their username.
     *
     * @param source The command source issuing the unban command.
     * @param targetName The username of the player to be unbanned.
     * @param senderName The name of the sender executing the unban.
     * @throws RuntimeException If a database access error occurs.
     */
    private void processUnban(CommandSource source, String targetName, String senderName) {
        try {
            if (!playerExistsFromUsername(targetName, "punish", "punish")) {
                source.sendMessage(withStaffPrefix(targetName + " does not exist in database"));
                return;
            }
            Instant unbanTime = Instant.now();
            Timestamp unbanTimeTS = Timestamp.from(unbanTime);
            unBanPlayer(source, targetName);
            logPunishmentByUsername(targetName, unbanTimeTS, unbanTimeTS, "UNBANS", senderName, senderName + " unbanned " + targetName);

        } catch (SQLException e) {
            source.sendMessage(withStaffPrefix("An error occurred processing this command."));
            throw new RuntimeException(e);
        }
    }




    /**
     * Unbans a player by their username.
     *
     * @param source The command source issuing the unban command.
     * @param targetName The username of the player to be unbanned.
     * @throws SQLException If a database access error occurs.
     */
    private void unBanPlayer(CommandSource source, String targetName) throws SQLException {
        if (!playerExistsFromUsername(targetName, "punish", "punish")) {
            source.sendMessage(withStaffPrefix("Player does not exist in database"));

        } else {
            setPunishedByUsername(targetName,"BANS",0);
            source.sendMessage(withStaffPrefix("You have unbanned &b" + targetName));
        }

    }
    /**
     * Provides asynchronous suggestions for the command arguments.
     * <p>
     * This method suggests online player names based on the current input.
     * If no arguments are provided, it suggests all online player names.
     * If a partial name is provided, it suggests names that start with the given input.
     *
     * @param invocation the command invocation containing the current arguments
     * @return a CompletableFuture containing a list of suggested player names
     */
    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        String[] args = invocation.arguments();

        // When the user is typing the first argument, suggest online player names.
        if (args.length == 0) {
            proxyServer.getAllPlayers().forEach(player -> suggestions.add(player.getUsername()));
        } else if (args.length == 1) {
            String partial = args[0].toLowerCase();
            proxyServer.getAllPlayers().forEach(player -> {
                String name = player.getUsername();
                if (name.toLowerCase().startsWith(partial)) {
                    suggestions.add(name);
                }
            });
        }
        return CompletableFuture.completedFuture(suggestions);
    }

}
