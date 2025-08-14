package com.tjxjnoobie.api.managers;

import com.tjxjnoobie.api.enums.GameModeEnum;
import com.tjxjnoobie.api.interfaces.IGameMode;

public class GameMode implements IGameMode {

    private GameModeEnum currentGameMode;

    @Override
    public GameModeEnum getCurrentGameMode(){
        return currentGameMode;
    }

    @Override
    public void setGameMode(GameModeEnum gamemode){
        currentGameMode = gamemode;
    }

    @Override
    public boolean isGameMode(GameModeEnum gameMode) {
        return currentGameMode == gameMode;
    }

    @Override
    public String getGameModeString() {
        return currentGameMode != null ? currentGameMode.name() : "UNKNOWN";
    }
}
