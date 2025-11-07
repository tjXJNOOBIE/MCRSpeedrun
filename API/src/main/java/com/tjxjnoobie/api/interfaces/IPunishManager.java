package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.platform.minecraft.velocity.logs.PunishLog;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Interface for punishment management operations
 */
public interface IPunishManager {


    default void setTimedPunishment(UUID targetUUID, String targetName, String punishmentTypeBans,
                                    Timestamp from, Timestamp timestamp, String s, String senderName,
                                    String number) throws SQLException {
    }

    default void logPunishment(UUID uuid, String punished, Timestamp startDate, Timestamp endDate,
                               String punishment, String sender, String reason) throws SQLException {
    }

    default void setPunishNumber(String punishmentTypeBans, int i, UUID targetUUID) throws SQLException {
    }

    default int getPunishmentNumber(String punishmentTypeBans, UUID targetUUID, String targetName) {
        return 0;
    }

    default void cacheAllPunishments() {
    }

    default boolean isPunished(UUID targetUUID, String targetName, String banned) {
        return false;
    }

    default void logPunishmentByUsername(String targetName, Timestamp unbanTimeTS, Timestamp unbanTimeTS1,
                                         String unbans, String senderName, String s) throws SQLException {
    }

    default void setPunishedByUsername(String targetName, String bans, int i) throws SQLException {
    }

    default void incrementPunishLogCount(String punishLog, UUID targetUUID, String punishmentTypeWarns) {
    }

    default int getPunishLogCount(UUID uuid, String punishment) {
        return 0;
    }

    default PunishLog getActivePunishment(UUID uuid, String name, String bans) throws SQLException {
        return null;
    }

    default void setPunished(UUID uuid, String bans, int i) throws SQLException {
    }
}