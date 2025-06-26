package com.tjxjnoobie.API.utils;

public class RetentionCalculator

{
    // Sample normalization caps (these can be adjusted as needed)
    private static final double MAX_PURCHASE_AMOUNT = 50.0; // Maximum purchase amount in dollars
    private static final double MAX_TIME_PLAYED = 50.0;     // Maximum time played in hours
    private static final int MAX_RETURN_JOINS = 30;         // Maximum number of return joins
    private static final int MAX_NUMBER_OF_PURCHASES = 2;   // Maximum number of purchases

    // Weights for each parameter (should sum to 1.0)
    private static final double WEIGHT_PURCHASE_AMOUNT = 0.35;
    private static final double WEIGHT_TIME_PLAYED = 0.30;
    private static final double WEIGHT_RETURN_JOINS = 0.20;
    private static final double WEIGHT_NUMBER_OF_PURCHASES = 0.15;

    /**
     * Calculates the retention score for a player.
     *
     * @param purchaseAmount     Total monetary value of purchases made by the player in the past 14 days.
     * @param timePlayed         Total hours the player has played in the past 30 days.
     * @param returnJoins        Total number of times the player has returned to the game in the past 30 days.
     * @param numberOfPurchases  Total number of purchases made by the player in the past 14 days.
     * @return                   The retention score formatted as a double with two decimal places.
     */
    public double calculateRetention(double purchaseAmount, double timePlayed, int returnJoins, int numberOfPurchases) {
        // Normalize each parameter to a scale of 0 to 1
        double normalizedPurchaseAmount = Math.min(purchaseAmount / MAX_PURCHASE_AMOUNT, 1.0);
        double normalizedTimePlayed = Math.min(timePlayed / MAX_TIME_PLAYED, 1.0);
        double normalizedReturnJoins = Math.min((double) returnJoins / MAX_RETURN_JOINS, 1.0);
        double normalizedNumberOfPurchases = Math.min((double) numberOfPurchases / MAX_NUMBER_OF_PURCHASES, 1.0);

        // Calculate the weighted sum
        double retentionScore = (WEIGHT_PURCHASE_AMOUNT * normalizedPurchaseAmount) +
                (WEIGHT_TIME_PLAYED * normalizedTimePlayed) +
                (WEIGHT_RETURN_JOINS * normalizedReturnJoins) +
                (WEIGHT_NUMBER_OF_PURCHASES * normalizedNumberOfPurchases);

        // Scale to 100 and format to two decimal places
        return Math.round(retentionScore * 10000.0) / 100.0;
    }
}
