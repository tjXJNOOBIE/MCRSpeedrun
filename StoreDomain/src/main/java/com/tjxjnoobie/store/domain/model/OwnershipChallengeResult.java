package com.tjxjnoobie.store.domain.model;

public record OwnershipChallengeResult(
        Long challengeId,
        String username,
        String code,
        boolean requiresInGameConfirmation
) {
}
