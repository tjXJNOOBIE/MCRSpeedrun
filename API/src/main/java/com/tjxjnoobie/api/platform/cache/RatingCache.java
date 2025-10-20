package com.tjxjnoobie.api.platform.cache;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.interfaces.IRatingAPI;
import com.tjxjnoobie.api.interfaces.IRatingCache;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;


public class RatingCache implements IRatingCache {

    public HashMap<UUID, Double> speedRunRating = new HashMap<>();
    private HashMap<UUID, Double> speedRunDeviation = new HashMap<>();
    private HashMap<UUID, Double> speedRunVolatility = new HashMap<>();
    private UUID uuid;
    private double rating;
    private double volatility;
    private double deviation;
    @Inject private IRatingAPI ratingAPI;


    public RatingCache() {

    }
    public RatingCache(IRatingAPI ratingAPI, UUID uuid, double rating, double deviation, double volatility) {
        this.ratingAPI = ratingAPI;
        this.uuid = uuid;
        this.rating = rating;
        this.deviation = deviation;
        this.volatility = volatility;
    }

    /**
     * Loads the speed run ratings, volatility, and deviation for a given UUID
     * from the "speedrun_ranking" table using the RatingAPI. Updates the
     * internal state of the RatingCache instance with the retrieved values.
     *
     * @param uuid the UUID of the player whose ratings are to be loaded
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void loadSpeedRunRatings(UUID uuid) throws SQLException {
        double Rating = ratingAPI.getRating("speedrun_ranking",uuid.toString());
        double Vol = ratingAPI.getVolatility("speedrun_ranking",uuid.toString());
        double Deviation = ratingAPI.getDeviation("speedrun_ranking",uuid.toString());
        rating = Rating;
        volatility = Vol;
        deviation = Deviation;
    }
    @Override
    public UUID getUuid() {
        return uuid;
    }
    @Override
    public void setUuid(UUID playerUUID) {
        uuid = playerUUID;
    }
    @Override
    public double getRating() throws SQLException {
        return rating;
    }
    @Override
    public void setRating(double playerRating) throws SQLException {
       rating = playerRating;;
    }
    @Override
    public double getVolatility() {
        return volatility;
    }
    @Override
    public void setVolatility(double playerVolatility) {
        volatility = playerVolatility;
    }
    @Override
    public double getDeviation() {
        return deviation;
    }
    @Override
    public void setDeviation(double playerDeviation) {
        deviation = playerDeviation;
    }


}
