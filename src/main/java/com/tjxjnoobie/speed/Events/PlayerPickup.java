package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.API.minecraft.utils.MCUtils;
import com.tjxjnoobie.speed.managers.GameManager;
import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.API.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.UUID;

public class PlayerPickup implements Listener {
    private final GameManager gameManager;
    private final Utils utils;
    private final MCUtils mcUtils;
    private final GameState gameState;

    public PlayerPickup(GameManager gameManager, Utils utils, MCUtils mcUtils, GameState gameState) {
        this.gameManager = gameManager;
        this.utils = utils;
        this.mcUtils = mcUtils;
        this.gameState = gameState;
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        Material item = e.getItem().getItemStack().getType();
        Entity entity = e.getEntity();
        Player player = (Player) entity;
        UUID uuid = entity.getUniqueId();
        String displayName = player.getDisplayName();
        String name = player.getName();
        Player allPlayers = mcUtils.getAllPlayers();
        Location allLocation = allPlayers.getLocation();
        boolean HasBlazeRod = gameManager.hasBlazeRod(uuid);
        boolean HasEye = gameManager.hasEnderEye(uuid);
        boolean HasPearl = gameManager.hasEnderPearl(uuid);
        int BlazeRodSize = gameManager.getBlazeRod().size();
        int PearlSize = gameManager.getEnderPearl().size();
        int EyeSize = gameManager.getEyeOfEnder().size();
        GameStateEnum currentState = gameState.getCurrentState();
        if(currentState != GameStateEnum.LOBBY) {
            if (item == Material.BLAZE_ROD && !HasBlazeRod && BlazeRodSize == 0 && entity instanceof Player) {
                Bukkit.broadcastMessage(utils.prefix + displayName + " has been the first to acquire a §cBlaze Rod!§f");
                gameManager.addBlazeRod(uuid, name);
                mcUtils.playSoundForAll(allLocation, Sound.ENTITY_BLAZE_DEATH, 1.0f, 1.0f);
            } else if (HasBlazeRod) {
                return;
            }

            if (item == Material.ENDER_EYE && !HasEye && EyeSize == 0 && entity instanceof Player) {
                Bukkit.broadcastMessage(utils.prefix + name + " has been the first to acquire a §cEye of Ender!§f");
                gameManager.addEnderEye(uuid, name);
                mcUtils.playSoundForAll(allLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            } else if (HasEye) {
                mcUtils.sendMessageToAll(utils.prefix+displayName+" has acquired a Eye of Ender!");
                return;
            }
            if (item == Material.ENDER_PEARL && !HasPearl && PearlSize == 0 && entity instanceof Player) {
                Bukkit.broadcastMessage(utils.prefix + name + " has been the first to acquire a §cEnder Pearl!§f");
                gameManager.addEnderEye(uuid, name);
                mcUtils.playSoundForAll(allLocation, Sound.ENTITY_ENDERMAN_DEATH, 1.0f, 1.0f);
            } else if (HasPearl) {
                return;
            }
        }else{
            return;
        }
    }
}
