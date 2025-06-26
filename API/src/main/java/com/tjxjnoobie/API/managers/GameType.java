package com.tjxjnoobie.API.managers;

import com.tjxjnoobie.enums.GameTypeEnum;
import com.tjxjnoobie.interfaces.IGameType;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameType implements IGameType {

    public HashMap<String, Boolean> gametypeHash = new HashMap<>();
    private GameTypeEnum gameTypeEnum;




    @Override
    public void GameTypEnum(){

        this.gameTypeEnum = GameTypeEnum.LOBBY;
    }
    @Override
    public GameTypeEnum getGameType(){
        return gameTypeEnum;
    }
    @Override
    public void setType(GameTypeEnum gametype){
        gameTypeEnum = gametype;
    }
    @Override
    public void setGameType(GameTypeEnum gameType, String serverid) throws SQLException {
        MySQL.executePreparedStatement("UPDATE servers SET GAMESTATE= ? WHERE SERVERID= ?",gameType.name(),serverid);
        Bukkit.getLogger().info("Game State set to " + gameType);
        setType(gameType);
    }
    @Override
    public String getType(String serverid, String database) {
        String gamestate = "";
        try {

            ResultSet rs = MySQL.getResult("SELECT * FROM servers WHERE SERVERID= ?",serverid);
            if ((rs.next())) {
                rs.getString("GAMETYPE");
            }
            gamestate = rs.getString("GAMETYPE");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return gamestate;
    }

    @Override
    public ArrayList<String> getGameTypes() {
        ArrayList<String> GameType = new ArrayList<>();
        String query = "SELECT GAMETYPE FROM servers";
        ResultSet rs = MySQL.getResult(query);
        try {
            while (rs.next()) {
                String id = rs.getString("GAMETYPE");
                GameType.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return GameType;

    }


}
