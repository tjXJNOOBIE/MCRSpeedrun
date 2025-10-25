package com.tjxjnoobie.api.platform.minecraft.utils;

import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.UUID;

public class MCUtils implements IMCUtils {

    private Plugin plugin;
    public ArrayList<String> debuggers = new ArrayList<>();
    @Inject
    private ISpeedRunContext speedRunContext;
    @Inject private ILocalServerMetaData localServerMetaData;

    @Override
    public ISpeedRunContext getSpeedRunContext(){
        return speedRunContext;
    }

    @Override
    public String getMinecraftPrefix(){
        return localServerMetaData.getServerID();
    }

    @Override
    public String getMinecraftStaffPrefix(){
        return localServerMetaData.getMinecraftStaffInGamePrefix();
    }

    @Override
    public void hidePlayerFromAll(Player toHide) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            viewer.hidePlayer(toHide);
        }
    }

    @Override
    public Player getAllPlayers() {
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
        IRankCache rankCache = speedRunContext.getRankCache();
        IUtils utils = speedRunContext.getUtils();
        UUID uuid = player.getUniqueId();
        IDebugger debugger = speedRunContext.getDebugger();
        if (rankCache.getPowerLevel(uuid) <= 10000 ||
                rankCache.hasPermission(uuid, "network.debug")
                        && debugger.isDebugger(uuid)) {
            player.sendMessage(utils.getStaffPrefix() + message);
        }
    }


    @Override
    public boolean isDebugger(String name) {
        return debuggers.contains(name);
    }
}
