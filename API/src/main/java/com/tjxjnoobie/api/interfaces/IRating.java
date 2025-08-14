package com.tjxjnoobie.api.interfaces;

/**
 * Interface for rating calculations and management
 */
public interface IRating {
    
    /**
     * Gets the current rating value
     * @return The rating value
     */
    double getRating();
    
    /**
     * Sets the rating value
     * @param rating The rating to set
     */
    void setRating(double rating);
    
    /**
     * Gets the rating deviation
     * @return The rating deviation
     */
    double getRatingDeviation();
    
    /**
     * Sets the rating deviation
     * @param deviation The deviation to set
     */
    void setRatingDeviation(double deviation);
    
    /**
     * Gets the volatility
     * @return The volatility value
     */
    double getVolatility();
    
    /**
     * Sets the volatility
     * @param volatility The volatility to set
     */
    void setVolatility(double volatility);
    
    /**
     * Calculates expected score against another rating
     * @param opponentRating The opponent's rating
     * @param opponentDeviation The opponent's rating deviation
     * @return Expected score (0.0 to 1.0)
     */
    double calculateExpectedScore(double opponentRating, double opponentDeviation);
    
    /**
     * Updates rating based on match result
     * @param opponentRating The opponent's rating
     * @param opponentDeviation The opponent's rating deviation
     * @param actualScore The actual match result (1.0 = win, 0.0 = loss, 0.5 = draw)
     */
    void updateRating(double opponentRating, double opponentDeviation, double actualScore);
    
    /**
     * Gets the confidence interval for the rating
     * @return Array containing [lower bound, upper bound]
     */
    double[] getConfidenceInterval();
    
    /**
     * Checks if the rating is provisional (high uncertainty)
     * @return true if rating is provisional
     */
    boolean isProvisional();
    
    /**
     * Gets the number of games played
     * @return Number of games
     */
    int getGamesPlayed();
    
    /**
     * Increments the games played counter
     */
    void incrementGamesPlayed();
    
    /**
     * Resets the rating to default values
     */
    void reset();
    
    /**
     * Creates a copy of this rating
     * @return A new rating instance with the same values
     */
    IRating copy();
}