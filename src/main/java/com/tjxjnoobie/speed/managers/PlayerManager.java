package com.tjxjnoobie.speed.managers;

import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.API.minecraft.utils.MCUtils;
import com.tjxjnoobie.abstracts.AbstractManager;
import com.tjxjnoobie.interfaces.IGameManager;
import com.tjxjnoobie.interfaces.IMCUtils;
import com.tjxjnoobie.interfaces.IUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class PlayerManager extends AbstractManager<SpeedRunContext> implements IUtils {

    public PlayerManager(SpeedRunContext speedRunContext) {
        super(speedRunContext);
    }
    
    @Override
    protected void doInitialize() throws Exception {
        // Initialize any resources needed for player management
        sendDebugMessage(null, "[PLAYER_MANAGER]", "PlayerManager initialized successfully");
    }
    
    @Override
    public GlobalContext getGlobalContext() {
        // PlayerManager doesn't have direct access to GlobalContext
        // This could be improved by having SpeedRunContext implement a common interface
        return null;
    }

    public void makeSpectator(UUID uuid, String name, Player player) {
        ensureInitialized();
        
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
            IMCUtils mcUtils = context.getMcUtils();
            IUtils utils = context.getUtils();
            IGameManager gameManager = context.getGameManager();
            Player target = gameManager.getInGamePlayers();
            
            mcUtils.hidePlayerFromAll(player);
            player.sendMessage(getPrefix() + "You are now a spectator");
            
            if (target != null) {
                player.sendMessage(getPrefix() + "You are spectating... " + target.getName());
                player.setSpectatorTarget(target);
            }
            
            player.setCanPickupItems(false);
            gameManager.addWatching(uuid, name);
            player.setGameMode(GameMode.SPECTATOR);
            
            sendDebugMessage(player, "[PLAYER_MANAGER]", "Player " + name + " is now spectating");
            
        } catch (Exception e) {
            sendDebugMessage(player, "[PLAYER_MANAGER]", "Error making player spectator: " + e.getMessage());
            throw new RuntimeException("Failed to make player spectator", e);
        }
    }

    public void eliminatePlayer(UUID uuid, String name, Player player) {
        ensureInitialized();
        
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        try {
            IUtils utils = context.getUtils();
            makeSpectator(uuid, name, player);
            player.sendMessage(getPrefix() + "§cYou were eliminated!");
            
            sendDebugMessage(player, "[PLAYER_MANAGER]", "Player " + name + " was eliminated");
            
        } catch (Exception e) {
            sendDebugMessage(player, "[PLAYER_MANAGER]", "Error eliminating player: " + e.getMessage());
            throw new RuntimeException("Failed to eliminate player", e);
        }
    }




}
