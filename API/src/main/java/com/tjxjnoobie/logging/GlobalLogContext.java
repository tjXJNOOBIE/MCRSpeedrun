package com.tjxjnoobie.logging;

import java.util.Map;

public record GlobalLogContext (
        String serverId,
        String region,
        String gameType,
        Map<String, Object> extraMetadata // extensible custom metadata
) {}
