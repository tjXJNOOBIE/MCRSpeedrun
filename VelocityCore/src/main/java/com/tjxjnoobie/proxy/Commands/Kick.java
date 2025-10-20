package com.tjxjnoobie.proxy.Commands;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IProxyUtils;
import com.tjxjnoobie.api.interfaces.IPunishManager;
import com.tjxjnoobie.api.interfaces.IRankCache;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class Kick implements SimpleCommand {
    @Inject IPunishManager punishManager;
    private final IGlobalContext globalContext;
    private final ProxyServer proxyServer;

    public Kick(IGlobalContext globalContext, ProxyServer proxyServer) {
        this.globalContext = globalContext;
        this.proxyServer = proxyServer;
    }
    /**
     * Executes the kick command, allowing a player or console to kick another player from the server.
     *
     * @param invocation The command invocation containing the source and arguments.
     * 
     * Validates the command source and arguments, checks permissions, and processes the kick action.
     * If the source is a player, it checks if they have the necessary permissions. If the source is
     * the console, it directly processes the command. Notifies the source of any errors or usage
     * instructions.
     */
    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        int length = args.length;
        IRankCache rankCache = globalContext.getRankCache();
        IProxyUtils proxyUtils = globalContext.getProxyUtils();
        CommandSource source = invocation.source();

        if (source instanceof Player sender) {
            String senderName = sender.getUsername();
            UUID uuid = sender.getUniqueId();
            if (rankCache.isStaff(uuid) || rankCache.hasPermission(uuid, "network.kick")) {
                if (length < 1 || length > 3) {
                    sender.sendMessage(proxyUtils.withStaffPrefix("&cUsage: /kick <player> [reason]"));
                    return;
                }
                String targetName = args[0];
                String reason = (length >= 2) ? String.join(" ", Arrays.copyOfRange(args, 1, length)) : null;
                Optional<Player> targetPlayer = proxyServer.getPlayer(targetName);

                handleKick(sender, targetPlayer, targetName, senderName, reason);
            } else {
                sender.sendMessage(proxyUtils.withPrefix("&cNo permission."));
            }
        } else if (source instanceof ConsoleCommandSource) {
            // Sender is Console
            if (length < 1 || length > 3) {
                source.sendMessage(proxyUtils.withStaffPrefix("&cUsage: /kick <player> [reason]"));
                return;
            }
            String targetName = args[0];
            Optional<Player> targetPlayer = proxyServer.getPlayer(targetName);
            ConsoleCommandSource console = proxyServer.getConsoleCommandSource();
            String reason = (length >= 2) ? String.join(" ", Arrays.copyOfRange(args, 1, length)) : null;
            handleKick(console, targetPlayer, targetName, "Console", reason);
        }
    }
    
    
        /**
     * Handles the process of kicking a player from the server.
     *
     * @param source The command source initiating the kick.
     * @param targetPlayer An optional containing the player to be kicked, if present.
     * @param targetName The name of the player to be kicked.
     * @param senderName The name of the sender executing the kick command.
     * @param reason The reason for the kick, if provided.
     * 
     * Attempts to kick the specified player and logs the action. If the player is not online,
     * notifies the source. Handles any SQL exceptions that occur during the process.
     */
    private void handleKick(CommandSource source, Optional<Player> targetPlayer, String targetName, String senderName, String reason) {
        IProxyUtils proxyUtils = globalContext.getProxyUtils();
        if (targetPlayer.isPresent()) {
            UUID targetUUID = targetPlayer.get().getUniqueId();
            try {
                kickPlayer(source, targetPlayer, targetUUID, targetName, senderName, reason);
                if (source instanceof Player player) {
                    player.sendMessage(constructKickMessage(proxyUtils, targetName, reason));
                }
            } catch (SQLException e) {
                source.sendMessage(proxyUtils.withStaffPrefix("An error occurred while trying to process this command"));
                System.out.println("Failed to kick player due to SQL error " + e.getMessage());
            }
        } else {
            source.sendMessage(proxyUtils.withStaffPrefix(targetName + " is offline"));
        }
    }

    private Component constructKickMessage(IProxyUtils proxyUtils, String targetName, String reason) {
        String message = "Kicked &b" + targetName + (reason != null && !reason.isEmpty() ? " &cfor&b " + reason : "");
        return proxyUtils.withStaffPrefix(message);
    }

    /**
     * Kicks a player from the server and logs the action.
     *
     * @param source The command source initiating the kick.
     * @param player An optional containing the player to be kicked, if present.
     * @param targetUUID The UUID of the player to be kicked.
     * @param targetName The name of the player to be kicked.
     * @param senderName The name of the sender executing the kick command.
     * @param reason The reason for the kick, if provided.
     * @throws SQLException If an SQL error occurs during the logging process.
     *
     * Disconnects the specified player from the server, sends a notification to the source,
     * increments the kick count, and logs the punishment details.
     */
    public void kickPlayer(CommandSource source, Optional<Player> player, UUID targetUUID, String targetName, String senderName, String reason) throws SQLException {
        IProxyUtils proxyUtils = globalContext.getProxyUtils();
        Instant kickTimeNow = Instant.now();
        Timestamp kickTime = Timestamp.from(kickTimeNow);
        int kicks = punishManager.getPunishmentNumber("KICKS", targetUUID, targetName);
        
        if (player.isPresent()) {
            try {
                player.get().disconnect(constructKickMessage(senderName, reason));
                source.sendMessage(constructKickMessageWithReason(targetName, reason));
                punishManager.setPunishNumber("KICKS", kicks + 1, targetUUID);
                punishManager.logPunishment(targetUUID, targetName, kickTime, null, "KICKS", senderName, (reason.isEmpty() ? null : reason));
                System.out.println("Successfully kicked player: " + targetName);
            } catch (Exception e) {
                System.out.println("Failed to kick player: " + targetName + ". Error: " + e.getMessage());

            }
        } else {
            source.sendMessage(proxyUtils.withStaffPrefix(targetName + " is offline"));
        }
    }

    private Component constructKickMessage(String senderName, String reason) {
        IProxyUtils proxyUtils = globalContext.getProxyUtils();
        return proxyUtils.colorzie("&4You were kicked by &b" + senderName + "\n " +
                (reason.isEmpty() ? null :"&eReason&7: &b " + reason));
    }

    private Component constructKickMessageWithReason(String targetName, String reason) {
        IProxyUtils proxyUtils = globalContext.getProxyUtils();
        return proxyUtils.withStaffPrefix("You have kicked &b" + targetName + " &cfor &b" + (reason.isEmpty() ? null : reason));
    }
}
