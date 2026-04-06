package com.tjxjnoobie.website.admin;

import java.math.BigDecimal;

public record AdminOverviewMetrics(
        BigDecimal totalRevenue,
        long activeRankCount,
        BigDecimal chargebackExposure,
        BigDecimal conversionRate
) {
}
