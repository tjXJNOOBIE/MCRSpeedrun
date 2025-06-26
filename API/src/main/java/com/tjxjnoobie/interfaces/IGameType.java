package com.tjxjnoobie.interfaces;

import com.tjxjnoobie.enums.GameTypeEnum;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Interface for game type management
 */
public interface IGameType {

    void GameTypEnum();

    /**
     * Gets the current game type
     * @return The current game type enum
     */
    GameTypeEnum getGameType();

    void setType(GameTypeEnum gametype);

    /**
     * Sets the game type
     * @param gameType The game type to set
     */
    void setGameType(GameTypeEnum gameType, String serverID) throws SQLException;
    


;

    String getType(String serverid, String database);

    ArrayList<String> getGameTypes();
}