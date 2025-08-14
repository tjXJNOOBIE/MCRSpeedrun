package com.tjxjnoobie.api.interfaces;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Interface for rating cache management
 */
public interface IRatingCache {




    void loadSpeedRunRatings(UUID uuid) throws SQLException;

    UUID getUuid();

    void setUuid(UUID playerUUID);

    double getRating() throws SQLException;

    void setRating(double playerRating) throws SQLException;

    double getVolatility();

    void setVolatility(double playerVolatility);

    double getDeviation();

    void setDeviation(double playerDeviation);
}