package com.tjxjnoobie.API.velocity;

import com.tjxjnoobie.API.managers.MySQL;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.API.utils.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RatingAPI {
    private final Utils utils;
    private static Map<UUID, Rating> ratingPlayers = new HashMap<>();

    public RatingAPI(Utils utils) {
        this.utils = utils;
    }

    public void processMatch(){

    }

    public double getRating(String table, String uuid) throws SQLException {
        System.out.println("Executing query: SELECT RATING FROM " + table + " WHERE UUID = '" + uuid + "'");
        ResultSet rs = MySQL.getResult("SELECT RATING FROM " + table + " WHERE UUID = ?", uuid);
        if (rs == null) {
            System.err.println("ResultSet is null. Check your query or connection.");
            return -1.0;
        }
        if (rs.next()) {
            double rating = rs.getDouble("RATING");
            System.out.println("Rating found: " + rating);
            return rating;
        } else {
            System.out.println("No record found for UUID: " + uuid);
        }
        return -1.0;
    }


    public double getVolatility(String table, String uuid) throws SQLException {
            ResultSet rs = MySQL.getResult("SELECT VOLATILITY FROM " + table + " WHERE UUID= ? ", uuid);
            double volatility;
            if (rs.next()) {
                volatility = rs.getDouble("VOLATILITY");
                return volatility;

        }
        return -1.0;
    }


    public double getDeviation(String table, String uuid) throws SQLException {
        ResultSet rs = MySQL.getResult("SELECT DEVIATION FROM "+ table+ " WHERE UUID= ? ",uuid);
        double deviation;
        if(rs.next()){
            deviation = rs.getDouble("DEVIATION");
            return deviation;
        }
        return -1.0;
    }

    }

