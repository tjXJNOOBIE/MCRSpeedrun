package com.tjxjnoobie.logging.context;

import java.util.Map;

public record LocalLogContext(
        String serverId,
        String gameType,
        Map<String, Object> extraMetadata
) {
}
