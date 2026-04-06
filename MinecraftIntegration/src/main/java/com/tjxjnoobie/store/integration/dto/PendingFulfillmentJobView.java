package com.tjxjnoobie.store.integration.dto;

public record PendingFulfillmentJobView(
        Long jobId,
        String idempotencyKey,
        String correlationId,
        String operation,
        String playerUuid,
        String playerUsername,
        String benefitType,
        String targetSystem,
        String targetKey,
        String targetValue
) {
}
