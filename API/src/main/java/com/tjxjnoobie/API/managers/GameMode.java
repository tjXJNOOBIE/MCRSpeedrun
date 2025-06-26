package com.tjxjnoobie.API.managers;

import com.tjxjnoobie.enums.GameModeEnum;
import com.tjxjnoobie.interfaces.IGameMode;

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
