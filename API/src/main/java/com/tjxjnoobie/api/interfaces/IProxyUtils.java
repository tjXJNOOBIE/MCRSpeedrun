package com.tjxjnoobie.api.interfaces;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Interface for proxy utility operations
 */
public interface IProxyUtils {


    String getDiscordString();

    String getWebsiteString();

    String getPrefixString();

    String getStaffPrefixString();

    Component colorzie(String message);

    void sendMessageToPlayer(String username, String legacyMessage);

    @NotNull Component withStaffPrefix(String s);

    Component withPrefix(String message);

    Duration parseDuration(String durationStr);

    String formatDuration(Duration banDuration);
}