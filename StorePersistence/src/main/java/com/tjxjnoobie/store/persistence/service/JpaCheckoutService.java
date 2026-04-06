package com.tjxjnoobie.store.persistence.service;

import com.tjxjnoobie.store.domain.model.AppliedCoupon;
import com.tjxjnoobie.store.domain.model.CheckoutQuote;
import com.tjxjnoobie.store.domain.model.CreateCheckoutSessionCommand;
import com.tjxjnoobie.store.domain.model.StorePackageView;
import com.tjxjnoobie.store.domain.service.CatalogService;
import com.tjxjnoobie.store.domain.service.CheckoutService;
import com.tjxjnoobie.store.domain.service.CouponService;
import com.tjxjnoobie.store.domain.service.PricingService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JpaCheckoutService implements CheckoutService {

    private final CatalogService catalogService;
    private final CouponService couponService;
    private final PricingService pricingService;

    public JpaCheckoutService(CatalogService catalogService,
                              CouponService couponService,
                              PricingService pricingService) {
        this.catalogService = catalogService;
        this.couponService = couponService;
        this.pricingService = pricingService;
    }

    @Override
    public CheckoutQuote prepareQuote(CreateCheckoutSessionCommand command) {
        List<StorePackageView> packages = command.items().stream()
                .map(item -> catalogService.getPackageBySlug(item.packageSlug()))
                .toList();
        AppliedCoupon coupon = packages.isEmpty()
                ? null
                : couponService.validate(command.couponCode(), packages.getFirst(), packages.getFirst().price(), Instant.now())
                .orElse(null);
        return pricingService.calculateQuote(command, packages, coupon);
    }
}
