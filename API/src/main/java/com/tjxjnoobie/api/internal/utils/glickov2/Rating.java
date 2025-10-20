package com.tjxjnoobie.api.internal.utils.glickov2;

import com.tjxjnoobie.api.interfaces.IRating;

public class Rating implements IRating {

    public String name; // not actually used by the calculation engine but useful to track whose rating is whose
    public double rating;
    public double ratingDeviation;
    public double volatility;
    public int numberOfResults = 0; // the number of results from which the rating has been calculated

    // the following variables are used to hold values temporarily whilst running calculations
    public double workingRating;
    public double workingRatingDeviation;
    public double workingVolatility;


    public Rating() {

    }
    public Rating(RatingCalculator ratingSystem) {
        this.rating = ratingSystem.getDefaultRating();
        this.ratingDeviation = ratingSystem.getDefaultRatingDeviation();
        this.volatility = ratingSystem.getDefaultVolatility();
    }

    public Rating(String name, RatingCalculator ratingSystem, double initRating, double initRatingDeviation, double initVolatility) {
        this.name = name;
        this.rating = initRating;
        this.ratingDeviation = initRatingDeviation;
        this.volatility = initVolatility;
    }

    /**
     * Return the average skill value of the player.
     *
     * @return double
     */
    public double getRating() {
        return this.rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * Return the average skill value of the player scaled down
     * to the scale used by the algorithm's internal workings.
     *
     * @return double
     */
    public double getGlicko2Rating() {
        return RatingCalculator.convertRatingToGlicko2Scale(this.rating);
    }

    /**
     * Set the average skill value, taking in a value in Glicko2 scale.
     *
     *
     */
    public void setGlicko2Rating(double rating) {
        this.rating = RatingCalculator.convertRatingToOriginalGlickoScale(rating);
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public double getRatingDeviation() {
        return ratingDeviation;
    }

    public void setRatingDeviation(double ratingDeviation) {
        this.ratingDeviation = ratingDeviation;
    }

    /**
     * Return the rating deviation of the player scaled down
     * to the scale used by the algorithm's internal workings.
     *
     * @return double
     */
    public double getGlicko2RatingDeviation() {
        return RatingCalculator.convertRatingDeviationToGlicko2Scale( ratingDeviation );
    }

    /**
     * Set the rating deviation, taking in a value in Glicko2 scale.
     *
     *
     */
    public void setGlicko2RatingDeviation(double ratingDeviation) {
        this.ratingDeviation = RatingCalculator.convertRatingDeviationToOriginalGlickoScale( ratingDeviation );
    }

    /**
     * Used by the calculation engine, to move interim calculations into their "proper" places.
     *
     */
    public void finaliseRating() {
        this.setGlicko2Rating(workingRating);
        this.setGlicko2RatingDeviation(workingRatingDeviation);
        this.setVolatility(workingVolatility);

        this.setWorkingRatingDeviation(0);
        this.setWorkingRating(0);
        this.setWorkingVolatility(0);
    }

    /**
     * Returns a formatted rating for inspection
     *
     * @return {ratingUid} / {ratingDeviation} / {volatility} / {numberOfResults}
     */
    @Override
    public String toString() {
        return name + " / " +
                rating + " / " +
                ratingDeviation + " / " +
                volatility + " / " +
                numberOfResults;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public void incrementNumberOfResults(int increment) {
        this.numberOfResults = numberOfResults + increment;
    }

    public String getName() {
        return name;
    }

    public void setWorkingVolatility(double workingVolatility) {
        this.workingVolatility = workingVolatility;
    }

    public void setWorkingRating(double workingRating) {
        this.workingRating = workingRating;
    }

    public void setWorkingRatingDeviation(double workingRatingDeviation) {
        this.workingRatingDeviation = workingRatingDeviation;
    }
}







