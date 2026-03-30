package com.tjxjnoobie.proxy.commands;

import com.tjxjnoobie.api.interfaces.IUtils;
import com.tjxjnoobie.api.internal.utils.glickov2.Rating;
import com.tjxjnoobie.api.internal.utils.glickov2.RatingCalculator;
import com.tjxjnoobie.api.internal.utils.glickov2.RatingPeriodResults;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.console.style.LogColors;
import com.tjxjnoobie.api.platform.velocity.commands.interfaces.ISim;
import com.velocitypowered.api.command.SimpleCommand;

import java.util.*;

public class Sim implements SimpleCommand, IUtils, ISim {


    private static final Map<UUID, Double> final_time = new HashMap<>();
    private static final double TOLERANCE = 0.00001;

    public Sim() {

    }

    @Override
    public void execute(Invocation invocation) {
        RatingCalculator ratingCalculator = new RatingCalculator();
        RatingPeriodResults ratingPeriodResults = new RatingPeriodResults();
        Map<UUID, Rating> players = new HashMap<>();
        // Simulate final times (replace with your actual logic)

        Log.info(LogColors.cyan("Players Map (UUID -> Player Name):"));
        for (Map.Entry<UUID, Rating> entry : players.entrySet()) {
            Log.info(LogColors.blue("  UUID: " + entry.getKey() + " -> Player: " + entry.getValue().getName()));
        }
        // Create players
        Random random = new Random();

        for (int i = 1; i <= 3; i++) {
            String playerName = "Player " + i;

            // Generate a random rating between 2300 and 2800 for high Elo simulation
            double randomRating = 1500.00;

            // Generate random deviation and volatility within reasonable ranges
            double randomDeviation = 350.0 ; // Between 30 and 100
            double randomVolatility = 0.06; // Between 0.03 and 0.06

            // Create the player
            Rating player = new Rating(playerName, ratingCalculator, randomRating, randomDeviation, randomVolatility);
            players.put(UUID.randomUUID(), player);

        }

            String freshPlayerName = "Fresh Player";
            UUID freshPlayerId = UUID.randomUUID();
            Rating freshPlayer = new Rating(freshPlayerName, ratingCalculator, 1500.00, 350, 0.08);
            players.put(freshPlayerId, freshPlayer);

// Simulate final times for established players


            // Debugging: Print out the final_time map
            Log.info(LogColors.cyan("Final Times Map (UUID -> Time):"));
            for (Map.Entry<UUID, Double> entry : final_time.entrySet()) {
                Log.info(LogColors.blue("  UUID: " + entry.getKey() + " -> Time: " + String.format("%.2f", entry.getValue())));
            }

            // Check alignment between players and final_time
            for (UUID playerId : players.keySet()) {
                if (!final_time.containsKey(playerId)) {
                    Log.warn(LogColors.yellow("Player UUID " + playerId + " found in players but not in final_time map."));
                } else {
                    Log.success(LogColors.green("Player UUID " + playerId + " is aligned with final_time map."));
                }
            }

        // Simulate matches
        for (int match = 1; match <= 100; match++) {
            List<UUID> uuids = new ArrayList<>(players.keySet());
            List<Map.Entry<UUID, Rating>> entryList = new ArrayList<>(players.entrySet());
            Log.info(LogColors.boldYellow("=== Before Match " + match + " ==="));
            final_time.clear();
            for (UUID playerId : players.keySet()) {// Skip fresh player since it's already added
                double simulatedTime = 5.0 + (10.0 * random.nextDouble());
                double simulatedTime2 = 5.0 + (10.0 * random.nextDouble());

                final_time.put(playerId, simulatedTime);
                final_time.put(freshPlayerId, simulatedTime2);// Add each established player to final_time map
            }
            for (int i = 0; i < uuids.size(); i++) {
                for (int j = i + 1; j < uuids.size(); j++) {
                    if (i == j) continue;
                    Map.Entry<UUID, Rating> entry1 = entryList.get(i);
                    Map.Entry<UUID, Rating> entry2 = entryList.get(j);
                    UUID player1Id = entry1.getKey();
                    UUID player2Id = entry2.getKey();

                    Rating player1 = entry1.getValue();
                    Rating player2 = entry2.getValue();


                    double player1Time = getFinalTime(player1Id);
                    double player2Time = getFinalTime(player2Id);

                    double player1RatingBefore = player1.getRating();
                    double player2RatingBefore = player2.getRating();
                    Log.info(LogColors.purple(player1.getName() + " Rating Before: " + String.format("%.2f", player1RatingBefore) + " | " + player2.getName() + " Rating Before: " + String.format("%.2f", player2RatingBefore)));
                    if (player1Time == player2Time) {
                        // Draw
                        ratingPeriodResults.addDraw(player1, player2);
                        ratingCalculator.updateRatings(ratingPeriodResults);
                        double player1RatingAfter = player1.getRating();
                        double player2RatingAfter = player2.getRating();

                        double player1NetChange = player1RatingAfter - player1RatingBefore;
                        double player2NetChange = player2RatingAfter - player2RatingBefore;

                        // Debug line for tie with Elo change
                        Log.info(LogColors.yellow(player1.getName() + " tied with " + player2.getName() + " (Final Time: " + String.format("%.2f", player1Time) + "). Elo Change: " + player1.getName() + " (" + String.format("%+.2f", player1NetChange) + "), " + player2.getName() + " (" + String.format("%+.2f", player2NetChange) + ")"));
                    } else if (player1Time > player2Time) {
                        // Player 1 wins
                        ratingPeriodResults.addResult(player1, player2);
                        ratingCalculator.updateRatings(ratingPeriodResults);

                        double player1RatingAfter = player1.getRating();
                        double player2RatingAfter = player2.getRating();

// Calculate net change correctly
                        double player1NetChange = player1RatingAfter - player1RatingBefore;
                        double player2NetChange = player2RatingAfter - player2RatingBefore;

// Display results with proper labels for gain/loss
                        Log.success(LogColors.green(player1.getName() + " won versus " + player2.getName() + " (Final Time: " + String.format("%.2f", player1Time) + " vs " + String.format("%.2f", player2Time) + "). Net Gain/Loss: " + player1.getName() + " (" + String.format("%+.2f", player1NetChange) + "), " + player2.getName() + " (" + String.format("%+.2f", player2NetChange) + ")"));
                    } else {
                        // Player 2 wins
                        ratingPeriodResults.addResult(player2, player1);
                        ratingCalculator.updateRatings(ratingPeriodResults);

                        double player1RatingAfter = player1.getRating();
                        double player2RatingAfter = player2.getRating();

// Calculate net change correctly
                        double player1NetChange = player1RatingAfter - player1RatingBefore;
                        double player2NetChange = player2RatingAfter - player2RatingBefore;

// Display results with proper labels for gain/loss
                        Log.warn(LogColors.red(player1.getName() + " lost to " + player2.getName() + " (Final Time: " + String.format("%.2f", player1Time) + " vs " + String.format("%.2f", player2Time) + "). Net Gain/Loss: " + player1.getName() + " (" + String.format("%+.2f", player1NetChange) + "), " + player2.getName() + " (" + String.format("%+.2f", player2NetChange) + ")"));
                    }


                }
            }


            // Update ratings

            List<Map.Entry<UUID, Rating>> entryList2 = new ArrayList<>(players.entrySet());

            // Sort players by time
            entryList2.sort((p1, p2) -> {
                UUID uuid1 = (p1.getKey());
                UUID uuid2 = p2.getKey();
                return Double.compare(getFinalTime(uuid2), getFinalTime(uuid1));
            });
            // Print updated rankings
            Log.info(LogColors.boldYellow("=== After Match " + match + " ==="));
            for (int rank = 0; rank < entryList.size(); rank++) {
                Map.Entry<UUID, Rating> entry = entryList.get(rank);
                UUID playerId = entry.getKey();
                Rating player = entry.getValue();
                Log.info(LogColors.cyan((rank + 1) + ". " + player.getName() + ": " + String.format("%.2f", player.getRating()) + " (Final Time: " + String.format("%.2f", getFinalTime(playerId)) + ")"));
            }
        }
    }
    public static double getFinalTime(UUID uuid) {
        return final_time.getOrDefault(uuid, 0.0);
    }

    }




