package com.tjxjnoobie.api.managers;

import com.tjxjnoobie.api.interfaces.IPlayerProfile;
import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerProfile implements IPlayerProfile {

    private UUID uuid;
    private String name;
    private String rank;
    private String grade;
    private int powerLevel;
    private double currency;
    private double globalRank;
    private boolean debugger;
    public HashMap<UUID, HashMap<String, Double>> currencies = new HashMap<>();
    private final Jedis jedis = Redis.jedis;

    public PlayerProfile(){

    }
    public PlayerProfile(UUID uuid, String name, String rank, String grade, int powerLevel, double currency, double globalRating, boolean debugger){
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.grade = grade;
        this.powerLevel = powerLevel;
        this.currency = currency;
        this.debugger = debugger;
        this.globalRank = globalRating;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }



    public String getGrade() {
        return grade;
    }

    public double getCurrency() {
        return currency;
    }

    public double getGlobalRank() {
        return globalRank;
    }

    public HashMap<UUID, HashMap<String, Double>> getCurrencies() {
        return currencies;
    }





    @Override
    public boolean playerExistsByUUID(UUID uuid, String table, String redisKey) throws SQLException {
        String existKey = redisKey +":"+ uuid.toString();
        // Check Redis first
        if (jedis.exists(existKey)) {
            System.out.println(uuid.toString() + " exists in Redis");
            return true;

        }

        // Fall back to check the database
        System.out.println(uuid.toString() + " does not exist in Redis, checking database...");
        ResultSet rs = MySQL.getResult("SELECT 1 FROM " + table + " WHERE UUID = ?", uuid.toString());
        boolean existsInDatabase = rs.next();
        System.out.println(uuid.toString() + " exists in database? " + existsInDatabase);
        return existsInDatabase;
    }

    @Override
    public boolean playerExistsFromUsername(String username, String table, String redisKey) throws SQLException {
        return playerExistsByUsername(username, table, redisKey,null);
    }

    @Override
    public boolean playerExistsFromUsername(String username, String table, String redisKey, String punishment) throws SQLException {
        return playerExistsByUsername(username, table, redisKey, punishment);
    }
    @Override
    public boolean playerExistsByUsername(String username, String table, String redisKey, String punishment) throws SQLException {
        String existKey = punishment == null ? (redisKey + ":" + username) : (redisKey + ":" + username + ":" + punishment);
        if (!jedis.hexists(existKey, username)) {
            System.out.println(username + " does not exist in Redis checking database");
            return false;
        }else{
            System.out.println(username+" does not exist in Redis checking database...");
            ResultSet rs = MySQL.getResult("SELECT 1 FROM " + table + " WHERE USERNAME = ?", uuid.toString());
            boolean exist = rs.next();
            System.out.println(username+" is in database? " + exist);
            return exist;
        }

    }
    @Override
    public String getUUIDFromUsername(String table, String username, String redisKey) throws SQLException {
        return getUUIDFromName(table, username, redisKey);
    }
    @Override
    public String getUUIDFromUsername(String table, String username, String redisKey, String punishment) throws SQLException {
        return getUUIDFromName(table, username, redisKey, punishment);
    }
    @Override
    public String getUUIDFromName(String table, String username, String redisKey, String punishment) throws SQLException {
        String existKey = punishment == null ? (redisKey + ":" + username) : (redisKey + ":" + username + ":" + punishment);
        if(!jedis.exists(existKey)) {
            ResultSet rs = MySQL.getResult("SELECT UUID FROM " + table + " WHERE USERNAME= ?", username);
            if (rs.next()) {
                System.out.println("Getting UUID from database via " + username);
                return rs.getString("UUID");
            }else{
                System.out.println("Getting UUID from Redis via key: "+ redisKey);
                System.out.println("Got "+jedis.hget(existKey,"UUID")+" from Redis");
                return jedis.hget(existKey,"UUID");
            }
        }
        return "Can't get UUID for "+username;
    }
    @Override
    public String getUUIDFromName(String table, String name, String redisKey) throws SQLException {
        System.out.println("Checking Redis for UUID via key: " + redisKey);
        String uuidFromRedis = jedis.hget(redisKey, "UUID");

        if (uuidFromRedis != null) {
            System.out.println("Got " + uuidFromRedis + " from Redis");
            return uuidFromRedis;
        } else {
            System.out.println("UUID not found in Redis, checking database via " + name);
            ResultSet rs = MySQL.getResult("SELECT UUID FROM " + table + " WHERE USERNAME= ?", name);
            if (rs.next()) {
                System.out.println("Getting UUID from database via " + name);
                return rs.getString("UUID");
            }
        }

        return "Can't get UUID for " + name;
    }
    @Override
    public void createProfile(UUID uuid, String name) throws SQLException {

        MySQL.executePreparedStatement("INSERT IGNORE INTO player_profile (UUID, NAME, RANK, POWERLEVEL, IP) VALUES (?, ?, ?, ?, ?)", uuid.toString(), name, "Member", "100", "Test");
        MySQL.executePreparedStatement("INSERT IGNORE INTO retention (UUID) VALUES (?)", uuid.toString());
        MySQL.executePreparedStatement("INSERT IGNORE INTO punish (UUID) VALUES (?)", uuid.toString());


    }



}

