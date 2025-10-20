package com.tjxjnoobie.api.interfaces;

import java.util.UUID;

/**
 * Interface for lobby statistics cache management
 */
public interface ILobbyStatsCache {
    


    String getGlobalGrade(UUID uuid);

    void loadLobbyStats(UUID uuid);
}