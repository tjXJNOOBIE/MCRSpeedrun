package com.tjxjnoobie.store.domain.model;

public record FulfillmentDispatch(
        Long jobId,
        String idempotencyKey,
        String correlationId,
        String operation,
        String playerUuid,
        String playerUsername,
        BenefitType benefitType,
        String targetSystem,
        String targetKey,
        String targetValue
) {
}
