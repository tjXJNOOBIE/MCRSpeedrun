package com.tjxjnoobie.store.integration.auth;

import java.util.List;

public record IntegrationTokenClaims(
        String nodeId,
        long issuedAtEpochSecond,
        long expiresAtEpochSecond,
        List<IntegrationScope> scopes
) {
}
