package com.tjxjnoobie.store.integration.dto;

public record IntegrationAuthRequest(
        String nodeId,
        String bootstrapSecret
) {
}
