package com.tjxjnoobie.store.domain.model;

import java.math.BigDecimal;

public record StorePackageView(
        Long id,
        String slug,
        String categorySlug,
        String name,
        String shortDescription,
        String descriptionHtml,
        BigDecimal price,
        String currency,
        boolean visible,
        boolean featured,
        boolean giftable,
        PackageType packageType,
        BillingInterval billingInterval,
        String accentKey
) {
}
