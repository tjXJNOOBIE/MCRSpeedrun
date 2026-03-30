package com.tjxjnoobie.api.platform.minecraft.speedrun;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Interface for speedrun statistics cache management
 */
public interface ISpeedrunStatsCache {
    

    /**
     * Gets player best time
     * @param playerId The player UUID
     * @return Best time in milliseconds
     */
    default String getBestTime(UUID playerId){
        return null;
    }
    


    default void addBestTime(UUID uuid, String finalTime){

    }

    default void createStorage(UUID uuid) throws SQLException{

    }

    default void setBestTimeLong(UUID uuid, long playerFinalTime){

    }

    default long getBestTimeLong(UUID uuid){
        return 0;
    }
}