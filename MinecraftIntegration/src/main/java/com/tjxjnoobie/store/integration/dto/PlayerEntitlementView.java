package com.tjxjnoobie.store.integration.dto;

public record PlayerEntitlementView(
        Long entitlementId,
        String playerUuid,
        String sourcePackage,
        String benefitType,
        String targetKey,
        String targetValue,
        String state,
        String effectiveAt,
        String expiresAt
) {
}
