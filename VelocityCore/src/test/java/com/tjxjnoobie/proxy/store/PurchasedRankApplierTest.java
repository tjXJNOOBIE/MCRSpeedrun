package com.tjxjnoobie.proxy.store;

import com.tjxjnoobie.api.interfaces.IRank;
import com.tjxjnoobie.api.interfaces.IRankCache;
import com.tjxjnoobie.api.managers.PlayerProfile;
import com.tjxjnoobie.store.integration.dto.PendingFulfillmentJobView;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PurchasedRankApplierTest {

    @Test
    void grantsRankWhenCurrentRankDiffers() throws Exception {
        IRank rank = mock(IRank.class);
        IRankCache cache = mock(IRankCache.class);
        PlayerProfile profile = mock(PlayerProfile.class);
        ProxyServer proxyServer = mock(ProxyServer.class);
        Player player = mock(Player.class);
        UUID uuid = UUID.randomUUID();
        when(rank.getRank(uuid)).thenReturn("Member");
        when(proxyServer.getPlayer(uuid)).thenReturn(Optional.of(player));

        PurchasedRankApplier applier = new PurchasedRankApplier(rank, cache, profile, proxyServer, "Member");
        FulfillmentOutcome outcome = applier.apply(job(uuid, "GrantBot01", "GRANT", "God"));

        verify(profile).createProfile(uuid, "GrantBot01");
        verify(rank).setRank(uuid, "God");
        verify(cache).refreshRankCache(uuid);
        verify(player).sendMessage(org.mockito.ArgumentMatchers.any());
        assertEquals("God", outcome.appliedValue());
    }

    @Test
    void skipsGrantWhenRankAlreadyMatches() throws Exception {
        IRank rank = mock(IRank.class);
        IRankCache cache = mock(IRankCache.class);
        PlayerProfile profile = mock(PlayerProfile.class);
        ProxyServer proxyServer = mock(ProxyServer.class);
        UUID uuid = UUID.randomUUID();
        when(rank.getRank(uuid)).thenReturn("God");
        when(proxyServer.getPlayer(uuid)).thenReturn(Optional.empty());

        PurchasedRankApplier applier = new PurchasedRankApplier(rank, cache, profile, proxyServer, "Member");
        FulfillmentOutcome outcome = applier.apply(job(uuid, "GrantBot02", "GRANT", "God"));

        verify(rank, never()).setRank(uuid, "God");
        verify(cache, never()).refreshRankCache(uuid);
        assertTrue(outcome.message().contains("already granted"));
    }

    @Test
    void revokesRankBackToFallback() throws Exception {
        IRank rank = mock(IRank.class);
        IRankCache cache = mock(IRankCache.class);
        PlayerProfile profile = mock(PlayerProfile.class);
        ProxyServer proxyServer = mock(ProxyServer.class);
        UUID uuid = UUID.randomUUID();
        when(rank.getRank(uuid)).thenReturn("God");
        when(proxyServer.getPlayer(uuid)).thenReturn(Optional.empty());

        PurchasedRankApplier applier = new PurchasedRankApplier(rank, cache, profile, proxyServer, "Member");
        FulfillmentOutcome outcome = applier.apply(job(uuid, "GrantBot03", "REVOKE", "God"));

        verify(rank).revokeRank(uuid, "Member");
        verify(cache).refreshRankCache(uuid);
        assertEquals("Member", outcome.appliedValue());
    }

    private PendingFulfillmentJobView job(UUID uuid, String username, String operation, String targetValue) {
        return new PendingFulfillmentJobView(
                1L,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                operation,
                uuid.toString(),
                username,
                "RANK",
                "VELOCITY",
                "rank",
                targetValue
        );
    }
}
