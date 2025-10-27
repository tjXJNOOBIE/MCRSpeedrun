package com.tjxjnoobie.api.machine.data;

import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;

public class LocalServerMetaData implements ILocalServerMetaData {

    public String serverID = "DEV";
    public String gameID;
    public String localServerPrefix = "§6§lNovus§8§l »»§f ";
    public String minecraftStaffInGamePrefix = "§4§lNovus §8§l»»§c ";


    @Override
    public String getLocalServerID() {
        return serverID;
    }
    @Override
    public String getLocalServerPrefix(){
        return localServerPrefix;
    }
    @Override
    public String getMinecraftStaffInGamePrefix(){
        return minecraftStaffInGamePrefix;
    }
    @Override
    public String getGameID() {
        return gameID;
    }

    @Override
    public void setServerID(String serverID) {
        this.serverID = serverID;

    }

    @Override
    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
}
