package com.tjxjnoobie.api.interfaces;

import java.sql.SQLException;

/**
 * Interface for rating API operations
 */
public interface IRatingAPI {
    


    double getRating(String speedrunRanking, String string) throws SQLException;

    double getVolatility(String speedrunRanking, String string) throws SQLException;

    double getDeviation(String speedrunRanking, String string) throws SQLException;
}