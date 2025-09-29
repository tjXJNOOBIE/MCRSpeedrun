package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.platform.minecraft.velocity.logs.PunishLog;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Interface for punishment management operations
 */
public interface IPunishManager {
    

    void setTimedPunishment(UUID targetUUID, String targetName, String punishmentTypeBans, Timestamp from, Timestamp timestamp, String s, String senderName, String number) throws SQLException;

    void logPunishment(UUID uuid, String punished, Timestamp startDate, Timestamp endDate, String punishment, String sender, String reason) throws SQLException;

    void setPunishNumber(String punishmentTypeBans, int i, UUID targetUUID) throws SQLException;

    int getPunishmentNumber(String punishmentTypeBans, UUID targetUUID, String targetName);

    void cacheAllPunishments();

    boolean isPunished(UUID targetUUID, String targetName, String banned);

    void logPunishmentByUsername(String targetName, Timestamp unbanTimeTS, Timestamp unbanTimeTS1, String unbans, String senderName, String s) throws SQLException;

    void setPunishedByUsername(String targetName, String bans, int i) throws SQLException;

    void incrementPunishLogCount(String punishLog, UUID targetUUID, String punishmentTypeWarns);

    int getPunishLogCount(UUID uuid, String punishment);

    PunishLog getActivePunishment(UUID uuid, String name, String bans) throws SQLException;

    void setPunished(UUID uuid, String bans, int i) throws SQLException;

}