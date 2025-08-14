package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameManager;
import com.tjxjnoobie.api.interfaces.IGameState;
import com.tjxjnoobie.api.interfaces.ISpeedRunContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class SpeedRunDamageEvent implements Listener {

    private final ISpeedRunContext speedRunContext;

    public SpeedRunDamageEvent(ISpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;


    }


    @EventHandler
    public void onDmg(EntityDamageEvent e) {
        IGameManager gameManager = speedRunContext.getGameManager();
        IGameState gameState = speedRunContext.getGameState();
        UUID uuid = e.getEntity().getUniqueId();
        e.setCancelled(e.getEntity() instanceof Player && gameManager.isSpectator(uuid));
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
        IGameManager gameManager = speedRunContext.getGameManager();
        UUID uuid = e.getEntity().getUniqueId();
        IGameState gameState = speedRunContext.getGameState();
        e.setCancelled(damager instanceof Player &&
                damaged instanceof LivingEntity && gameManager.isSpectator(uuid));
        if(gameState.getCurrentState() == GameStateEnum.LOBBY || gameState.getCurrentState() == GameStateEnum.PREGAME){
            e.setCancelled(true);
        }else{
            e.setCancelled(false);
        }
    }

}
