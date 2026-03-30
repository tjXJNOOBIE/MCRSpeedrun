package com.tjxjnoobie.api.platform.minecraft.managers;

import com.tjxjnoobie.api.interfaces.IFairFight;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FairFight implements Listener, IFairFight {

    private Plugin plugin;



    // Map to track active fights: FightPair -> BukkitTask
    private final Map<FightPair, BukkitTask> activeFights = new ConcurrentHashMap<>();

    // Map to track which players are currently hidden from each fighting player
    private final Map<UUID, Set<UUID>> hiddenPlayers = new ConcurrentHashMap<>();


    @EventHandler
    public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
        // Only handle player vs player damage
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        startOrResetFight(damager, damaged);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getPlayer();
        Player killer = deadPlayer.getKiller();

        // If a player dies and has a killer, end their fight
        if (killer != null) {
            endFight(deadPlayer, killer, true);
        } else {
            // Check if the dead player was in any fight and end it(Edge case if killer is null)
            endAllFightsForPlayer(deadPlayer);
        }
    }

    public void startOrResetFight(Player player1, Player player2) {
        FightPair fightPair = new FightPair(player1.getUniqueId(), player2.getUniqueId());

        // Cancel existing timer if fight is already active
        BukkitTask existingTask = activeFights.get(fightPair);
        if (existingTask != null) {
            existingTask.cancel();
        } else {
            // New fight - send debug message and hide other players
            sendDebugMessage(player1.getName() + " has started a fight with " + player2.getName());
            hideOtherPlayers(player1, player2);
        }

        // Start new 30-second timer
        BukkitTask newTask = new BukkitRunnable() {
            @Override
            public void run() {
                endFight(player1, player2, false);
            }
        }.runTaskLater(plugin, 30 * 20); // 30 seconds = 600 ticks

        activeFights.put(fightPair, newTask);
    }
    private Map<UUID, Set<UUID>> getHiddenPlayers() {
        return hiddenPlayers;
    }

    private Map<FightPair, BukkitTask> getActiveFights() {
        return activeFights;
    }
    private boolean isFighting(FightPair fight){
        return activeFights.containsKey(fight);
    }
    public void endFight(Player player1, Player player2, boolean playerDied) {
        FightPair fightPair = new FightPair(player1.getUniqueId(), player2.getUniqueId());

        // Cancel and remove the timer
        BukkitTask task = activeFights.remove(fightPair);
        if (task != null) {
            task.cancel();
        }

        // Send debug message
        sendDebugMessage("Fight between Player " + player1.getName() + " and Player " + player2.getName() + " has ended");

        // Show all players to both fighters
        showAllPlayers(player1, player2);
    }

    public void endAllFightsForPlayer(Player player) {
        UUID playerUUID = player.getUniqueId();

        // Find and end all fights involving this player
        List<FightPair> fightsToEnd = new ArrayList<>();
        for (FightPair fightPair : activeFights.keySet()) {
            if (fightPair.contains(playerUUID)) {
                fightsToEnd.add(fightPair);
            }
        }

        for (FightPair fightPair : fightsToEnd) {
            Player otherPlayer = getOtherPlayer(fightPair, playerUUID);
            if (otherPlayer != null) {
                endFight(player, otherPlayer, true);
            }
        }
    }

    public void hideOtherPlayers(Player player1, Player player2) {
        Set<UUID> hiddenFromPlayer1 = new HashSet<>();
        Set<UUID> hiddenFromPlayer2 = new HashSet<>();

        // Hide all other players from both fighters
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            UUID onlineUUID = onlinePlayer.getUniqueId();

            // Don't hide the fighters from each other
            if (onlineUUID.equals(player1.getUniqueId()) || onlineUUID.equals(player2.getUniqueId())) {
                continue;
            }

            // Hide this player from both fighters
            player1.hidePlayer(plugin, onlinePlayer);
            player2.hidePlayer(plugin, onlinePlayer);

            hiddenFromPlayer1.add(onlineUUID);
            hiddenFromPlayer2.add(onlineUUID);
        }

        hiddenPlayers.put(player1.getUniqueId(), hiddenFromPlayer1);
        hiddenPlayers.put(player2.getUniqueId(), hiddenFromPlayer2);
    }

    public void showAllPlayers(Player player1, Player player2) {
        // Show all previously hidden players to both fighters
        Set<UUID> hiddenFromPlayer1 = hiddenPlayers.remove(player1.getUniqueId());
        Set<UUID> hiddenFromPlayer2 = hiddenPlayers.remove(player2.getUniqueId());

        if (hiddenFromPlayer1 != null) {
            for (UUID hiddenUUID : hiddenFromPlayer1) {
                Player hiddenPlayer = Bukkit.getPlayer(hiddenUUID);
                if (hiddenPlayer != null && hiddenPlayer.isOnline()) {
                    player1.showPlayer(plugin, hiddenPlayer);
                }
            }
        }

        if (hiddenFromPlayer2 != null) {
            for (UUID hiddenUUID : hiddenFromPlayer2) {
                Player hiddenPlayer = Bukkit.getPlayer(hiddenUUID);
                if (hiddenPlayer != null && hiddenPlayer.isOnline()) {
                    player2.showPlayer(plugin, hiddenPlayer);
                }
            }
        }
    }

    public Player getOtherPlayer(FightPair fightPair, UUID playerUUID) {
        UUID otherUUID = fightPair.getOther(playerUUID);
        return otherUUID != null ? Bukkit.getPlayer(otherUUID) : null;
    }

    public void sendDebugMessage(String message) {
        // Send debug message to console and any online players with debug permissions
        Bukkit.getLogger().info(" " + message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("speedrun.debug") || player.isOp()) {
                player.sendMessage("§7[Debug] " + message);
            }
        }
    }

    // Helper class to represent a fight between two players
    // Record because we don't want to edit the fight mid-fight,
    // Someone must die or leave for the fight to end

}