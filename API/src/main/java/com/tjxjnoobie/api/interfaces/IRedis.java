package com.tjxjnoobie.api.interfaces;

/**
 * Interface for Redis operations
 */
public interface IRedis {
    



    default void connectToRedis() throws ClassNotFoundException{
    }

    default void disconnectFromRedis(){

    }

    default void publishToRedis(String message){

    }

    default void publishRedisUpdate(String message){

    }

    default void handleRedisMessage(String message){
}
}