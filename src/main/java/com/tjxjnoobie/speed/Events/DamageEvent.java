package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.speed.managers.GameManager;
import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.enums.GameStateEnum;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class DamageEvent implements Listener {

    private final GameManager gameManager;
    private final GameState gameState;
    public DamageEvent(GameManager gameManager, GameState gameState) {
        this.gameManager = gameManager;
        this.gameState = gameState;

    }


    @EventHandler
    public void onDmg(EntityDamageEvent e) {
        UUID uuid = e.getEntity().getUniqueId();
        e.setCancelled(e.getEntity() instanceof Player && getGameManager().isSpectator(uuid));
        if(gameState.getCurrentState() == GameStateEnum.LOBBY || gameState.getCurrentState() == GameStateEnum.PREGAME){
            e.setCancelled(true);
        }else{
            e.setCancelled(false);
        }
    }
    @EventHandler
    public void onMobDmg(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = e.getDamager();
        UUID uuid = e.getEntity().getUniqueId();
        e.setCancelled(damager instanceof Player &&
                damaged instanceof LivingEntity && getGameManager().isSpectator(uuid));
        if(gameState.getCurrentState() == GameStateEnum.LOBBY || gameState.getCurrentState() == GameStateEnum.PREGAME){
            e.setCancelled(true);
        }else{
            e.setCancelled(false);
        }
    }

    public GameManager getGameManager(){
        return gameManager;
    }
}
