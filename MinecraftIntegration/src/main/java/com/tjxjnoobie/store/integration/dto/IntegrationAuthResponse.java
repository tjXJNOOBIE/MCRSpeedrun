package com.tjxjnoobie.store.integration.dto;

public record IntegrationAuthResponse(
        String accessToken,
        long expiresAtEpochSecond
) {
}
