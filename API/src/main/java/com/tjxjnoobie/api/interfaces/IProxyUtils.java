package com.tjxjnoobie.api.interfaces;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Interface for proxy utility operations
 */
public interface IProxyUtils extends org.tavall.dependency.IDependencyInjectableInterface {


    default String getDiscordString() {
        return null;
    }

    default String getWebsiteString(){
        return null;
    }

    default String getPrefixString(){
        return null;
    }

    default String getStaffPrefixString(){
        return null;
    }

    default Component colorzie(String message){
        return null;
    }

    default void sendMessageToPlayer(String username, String legacyMessage){

    }

    default @NotNull Component withStaffPrefix(String s){
        return null;
    }

    default Component withPrefix(String message){
        return null;
    }

    default Duration parseDuration(String durationStr){
        return null;
    }

    default String formatDuration(Duration banDuration){
        return null;
    }
}
