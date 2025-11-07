package com.tjxjnoobie.api.interfaces;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Interface for statistics management operations
 */
public interface IStatsManager {
    
    /**
     * Gets player statistics
     * @param playerId The player's UUID
     * @param gameMode The game mode for which to get statistics
     * @param statFor The statistic to retrieve (e.g., "WINS", "LOSSES", "PLAYED")
     * @return Player statistics object
     */
    default int getStat(String gameMode, UUID playerId, String statFor){
        return 0;
    }




    default void setStat(String game,String stat, UUID uuid, Object wins) throws SQLException{

    }

    default void setBestSRTime(String bestTime, UUID uuid ) throws SQLException{

    }


    default String getBestSRTime(UUID uuid) throws SQLException{
        return null;
    }

    default String getGrade(String gameMode, UUID uuid){
        return null;
    }
}