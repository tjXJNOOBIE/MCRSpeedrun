/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.game.manager;

import com.tjxjnoobie.api.interfaces.IGameType;
import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.managers.MySQL;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * GameType – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 10/23/2025
 */
public class GameType implements IGameType {

    private GameTypeEnum currentGameType = GameTypeEnum.LOBBY;

    @Override
    public HashMap<String, Boolean> getGameTypeHash() {
        return new HashMap<>();
    }

    @Override
    public GameTypeEnum getGameTypeEnum() {
        return currentGameType;
    }

    @Override
    public void setGameTypeEnum(GameTypeEnum gameType) {
        this.currentGameType = gameType;
        Bukkit.getLogger().info("Game type changed to: " + gameType);
    }

    @Override
    public GameTypeEnum getGameType() {
        return getGameTypeEnum();
    }

    @Override
    public void setType(GameTypeEnum gametype) {
        setGameTypeEnum(gametype);
    }

    @Override
    public void setGameType(GameTypeEnum gameType, String serverid) throws SQLException {
        MySQL.executePreparedStatement("UPDATE servers SET GAMETYPE= ? WHERE SERVERID= ?", gameType.name(), serverid);
        Bukkit.getLogger().info("Game Type set to " + gameType);
        setType(gameType);
    }

    @Override
    public String getType(String serverid, String database) {
        String gametype = "";
        try {
            ResultSet rs = MySQL.getResult("SELECT * FROM servers WHERE SERVERID= ?", serverid);
            if (rs.next()) {
                gametype = rs.getString("GAMETYPE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gametype;
    }

    @Override
    public ArrayList<String> getGameTypes() {
        ArrayList<String> gameTypes = new ArrayList<>();
        String query = "SELECT GAMETYPE FROM servers";
        ResultSet rs = MySQL.getResult(query);
        try {
            while (rs.next()) {
                String type = rs.getString("GAMETYPE");
                if (!gameTypes.contains(type)) { // Avoid duplicates
                    gameTypes.add(type);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gameTypes;
    }
}
