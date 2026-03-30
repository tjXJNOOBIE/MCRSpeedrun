package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
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

 public class SpeedRunPlayerPickup implements Listener, IMCUtils, IGameManager {


    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {

        Material item = e.getItem().getItemStack().getType();
        Entity entity = e.getEntity();
        Player player = (Player) entity;
        UUID uuid = entity.getUniqueId();
        String displayName = player.getDisplayName();
        String name = player.getName();
        Player allPlayers = getAllMinecraftPlayers();
        Location allLocation = allPlayers.getLocation();
        boolean HasBlazeRod = hasBlazeRod(uuid);
        boolean HasEye = hasEnderEye(uuid);
        boolean HasPearl = hasEnderPearl(uuid);
        int BlazeRodSize = getBlazeRod().size();
        int PearlSize = getEnderPearl().size();
        int EyeSize = getEyeOfEnder().size();
        GameStateEnum currentState = getCurrentState();
        if(currentState != GameStateEnum.LOBBY) {
            if (item == Material.BLAZE_ROD && !HasBlazeRod && BlazeRodSize == 0 && entity instanceof Player) {
                Bukkit.broadcastMessage(getMinecraftPrefix() + displayName + " has been the first to acquire a §cBlaze Rod!§f");
                addBlazeRod(uuid, name);
                playSoundForAll(allLocation, Sound.ENTITY_BLAZE_DEATH, 1.0f, 1.0f);
            } else if (HasBlazeRod) {
                return;
            }

            if (item == Material.ENDER_EYE && !HasEye && EyeSize == 0 && entity instanceof Player) {
                Bukkit.broadcastMessage(getMinecraftPrefix() + name + " has been the first to acquire a §cEye of Ender!§f");
                addEnderEye(uuid, name);
                playSoundForAll(allLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            } else if (HasEye) {
                sendMessageToAll(getMinecraftPrefix()+displayName+" has acquired a Eye of Ender!");
                return;
            }
            if (item == Material.ENDER_PEARL && !HasPearl && PearlSize == 0 && entity instanceof Player) {
                Bukkit.broadcastMessage(getMinecraftPrefix() + name + " has been the first to acquire a §cEnder Pearl!§f");
                addEnderEye(uuid, name);
                playSoundForAll(allLocation, Sound.ENTITY_ENDERMAN_DEATH, 1.0f, 1.0f);
            } else if (HasPearl) {
                return;
            }
        }else{
            return;
        }
    }
}