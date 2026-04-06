package com.tjxjnoobie.website.api.dto;

import com.tjxjnoobie.store.domain.model.CouponType;
import com.tjxjnoobie.store.domain.model.PromotionStatus;
import com.tjxjnoobie.store.domain.model.PromotionTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminPromotionUpsertRequest(
        @NotBlank String name,
        @NotBlank String slug,
        @NotNull PromotionStatus status,
        @NotNull PromotionTargetType targetType,
        @NotNull CouponType discountType,
        @NotNull BigDecimal discountValue,
        String categorySlug,
        String packageSlug,
        Instant startsAt,
        Instant endsAt
) {
}
