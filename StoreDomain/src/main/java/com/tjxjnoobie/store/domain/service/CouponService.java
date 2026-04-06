package com.tjxjnoobie.store.domain.service;

import com.tjxjnoobie.store.domain.model.AppliedCoupon;
import com.tjxjnoobie.store.domain.model.StorePackageView;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public interface CouponService {
    Optional<AppliedCoupon> validate(String code, StorePackageView storePackage, BigDecimal subtotal, Instant now);
}
