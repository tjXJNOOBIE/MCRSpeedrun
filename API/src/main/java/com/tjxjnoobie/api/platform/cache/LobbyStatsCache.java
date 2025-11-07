package com.tjxjnoobie.api.platform.cache;

import com.tjxjnoobie.api.interfaces.ILobbyStatsCache;
import com.tjxjnoobie.api.interfaces.IStatsManager;

import java.util.HashMap;
import java.util.UUID;


public class LobbyStatsCache implements ILobbyStatsCache, IStatsManager {


    public HashMap<UUID, String> globalGrade = new HashMap<>();


    public void loadLobbyStats(UUID uuid){
        String globalGrades = getGrade("player_profile", uuid);
        globalGrade.put(uuid,globalGrades);
    }



    public String getGlobalGrade(UUID uuid){
        return globalGrade.get(uuid);
    }



}
