package com.tjxjnoobie.proxy.Commands;

import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class Ban implements SimpleCommand, IUtils {

    @Inject private IGlobalContext globalContext;
    @com.google.inject.Inject private ProxyServer proxyServer;


    /**
     * Executes the ban command, which bans a player from the server.
     *
     * @param invocation The command invocation containing the source and arguments.
     *
     * The command requires at least one argument (player name) and can optionally
     * include a duration and a reason. If the source is a player, they must have
     * staff status or the "network.ban" permission to execute the command.
     *
     * Usage: /ban <player> [duration] [reason]
     */
    @Override
    public void execute(Invocation invocation)  {
        IRankCache rankCache = globalContext.getRankCache();
        Component staffPrefix = globalContext.getProxyUtils().withStaffPrefix("&cUsage: /ban <player> [duration] [reason]");
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        int length = args.length;
        final String DEFAULT_DURATION = "Permanent";
        final String DEFAULT_REASON = "Not provided";

        if (length < 1 || length > 3) {
            source.sendMessage(staffPrefix);
            return;
        }
        String targetName = args[0];
        ConsoleCommandSource console = proxyServer.getConsoleCommandSource();
        Optional<Player> targetPlayer = proxyServer.getPlayer(targetName);

        String durationStr = (args.length >= 2) ? args[1] : DEFAULT_DURATION;
        String reason = (args.length == 3) ? args[2] : DEFAULT_REASON;

        if(source instanceof Player sender) {
            UUID senderUUID = sender.getUniqueId();
            if (rankCache.isStaff(senderUUID) ||
                       rankCache.hasPermission(senderUUID, "network.ban")) {
                try {
                    handleBan(sender, targetPlayer, targetName, reason, durationStr);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if(source instanceof ConsoleCommandSource) {
            try {
                handleBan(console, targetPlayer, targetName, reason, durationStr);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * Handles the banning process for a player. Retrieves the player's UUID and
     * initiates the ban if the UUID is successfully obtained. The ban can be
     * permanent or for a specified duration.
     *
     * @param source The CommandSource issuing the ban command.
     * @param targetPlayer An Optional containing the target Player if they are online.
     * @param targetName The name of the target player.
     * @param reason The reason for the ban.
     * @param durationStr The duration of the ban, or "Permanent" if the ban is indefinite.
     */
    private void handleBan(CommandSource source, Optional<Player> targetPlayer, String targetName, String reason, String durationStr) throws SQLException {
        UUID targetUUID = retrieveUUID(targetPlayer, targetName, source);
        if (targetUUID == null) {
            return;
        }
        banPlayer(source, targetUUID, targetName, reason, (durationStr.toLowerCase().startsWith("p")) ? "Permanent" : durationStr);
    }
    /**
     * Retrieves the UUID of a target player. If the player is online, their UUID is obtained directly.
     * Otherwise, attempts to retrieve the UUID from the database using the player's name.
     * Sends a message to the command source if the UUID cannot be retrieved or if an error occurs.
     *
     * @param targetPlayer An Optional containing the target Player if they are online.
     * @param targetName The name of the target player.
     * @param source The CommandSource to send messages to.
     * @return The UUID of the target player, or null if it cannot be retrieved.
     * @throws RuntimeException If an SQL exception occurs during UUID retrieval.
     */
    private UUID retrieveUUID(Optional<Player> targetPlayer, String targetName, CommandSource source) throws SQLException {
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
            }
        }
        return targetUUID;
    }
    /**
     * Bans a player from the server by disconnecting them and recording the ban details.
     * If the player is online, they are immediately disconnected with a ban message.
     * If the player is offline, the ban is recorded for future enforcement.
     * The ban can be permanent or for a specified duration.
     *
     * @param source The CommandSource issuing the ban command.
     * @param targetUUID The UUID of the player to be banned.
     * @param targetName The name of the player to be banned.
     * @param reason The reason for the ban.
     * @param durationStr The duration of the ban, or "Permanent" if indefinite.
     * @throws RuntimeException If an SQL exception occurs during the ban process.
     */
    private void banPlayer(CommandSource source, UUID targetUUID, String targetName, String reason, String durationStr) throws SQLException {
        final String PUNISHMENT_TYPE_BANS = "BANS";
        IPunishManager punishManager = globalContext.getPunishManager();
        IProxyUtils proxyUtils = globalContext.getProxyUtils();
        Duration banDuration = determineBanDuration(durationStr, source, proxyUtils);
        IPlayerProfile playerProfile = globalContext.getPlayerProfile();
        String senderName = (source instanceof Player) ? ((Player) source).getUsername() : "Console";

        String formattedDuration = banDuration.isZero() ? "Permanent" : proxyUtils.formatDuration(banDuration);
        Optional<Player> optionalPlayer = proxyServer.getPlayer(targetName);
        Instant banStart = Instant.now();
        Instant banEnd = banDuration.isZero() ? null : banStart.plus(banDuration);

        if (!punishManager.isPunished(targetUUID, targetName, "BANNED")) {
            if (optionalPlayer.isPresent()) {
                int bans = punishManager.getPunishmentNumber(PUNISHMENT_TYPE_BANS, targetUUID, targetName);
                optionalPlayer.get().disconnect(proxyUtils.colorzie("&4You were banned by &b" + senderName + "\n " +
                        "&eDuration&7: &c" + formattedDuration + "\n" +
                        "&eReason&7: &b " + reason + "\n" +
                        "&cYou may appeal on Discord @ " + proxyUtils.getDiscordString() + " or on the website @ " + proxyUtils.getWebsiteString()));
                punishManager.setTimedPunishment(targetUUID, targetName, PUNISHMENT_TYPE_BANS, Timestamp.from(banStart), banEnd != null ? Timestamp.from(banEnd) : null, reason, senderName, "1");
                punishManager.setPunishNumber(PUNISHMENT_TYPE_BANS, bans + 1, targetUUID);
                source.sendMessage(proxyUtils.withStaffPrefix("You have banned &b" + targetName + "&c for &b" + formattedDuration + (reason.isEmpty() ? "" : " &cfor &b" + reason)));
            } else {
                if (playerProfile.playerExistsFromUsername(targetName, "punish", "punish")) {
                    source.sendMessage(proxyUtils.withStaffPrefix(targetName + " does not exist in database"));
                } else {
                    int bans = punishManager.getPunishmentNumber(PUNISHMENT_TYPE_BANS, targetUUID, targetName);
                    source.sendMessage(proxyUtils.withStaffPrefix(targetName + " is offline, attempting ban..."));
                    punishManager.setTimedPunishment(targetUUID, targetName, PUNISHMENT_TYPE_BANS, Timestamp.from(banStart), banEnd != null ? Timestamp.from(banEnd) : null, (reason.isEmpty() ? "No reason provided" : reason), senderName, "1");
                    punishManager.setPunishNumber(PUNISHMENT_TYPE_BANS, bans + 1, targetUUID);
                    source.sendMessage(proxyUtils.withStaffPrefix("You have banned &b" + targetName + "&c for &b" + formattedDuration + (reason.isEmpty() ? "" : " &cfor &b" + reason)));
                }
            }
        } else {
            source.sendMessage(proxyUtils.withStaffPrefix(targetName + " is already banned."));
        }
    }
    /**
     * Determines the ban duration based on the provided string representation.
     * If the duration string is null, empty, or indicates a permanent ban, it returns a zero duration(Perm Ban).
     * Otherwise, it attempts to parse the duration using the provided ProxyUtils instance.
     * If parsing fails, it sends an error message to the command source and returns null.
     *
     * @param durationStr the string representation of the ban duration.
     * @param source the command source to send error messages to.
     * @param proxyUtils the ProxyUtils instance used for parsing the duration.
     * @return the parsed Duration or zero for permanent bans, or null if parsing fails.
     */
    private Duration determineBanDuration(String durationStr, CommandSource source, IProxyUtils proxyUtils) {
        if (durationStr == null || durationStr.trim().isEmpty() || durationStr.equalsIgnoreCase("permanent")) {
            return Duration.ZERO;
        } else {
            try {
                return proxyUtils.parseDuration(durationStr);
            } catch (IllegalArgumentException e) {
                source.sendMessage(proxyUtils.withStaffPrefix("Invalid ban duration format. Use formats like 1m, 1h, 1d, 1m, 1yr, p, permanent."));
                return null;
            }
        }
    }
    /**
     * Provides asynchronous suggestions for the ban command based on the current
     * input arguments. Suggests online player names when typing the first argument,
     * possible duration formats for the second argument, and common ban reasons
     * for the third argument.
     *
     * @param invocation The command invocation containing the source and arguments.
     * @return A CompletableFuture containing a list of suggestion strings.
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
        } else if (args.length == 2) {
            String partial = args[1].toLowerCase();
            List<String> durations = List.of("1m", "1h", "1d", "1w", "1mo", "1yr", "p");
            durations.forEach(duration -> {
                if (duration.toLowerCase().startsWith(partial)) {
                    suggestions.add(duration);
                }
            });
        } else if (args.length == 3) {
            String partial = args[2].toLowerCase();
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
