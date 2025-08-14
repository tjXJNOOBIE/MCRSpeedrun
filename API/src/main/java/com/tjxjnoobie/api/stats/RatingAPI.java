package com.tjxjnoobie.api.stats;

import com.tjxjnoobie.api.interfaces.IRatingAPI;
import com.tjxjnoobie.api.managers.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingAPI implements IRatingAPI {

    public RatingAPI() {

    }



    @Override
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

    @Override
    public double getVolatility(String table, String uuid) throws SQLException {
            ResultSet rs = MySQL.getResult("SELECT VOLATILITY FROM " + table + " WHERE UUID= ? ", uuid);
            double volatility;
            if (rs.next()) {
                volatility = rs.getDouble("VOLATILITY");
                return volatility;

        }
        return -1.0;
    }

    @Override
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

