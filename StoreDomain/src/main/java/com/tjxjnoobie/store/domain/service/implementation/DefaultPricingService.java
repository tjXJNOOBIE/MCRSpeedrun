package com.tjxjnoobie.store.domain.service.implementation;

import com.tjxjnoobie.store.domain.model.AppliedCoupon;
import com.tjxjnoobie.store.domain.model.CheckoutItemCommand;
import com.tjxjnoobie.store.domain.model.CheckoutQuote;
import com.tjxjnoobie.store.domain.model.CheckoutQuoteLine;
import com.tjxjnoobie.store.domain.model.CreateCheckoutSessionCommand;
import com.tjxjnoobie.store.domain.model.CouponType;
import com.tjxjnoobie.store.domain.model.StorePackageView;
import com.tjxjnoobie.store.domain.service.PricingService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DefaultPricingService implements PricingService {

    @Override
    public CheckoutQuote calculateQuote(CreateCheckoutSessionCommand command,
                                        List<StorePackageView> packages,
                                        AppliedCoupon appliedCoupon) {
        Map<String, StorePackageView> packagesBySlug = packages.stream()
                .collect(Collectors.toMap(StorePackageView::slug, Function.identity()));
        List<CheckoutQuoteLine> lines = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CheckoutItemCommand item : command.items()) {
            StorePackageView storePackage = packagesBySlug.get(item.packageSlug());
            if (storePackage == null) {
                continue;
            }

            BigDecimal lineTotal = storePackage.price().multiply(BigDecimal.valueOf(item.quantity()));
            subtotal = subtotal.add(lineTotal);
            lines.add(new CheckoutQuoteLine(storePackage.slug(), storePackage.name(), item.quantity(),
                    storePackage.price(), lineTotal));
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (appliedCoupon != null) {
            if (appliedCoupon.couponType() == CouponType.PERCENTAGE) {
                discount = subtotal.multiply(appliedCoupon.percentOff())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            } else {
                discount = appliedCoupon.amountOff().min(subtotal);
            }
        }

        BigDecimal total = subtotal.subtract(discount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        return new CheckoutQuote(lines, subtotal.setScale(2, RoundingMode.HALF_UP),
                discount.setScale(2, RoundingMode.HALF_UP), total, appliedCoupon);
    }
}
