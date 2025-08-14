package com.tjxjnoobie.api.platform.cache;

import com.tjxjnoobie.api.contexts.GlobalContext;
import com.tjxjnoobie.api.interfaces.IStatsManager;

import java.util.HashMap;
import java.util.UUID;

public class LobbyStatsCache {


    public HashMap<UUID, String> globalGrade = new HashMap<>();

    private final GlobalContext globalContext;

    public LobbyStatsCache(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    public void loadLobbyStats(UUID uuid){
        IStatsManager statsManager = globalContext.getStatsManager();
        String globalGrades = statsManager.getGrade("player_profile", uuid);
        globalGrade.put(uuid,globalGrades);
    }



    public String getGlobalGrade(UUID uuid){
        return globalGrade.get(uuid);
    }



}
