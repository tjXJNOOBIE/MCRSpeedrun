package com.tjxjnoobie.api.platform.cache;

import com.tjxjnoobie.api.interfaces.IStatsManager;
import com.tjxjnoobie.api.platform.minecraft.speedrun.ISpeedrunStatsCache;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class SpeedrunStatsCache implements ISpeedrunStatsCache, IStatsManager {

    public HashMap<UUID, Integer> wins = new HashMap<>();
    public HashMap<UUID, Integer> losses = new HashMap<>();
    public HashMap<UUID, Integer> played = new HashMap<>();

    public HashMap<UUID, Integer> ender_trips = new HashMap<>();
    public HashMap<UUID, Integer> nether_trips = new HashMap<>();
    public  HashMap<UUID, Integer> player_elims = new HashMap<>();
    public HashMap<UUID, String> best_time = new HashMap<>();
    public HashMap<UUID, Long> best_timelong = new HashMap<>();




    public void createStorage(UUID uuid) throws SQLException {
        int playerWins =  getStat("speedrun_stats",uuid,"WINS");
        int playerLosses =  getStat("speedrun_stats",uuid,"LOSSES");
        int gamesPlayed =  getStat("speedrun_stats",uuid,"PLAYED");
        int enderTrips =  getStat("speedrun_stats",uuid,"ENDER_TRIPS");
        int netherTrips =  getStat("speedrun_stats",uuid,"NETHER_TRIPS");
        int elims =  getStat("speedrun_stats",uuid,"ELIMS");
        long bestTimeLong =  getStat("speedrun_stats",uuid,"BEST_TIMELONG");
        String bestTime =  getBestSRTime(uuid);
        wins.put(uuid,playerWins);
        losses.put(uuid,playerLosses);
        played.put(uuid,gamesPlayed);
        ender_trips.put(uuid,enderTrips);
        player_elims.put(uuid,elims);
        nether_trips.put(uuid,netherTrips);
        best_timelong.put(uuid,bestTimeLong);
        best_time.put(uuid,bestTime);


    }
    public void updateSRStats(UUID uuid) throws SQLException {
         setBestSRTime(getBestTime(uuid),uuid);
         setStat("speedrun_stats","WINS",uuid,getWins(uuid));
         setStat("speedrun_stats","LOSSES",uuid,getLosses(uuid));
         setStat("speedrun_stats","BESTTIME_LONG",uuid,getBestTimeLong(uuid));


    }
    public void addWins(UUID uuid, int win){
        wins.put(uuid,getWins(uuid)+win);
    }
    public void addLosses(UUID uuid, int losses){
        wins.put(uuid,getLosses(uuid)+losses);
    }
    public void addEnderTrips(UUID uuid, int end_trips){
        ender_trips.put(uuid,getEnderTrips(uuid)+end_trips);
    }
    public void addNetherTrips(UUID uuid, int nether_trip){
        nether_trips.put(uuid,getNetherTrips(uuid)+nether_trip);
    }
    public void addElim(UUID uuid, int elims){
        player_elims.put(uuid,getElims(uuid)+elims);
    }
    public void addBestTime(UUID uuid, String bestTime){
        best_time.put(uuid,bestTime);
    }

    public void setBestTimeLong(UUID uuid, long bestTime){
        best_timelong.put(uuid,bestTime);
    }

    public Integer getWins(UUID uuid){
        return wins.get(uuid);
    }
    public Integer getLosses(UUID uuid){
        return losses.get(uuid);
    }

    public Integer getEnderTrips(UUID uuid){
        return ender_trips.get(uuid);
    }
    public Integer getNetherTrips(UUID uuid){
        return nether_trips.get(uuid);
    }

    public Integer getElims(UUID uuid){
        return player_elims.get(uuid);
    }
    public String getBestTime(UUID uuid){
        return best_time.get(uuid);
    }

    public long getBestTimeLong(UUID uuid){
        return best_timelong.get(uuid);
    }
}