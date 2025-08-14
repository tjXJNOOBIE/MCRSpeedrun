package com.tjxjnoobie.api.managers;

import com.tjxjnoobie.api.interfaces.IRetentionManager;
import com.tjxjnoobie.api.internal.utils.RetentionCalculator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class RetentionManager implements IRetentionManager {

   private final Map<UUID, RetentionProfile> retentionCache = new HashMap<>();

    @Override
    public void setSpent(UUID uuid, double spent) throws SQLException {
        double spend = getSpent(uuid) +spent;
        MySQL.executePreparedStatement("UPDATE retention SET SPENT = ? WHERE UUID= ?",spend,uuid.toString());

    }
    @Override
    public void setPurchaseAmount(UUID uuid, double amount) throws SQLException {
        double purchases = getPurchaseAmount(uuid) + amount;
        MySQL.executePreparedStatement("UPDATE retention SET PURCHASES = ? WHERE UUID= ?",purchases,uuid.toString());

    }
    @Override
    public void setRejoins(UUID uuid, int rejoins) throws SQLException {
        int rejoin = getRejoins(uuid) +rejoins;
        MySQL.executePreparedStatement("UPDATE retention SET REJOINS = ? WHERE UUID= ?",rejoin,uuid.toString());

    }
    @Override
    public void setCreator(UUID uuid, String creator) throws SQLException {
        MySQL.executePreparedStatement("UPDATE retention SET CREATOR = ? WHERE UUID= ?",creator,uuid.toString());

    }
    @Override
    public void setTimePlayed(UUID uuid, double timeplayed) throws SQLException {
        double played = getTimePlayed(uuid) +timeplayed;
        MySQL.executePreparedStatement("UPDATE retention SET TIMEPLAYED = ? WHERE UUID= ?",played,uuid.toString());

    }
    @Override
    public double getSpent(UUID uuid) throws SQLException {
        ResultSet rs = MySQL.getResult("SELCET SPENT FROM retention WHERE UUID= ?",uuid.toString());
        if(rs.next()) {
            return rs.getDouble("SPENT");
        }
        return -1.0;
    }
    @Override
    public double getTimePlayed(UUID uuid) throws SQLException {
        ResultSet rs = MySQL.getResult("SELCET TIMEPLAYED FROM retention WHERE UUID= ?",uuid.toString());
        if(rs.next()) {
            return rs.getDouble("TIMEPLAYED");
        }
        return -1.0;
    }

    @Override
    public int getRejoins(UUID uuid) throws SQLException {
        ResultSet rs = MySQL.getResult("SELCET REJOINS FROM retention WHERE UUID= ?",uuid.toString());
        if(rs.next()) {
            return rs.getInt("REJOINS");
        }
        return -1;
    }
    @Override
    public int getPurchaseAmount(UUID uuid) throws SQLException {
        ResultSet rs = MySQL.getResult("SELCET PURCHASES FROM retention WHERE UUID= ?",uuid.toString());
        if(rs.next()) {
            return rs.getInt("PURCHASES");
        }
        return -1;
    }
    @Override
    public String getCreator(UUID uuid) throws SQLException {
        ResultSet rs = MySQL.getResult("SELCET CREATOR FROM retention WHERE UUID= ?",uuid.toString());
        if(rs.next()) {
            return rs.getString("CREATOR");
        }
        return "Error find creator";
    }
    @Override
    public void updateLastActiveDate(UUID uuid) throws SQLException {
        // Create a Timestamp representing the current date and time.
        Timestamp now = new Timestamp(System.currentTimeMillis());
        // Update the ACTIVEDATE column for this player's UUID.
        MySQL.executePreparedStatement("UPDATE retention SET LAST_ACTIVE = ? WHERE UUID = ?", now, uuid.toString());
    }
    @Override
    public void updateRetentionRatings() throws SQLException {
        // Retrieve all rows from the retention table.
        String query = "SELECT * FROM retention";
        ResultSet rs = MySQL.getResult(query);

        // Create a single instance of the RetentionCalculator to use for all rows.
        RetentionCalculator retentionCalculator = new RetentionCalculator();

        while (rs.next()) {
            // Get the UUID as a string from the current row.
            String uuidStr = rs.getString("UUID");

            // Check if the UUID is null or empty.
            if (uuidStr == null || uuidStr.trim().isEmpty()) {
                // Throw an exception or handle the error as desired.
                throw new IllegalArgumentException("Encountered null or empty UUID in database for row ID: " + rs.getInt("id"));
            }

            // Parse the UUID.
            UUID uuid = UUID.fromString(uuidStr);

            // Retrieve other columns (default values will be used if the column is NULL)
            double spent = rs.getDouble("SPENT");
            double timePlayed = rs.getDouble("TIMEPLAYED");
            int rejoins = rs.getInt("REJOINS");
            int purchases = rs.getInt("PURCHASES");

            // Calculate the retention rating directly.
            double rating = retentionCalculator.calculateRetention(spent, timePlayed, rejoins, purchases);

            // Update the retention rating (RR) column in the database using the UUID.
            MySQL.executePreparedStatement("UPDATE retention SET RR = ? WHERE UUID = ?", rating, uuid.toString());
        }
    }
    @Override
    public List<RetentionProfile> getAllPlayerData() throws SQLException {
        List<RetentionProfile> playerDataList = new ArrayList<>();
        // Only select rows where UUID is not null to ensure we get a valid retention profile.
        String query = "SELECT * FROM retention WHERE UUID IS NOT NULL";
        ResultSet rs = MySQL.getResult(query);
        while (rs.next()) {
            String uuidStr = rs.getString("UUID");
            // You could also generate a new UUID here if needed instead of skipping.
            if (uuidStr == null) {
                continue;
            }
            UUID uuid = UUID.fromString(uuidStr);
            double spent = rs.getDouble("SPENT");
            double timePlayed = rs.getDouble("TIMEPLAYED");
            int rejoins = rs.getInt("REJOINS");
            int purchases = rs.getInt("PURCHASES");
            String creator = rs.getString("CREATOR");
            double rr = rs.getDouble("RR"); // Existing retention rating (if any)
            playerDataList.add(new RetentionProfile(uuid, spent, timePlayed, rejoins, purchases, creator, rr));
        }
        return playerDataList;
    }
    @Override
    public List<RetentionProfile> getPlayerDataLast30Days() throws SQLException {
        List<RetentionProfile> playerDataList = new ArrayList<>();
        String query = "SELECT * FROM retention WHERE LAST_ACTIVE >= CURDATE() - INTERVAL 30 DAY";
        ResultSet rs = MySQL.getResult(query);
        while (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString("UUID"));
            double spent = rs.getDouble("SPENT");
            double timePlayed = rs.getDouble("TIMEPLAYED");
            int rejoins = rs.getInt("REJOINS");
            int purchases = rs.getInt("PURCHASES");
            int rr = rs.getInt("RR");
            String creator = rs.getString("CREATOR");
            playerDataList.add(new RetentionProfile(uuid, spent, timePlayed, rejoins, purchases,creator,rr));
        }
        return playerDataList;
    }
    @Override
    public void calculate30DayRetention() {
        try {
            // Connect to the database

            RetentionCalculator retentionCalculator = new RetentionCalculator();
            // Step 1: Backup the retention table with today's date.
            backupRetentionTable();

            // Step 2: Retrieve player data from the last 30 days.
            List<RetentionProfile> playerDataList = getPlayerDataLast30Days();

            // Step 3: Calculate the average retention score.
            double totalRetentionScore = 0.0;
            for (RetentionProfile data : playerDataList) {
                double score = retentionCalculator.calculateRetention(data.getSpent(), data.getTimePlayed(), data.getRejoins(), data.getPurchases());
                totalRetentionScore += score;
            }
            double averageRetentionScore = playerDataList.isEmpty() ? 0.0 : totalRetentionScore / playerDataList.size();
            System.out.println("Average Retention Score over the last 30 days: " + averageRetentionScore);

            // Step 4: Clear the retention table so new data can be accepted.
            clearRetentionTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param count
     */

    @Override
    public void loadMockPlayers(int count) {
        try {
            MySQL.connect();
            int i = 0;
            while (i < count) {
                // Generate a random UUID for the player.
                UUID uuid = UUID.randomUUID();

                // Generate random values for the player's metrics.
                double spent = Math.round((Math.random() * 50.0) * 100.0) / 100.0;         // Random spent between 0 and 50 dollars.
                double timePlayed = Math.round((Math.random() * 50.0) * 100.0) / 100.0;      // Random time between 0 and 50 hours.
                int rejoins = (int) (Math.random() * 30);                                  // Random rejoins between 0 and 29.
                int purchases = (int) (Math.random() * 3);                                 // Random purchases between 0 and 2.
                String creator = "Creator" + (i % 5);                                      // Cycle through 5 sample creators.

                // Insert the generated mock player record.
                insertMockPlayer(uuid, spent, timePlayed, rejoins, purchases, creator);

                i++;
            }
            System.out.println(count + " mock players inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void loadRetentionCache() throws SQLException {
        retentionCache.clear();
        String query = "SELECT * FROM retention";
        ResultSet rs = MySQL.getResult(query);
        while (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString("UUID"));
            double spent = rs.getDouble("SPENT");
            double timePlayed = rs.getDouble("TIMEPLAYED");
            int rejoins = rs.getInt("REJOINS");
            int purchases = rs.getInt("PURCHASES");
            String creator = rs.getString("CREATOR");
            double rr = rs.getDouble("RR");
            RetentionProfile profile = new RetentionProfile(uuid, spent, timePlayed, rejoins, purchases, creator, rr);
            retentionCache.put(uuid, profile);
        }
        System.out.println("Retention cache loaded with " + retentionCache.size() + " entries.");
    }

    /**
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Map<String, Double> getAverageRetentionByCreatorDescending() throws SQLException {
        // Get all player data for the last 30 days
        List<RetentionProfile> playerDataList = getPlayerDataLast30Days();

        // Maps to hold total retention scores and count of players per creator
        Map<String, Double> totalScores = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        // Instantiate the RetentionCalculator (or use its static method if applicable)
        RetentionCalculator retentionCalculator = new RetentionCalculator();

        // Iterate through each player's retention data
        for (RetentionProfile data : playerDataList) {
            double score = retentionCalculator.calculateRetention(
                    data.getSpent(), data.getTimePlayed(), data.getRejoins(), data.getPurchases());
            String creator = data.getCreator();

            totalScores.put(creator, totalScores.getOrDefault(creator, 0.0) + score);
            counts.put(creator, counts.getOrDefault(creator, 0) + 1);
        }

        // Compute the average score for each creator
        Map<String, Double> averageScores = new HashMap<>();
        for (String creator : totalScores.keySet()) {
            double avg = totalScores.get(creator) / counts.get(creator);
            averageScores.put(creator, avg);
        }

        // Sort the results in descending order by average retention score
        Map<String, Double> sortedAverageScores = averageScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));

        return sortedAverageScores;
    }
    @Override
    public List<RetentionProfile> getCachedRetentionDataSorted() {
        return retentionCache.values().stream()
                .sorted((a, b) -> Double.compare(b.getRR(), a.getRR()))
                .collect(Collectors.toList());
    }
    /**
     * Inserts a single mock player record into the retention table.
     *
     * @param uuid       The player's UUID.
     * @param spent      Total amount spent by the player.
     * @param timePlayed Total time played (in hours).
     * @param rejoins    Number of times the player rejoined.
     * @param purchases  Number of purchases made.
     * @param creator    The associated creator.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public void insertMockPlayer(UUID uuid, double spent, double timePlayed, int rejoins, int purchases, String creator) throws SQLException {
        // Assuming your retention table has the following columns:
        // UUID, SPENT, TIMEPLAYED, REJOINS, PURCHASES, CREATOR, ACTIVEDATE
        String query = "INSERT INTO retention (UUID, SPENT, TIMEPLAYED, REJOINS, PURCHASES, CREATOR, LAST_ACTIVE) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // Use the current timestamp for the ACTIVEDATE field.
        Timestamp now = new Timestamp(System.currentTimeMillis());
        MySQL.executePreparedStatement(query, uuid.toString(), spent, timePlayed, rejoins, purchases, creator, now);
    }

    /**
     *
     * @throws SQLException
     */
    @Override
    public void backupRetentionTable() throws SQLException {
        // Include year, month, day, hour, minute, and second for better accuracy.
        String dateSuffix = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        String backupTableName = "retention_" + dateSuffix;
        // Create a backup table using the current retention data.
        String query = "CREATE TABLE " + backupTableName + " AS SELECT * FROM retention;";
        MySQL.executePreparedStatement(query);
        System.out.println("Backup created: " + backupTableName);
    }

            // New method: Clear all data from the retention table.

    /**
     *
     * @throws SQLException
     */
    @Override
    public void clearRetentionTable() throws SQLException {
                MySQL.executePreparedStatement("TRUNCATE TABLE retention;");
                System.out.println("Retention table cleared.");
            }
        }


