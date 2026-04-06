package com.tjxjnoobie.store.domain.model;

import java.time.Instant;

public record EntitlementSummary(
        Long entitlementId,
        String packageName,
        BenefitType benefitType,
        String targetKey,
        String targetValue,
        EntitlementState state,
        Instant effectiveAt,
        Instant expiresAt
) {
}
