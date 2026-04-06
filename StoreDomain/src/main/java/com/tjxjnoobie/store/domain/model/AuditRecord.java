package com.tjxjnoobie.store.domain.model;

public record AuditRecord(
        ActorType actorType,
        String actorId,
        String action,
        String targetType,
        String targetId,
        String correlationId,
        String metadataJson
) {
}
