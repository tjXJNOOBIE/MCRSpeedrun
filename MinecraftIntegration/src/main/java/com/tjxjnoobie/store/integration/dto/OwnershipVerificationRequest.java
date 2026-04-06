package com.tjxjnoobie.store.integration.dto;

public record OwnershipVerificationRequest(
        String code,
        String playerUuid,
        String playerUsername
) {
}
