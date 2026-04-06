package com.tjxjnoobie.website.api.dto;

import com.tjxjnoobie.store.domain.model.BillingInterval;
import com.tjxjnoobie.store.domain.model.PackageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record AdminPackageUpsertRequest(
        @NotBlank String categorySlug,
        @NotBlank String slug,
        @NotBlank String name,
        @NotBlank String shortDescription,
        @NotBlank String descriptionHtml,
        @NotNull BigDecimal price,
        boolean visible,
        boolean featured,
        boolean giftable,
        @NotNull PackageType packageType,
        @NotNull BillingInterval billingInterval,
        String accentKey,
        @Valid @NotEmpty List<AdminBenefitRequest> benefits
) {
}
