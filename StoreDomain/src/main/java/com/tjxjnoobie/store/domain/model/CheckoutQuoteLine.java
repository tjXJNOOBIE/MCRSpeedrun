package com.tjxjnoobie.store.domain.model;

import java.math.BigDecimal;

public record CheckoutQuoteLine(
        String packageSlug,
        String packageName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
