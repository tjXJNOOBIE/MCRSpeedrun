package com.tjxjnoobie.api.interfaces;

import java.util.UUID;

/**
 * Interface for lobby statistics cache management
 */
public interface ILobbyStatsCache {
    


    default String getGlobalGrade(UUID uuid){
        return null;
    }

    default void loadLobbyStats(UUID uuid){

    }
}