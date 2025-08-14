package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.managers.RetentionProfile;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IRetentionManager {


    void setSpent(UUID uuid, double spent) throws SQLException;

    void setPurchaseAmount(UUID uuid, double amount) throws SQLException;

    void setRejoins(UUID uuid, int rejoins) throws SQLException;

    void setCreator(UUID uuid, String creator) throws SQLException;

    void setTimePlayed(UUID uuid, double timeplayed) throws SQLException;

    double getSpent(UUID uuid) throws SQLException;

    double getTimePlayed(UUID uuid) throws SQLException;

    int getRejoins(UUID uuid) throws SQLException;

    int getPurchaseAmount(UUID uuid) throws SQLException;

    String getCreator(UUID uuid) throws SQLException;

    void updateLastActiveDate(UUID uuid) throws SQLException;

    void updateRetentionRatings() throws SQLException;

    List<RetentionProfile> getAllPlayerData() throws SQLException;

    List<RetentionProfile> getPlayerDataLast30Days() throws SQLException;

    void calculate30DayRetention();

    void loadMockPlayers(int count);

    void loadRetentionCache() throws SQLException;

    Map<String, Double> getAverageRetentionByCreatorDescending() throws SQLException;

    List<RetentionProfile> getCachedRetentionDataSorted();

    void insertMockPlayer(UUID uuid, double spent, double timePlayed, int rejoins, int purchases, String creator) throws SQLException;

    void backupRetentionTable() throws SQLException;

    // New method: Clear all data from the retention table.
    void clearRetentionTable() throws SQLException;
}
