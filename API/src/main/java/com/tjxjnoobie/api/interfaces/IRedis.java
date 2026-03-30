package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
import com.tjxjnoobie.api.dependency.composition.domains.IInfrastructureDomain;

/**
 * Interface for Redis operations
 */
@ComposesToInterface(IInfrastructureDomain.class)
public interface IRedis {
    


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
