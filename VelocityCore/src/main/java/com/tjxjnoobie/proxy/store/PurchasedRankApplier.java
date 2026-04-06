package com.tjxjnoobie.proxy.store;

import com.tjxjnoobie.api.interfaces.IRank;
import com.tjxjnoobie.api.interfaces.IRankCache;
import com.tjxjnoobie.api.managers.PlayerProfile;
import com.tjxjnoobie.store.integration.dto.PendingFulfillmentJobView;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

public class PurchasedRankApplier {

    private final IRank rankService;
    private final IRankCache rankCache;
    private final PlayerProfile playerProfile;
    private final ProxyServer proxyServer;
    private final String fallbackRank;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public PurchasedRankApplier(IRank rankService,
                                IRankCache rankCache,
                                PlayerProfile playerProfile,
                                ProxyServer proxyServer,
                                String fallbackRank) {
        this.rankService = rankService;
        this.rankCache = rankCache;
        this.playerProfile = playerProfile;
        this.proxyServer = proxyServer;
        this.fallbackRank = fallbackRank == null || fallbackRank.isBlank() ? "Member" : fallbackRank;
    }

    public FulfillmentOutcome apply(PendingFulfillmentJobView job) {
        if (!supports(job)) {
            throw new StoreBackendException("Unsupported fulfillment target: "
                    + job.benefitType() + "/" + job.targetSystem(), false);
        }

        UUID playerUuid = parseUuid(job.playerUuid());
        String username = resolveUsername(job);

        try {
            playerProfile.createProfile(playerUuid, username);
            String operation = normalize(job.operation());
            if ("REVOKE".equals(operation)) {
                return revokeRank(playerUuid, username);
            }
            return grantRank(playerUuid, username, job);
        } catch (SQLException exception) {
            throw new StoreBackendException("Rank fulfillment failed: " + exception.getMessage(), true, exception);
        }
    }

    private boolean supports(PendingFulfillmentJobView job) {
        String benefitType = normalize(job.benefitType());
        String targetSystem = normalize(job.targetSystem());
        return ("RANK".equals(benefitType) || "PERMISSION_GROUP".equals(benefitType))
                && (targetSystem.isBlank() || "VELOCITY".equals(targetSystem) || "NETWORK".equals(targetSystem));
    }

    private FulfillmentOutcome grantRank(UUID playerUuid, String username, PendingFulfillmentJobView job) throws SQLException {
        String targetRank = extractTargetRank(job);
        String currentRank = safeRankLookup(playerUuid);
        if (targetRank.equalsIgnoreCase(currentRank)) {
            return new FulfillmentOutcome(targetRank, "Rank already granted.");
        }

        rankService.setRank(playerUuid, targetRank);
        rankCache.refreshRankCache(playerUuid);
        notifyPlayer(playerUuid, "<gold>Store</gold> <gray>Applied your purchased rank:</gray> <yellow>"
                + targetRank + "</yellow>");
        return new FulfillmentOutcome(targetRank, "Granted rank " + targetRank + " to " + username + ".");
    }

    private FulfillmentOutcome revokeRank(UUID playerUuid, String username) throws SQLException {
        String currentRank = safeRankLookup(playerUuid);
        if (fallbackRank.equalsIgnoreCase(currentRank)) {
            return new FulfillmentOutcome(fallbackRank, "Rank already reverted.");
        }

        rankService.revokeRank(playerUuid, fallbackRank);
        rankCache.refreshRankCache(playerUuid);
        notifyPlayer(playerUuid, "<gold>Store</gold> <gray>Your purchased rank was revoked. Current rank:</gray> <yellow>"
                + fallbackRank + "</yellow>");
        return new FulfillmentOutcome(fallbackRank, "Reverted rank for " + username + " to " + fallbackRank + ".");
    }

    private UUID parseUuid(String rawUuid) {
        try {
            return UUID.fromString(rawUuid);
        } catch (IllegalArgumentException exception) {
            throw new StoreBackendException("Invalid player UUID in fulfillment payload: " + rawUuid, false, exception);
        }
    }

    private String resolveUsername(PendingFulfillmentJobView job) {
        if (job.playerUsername() != null && !job.playerUsername().isBlank()) {
            return job.playerUsername().trim();
        }
        return "player-" + job.playerUuid().substring(0, Math.min(8, job.playerUuid().length()));
    }

    private String extractTargetRank(PendingFulfillmentJobView job) {
        if (job.targetValue() != null && !job.targetValue().isBlank()) {
            return job.targetValue().trim();
        }
        if (job.targetKey() != null && !job.targetKey().isBlank()) {
            return job.targetKey().trim();
        }
        throw new StoreBackendException("No target rank was supplied for fulfillment job " + job.jobId(), false);
    }

    private String safeRankLookup(UUID playerUuid) throws SQLException {
        String currentRank = rankService.getRank(playerUuid);
        return currentRank == null ? "" : currentRank.trim();
    }

    private void notifyPlayer(UUID playerUuid, String message) {
        proxyServer.getPlayer(playerUuid)
                .ifPresent(player -> player.sendMessage(miniMessage.deserialize(message)));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }
}
