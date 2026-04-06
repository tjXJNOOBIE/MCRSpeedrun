package com.tjxjnoobie.store.integration.dto;

public record FulfillmentCompleteRequest(
        String correlationId,
        String appliedValue,
        String message
) {
}
