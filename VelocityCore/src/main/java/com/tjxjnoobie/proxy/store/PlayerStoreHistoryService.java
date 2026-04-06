package com.tjxjnoobie.proxy.store;

import com.tjxjnoobie.store.integration.dto.OwnershipVerificationResult;

import java.util.UUID;

public class PlayerStoreHistoryService {

    private final StoreBackendClient backendClient;

    public PlayerStoreHistoryService(StoreBackendClient backendClient) {
        this.backendClient = backendClient;
    }

    public boolean isEnabled() {
        return backendClient.isEnabled();
    }

    public OwnershipVerificationResult verifyOwnership(UUID playerUuid, String playerUsername, String code) {
        return backendClient.verifyOwnership(code, playerUuid, playerUsername);
    }

    public PlayerStoreSnapshot getSnapshot(UUID playerUuid) {
        return new PlayerStoreSnapshot(
                backendClient.getOrders(playerUuid),
                backendClient.getEntitlements(playerUuid)
        );
    }
}
