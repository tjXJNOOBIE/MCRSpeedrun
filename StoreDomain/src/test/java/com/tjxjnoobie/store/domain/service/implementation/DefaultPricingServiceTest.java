package com.tjxjnoobie.store.domain.service.implementation;

import com.tjxjnoobie.store.domain.model.AppliedCoupon;
import com.tjxjnoobie.store.domain.model.BillingInterval;
import com.tjxjnoobie.store.domain.model.CheckoutItemCommand;
import com.tjxjnoobie.store.domain.model.CheckoutQuote;
import com.tjxjnoobie.store.domain.model.CreateCheckoutSessionCommand;
import com.tjxjnoobie.store.domain.model.CouponType;
import com.tjxjnoobie.store.domain.model.PackageType;
import com.tjxjnoobie.store.domain.model.StorePackageView;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultPricingServiceTest {

    private final DefaultPricingService service = new DefaultPricingService();

    @Test
    void calculatesSubtotalDiscountAndTotal() {
        CreateCheckoutSessionCommand command = new CreateCheckoutSessionCommand(
                "buyer",
                "recipient",
                "SUMMER26",
                List.of(new CheckoutItemCommand("god-rank", 1))
        );
        StorePackageView storePackage = new StorePackageView(
                1L, "god-rank", "ranks", "God Rank", "Lifetime rank", "<p>Rank</p>",
                new BigDecimal("150.00"), "USD", true, true, true,
                PackageType.SINGLE, BillingInterval.NONE, "amber"
        );
        AppliedCoupon coupon = new AppliedCoupon("SUMMER26", CouponType.PERCENTAGE,
                new BigDecimal("20.0"), BigDecimal.ZERO);

        CheckoutQuote quote = service.calculateQuote(command, List.of(storePackage), coupon);

        assertEquals(new BigDecimal("150.00"), quote.subtotal());
        assertEquals(new BigDecimal("30.00"), quote.discountTotal());
        assertEquals(new BigDecimal("120.00"), quote.total());
    }
}
