package com.tjxjnoobie.proxy.commands;

import com.tjxjnoobie.api.interfaces.IRank;
import com.tjxjnoobie.proxy.store.PlayerStoreHistoryService;
import com.tjxjnoobie.proxy.store.PlayerStoreSnapshot;
import com.tjxjnoobie.store.integration.dto.PlayerEntitlementView;
import com.tjxjnoobie.store.integration.dto.PlayerOrderView;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StoreCommandTest {

    @Test
    void purchasesCommandShowsRankOrdersAndEntitlements() throws SQLException {
        IRank rank = mock(IRank.class);
        PlayerStoreHistoryService historyService = mock(PlayerStoreHistoryService.class);
        Player player = mock(Player.class);
        UUID uuid = UUID.randomUUID();
        when(historyService.isEnabled()).thenReturn(true);
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getUsername()).thenReturn("StoreBot01");
        when(rank.getRank(uuid)).thenReturn("God");
        when(historyService.getSnapshot(uuid)).thenReturn(new PlayerStoreSnapshot(
                List.of(new PlayerOrderView(1L, "NOV-ORDER", "God Rank", "PAID", new BigDecimal("149.99"), "2026-04-04T00:00:00Z")),
                List.of(new PlayerEntitlementView(1L, uuid.toString(), "God Rank", "RANK", "rank", "God", "ACTIVE", "2026-04-04T00:00:00Z", null))
        ));

        StoreCommand command = new StoreCommand(rank, historyService);
        SimpleCommand.Invocation invocation = new SimpleCommand.Invocation() {
            @Override
            public CommandSource source() {
                return player;
            }

            @Override
            public String alias() {
                return "store";
            }

            @Override
            public String[] arguments() {
                return new String[]{"purchases"};
            }
        };

        command.execute(invocation);

        ArgumentCaptor<Component> messages = ArgumentCaptor.forClass(Component.class);
        verify(player, org.mockito.Mockito.atLeast(3)).sendMessage(messages.capture());
        String combined = messages.getAllValues().stream()
                .map(PlainTextComponentSerializer.plainText()::serialize)
                .reduce("", (left, right) -> left + "\n" + right);

        assertTrue(combined.contains("Current proxy rank: God"));
        assertTrue(combined.contains("NOV-ORDER"));
        assertTrue(combined.contains("God Rank"));
    }
}
