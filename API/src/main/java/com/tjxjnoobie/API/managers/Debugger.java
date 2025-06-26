package com.tjxjnoobie.API.managers;

import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.interfaces.IDebugger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.tjxjnoobie.API.managers.MySQL.executePreparedStatement;
import static com.tjxjnoobie.API.managers.MySQL.getResult;

public class Debugger implements IDebugger {

    public Map<String, String> debuggers = new HashMap<>();

    @Override
    public boolean isDebuggerSQL(UUID uuid) {
        // Check if the specified player has the debugger flag set to true
        try {
            // Query the DEBUGGER column for the given player
            ResultSet rs = getResult("SELECT DEBUGGER FROM player_profile WHERE UUID = ?", uuid.toString());
            if (rs.next()) {
                return rs.getBoolean("DEBUGGER");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public void setDebugger(UUID uuid, boolean debuggerStatus) {
        // Update the DEBUGGER flag for the specified player
        try {
            executePreparedStatement("UPDATE player_profile SET DEBUGGER = ? WHERE UUID = ?", debuggerStatus, uuid.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getDebuggersSQL() {
        // Return a comma-separated list of player names where DEBUGGER is true
        StringBuilder debuggersList = new StringBuilder();
        try {
            // We assume that you have a column named "player" for the player's name
            ResultSet rs = getResult("SELECT NAME FROM player_profile WHERE DEBUGGER = 1");
            while (rs.next()) {
                if (debuggersList.length() > 0) {
                    debuggersList.append(", ");
                }
                debuggersList.append(rs.getString("NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return debuggersList.toString();
    }
    @Override
    public String getDebuggers() {
        StringBuilder builder = new StringBuilder();
        for (String name : debuggers.values()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(name);
        }
        return builder.toString();
    }
    @Override
    public void loadDebuggersCache() {
        try {
            ResultSet rs = getResult("SELECT UUID, NAME FROM player_profile WHERE DEBUGGER = 1");
            while (rs.next()) {
                String uuid = rs.getString("UUID");
                String name = rs.getString("NAME");
                debuggers.put(uuid, name);
            }
            System.out.println("Loaded " + debuggers.size() + " debuggers into cache.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean isDebugger(UUID uuid){
        return debuggers.containsKey(uuid.toString());
    }
    @Override
    public Map<String,String> getDebuggerHash(UUID uuid){
        return debuggers;
    }
    @Override
    public void setDebuggerHash(UUID uuid, String name){
        debuggers.put(uuid.toString(),name);
    }
    
    /**
     * Sends a debug message to a player if they are a debugger
     * @param playerId The player to send the message to
     * @param globalContext The GlobalContext for accessing utilities
     * @param message The debug message
     */
    @Override
    public void sendDebugMessage(UUID playerId, GlobalContext globalContext, String message) {
        if (playerId != null && globalContext != null) {
            boolean isDebugger = globalContext.getDebugger().isDebugger(playerId);
            if (isDebugger) {
                String staffPrefix = globalContext.getUtils().getStaffPrefix();

                // Now you need a way to get a Player from the UUID (platform dependent)
                // For Bukkit:
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    player.sendMessage(staffPrefix + "§c" + message);
                } else {
                    // Player not online, fallback or log
                    System.out.println(staffPrefix + "§c[Debug] " + message);
                }
            }
        } else{
            System.out.println("Error sending debug message: Player or GlobalContext is null.");
        }
    }
    
    /**
     * Sends a debug message with a custom prefix
     * @param player The player to send the message to
     * @param globalContext The GlobalContext for accessing utilities
     * @param prefix The custom prefix (e.g., "[BUILDER]", "[MANAGER]")
     * @param message The debug message
     */
    @Override
    public void sendDebugMessage(Player player, GlobalContext globalContext, String prefix, String message) {
        if (player != null && globalContext != null) {
            boolean isDebugger = globalContext.getDebugger().isDebugger(player.getUniqueId());
            if (isDebugger) {
                String staffPrefix = globalContext.getUtils().getStaffPrefix();
                player.sendMessage(staffPrefix + "§c" + prefix + " " + message);
            }
        }
    }
}