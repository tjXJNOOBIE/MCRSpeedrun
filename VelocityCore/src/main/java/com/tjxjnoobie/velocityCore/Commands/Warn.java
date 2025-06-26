package com.tjxjnoobie.velocityCore.Commands;

import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.API.managers.PlayerProfile;
import com.tjxjnoobie.API.utils.ProxyUtils;
import com.tjxjnoobie.API.velocity.managers.PunishManager;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Warn implements SimpleCommand {

    private final GlobalContext globalContext;
    private final ProxyServer proxyServer;
    private static final String PUNISHMENT_TYPE_WARNS = "WARNS";
    private static final String PUNISHMENT_STATUS_WARNED = "WARNED";

    public Warn(GlobalContext globalContext, ProxyServer proxyServer) {
        this.globalContext = globalContext;
        this.proxyServer = proxyServer;
    }

    @Override
    public void execute(Invocation invocation) {
        RankCache rankCache = globalContext.getRankCache();
        ProxyUtils proxyUtils = globalContext.getProxyUtils();
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
                handleWarn(sender, targetPlayer, targetName, reason);
            }else{
                sender.sendMessage(proxyUtils.withPrefix("No permission."));
            }
        } else if (source instanceof ConsoleCommandSource) {
            handleWarn(console, targetPlayer, targetName, reason);
        }
    }

    private void handleWarn(CommandSource source, Optional<Player> targetPlayer, String targetName, String reason) {
        UUID targetUUID = retrieveUUID(targetPlayer, targetName, source);
        if (targetUUID == null) {
            return;
        }
        warnPlayer(source, targetUUID, targetName, reason);
    }

    private UUID retrieveUUID(Optional<Player> targetPlayer, String targetName, CommandSource source) {
        final String PUNISH_TABLE = "punish";
        PlayerProfile playerProfile = globalContext.getPlayerProfile();
        ProxyUtils proxyUtils = globalContext.getProxyUtils();
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
            } catch (SQLException e) {
                source.sendMessage(proxyUtils.withStaffPrefix("An error occurred processing this command."));
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "SQL Exception while retrieving UUID for " + targetName, e);
            } catch (IllegalArgumentException e) {
                source.sendMessage(proxyUtils.withStaffPrefix("Invalid UUID format for player: " + targetName));
            }
        }
        return targetUUID;
    }

    private void warnPlayer(CommandSource source, UUID targetUUID, String targetName, String reason) {
        PunishManager punishManager = globalContext.getPunishManager();
        ProxyUtils proxyUtils = globalContext.getProxyUtils();
        Instant warnStart = Instant.now();
        String senderName = (source instanceof Player) ? ((Player) source).getUsername() : "Console";

        try {
            int warns = punishManager.getPunishmentNumber(PUNISHMENT_TYPE_WARNS,targetUUID,targetName);
            punishManager.incrementPunishLogCount("punishLog",targetUUID,PUNISHMENT_TYPE_WARNS);
            punishManager.setTimedPunishment(targetUUID, targetName, PUNISHMENT_TYPE_WARNS, Timestamp.from(warnStart), null, reason, senderName, "1");
            source.sendMessage(proxyUtils.withStaffPrefix("You have warned &b" + targetName + (reason.isEmpty() ? "" : " &cfor &b" + reason) + ". Total warns: " + warns));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
