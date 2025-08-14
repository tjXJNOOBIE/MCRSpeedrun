package com.tjxjnoobie.api.platform.velocity.utils;

import com.tjxjnoobie.api.interfaces.IProxyUtils;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyUtils implements IProxyUtils {

    private final ProxyServer proxyServer;
    public Component staffPrefix = colorzie("&c&lZero &8&l»»&c ");
    public Component prefix = colorzie("&4&lZero&8&l »»&f ");
    public Component discord = colorzie("discord.gg/test");
    public Component website = colorzie("www.website.com");
    public String prefixString = "&e&lNovus&8&l »»&f ";
    public Component staffPrefixString = colorzie("&4&lNovus&8&l»»&c ");
    public String websiteString = "www.website.com";
    public String discordString = "discord.gg/test";

    public String preifxString = "&6&lZero &b&l»»&f ";

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    public ProxyUtils(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public String getDiscordString(){
        return discordString;
    }
    @Override
    public String getWebsiteString(){
        return websiteString;
    }
    @Override
    public String getPrefixString(){
        return discordString;
    }
    @Override
    public String getStaffPrefixString(){
        return discordString;
    }
    @Override
    public Component colorzie(String message){
        return LEGACY_SERIALIZER.deserialize(message);

    }
    @Override
    public void sendMessageToPlayer(String username, String legacyMessage) {
        Optional<Player> optionalPlayer = proxyServer.getPlayer(username);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(legacyMessage);
            player.sendMessage(message);
        } else {
            System.out.println("Player " + username + " is not online.");
        }
    }
    @Override
    public Component withStaffPrefix(String message) {
        Component messageComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        return staffPrefix.append(messageComponent);
    }
    @Override
    public Component withPrefix(String message) {
        Component messageComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        return prefix.append(messageComponent);
    }
    @Override
    public Duration parseDuration(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()
    || durationStr.equalsIgnoreCase("permanent")) {
            return null; // Permanent punishment
        }

        Pattern pattern = Pattern.compile("(?i)^(\\d+(\\.\\d+)?)([smhdwmo]|yr)$");
        Matcher matcher = pattern.matcher(durationStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid duration format: " + durationStr);
        }

        double value = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(3).toLowerCase();

        switch (unit) {
            case "s":
                return Duration.ofMillis((long) (value * 1000));
            case "m":
                return Duration.ofMillis((long) (value * 60 * 1000));
            case "h":
                return Duration.ofMillis((long) (value * 60 * 60 * 1000));
            case "d":
                return Duration.ofMillis((long) (value * 24 * 60 * 60 * 1000));
            case "w":
                return Duration.ofMillis((long) (value * 7 * 24 * 60 * 60 * 1000));
            case "mo":
                return Duration.ofMillis((long) (value * 30 * 24 * 60 * 60 * 1000));
            case "yr":
                return Duration.ofMillis((long) (value * 365 * 24 * 60 * 60 * 1000));
            default:
                throw new IllegalArgumentException("Unknown time unit: " + unit);
        }
    }
    @Override
    public String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append(" day").append(days > 1 ? "s" : "").append(", ");
        if (hours > 0) sb.append(hours).append(" hour").append(hours > 1 ? "s" : "").append(", ");
        if (minutes > 0) sb.append(minutes).append(" minute").append(minutes > 1 ? "s" : "").append(", ");
        sb.append(seconds).append(" second").append(seconds != 1 ? "s" : "");

        return sb.toString();

    }
}
