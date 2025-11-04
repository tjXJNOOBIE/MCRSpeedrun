package com.tjxjnoobie.api.platform.minecraft.velocity.managers;

import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IPlayerProfile;
import com.tjxjnoobie.api.interfaces.IPunishManager;
import com.tjxjnoobie.api.interfaces.IUtils;
import com.tjxjnoobie.api.managers.MySQL;
import com.tjxjnoobie.api.managers.Redis;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.minecraft.velocity.logs.PunishLog;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class PunishManager implements IUtils, IPunishManager, IPlayerProfile {

    @Inject private IGlobalContext globalContext;
    private final Jedis jedis = Redis.jedis;


    @Override
    public void cacheAllPunishments() {

        String query = "SELECT UUID, USERNAME, MUTES, KICKS, BANS, WARNS, BANNED, WARNED, MUTED, " +
                "IPBANNED, BAN_START, BAN_END, MUTE_START, MUTE_END, WARN_START, WARN_END, WARN_BY, BAN_REASON, BAN_BY, MUTE_REASON, WARN_REASON, MUTE_BY FROM punish";
        try {
            ResultSet rs = MySQL.getResult(query);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                String uuid = rs.getString("UUID");
                String name = rs.getString("USERNAME");
                String banReason = rs.getString("BAN_REASON");
                String bannedBy = rs.getString("BAN_BY");
                String warnReason = rs.getString("WARN_REASON");
                String warnedBy = rs.getString("WARN_BY");
                String muteReason = rs.getString("MUTE_REASON");
                String mutedBy = rs.getString("MUTE_BY");
                int mutes = rs.getInt("MUTES");
                int kicks = rs.getInt("KICKS");
                int warns = rs.getInt("WARNS");
                int bans = rs.getInt("BANS");
                boolean isBanned = rs.getBoolean("BANNED");
                boolean isIPBanned = rs.getBoolean("IPBANNED");
                boolean isWarned = rs.getBoolean("WARNED");
                Timestamp banStart = rs.getTimestamp("BAN_START");
                Timestamp banEnd = rs.getTimestamp("BAN_END");
                Timestamp warnStart = rs.getTimestamp("WARN_START");
                Timestamp warnEnd = rs.getTimestamp("WARN_END");
                Timestamp muteStart = rs.getTimestamp("MUTE_START");
                Timestamp muteEnd = rs.getTimestamp("MUTE_END");


                // Initialize date strings

                String banStartStr = formatTimestamp(banStart, formatter);
                String banEndStr = formatTimestamp(banEnd, formatter);
                String warnStartStr = formatTimestamp(warnStart, formatter);
                String warnEndStr = formatTimestamp(warnEnd, formatter);
                String muteStartStr = formatTimestamp(muteStart, formatter);
                String muteEndStr = formatTimestamp(muteEnd, formatter);


                // Create a Redis hash for the player
                String redisKey = "punish:" + uuid;
                String userNameKey = "punish:" + name;
                jedis.hset(redisKey, "USERNAME", name);
                jedis.hset(redisKey, "MUTES", String.valueOf(mutes));
                jedis.hset(redisKey, "KICKS", String.valueOf(kicks));
                jedis.hset(redisKey, "WARNS", String.valueOf(warns));
                jedis.hset(redisKey, "BANS", String.valueOf(bans));
                jedis.hset(redisKey, "BANNED", isBanned ? "1" : "0");
                jedis.hset(redisKey, "IPBANNED", isIPBanned ? "1" : "0");
                jedis.hset(redisKey, "WARNED", isWarned ? "1" : "0");
                jedis.hset(redisKey, "BAN_START", Objects.requireNonNullElse(banStartStr, "NONE"));
                jedis.hset(redisKey, "BAN_END", Objects.requireNonNullElse(banEndStr, "NONE"));
                jedis.hset(redisKey, "BAN_REASON", Objects.requireNonNullElse(banReason, "NONE"));
                jedis.hset(redisKey, "BANNED_BY", Objects.requireNonNullElse(bannedBy, "NONE"));
                jedis.hset(redisKey, "WARN_START", Objects.requireNonNullElse(warnStartStr, "NONE"));
                jedis.hset(redisKey, "WARN_END", Objects.requireNonNullElse(warnEndStr, "NONE"));
                jedis.hset(redisKey, "WARN_REASON", Objects.requireNonNullElse(warnReason, "NONE"));
                jedis.hset(redisKey, "WARNED_BY", Objects.requireNonNullElse(warnedBy, "NONE"));
                jedis.hset(redisKey, "MUTE_START", Objects.requireNonNullElse(muteStartStr, "NONE"));
                jedis.hset(redisKey, "MUTE_END", Objects.requireNonNullElse(muteEndStr, "NONE"));
                jedis.hset(redisKey, "MUTE_REASON", Objects.requireNonNullElse(muteReason, "NONE"));
                jedis.hset(redisKey, "MUTED_BY", Objects.requireNonNullElse(mutedBy, "NONE"));
                // Username hashes
                jedis.hset(userNameKey, "USERNAME", name);
                jedis.hset(userNameKey, "UUID", uuid);
                jedis.hset(userNameKey, "MUTES", String.valueOf(mutes));
                jedis.hset(userNameKey, "KICKS", String.valueOf(kicks));
                jedis.hset(userNameKey, "WARNS", String.valueOf(warns));
                jedis.hset(userNameKey, "BANS", String.valueOf(bans));
                jedis.hset(userNameKey, "BANNED", isBanned ? "1" : "0");
                jedis.hset(userNameKey, "IPBANNED", isIPBanned ? "1" : "0");
                jedis.hset(userNameKey, "WARNED", isWarned ? "1" : "0");
                jedis.hset(userNameKey, "BAN_START", Objects.requireNonNullElse(banStartStr, "NONE"));
                jedis.hset(userNameKey, "BAN_END", Objects.requireNonNullElse(banEndStr, "NONE"));
                jedis.hset(userNameKey, "BAN_REASON", Objects.requireNonNullElse(banReason, "NONE"));
                jedis.hset(userNameKey, "BANNED_BY", Objects.requireNonNullElse(bannedBy, "NONE"));
                jedis.hset(userNameKey, "WARN_START", Objects.requireNonNullElse(warnStartStr, "NONE"));
                jedis.hset(userNameKey, "WARN_END", Objects.requireNonNullElse(warnEndStr, "NONE"));
                jedis.hset(userNameKey, "WARN_REASON", Objects.requireNonNullElse(warnReason, "NONE"));
                jedis.hset(userNameKey, "WARNED_BY", Objects.requireNonNullElse(warnedBy, "NONE"));
                jedis.hset(userNameKey, "MUTE_START", Objects.requireNonNullElse(muteStartStr, "NONE"));
                jedis.hset(userNameKey, "MUTE_END", Objects.requireNonNullElse(muteEndStr, "NONE"));
                jedis.hset(userNameKey, "MUTE_REASON", Objects.requireNonNullElse(muteReason, "NONE"));
                jedis.hset(userNameKey, "MUTED_BY", Objects.requireNonNullElse(mutedBy, "NONE"));
            }
            System.out.println("All punishment data has been cached to Redis.");
        } catch (SQLException e) {
            System.out.println("Could not cache punishment data to Redis");
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPunished(UUID uuid, String username, String punishment) {
        // Check Redis cache first
        String cacheKey = "punish:" + uuid.toString();
        String cachedValue = jedis.hget(cacheKey, punishment);

        String usernameCacheKey = "punish:" + username;
        String usernameCachedValue = jedis.hget(usernameCacheKey, punishment);
        System.out.println("Checking Redis key validation for UUID via key " + cacheKey);

        if (cachedValue != null) {
            System.out.println(username + " punishment status for " + punishment + " is " + cachedValue + " via UUID");

            if (cachedValue.equals("1")) {
                System.out.println("Cached value is true via UUID " + uuid.toString());
                return true;
            } else if (cachedValue.equals("0")) {
                System.out.println("Cached value is false via UUID " + uuid.toString());

                return false;
            } else {
                System.out.println("Cached value is not a boolean");
                return false;
            }
        } else if (usernameCachedValue != null) {
            System.out.println("Checking Redis key validation for USERNAME via key " + usernameCacheKey);
            if (usernameCachedValue.equals("1")) {
                System.out.println("Cached value is true via USERNAME");
                return true;
            } else if (usernameCachedValue.equals("0")) {
                System.out.println("Cached value is false via USERNAME");
                return false;
            } else {
                System.out.println("Cached value is not a boolean");
                return false;
            }
        } else {
            System.out.println("No suitable Redis key found, doing database lookup");
            // If not in cache, query MySQL
            try {
                ResultSet rs = MySQL.getResult("SELECT " + punishment + " FROM punish WHERE UUID= ?", uuid.toString());
                if (rs.next()) {
                    boolean isPunished = rs.getBoolean(punishment);
                    if (isPunished) {
                        jedis.hset(cacheKey, punishment, "1");
                        jedis.hset(usernameCacheKey, punishment, "1");
                        return true;
                    } else {
                        jedis.hset(cacheKey, punishment, "0");
                        jedis.hset(usernameCacheKey, punishment, "0");
                        return false;
                    }


                }
            } catch (SQLException e) {
                System.out.println("Could not get lookup player via UUID or USERNAME " + uuid.toString() + " / " + username);
            }
        }
        return false;
    }

    @Override
    public int getPunishmentNumber(String punishment, UUID uuid, String username) {
        // Check Redis cache first
        String cacheKey = "punish:" + uuid.toString();
        String cachedValue = jedis.hget(cacheKey, punishment);
        String usernameCacheKey = "punish:" + username;
        String usernameCachedValue = jedis.hget(usernameCacheKey, punishment);
        if (cachedValue != null) {
            return Integer.parseInt(cachedValue);
        } else if (usernameCachedValue != null) {
            return Integer.parseInt(usernameCachedValue);

        } else {

            // If not in cache, query MySQL
            System.out.println("No Redis information found for UUID or USERNAME " + uuid.toString() + " / " + username + " accessing database...");
            int punishmentNumber = -1;
            try {
                ResultSet rs = MySQL.getResult("SELECT " + punishment + " FROM punish WHERE UUID = ?", uuid.toString());
                if (rs.next()) {
                    punishmentNumber = rs.getInt(punishment);
                    // Cache the result in Redis
                    System.out.println("UUID " + uuid.toString() + " found in database, caching in Redis");
                    jedis.hset(cacheKey, punishment, Integer.toString(punishmentNumber));
                    jedis.hset(usernameCacheKey, punishment, Integer.toString(punishmentNumber));
                } else {
                    System.out.println("UUID " + uuid.toString() + " not found in database");
                }
            } catch (SQLException e) {
                System.out.println("Can't get punishment number");
            }

            return punishmentNumber;
        }
    }

    @Override
    public void incrementPunishLogCount(String redisKey, UUID uuid, String punishment) {
        // Determine the base key for the punishment logs
        String baseKey = redisKey + ":" + uuid.toString() + ":" + punishment;

        // Find the next available number for the hash key
        int nextNumber = 0;
        while (jedis.exists(baseKey + ":" + nextNumber)) {
            nextNumber++;
        }

        // Create a new hash entry with the next available number
        String newHashKey = baseKey + ":" + nextNumber;
        jedis.hset(newHashKey, "WARN_AMOUNT", "1"); // Initialize with a default value or as needed
    }
    @Override
    public int getPunishLogCount(UUID uuid, String punishment) {
        // Construct the base key pattern for the punishment logs
        String baseKeyPattern = "punishLog:" + uuid.toString() + ":" + punishment + ":*";

        // Use Redis SCAN command to iterate over keys matching the pattern
        int count = 0;
        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams scanParams = new ScanParams().match(baseKeyPattern);

        do {
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
            cursor = scanResult.getCursor();
            count += scanResult.getResult().size();
        } while (!cursor.equals(ScanParams.SCAN_POINTER_START));

        return count;
    }
    // Ends with ED(e.g) BANNED,WARNED_BY
    public String getPunishedBy(UUID uuid, String punishment) throws SQLException {
        String punishedBy = "";
        String cacheKey = "punish:" + uuid;
        String cacheValue = jedis.hget(cacheKey, punishment);
        if (cacheValue != null) {
            return jedis.hget(cacheKey, punishment + "BY");
        }
        ResultSet rs = MySQL.getResult("SELECT " + punishment + "_BY WHERE UUID= ?", uuid);
        return rs.getString(punishment + "_BY");


    }

    @Override
    public PunishLog getActivePunishment(UUID uuid, String username, String punishment) throws SQLException {

        String punishLogKey = "punishLog:" + uuid.toString()+":"+punishment;
        String punishLogUsernameKey = "punishLog:" + username+":"+punishment;
        System.out.println("Searching for active punishment via UUID and USERNAME " + uuid.toString() + " / " + username);
        if (jedis.hexists(punishLogKey, "PUNISHED_UUID")) {
            System.out.println("Punishment found via UUID " + uuid.toString());
            String punishmentStr = jedis.hget(punishLogKey, "PUNISHMENT");
            String punishedUUID = jedis.hget(punishLogKey, "UUID");
            String startDateStr = jedis.hget(punishLogKey, "START_DATE");
            String endDateStr = jedis.hget(punishLogKey, "END_DATE");
            Timestamp startDate = Timestamp.valueOf(startDateStr);
            Timestamp endDate = (endDateStr.equalsIgnoreCase("Permanent") ? null : Timestamp.valueOf(endDateStr));
            String sender = jedis.hget(punishLogKey, "SENDER");
            String punished = jedis.hget(punishLogKey, "PUNISHED");
            String reason = jedis.hget(punishLogKey, "REASON");
            return new PunishLog(punishedUUID, punishmentStr, startDate, (endDateStr.equalsIgnoreCase("None") ? null : endDate), sender, punished, reason);
        } else if (jedis.hexists(punishLogUsernameKey, "PUNISHED_UUID")) {
           System.out.println("Punishment not found via UUID... checking USERNAME " + username);
            System.out.println("Punishment found for via USERNAME " + username);
            String punishedUUID = jedis.hget(punishLogUsernameKey, "UUID");
            String punishmentStr = jedis.hget(punishLogUsernameKey, "PUNISHMENT");
            String startDateStr = jedis.hget(punishLogUsernameKey, "START_DATE");
            String endDateStr = jedis.hget(punishLogUsernameKey, "END_DATE");
            Timestamp startDate = Timestamp.valueOf(startDateStr);
            Timestamp endDate = (endDateStr.equalsIgnoreCase("Permanent") ? null : Timestamp.valueOf(endDateStr));
            String sender = jedis.hget(punishLogUsernameKey, "SENDER");
            String punished = jedis.hget(punishLogUsernameKey, "PUNISHED");
            String reason = jedis.hget(punishLogUsernameKey, "REASON");
            return new PunishLog(punishedUUID, punishmentStr, startDate, (endDateStr.equalsIgnoreCase("None") ? null : endDate), sender, punished, reason);
        } else {
            System.out.println("Punishment not found in Redis via USERNAME " + username);
            System.out.println("Checking database via UUID "+ uuid.toString());
            String query = "SELECT PUNISHED_UUID, PUNISHMENT, START_DATE, END_DATE, SENDER, PUNISHED, REASON " +
                    "FROM punish_logs WHERE PUNISHED_UUID= ? AND PUNISHMENT= ? ";
            ResultSet rs = MySQL.getResult(query, uuid.toString(), punishment);
            if (rs.next()) {
                System.out.println("Punishment found via UUID " + uuid.toString() + " in database");
                Timestamp startDate = rs.getTimestamp("START_DATE");
                Timestamp endDate = rs.getTimestamp("END_DATE");  // may be null
                // Check if the punishment is active:
                Timestamp now = new Timestamp(System.currentTimeMillis());
                if (startDate.before(now) && (endDate.after(now))) {
                    System.out.println(username+" has a active punishment. PUNISHMENT: " + punishment +" END DATE: " + endDate.toString());
                    String punishedUUID = rs.getString("PUNISHED_UUID");
                    String punishmentStr = rs.getString("PUNISHMENT");
                    String sender = rs.getString("SENDER");
                    String punished = rs.getString("PUNISHED");
                    String reason = rs.getString("REASON");
                    return new PunishLog(punishedUUID, punishmentStr, startDate, endDate, sender, punished, reason);
                }
            } else{
                System.out.println("No punishment found for " + username+" in database or Redis");
            }
        }
        return null;
    }



    @Override
    public void setTimedPunishment(UUID uuid ,String username, String punishment, Timestamp punishStart, Timestamp punishEnd, String reason, String punishedBy, String result) throws SQLException {
        String booleanResult = switch (punishment) {
            case "BANS" -> "BANNED";
            case "WARN" -> "WARNED";
            case "MUTE" -> "MUTED";
            default -> null;
        };

        String redisKey = "punish:"+uuid.toString();
        String usernameRedisKey = "punish:"+username;

        int punishNumber = getPunishmentNumber(punishment,uuid,username) +1;
        jedis.hset(redisKey,punishment, String.valueOf(punishNumber));
        jedis.hset(redisKey, booleanResult,result);
        // Username hashes
        jedis.hset(usernameRedisKey, String.valueOf(punishNumber),result);
        jedis.hset(usernameRedisKey, booleanResult,result);
        setPunishNumber(punishment,punishNumber,uuid);
        logPunishment(uuid,username,punishStart,punishEnd,punishment,punishedBy,reason);
    }
    @Override
    public void logPunishmentByUsername(String username , Timestamp startDate, Timestamp endDate , String punishment, String sender, String reason) throws SQLException {
        String punishKey = "punishLog:"+username+":"+punishment;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String UUIDStr = getUUIDFromUsername("punish",username,"punishLog");
        jedis.hset(punishKey, "PUNISHED_UUID",UUIDStr);
        jedis.hset(punishKey,"START_DATE",Objects.requireNonNullElse(startDate.toLocalDateTime().format(formatter),"NONE"));
        jedis.hset(punishKey,"END_DATE",Objects.requireNonNullElse(endDate.toLocalDateTime().format(formatter), "Permanent"));
        jedis.hset(punishKey, "PUNISHMENT", punishment);
        jedis.hset(punishKey, "SENDER",sender);
        jedis.hset(punishKey,"REASON",Objects.requireNonNullElse(reason,"Not provided"));
        // Log into MySQL
        MySQL.executePreparedStatement("INSERT INTO punish_logs (START_DATE, END_DATE, PUNISHMENT, REASON, SENDER, PUNISHED, PUNISHED_UUID) VALUES (?, ?, ?, ?, ?, ?, ?)",startDate,endDate,punishment,reason,sender,username,UUIDStr);

    }
    @Override
    public void logPunishment(UUID uuid, String punished, Timestamp startDate, Timestamp endDate, String punishment, String sender, String reason) throws SQLException {
        int logNumber = getPunishLogCount(uuid,punishment);

        String punishKey = "punishLog:"+uuid.toString()+":"+punishment+":"+logNumber;
        String usernamePunishKey = "punishLog:"+punished+":"+punishment+":"+logNumber;


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startDateStr = startDate.toLocalDateTime().format(formatter);
        jedis.hset(punishKey,"PUNISHED_UUID",uuid.toString());
        jedis.hset(punishKey,"START_DATE",startDateStr);

        jedis.hset(punishKey,"END_DATE",Objects.requireNonNullElse(endDate.toLocalDateTime().format(formatter), "Permanent"));
        jedis.hset(punishKey,"PUNISHMENT", punishment);
        jedis.hset(punishKey,"PUNISHED", punished);
        jedis.hset(punishKey,"SENDER",sender);
        jedis.hset(punishKey,"REASON",Objects.requireNonNullElse(reason,"Not provided"));
        // Username hashes
        jedis.hset(usernamePunishKey,"PUNISHED_UUID",uuid.toString());
        jedis.hset(usernamePunishKey,"START_DATE",startDateStr);
        jedis.hset(usernamePunishKey,"END_DATE",Objects.requireNonNullElse(endDate.toLocalDateTime().format(formatter), "Permanent"));jedis.hset(usernamePunishKey,"END_DATE","Permanent");
        jedis.hset(usernamePunishKey,"PUNISHMENT", punishment);
        jedis.hset(usernamePunishKey,"PUNISHED", punished);
        jedis.hset(usernamePunishKey,"SENDER",sender);
        jedis.hset(usernamePunishKey,"REASON",reason);
        // Log into MySQL
        MySQL.executePreparedStatement("INSERT INTO punish_logs (START_DATE, END_DATE, PUNISHMENT, REASON, SENDER, PUNISHED ,PUNISHED_UUID) VALUES (?, ?, ?, ?, ?, ?, ?)",startDate,endDate,punishment,reason,sender,punished,uuid.toString());
    }




    @Override
    public void setPunishNumber(String punishment, int punishNumber, UUID uuid) throws SQLException {
        String redisKey = "punish:"+uuid.toString();
        MySQL.executePreparedStatement("UPDATE punish SET "+punishment+"= ? WHERE UUID= ?",punishNumber,uuid.toString());
        jedis.hset(redisKey,punishment, String.valueOf(punishNumber));

    }

    @Override
    public void setPunished(UUID uuid,String punishment, int isPunished) throws SQLException {
        String redisKey = "punish:"+uuid.toString();
        MySQL.executePreparedStatement("UPDATE punish SET "+punishment+"= ? WHERE UUID= ?",isPunished,uuid.toString());
        jedis.hset(redisKey,punishment,String.valueOf(isPunished));

    }


    @Override
    public void setPunishedByUsername(String username,String punishment, int isPunished) throws SQLException {
        String redisKey = "punish:"+username;
        MySQL.executePreparedStatement("UPDATE punish SET "+punishment+"= ? WHERE UUID= ?",isPunished,username);
        jedis.hset(redisKey,punishment,String.valueOf(isPunished));

    }

}

