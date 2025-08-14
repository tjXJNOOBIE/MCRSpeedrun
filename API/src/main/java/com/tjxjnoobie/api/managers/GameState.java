package com.tjxjnoobie.api.managers;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameState;
import com.tjxjnoobie.api.internal.utils.Utils;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class GameState implements IGameState {

    public HashMap<String, Boolean> gamestate = new HashMap<>();
    private final Utils utils;
    private GameStateEnum currentState;

    public GameState(Utils utils) {
        this.utils = utils;
    }


    @Override
    public void GameStateEnum(){

        this.currentState = GameStateEnum.LOBBY;
    }
    @Override
    public GameStateEnum getCurrentState(){

       return currentState;
    }
    @Override
    public void setState(GameStateEnum gamestate){
        currentState = gamestate;
    }
    @Override
    public void setGameState(GameStateEnum gamestate, String serverid) throws SQLException {
        MySQL.executePreparedStatement("UPDATE servers SET GAMESTATE= ? WHERE SERVERID= ?",gamestate.name(),serverid);
        Bukkit.getLogger().info("Game State set to " + gamestate);
        setState(gamestate);

    }
    @Override
    public String getState(String serverid, String database) {
        String gamestate = "";
        try {

            ResultSet rs = MySQL.getResult("SELECT * FROM servers WHERE SERVERID= ?",serverid);
            if ((rs.next())) {
                rs.getString("GAMESTATE");
            }
            gamestate = rs.getString("GAMESTATE");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return gamestate;
    }
    @Override
    public void createServerID(String serverId, String gameID, String table, String gameType) throws SQLException {
        MySQL.executePreparedStatement("INSERT INTO " + table + " (SERVERID, GAMETYPE, GAMESTATE, SPAWNWORLD, GAMEID) VALUES (?,?,?,?,?)",serverId,gameType,"LOBBY","lobby", gameID);
        Bukkit.getLogger().info("Server ID "+serverId+" Created");
    }
    @Override
    public void removeServerID(String serverID){
        try{
            PreparedStatement statement = MySQL.connection.prepareStatement("DELETE FROM servers WHERE SERVERID ='"+serverID+"'");
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public ArrayList<String> getAllGameIDs() {
        ArrayList<String> IDs = new ArrayList<>();
        String query = "SELECT ID FROM servers";
        ResultSet rs = MySQL.getResult(query);
        try {
            while (rs.next()) {
                String id = rs.getString("ID");
                IDs.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return IDs;

    }
    @Override
    public ArrayList<String> getServerIDs() {
        ArrayList<String> IDs = new ArrayList<>();
        String query = "SELECT ID FROM servers";
        ResultSet rs = MySQL.getResult(query);
        try {
            while (rs.next()) {
                String id = rs.getString("ID");
                IDs.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return IDs;

    }



}


