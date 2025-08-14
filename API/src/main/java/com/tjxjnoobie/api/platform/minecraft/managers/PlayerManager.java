package com.tjxjnoobie.api.platform.minecraft.managers;

import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerManager implements IPlayerManager, IUtils, IMCUtils {

    private final IGlobalContext globalContext;
    private final ISpeedRunContext speedRunContext;
    public PlayerManager(IGlobalContext globalContext, ISpeedRunContext speedRunContext) {
        this.globalContext = globalContext;
        this.speedRunContext = speedRunContext;
    }
    

    @Override
    public void makeSpectator(UUID uuid, String name, Player player) {

        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        try {
            IMCUtils mcUtils = globalContext.getMcUtils();
            IUtils utils = globalContext.getUtils();
            IGameManager gameManager = speedRunContext.getGameManager();
            Player target = gameManager.getInGamePlayers();
            
            hidePlayerFromAll(player,globalContext);
            player.sendMessage(prefix + "You are now a spectator");
            
            if (target != null) {
                player.sendMessage(getPrefix() + "You are spectating... " + target.getName());
                player.setSpectatorTarget(target);
            }
            
            player.setCanPickupItems(false);
            gameManager.addWatching(uuid, name);
            player.setGameMode(GameMode.SPECTATOR);
            
            sendDebugMessage(globalContext,player, "[PLAYER MANAGER] "+ "Player " + name + " is now spectating");
            
        } catch (Exception e) {
            sendDebugMessage(globalContext, player, "[PLAYER_MANAGER] "+ "Error making player spectator: " + e.getMessage());
            throw new RuntimeException("Failed to make player spectator", e);
        }
    }

    public void eliminatePlayer(UUID uuid, String name, Player player) {

        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        try {

            makeSpectator(uuid, name, player);
            player.sendMessage(getPrefix() + "§cYou were eliminated!");
            
            sendDebugMessage(globalContext,player, "[PLAYER_MANAGER] "+ "player " + name + " was eliminated");
            
        } catch (Exception e) {
            sendDebugMessage(globalContext,player, "[PLAYER_MANAGER] "+ "Error eliminating player: " +name + e.getMessage());
            throw new RuntimeException("Failed to eliminate player", e);
        }
    }




}
