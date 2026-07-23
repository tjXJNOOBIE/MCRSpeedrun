package com.tjxjnoobie.api.interfaces;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Interface for rating cache management
 */
public interface IRatingCache extends org.tavall.dependency.IDependencyInjectableInterface {




    default void loadSpeedRunRatings(UUID uuid) throws SQLException {

    }

    default UUID getUuid() {
        return null;
    }

    default void setUuid(UUID playerUUID){

    }

   default double getRating() throws SQLException {
        return 0.0;
    }

    default void setRating(double playerRating) throws SQLException {

    }

    default double getVolatility() {
        return 0.0;
    }

    default void setVolatility(double playerVolatility) {

    }

    default double getDeviation(){
        return 0.0;
    }

   default void setDeviation(double playerDeviation){

   }
}
