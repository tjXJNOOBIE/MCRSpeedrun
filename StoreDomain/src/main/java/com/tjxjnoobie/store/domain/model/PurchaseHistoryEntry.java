package com.tjxjnoobie.store.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record PurchaseHistoryEntry(
        Long orderId,
        String orderNumber,
        String packageName,
        OrderState orderState,
        BigDecimal total,
        Instant purchasedAt
) {
}
