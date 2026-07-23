package com.tjxjnoobie.api.interfaces;

/**
 * Interface for rating calculations and management
 */
public interface IRating extends org.tavall.dependency.IDependencyInjectableInterface {
    
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
    

}
