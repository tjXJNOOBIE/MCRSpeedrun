package com.tjxjnoobie.api.interfaces;

import org.tavall.dependency.composition.domains.InfrastructureDomain;

/**
 * Interface for Redis operations
 */
public interface IRedis extends InfrastructureDomain {
    


    default void connectToRedis() {
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
