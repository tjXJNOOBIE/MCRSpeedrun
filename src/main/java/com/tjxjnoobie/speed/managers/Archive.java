package com.tjxjnoobie.speed.managers;

import com.tjxjnoobie.API.managers.MySQL;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Archive {



    public void saveGame(String id, String winner, Integer playing, Integer nether_trips
            , Integer ender_trips, ArrayList<String> players, HashMap<String, Integer> pos) throws SQLException {
        MySQL.executePreparedStatement("INSERT INTO archive (SERVERID, PLAYING, GAMESPLAYED) VALUES ('" + id + "',0,0);");
    }
}
