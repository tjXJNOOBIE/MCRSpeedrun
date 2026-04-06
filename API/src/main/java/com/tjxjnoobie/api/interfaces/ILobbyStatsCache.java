package com.tjxjnoobie.api.interfaces;

import java.util.UUID;

/**
 * Interface for lobby statistics cache management
 */
public interface ILobbyStatsCache extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {
    


    default String getGlobalGrade(UUID uuid){
        return null;
    }

    default void loadLobbyStats(UUID uuid){

    }
}
