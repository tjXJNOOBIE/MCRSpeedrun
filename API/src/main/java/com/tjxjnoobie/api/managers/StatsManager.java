package com.tjxjnoobie.api.managers;

import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class StatsManager {

    public int getStat(String game, UUID uuid, String stat) {
        int i = 0;
        try {

            ResultSet rs = MySQL.getResult("SELECT * FROM "+game+" WHERE UUID= ?",uuid.toString());
            if (rs.next()) {
                i = rs.getInt(stat);
            }else if (rs.wasNull()) {
                i = 0;
                Bukkit.getLogger().warning(stat+" for UUID " +uuid+ " does not exist or is a invalid stat");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }
    public Integer getRank(String uuidORusername, String column) {
        Integer i = Integer.valueOf(0);
        try {

            ResultSet rs = MySQL.getResult("SELECT * FROM ffa_stats WHERE "+column+"='" + uuidORusername + "'");
            if ((rs.next())) {
                i = rs.getInt("RANK");
                return rs.getInt("RANK");

            }

        } catch (SQLException e) {
            e.printStackTrace();

        }

        return i;
    }

    public String getPos(String statfor, String table, int Pos) {
        String name = "CantGetName";
        String query = "SELECT * FROM `" + table + "` ORDER BY `" + statfor + "` DESC LIMIT ?";

        try (PreparedStatement st = MySQL.connection.prepareStatement(query)) {
            st.setInt(1, Pos);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("USERNAME");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return name;
    }

    public String getGrade(String game, UUID uuid) {
        try {
            ResultSet rs = MySQL.getResult("SELECT GRADE FROM "+game+" WHERE UUID= ?",uuid.toString());

            if (rs.next()) {
                return rs.getString("GRADE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Can't get Grade";
    }

    public String getBestSRTime(UUID uuid) throws SQLException {
        ResultSet rs = MySQL.getResult("SELECT BEST_TIME FROM speedrun_stats WHERE UUID= ?",uuid.toString());
        if(rs.next()) {
            return rs.getString("BEST_TIME");
        }
        return "Can't get Time";
    }

    public long getBestSRTimeLong(UUID uuid) throws SQLException {
        ResultSet rs = MySQL.getResult("SELECT BEST_TIMELONG FROM speedrun_stats WHERE UUID= ?", uuid.toString());
        if(rs.next()){
            return rs.getLong("BEST_TIMELONG");
        }
        return 0;
    }

    public void setStat(String game,String stat, UUID uuid, Object wins) throws SQLException {
        MySQL.executePreparedStatement("UPDATE "+game+" SET "+stat+"= ? WHERE UUID= ?",wins,uuid.toString());

    }

    public void setBestSRTime(String bestSRTime, UUID uuid) throws SQLException {
        MySQL.executePreparedStatement("UPDATE speedrun_stats SET BEST_TIME = ? WHERE UUID= ?",bestSRTime,uuid.toString());

    }

}

