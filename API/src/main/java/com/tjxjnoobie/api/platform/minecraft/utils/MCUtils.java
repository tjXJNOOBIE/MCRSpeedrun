package com.tjxjnoobie.api.platform.minecraft.utils;

import com.tjxjnoobie.api.dependency.annotations.DelegatesToInterface;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.UUID;

@DelegatesToInterface(getClassForDelegation = IMCUtils.class)
public class MCUtils implements IMCUtils, IDebugger, IRankCache {
    //TODO: First test candidate for our new injection system
    private Plugin plugin;
    public ArrayList<String> debuggers = new ArrayList<>();
    @Inject
    private ISpeedRunContext speedRunContext;

    @Override
    public ISpeedRunContext getSpeedRunContext(){
        return speedRunContext;
    }

    @Override
    public String getMinecraftPrefix(){
        return getLocalServerPrefix();
    }



    @Override
    public String getMinecraftServerID(){
        return getLocalServerID();
    }

    @Override
    public void hidePlayerFromAll(Player toHide) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            viewer.hidePlayer(toHide);
        }
    }

    @Override
    public Player getAllMinecraftPlayers() {
        for (Player ap : Bukkit.getOnlinePlayers()) {
            if (ap != null) {
                return ap;
            } else {
                return null;
            }
        }
        return null;

    }

    @Override
    public void playSoundForAll(Location location, Sound sound, float v, float v1) {
        for (Player ap : Bukkit.getOnlinePlayers()) {
            ap.playSound(location, sound, v, v1);

        }
    }

    @Override
    public void sendMessageToAll(String message) {
        for (Player ap : Bukkit.getOnlinePlayers()) {
            ap.sendMessage(message);
        }
    }

    @Override
    public void playDramaticBoom(Player player) {
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 0.5f); // Low-pitched cave sound
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 1.0f, 0.8f), 10L); // Portal activation after 10 ticks
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f), 20L); // Thunder after 20 ticks
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.6f), 30L); // Explosion after 30 ticks
    }
    @Override
    public void cancelBukkitTask(BukkitTask task) {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            Bukkit.getLogger().info(task.toString() + " Task has been cancelled.");
        }
    }
    @Override
    public void sendDebugMessage(Player player, String message) {
        //TODO: Move depends to field injection
        UUID uuid = player.getUniqueId();
        if (getPowerLevel(uuid) <= 10000 ||
                hasPermission(uuid, "network.debug")
                        && isDebugger(uuid)) {
            player.sendMessage(getMinecraftStaffInGamePrefix() + message);
        }
    }


    @Override
    public boolean isDebugger(String name) {
        return debuggers.contains(name);
    }
}
