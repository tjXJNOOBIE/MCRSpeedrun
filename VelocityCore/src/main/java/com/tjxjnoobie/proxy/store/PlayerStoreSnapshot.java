package com.tjxjnoobie.proxy.store;

import com.tjxjnoobie.store.integration.dto.PlayerEntitlementView;
import com.tjxjnoobie.store.integration.dto.PlayerOrderView;

import java.util.List;

public record PlayerStoreSnapshot(
        List<PlayerOrderView> orders,
        List<PlayerEntitlementView> entitlements
) {
}
