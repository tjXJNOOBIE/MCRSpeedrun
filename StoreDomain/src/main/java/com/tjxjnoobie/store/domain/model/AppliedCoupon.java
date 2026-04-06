package com.tjxjnoobie.store.domain.model;

import java.math.BigDecimal;

public record AppliedCoupon(
        String code,
        CouponType couponType,
        BigDecimal percentOff,
        BigDecimal amountOff
) {
}
