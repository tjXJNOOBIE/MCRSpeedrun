package com.tjxjnoobie.store.integration.dto;

import java.math.BigDecimal;

public record PlayerOrderView(
        Long orderId,
        String orderNumber,
        String packageName,
        String orderState,
        BigDecimal total,
        String purchasedAt
) {
}
