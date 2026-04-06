package com.tjxjnoobie.store.domain.service.implementation;

import com.tjxjnoobie.store.domain.model.AppliedCoupon;
import com.tjxjnoobie.store.domain.model.StorePackageView;
import com.tjxjnoobie.store.domain.service.CouponService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Component
public class DefaultCouponService implements CouponService {

    @Override
    public Optional<AppliedCoupon> validate(String code, StorePackageView storePackage, BigDecimal subtotal, Instant now) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        String normalized = code.trim().toUpperCase(Locale.ROOT);
        if ("SUMMER26".equals(normalized)) {
            return Optional.of(new AppliedCoupon(normalized, com.tjxjnoobie.store.domain.model.CouponType.PERCENTAGE,
                    new BigDecimal("20.0"), BigDecimal.ZERO));
        }
        if ("VIP-ONBOARD".equals(normalized) && subtotal.compareTo(new BigDecimal("50.00")) >= 0) {
            return Optional.of(new AppliedCoupon(normalized, com.tjxjnoobie.store.domain.model.CouponType.FIXED_AMOUNT,
                    BigDecimal.ZERO, new BigDecimal("50.00")));
        }
        return Optional.empty();
    }
}
