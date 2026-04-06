package com.tjxjnoobie.store.integration.dto;

public record FulfillmentFailRequest(
        String correlationId,
        String error,
        boolean retryable
) {
}
