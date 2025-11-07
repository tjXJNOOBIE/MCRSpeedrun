package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.UUID;

public class SpeedRunChangeWorld implements Listener, IMCUtils, IGameManager, IGameState {





    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {

        World world = event.getPlayer().getWorld();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String displayName = player.getDisplayName();
        String name = player.getName();
        Location aplocation = getAllMinecraftPlayers().getLocation();
        Player aplayers = getAllMinecraftPlayers();
        GameStateEnum currentState = getCurrentState();
        if (isSpectator(uuid)) {
            return;
        }

        if (currentState == GameStateEnum.INGAME) {
            if (world.getEnvironment() == World.Environment.NETHER && getPlayersInNetherInt() == 0) {
                addInNether(uuid, name);
                Bukkit.broadcastMessage(getMinecraftPrefix() + displayName + " has entered the nether for the first time!");
                playSoundForAll(aplocation, Sound.ENTITY_GHAST_DEATH, 1.0f, 1.0f);
                return;
            }

            if (world.getEnvironment().equals(World.Environment.THE_END) && getPlayersInEndInt() == 0) {
                Bukkit.broadcastMessage(getMinecraftPrefix()+ displayName + " has entered the end for the first time!");
                addInEnder(uuid, name);
                playSoundForAll(aplocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            }
            return;
        }
    }
    @EventHandler
    public void onPlayerUsePortal(PlayerPortalEvent event) {

        World toWorld = event.getTo().getWorld();
        Location defaultLocation = event.getTo();
        String playerUUID = event.getPlayer().getUniqueId().toString();
        String playerWorld = playerUUID+"_"+toWorld.getEnvironment().name();
        World nether = Bukkit.getWorld(playerWorld);
        double x = defaultLocation.getX();
        double y = defaultLocation.getY();
        double z = defaultLocation.getZ();
        float pitch = defaultLocation.getPitch();
        float yaw = defaultLocation.getYaw();
        Location netherSpawn = new Location(nether, x,y,z,pitch,yaw);
        String displayName = event.getPlayer().getDisplayName();
        if (getCurrentState() == GameStateEnum.INGAME) {
            if (toWorld != null && toWorld.getEnvironment() == World.Environment.NORMAL) {
                Bukkit.broadcastMessage(getMinecraftPrefix()+ displayName + " has reentered the Overworld!");
            }

        if(toWorld.getEnvironment() == World.Environment.NETHER){
            if(nether == null){
                Bukkit.getLogger().info("Destination world does not exist, restoring to vanilla");
                return;
            }
            event.setTo(netherSpawn);
            Bukkit.broadcastMessage("Teleported to nether: " +toWorld.getName());

        }
    }
        }



}
