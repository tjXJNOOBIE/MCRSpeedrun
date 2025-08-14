package com.tjxjnoobie.api.platform.minecraft.utils;

import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.UUID;

public class MCUtils implements IMCUtils {
    private final Plugin plugin;

    public ArrayList<String> debuggers = new ArrayList<>();
    private final IGlobalContext globalContext;
    public MCUtils(Plugin plugin, IGlobalContext globalContext) {
        this.plugin = plugin;
        this.globalContext = globalContext;
    }

    public void hidePlayerFromAll(Player toHide) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            viewer.hidePlayer(toHide);
        }
    }
    public Player getAllPlayers(){
        for(Player ap : Bukkit.getOnlinePlayers()) {
            if (ap != null) {
                return ap;
            } else {
                return null;
            }
        }
        return null;

    }

    public void playSoundForAll(Location location, Sound sound, float v, float v1) {
        for (Player ap : Bukkit.getOnlinePlayers()) {
            ap.playSound(location, sound, v,v1);

        }
    }

    public void sendMessageToAll(String message){
        for(Player ap : Bukkit.getOnlinePlayers()){
            ap.sendMessage(message);
        }
    }
    public void playDramaticBoom(Player player) {
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 0.5f); // Low-pitched cave sound
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 1.0f, 0.8f), 10L); // Portal activation after 10 ticks
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f), 20L); // Thunder after 20 ticks
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.6f), 30L); // Explosion after 30 ticks
    }
    public void cancelTask(BukkitTask task) {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            Bukkit.getLogger().info(task.toString()+" Task has been cancelled.");
        }
    }
public void sendDebugMessage(Player player,String message){
        IRankCache rankCache = globalContext.getRankCache();
        IUtils utils = globalContext.getUtils();
        UUID uuid = player.getUniqueId();
        IDebugger debugger = globalContext.getDebugger();
    if (rankCache.getPowerLevel(uuid) <= 10000 ||
            rankCache.hasPermission(uuid, "network.debug")
            && debugger.isDebugger(uuid)) {
        player.sendMessage(utils.getStaffPrefix() +message);
    }
}



    public boolean isDebugger(String name){
        return debuggers.contains(name);
    }
}
