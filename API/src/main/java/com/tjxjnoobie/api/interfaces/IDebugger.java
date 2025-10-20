package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.dependency.contexts.GlobalContext;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Interface for debugging operations
 */
public interface IDebugger {
    
    /**
     * Checks if a player is a debugger
     * @param playerId The player's UUID
     * @return true if player is a debugger
     */
    boolean isDebugger(UUID playerId);



    void setDebugger(UUID uuid, boolean debuggerStatus);


    String getDebuggersSQL();

    /**
     * Gets all debugger UUIDs
     *
     * @return Array of debugger UUIDs
     */
    String getDebuggers();


    boolean isDebuggerSQL(UUID uuid);

    Map<String,String> getDebuggerHash(UUID uuid);

    void setDebuggerHash(UUID uuid, String name);

    void loadDebuggersCache();


    void sendDebugMessage(UUID playerId, GlobalContext globalContext, String message);

    void sendDebugMessage(Player player, GlobalContext globalContext, String prefix, String message);
}