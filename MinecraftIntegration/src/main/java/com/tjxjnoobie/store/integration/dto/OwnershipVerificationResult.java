package com.tjxjnoobie.store.integration.dto;

public record OwnershipVerificationResult(
        boolean verified,
        String message
) {
}
