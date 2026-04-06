package com.tjxjnoobie.store.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutQuote(
        List<CheckoutQuoteLine> lines,
        BigDecimal subtotal,
        BigDecimal discountTotal,
        BigDecimal total,
        AppliedCoupon appliedCoupon
) {
}
