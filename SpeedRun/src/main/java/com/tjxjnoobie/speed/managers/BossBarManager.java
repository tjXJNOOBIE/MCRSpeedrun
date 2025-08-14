package com.tjxjnoobie.speed.managers;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {
    private BossBar bossBar;

    public BossBarManager(String title, BarColor barColor, BarStyle barStyle) {
        bossBar = Bukkit.createBossBar(title, barColor, barStyle);
    }

    public void addPlayer(Player player) {
        bossBar.addPlayer(player);


    }

    public BossBar getBossBar() {

        return bossBar;
    }

    public void removePlayer(Player player) {
        if (player != null && bossBar.getPlayers().contains(player)) {
            bossBar.removePlayer(player);
            System.out.println("Player removed from boss bar.");
        } else {
            System.out.println("Player is not in the boss bar or is null.");
        }
    }

    public void updateProgress(double progress) {
        bossBar.setProgress(progress); // Progress should be between 0.0 and 1.0
    }

    public void updateTitle(String title) {
        bossBar.setTitle(title);
    }
}