package com.tjxjnoobie.api.platform.cache;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.ILobbyStatsCache;
import com.tjxjnoobie.api.interfaces.IStatsManager;
import com.tjxjnoobie.api.platform.global.registry.IAbstractRegistry;

import java.util.HashMap;
import java.util.UUID;

public class LobbyStatsCache implements ILobbyStatsCache, IAbstractRegistry {


    public HashMap<UUID, String> globalGrade = new HashMap<>();

    @Inject private IGlobalContext globalContext;


    public void loadLobbyStats(UUID uuid){
        IStatsManager statsManager = globalContext.getStatsManager();
        String globalGrades = statsManager.getGrade("player_profile", uuid);
        globalGrade.put(uuid,globalGrades);
    }



    public String getGlobalGrade(UUID uuid){
        return globalGrade.get(uuid);
    }



}
