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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Warn implements SimpleCommand {

    @Inject private IGlobalContext globalContext;
    @com.google.inject.Inject private ProxyServer proxyServer;
    private static final String PUNISHMENT_TYPE_WARNS = "WARNS";
    private static final String PUNISHMENT_STATUS_WARNED = "WARNED";



    @Override
    public void execute(Invocation invocation) {
        IRankCache rankCache = globalContext.getRankCache();
        IProxyUtils proxyUtils = globalContext.getProxyUtils();
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        int length = args.length;
        final String DEFAULT_REASON = "Not provided";

        if (length < 1 || length > 2) {
            source.sendMessage(proxyUtils.withStaffPrefix("&cUsage: /warn <player> [reason]"));
            return;
        }
        String targetName = args[0];
        ConsoleCommandSource console = proxyServer.getConsoleCommandSource();
        Optional<Player> targetPlayer = proxyServer.getPlayer(targetName);

        String reason = (args.length == 2) ? args[1] : DEFAULT_REASON;

        if (source instanceof Player sender) {
            UUID senderUUID = sender.getUniqueId();
            if (rankCache.isStaff(senderUUID) || rankCache.hasPermission(senderUUID, "network.warn")) {
                try {
                    handleWarn(sender, targetPlayer, targetName, reason);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{
                sender.sendMessage(proxyUtils.withPrefix("No permission."));
            }
        } else if (source instanceof ConsoleCommandSource) {
            try {
                handleWarn(console, targetPlayer, targetName, reason);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleWarn(CommandSource source, Optional<Player> targetPlayer, String targetName, String reason) throws SQLException {
        UUID targetUUID = retrieveUUID(targetPlayer, targetName, source);
        if (targetUUID == null) {
            return;
        }
        warnPlayer(source, targetUUID, targetName, reason);
    }

    private UUID retrieveUUID(Optional<Player> targetPlayer, String targetName, CommandSource source) {
        final String PUNISH_TABLE = "punish";
        IPlayerProfile playerProfile = globalContext.getPlayerProfile();
        IProxyUtils proxyUtils = globalContext.getProxyUtils();
        UUID targetUUID = null;
        if (targetPlayer.isPresent()) {
            targetUUID = targetPlayer.get().getUniqueId();
        } else {
            try {
                String uuidString = playerProfile.getUUIDFromUsername(PUNISH_TABLE, targetName, PUNISH_TABLE);
                if (uuidString != null && !uuidString.isEmpty()) {
                    targetUUID = UUID.fromString(uuidString);
                } else {
                    source.sendMessage(proxyUtils.withStaffPrefix("Could not retrieve UUID for " + targetName));
                }
            } catch (IllegalArgumentException e) {
                source.sendMessage(proxyUtils.withStaffPrefix("Invalid UUID format for player: " + targetName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return targetUUID;
    }

    private void warnPlayer(CommandSource source, UUID targetUUID, String targetName, String reason) throws SQLException {
        IPunishManager punishManager = globalContext.getPunishManager();
        IProxyUtils proxyUtils = globalContext.getProxyUtils();
        Instant warnStart = Instant.now();
        String senderName = (source instanceof Player) ? ((Player) source).getUsername() : "Console";

        int warns = punishManager.getPunishmentNumber(PUNISHMENT_TYPE_WARNS,targetUUID,targetName);
        punishManager.incrementPunishLogCount("punishLog",targetUUID,PUNISHMENT_TYPE_WARNS);
        punishManager.setTimedPunishment(targetUUID, targetName, PUNISHMENT_TYPE_WARNS, Timestamp.from(warnStart), null, reason, senderName, "1");
        source.sendMessage(proxyUtils.withStaffPrefix("You have warned &b" + targetName + (reason.isEmpty() ? "" : " &cfor &b" + reason) + ". Total warns: " + warns));
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        String[] args = invocation.arguments();

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
        } else if (args.length == 2) {
            String partial = args[1].toLowerCase();
            List<String> reasons = List.of("Cheating", "Exploiting", "Duping", "Threats", "Harassment", "Spamming");
            reasons.forEach(reason -> {
                if (reason.toLowerCase().startsWith(partial)) {
                    suggestions.add(reason);
                }
            });
        }

        return CompletableFuture.completedFuture(suggestions);
    }
}
