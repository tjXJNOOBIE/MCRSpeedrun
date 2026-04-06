package com.tjxjnoobie.store.domain.service;

import com.tjxjnoobie.store.domain.model.PurchaseHistoryEntry;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<PurchaseHistoryEntry> getOrdersForPlayer(UUID playerUuid);

    List<PurchaseHistoryEntry> getRecentOrders();
}
