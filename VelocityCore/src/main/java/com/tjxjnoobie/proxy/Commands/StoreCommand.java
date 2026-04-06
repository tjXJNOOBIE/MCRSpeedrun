package com.tjxjnoobie.proxy.commands;

import com.tjxjnoobie.api.interfaces.IRank;
import com.tjxjnoobie.proxy.store.PlayerStoreHistoryService;
import com.tjxjnoobie.proxy.store.PlayerStoreSnapshot;
import com.tjxjnoobie.store.integration.dto.OwnershipVerificationResult;
import com.tjxjnoobie.store.integration.dto.PlayerEntitlementView;
import com.tjxjnoobie.store.integration.dto.PlayerOrderView;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

public class StoreCommand implements SimpleCommand {

    private final IRank rankService;
    private final PlayerStoreHistoryService playerStoreHistoryService;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public StoreCommand(IRank rankService, PlayerStoreHistoryService playerStoreHistoryService) {
        this.rankService = rankService;
        this.playerStoreHistoryService = playerStoreHistoryService;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player player)) {
            source.sendMessage(message("<red>This command can only be used in-game.</red>"));
            return;
        }

        if (!playerStoreHistoryService.isEnabled()) {
            player.sendMessage(message("<red>Store integration is not configured on this proxy.</red>"));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length == 0) {
            sendUsage(player);
            return;
        }

        String subcommand = args[0].trim().toLowerCase();
        switch (subcommand) {
            case "verify" -> handleVerify(player, args);
            case "purchases" -> handlePurchases(player);
            default -> sendUsage(player);
        }
    }

    private void handleVerify(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(message("<yellow>Usage:</yellow> <gray>/store verify &lt;code&gt;</gray>"));
            return;
        }

        OwnershipVerificationResult result = playerStoreHistoryService.verifyOwnership(
                player.getUniqueId(),
                player.getUsername(),
                args[1].trim()
        );
        if (result.verified()) {
            player.sendMessage(message("<gold>Store</gold> <gray>Ownership verified. Refresh the website to finish sign-in.</gray>"));
            return;
        }
        player.sendMessage(message("<red>" + escape(result.message()) + "</red>"));
    }

    private void handlePurchases(Player player) {
        PlayerStoreSnapshot snapshot = playerStoreHistoryService.getSnapshot(player.getUniqueId());
        player.sendMessage(message("<gold>Store</gold> <gray>Recent orders for <yellow>" + escape(player.getUsername()) + "</yellow></gray>"));
        try {
            player.sendMessage(message("<gray>Current proxy rank:</gray> <yellow>" + escape(rankService.getRank(player.getUniqueId())) + "</yellow>"));
        } catch (Exception exception) {
            player.sendMessage(message("<dark_gray>Current proxy rank unavailable: " + escape(exception.getMessage()) + "</dark_gray>"));
        }

        List<PlayerOrderView> orders = snapshot.orders();
        if (orders.isEmpty()) {
            player.sendMessage(message("<dark_gray>No paid orders found yet.</dark_gray>"));
        } else {
            orders.stream()
                    .limit(5)
                    .forEach(order -> player.sendMessage(message("<gray>- <yellow>" + escape(order.orderNumber())
                            + "</yellow> <white>" + escape(order.packageName()) + "</white> <dark_gray>("
                            + escape(order.orderState()) + ", $" + order.total() + ")</dark_gray>")));
        }

        List<PlayerEntitlementView> entitlements = snapshot.entitlements();
        player.sendMessage(message("<gold>Store</gold> <gray>Active entitlements</gray>"));
        if (entitlements.isEmpty()) {
            player.sendMessage(message("<dark_gray>No active entitlements found.</dark_gray>"));
            return;
        }

        entitlements.stream()
                .limit(5)
                .forEach(entitlement -> player.sendMessage(message("<gray>- <yellow>" + escape(entitlement.sourcePackage())
                        + "</yellow> <white>" + escape(entitlement.targetValue()) + "</white> <dark_gray>("
                        + escape(entitlement.benefitType()) + ")</dark_gray>")));
    }

    private void sendUsage(Player player) {
        player.sendMessage(message("<gold>Store</gold> <gray>Available commands:</gray>"));
        player.sendMessage(message("<gray>/store verify &lt;code&gt;</gray> <dark_gray>- complete website ownership verification</dark_gray>"));
        player.sendMessage(message("<gray>/store purchases</gray> <dark_gray>- view recent orders and active entitlements</dark_gray>"));
    }

    private Component message(String input) {
        return miniMessage.deserialize(input);
    }

    private String escape(String input) {
        return input == null ? "" : input.replace("<", "").replace(">", "");
    }
}
