package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.platform.minecraft.managers.FightPair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

/**
 * Interface for fair fight system
 */
public interface IFairFight extends Listener {

    @EventHandler
    void onPlayerDamagePlayer(EntityDamageByEntityEvent event);

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event);

    void startOrResetFight(Player player1, Player player2);

    void endFight(Player player1, Player player2, boolean playerDied);
    void endAllFightsForPlayer(Player player);
    void hideOtherPlayers(Player player1, Player player2);
    void showAllPlayers(Player player1, Player player2);
    Player getOtherPlayer(FightPair fightPair, UUID playerUUID);
}