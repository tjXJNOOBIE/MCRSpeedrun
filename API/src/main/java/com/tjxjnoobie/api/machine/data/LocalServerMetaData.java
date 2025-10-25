package com.tjxjnoobie.api.machine.data;

import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;

public class LocalServerMetaData implements ILocalServerMetaData {

    public String serverID = "DEV";
    public String gameID;
    public String minecraftInGamePrefix = "";
    public String minecraftStaffInGamePrefix = "";


    @Override
    public String getServerID() {
        return serverID;
    }
    @Override
    public String getMinecraftInGamePrefix(){
        return minecraftInGamePrefix;
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
