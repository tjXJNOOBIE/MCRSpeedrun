package com.tjxjnoobie.website.account;

import java.util.UUID;

public record PlayerAccountSession(
        UUID playerUuid,
        String username
) {
}
