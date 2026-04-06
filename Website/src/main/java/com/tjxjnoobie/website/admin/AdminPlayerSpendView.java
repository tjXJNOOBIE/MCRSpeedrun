package com.tjxjnoobie.website.admin;

import java.math.BigDecimal;

public record AdminPlayerSpendView(
        String playerUuid,
        String username,
        BigDecimal totalSpent,
        long orderCount,
        boolean flagged
) {
}
