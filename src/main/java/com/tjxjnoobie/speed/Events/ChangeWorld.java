package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.API.managers.GameMode;
import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.API.minecraft.utils.MCUtils;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.speed.managers.*;
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

public class ChangeWorld implements Listener {

    private final Utils utils;
    private final MCUtils mcUtils;
    private final GameMode gameMode;
    private final GameManager gameManager;
    private final GameState gameState;

    public ChangeWorld(GameManager gameManager, Utils utils, MCUtils mcUtils, GameMode gameMode, GameState gameState) {
        this.gameManager = gameManager;
        this.utils = utils;
        this.mcUtils = mcUtils;
        this.gameMode = gameMode;
        this.gameState = gameState;
    }



    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        World world = event.getPlayer().getWorld();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String displayName = player.getDisplayName();
        String name = player.getName();
        Location aplocation = mcUtils.getAllPlayers().getLocation();
        Player aplayers = mcUtils.getAllPlayers();
        GameStateEnum currentState = gameState.getCurrentState();
        if (getGameManager().isSpectator(uuid)) {
            return;
        }

        if (currentState == GameStateEnum.INGAME) {
            if (world.getEnvironment() == World.Environment.NETHER && getGameManager().getPlayersInNetherInt() == 0) {
                getGameManager().addInNether(uuid, name);
                Bukkit.broadcastMessage(Prefix() + displayName + " has entered the nether for the first time!");
                mcUtils.playSoundForAll(aplocation, Sound.ENTITY_GHAST_DEATH, 1.0f, 1.0f);
                return;
            }

            if (world.getEnvironment().equals(World.Environment.THE_END) && getGameManager().getPlayersInEndInt() == 0) {
                Bukkit.broadcastMessage(Prefix() + displayName + " has entered the end for the first time!");
                getGameManager().addInEnder(uuid, name);
                mcUtils.playSoundForAll(aplocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
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
        if (gameState.getCurrentState() == GameStateEnum.INGAME) {
            if (toWorld != null && toWorld.getEnvironment() == World.Environment.NORMAL) {
                Bukkit.broadcastMessage(Prefix() + displayName + " has reentered the Overworld!");
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

    public boolean isDebugger(String name){
        return mcUtils.isDebugger(name);
    }
    public void getLogger(){
         Bukkit.getLogger();
    }
    public String staffPrefix(){
        return utils.getStaffPrefix();
    }
    public String Prefix(){
        return utils.getPrefix();
    }
    public GameManager getGameManager(){
        return gameManager;
    }


}
