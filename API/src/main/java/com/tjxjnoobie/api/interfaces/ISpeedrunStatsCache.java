package com.tjxjnoobie.api.interfaces;

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
    String getBestTime(UUID playerId);
    


    void addBestTime(UUID uuid, String finalTime);

    void createStorage(UUID uuid) throws SQLException;

    void setBestTimeLong(UUID uuid, long playerFinalTime);

    long getBestTimeLong(UUID uuid);
}