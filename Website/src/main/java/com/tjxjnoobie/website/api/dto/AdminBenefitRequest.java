package com.tjxjnoobie.website.api.dto;

import com.tjxjnoobie.store.domain.model.BenefitType;
import com.tjxjnoobie.store.domain.model.StackingPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminBenefitRequest(
        @NotNull BenefitType benefitType,
        @NotBlank String targetSystem,
        @NotBlank String targetKey,
        @NotBlank String targetValue,
        Integer durationDays,
        @NotNull StackingPolicy stackingPolicy
) {
}
