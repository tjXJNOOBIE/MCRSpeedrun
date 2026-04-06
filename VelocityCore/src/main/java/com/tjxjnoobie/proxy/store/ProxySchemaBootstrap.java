package com.tjxjnoobie.proxy.store;

import com.tjxjnoobie.api.managers.MySQL;
import org.slf4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProxySchemaBootstrap {

    private final Logger logger;

    public ProxySchemaBootstrap(Logger logger) {
        this.logger = logger;
    }

    public void ensureInitialized() {
        execute("""
                CREATE TABLE IF NOT EXISTS player_profile (
                    `UUID` VARCHAR(36) NOT NULL,
                    `NAME` VARCHAR(16) NOT NULL,
                    `RANK` VARCHAR(64) NOT NULL DEFAULT 'Member',
                    `GRADE` VARCHAR(64) NULL,
                    `POWERLEVEL` INT NOT NULL DEFAULT 100,
                    `CURRENCY` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                    `GLOBALRANK` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                    `PERMISSIONS` VARCHAR(2048) NOT NULL DEFAULT '[]',
                    `IP` VARCHAR(64) NOT NULL DEFAULT '',
                    `DEBUGGER` TINYINT(1) NOT NULL DEFAULT 0,
                    PRIMARY KEY (`UUID`),
                    UNIQUE KEY `uk_player_profile_name` (`NAME`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
        execute("""
                CREATE TABLE IF NOT EXISTS retention (
                    `UUID` VARCHAR(36) NOT NULL,
                    `SPENT` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                    `TIMEPLAYED` DOUBLE NOT NULL DEFAULT 0,
                    `REJOINS` INT NOT NULL DEFAULT 0,
                    `PURCHASES` INT NOT NULL DEFAULT 0,
                    `CREATOR` VARCHAR(64) NULL,
                    `RR` DOUBLE NOT NULL DEFAULT 0,
                    `LAST_ACTIVE` DATETIME NULL,
                    PRIMARY KEY (`UUID`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
        execute("""
                CREATE TABLE IF NOT EXISTS punish (
                    `UUID` VARCHAR(36) NOT NULL,
                    `USERNAME` VARCHAR(16) NULL,
                    `MUTES` INT NOT NULL DEFAULT 0,
                    `KICKS` INT NOT NULL DEFAULT 0,
                    `BANS` INT NOT NULL DEFAULT 0,
                    `WARNS` INT NOT NULL DEFAULT 0,
                    `BANNED` TINYINT(1) NOT NULL DEFAULT 0,
                    `WARNED` TINYINT(1) NOT NULL DEFAULT 0,
                    `MUTED` TINYINT(1) NOT NULL DEFAULT 0,
                    `IPBANNED` TINYINT(1) NOT NULL DEFAULT 0,
                    `BAN_START` DATETIME NULL,
                    `BAN_END` DATETIME NULL,
                    `MUTE_START` DATETIME NULL,
                    `MUTE_END` DATETIME NULL,
                    `WARN_START` DATETIME NULL,
                    `WARN_END` DATETIME NULL,
                    `WARN_BY` VARCHAR(64) NULL,
                    `BAN_REASON` VARCHAR(255) NULL,
                    `BAN_BY` VARCHAR(64) NULL,
                    `MUTE_REASON` VARCHAR(255) NULL,
                    `WARN_REASON` VARCHAR(255) NULL,
                    `MUTE_BY` VARCHAR(64) NULL,
                    PRIMARY KEY (`UUID`),
                    KEY `idx_punish_username` (`USERNAME`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
        execute("""
                CREATE TABLE IF NOT EXISTS punish_logs (
                    `ID` BIGINT NOT NULL AUTO_INCREMENT,
                    `START_DATE` DATETIME NULL,
                    `END_DATE` DATETIME NULL,
                    `PUNISHMENT` VARCHAR(32) NOT NULL,
                    `REASON` VARCHAR(255) NULL,
                    `SENDER` VARCHAR(64) NULL,
                    `PUNISHED` VARCHAR(64) NULL,
                    `PUNISHED_UUID` VARCHAR(36) NOT NULL,
                    PRIMARY KEY (`ID`),
                    KEY `idx_punish_logs_player` (`PUNISHED_UUID`, `PUNISHMENT`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
        execute("""
                CREATE TABLE IF NOT EXISTS ranks (
                    `RANK` VARCHAR(64) NOT NULL,
                    `POWERLEVEL` INT NOT NULL DEFAULT 0,
                    `PERMISSIONS` VARCHAR(2048) NOT NULL DEFAULT '[]',
                    PRIMARY KEY (`RANK`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        upsertRank("Member", 100, "[]");
        upsertRank("God", 1000, "[]");

        logger.info("Ensured proxy rank schema is initialized.");
    }

    private void upsertRank(String rankName, int powerLevel, String permissionsJson) {
        try (PreparedStatement statement = MySQL.connection.prepareStatement("""
                INSERT INTO ranks (`RANK`, `POWERLEVEL`, `PERMISSIONS`)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    `POWERLEVEL` = VALUES(`POWERLEVEL`),
                    `PERMISSIONS` = VALUES(`PERMISSIONS`)
                """)) {
            statement.setString(1, rankName);
            statement.setInt(2, powerLevel);
            statement.setString(3, permissionsJson);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to seed proxy ranks.", exception);
        }
    }

    private void execute(String sql) {
        try (PreparedStatement statement = MySQL.connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to initialize proxy schema.", exception);
        }
    }
}
