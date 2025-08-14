package com.tjxjnoobie.api.interfaces;

/**
 * Interface for Redis operations
 */
public interface IRedis {
    



    void connectToRedis() throws ClassNotFoundException;


    void disconnectFromRedis();

    void publishToRedis(String message);

    void publishRedisUpdate(String message);

    void handleRedisMessage(String message);
}