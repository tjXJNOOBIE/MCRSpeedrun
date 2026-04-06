package com.tjxjnoobie.store.domain.model;

import java.util.UUID;

public record UsernameResolution(
        UUID uuid,
        String normalizedUsername
) {
}
