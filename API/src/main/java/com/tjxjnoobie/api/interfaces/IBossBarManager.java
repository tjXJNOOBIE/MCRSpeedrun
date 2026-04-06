package com.tjxjnoobie.api.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.stream.StreamSupport;

/**
 * Interface for boss bar management operations
 */
public interface IBossBarManager extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {
    
    default BossBar createBossBar(String title, BarColor barColor, BarStyle barStyle) {
        return Bukkit.createBossBar(title, barColor, barStyle);
    }
    
    default BossBar getBossBar() {
        return createBossBar("", BarColor.WHITE, BarStyle.SOLID);
    }
    
    default BossBar getBossBar(String title) {
        if (title == null) {
            return null;
        }
        
        Iterator<KeyedBossBar> iterator = Bukkit.getBossBars();
        Iterable<KeyedBossBar> iterable = () -> iterator;
        
        return StreamSupport.stream(iterable.spliterator(), false)
            .filter(bar -> title.equals(bar.getTitle()))
            .findFirst()
            .orElse(null);
    }
    
    default void addPlayer(Player player) {
        getBossBar().addPlayer(player);
    }
    
    default void removePlayer(Player player) {
        if (player != null && getBossBar().getPlayers().contains(player)) {
            getBossBar().removePlayer(player);
            System.out.println("Player removed from boss bar.");
        } else {
            System.out.println("Player is not in the boss bar or is null.");
        }
    }
    
    default void updateProgress(double progress) {
        getBossBar().setProgress(progress); // Progress should be between 0.0 and 1.0
    }
    
    default void updateTitle(String title) {
        getBossBar().setTitle(title);
    }
    
    default String[] getActiveBossBars() {
        Iterator<KeyedBossBar> iterator = Bukkit.getBossBars();
        Iterable<KeyedBossBar> iterable = () -> iterator;
        
        return StreamSupport.stream(iterable.spliterator(), false)
            .map(BossBar::getTitle)
            .toArray(String[]::new);
    }
    
    /**
     * Cleans up all boss bars
     */
    default void cleanup() {
        if (getBossBar() != null) {
            getBossBar().removeAll();
        }
    }
}
