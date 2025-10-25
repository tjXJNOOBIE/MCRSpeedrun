package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
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

public class SpeedRunChangeWorld implements Listener, IMCUtils {

    @Inject private ISpeedRunContext speedRunContext;

    public SpeedRunChangeWorld( ISpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;

    }



    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        IMCUtils mcUtils = speedRunContext.getMcUtils();
        IGameManager gameManager = speedRunContext.getGameManager();
        IGameState gameState = speedRunContext.getGameState();
        World world = event.getPlayer().getWorld();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String displayName = player.getDisplayName();
        String name = player.getName();
        Location aplocation = mcUtils.getAllPlayers().getLocation();
        Player aplayers = mcUtils.getAllPlayers();
        GameStateEnum currentState = gameState.getCurrentState();
        if (gameManager.isSpectator(uuid)) {
            return;
        }

        if (currentState == GameStateEnum.INGAME) {
            if (world.getEnvironment() == World.Environment.NETHER && gameManager.getPlayersInNetherInt() == 0) {
                gameManager.addInNether(uuid, name);
                Bukkit.broadcastMessage(getMinecraftPrefix() + displayName + " has entered the nether for the first time!");
                mcUtils.playSoundForAll(aplocation, Sound.ENTITY_GHAST_DEATH, 1.0f, 1.0f);
                return;
            }

            if (world.getEnvironment().equals(World.Environment.THE_END) && gameManager.getPlayersInEndInt() == 0) {
                Bukkit.broadcastMessage(getMinecraftPrefix()+ displayName + " has entered the end for the first time!");
                gameManager.addInEnder(uuid, name);
                mcUtils.playSoundForAll(aplocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            }
            return;
        }
    }
    @EventHandler
    public void onPlayerUsePortal(PlayerPortalEvent event) {
        IGameMode gameMode = speedRunContext.getGameMode();
        IGameManager gameManager = speedRunContext.getGameManager();
        IGameState gameState = speedRunContext.getGameState();
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
        if (gameState.getCurrentState() == GameStateEnum.INGAME) {
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
