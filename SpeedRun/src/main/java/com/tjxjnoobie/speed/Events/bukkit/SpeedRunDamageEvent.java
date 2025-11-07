package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameManager;
import com.tjxjnoobie.api.interfaces.IGameState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class SpeedRunDamageEvent implements Listener, IGameState, IGameManager {




    @EventHandler
    public void onDmg(EntityDamageEvent e) {
        UUID uuid = e.getEntity().getUniqueId();
        e.setCancelled(e.getEntity() instanceof Player && isSpectator(uuid));
        if(getCurrentState() == GameStateEnum.LOBBY || getCurrentState() == GameStateEnum.PREGAME){
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
                damaged instanceof LivingEntity && isSpectator(uuid));
        if(getCurrentState() == GameStateEnum.LOBBY || getCurrentState() == GameStateEnum.PREGAME){
            e.setCancelled(true);
        }else{
            e.setCancelled(false);
        }
    }

}
