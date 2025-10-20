package com.tjxjnoobie.api.machine.data;

import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;

public class LocalServerMetaData implements ILocalServerMetaData {

    private String serverID;
    private String gameID;

    @Override
    public String getServerID() {
        return serverID;
    }
    @Override
    public void setServerID(String serverID) {
        this.serverID = serverID;

    }
    @Override
    public String getGameID() {
        return gameID;
    }
    @Override
    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
}
