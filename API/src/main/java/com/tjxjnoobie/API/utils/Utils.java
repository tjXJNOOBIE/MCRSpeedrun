package com.tjxjnoobie.API.utils;

import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.enums.GameTypeEnum;
import com.tjxjnoobie.interfaces.IGameType;
import com.tjxjnoobie.interfaces.IUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Utils implements IUtils {

    public final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public String prefix ="§6§lNovus§8§l »»§f ";
    public String staffPrefix = "§4§lNovus §8§l»»§c ";
    public String serverID;
    public String gameID;
    private final GlobalContext globalContext;
    
    // Configuration storage
    private final Map<String, Object> configValues = new HashMap<>();

    public Utils(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }


    public  String generateRandomID(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder id = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            id.append(CHARACTERS.charAt(index));
        }

        return id.toString();
    }




    public void createServerID(){
       serverID = generateRandomID(5);
    }
    public void createGameID(){
        gameID = generateRandomID(6);
    }


    public String getServerID(){
        return serverID;
    }
    public String getGameID(){
        return gameID;
    }
    public String getPrefix(){
        return prefix;
    }
    public String getStaffPrefix(){
        return staffPrefix;
    }


    public String formatTimestamp(Timestamp timestamp, DateTimeFormatter formatter) {
        return (timestamp != null) ? timestamp.toLocalDateTime().format(formatter) : null;
    }
    public void setGameType() throws SQLException {
        IGameType gameType = globalContext.getGameType();
        // Get the current working directory as a string
        String currentDir = System.getProperty("user.dir");
        // Create a File object with the directory path
        File directory = new File(currentDir);
        // Retrieve the directory name
        String directoryName = directory.getName();
        if(directoryName.contains("speed")){
            gameType.setGameType(GameTypeEnum.SPEEDRUN, getServerID());

        }else if(directoryName.contains("lobby")){
            gameType.setGameType(GameTypeEnum.LOBBY, getServerID());

        }else if(directoryName.contains("nexus")){
            gameType.setGameType(GameTypeEnum.NEXUS, getServerID());
        }else if(directoryName.contains("kingdom")){
            gameType.setGameType(GameTypeEnum.KINGDOM, getServerID());
        }else if(directoryName.contains("proxy")){
            gameType.setGameType(GameTypeEnum.PROXY, getServerID());
        }else{
            gameType.setGameType(GameTypeEnum.DEV, getServerID());

        }

    }



    public String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm");
        return sdf.format(cal.getTime());
    }

    public String getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");
        return sdf.format(cal.getTime());
    }

    public String getAdvancedTime() {
        TimeZone timeZone = Calendar.getInstance().getTimeZone();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(cal.getTime()) + " " + timeZone.getDisplayName(false, 0);
    }
    
    // Implementation of IUtils interface methods
    
    /**
     * Formats a time duration from milliseconds
     * @param milliseconds The time in milliseconds
     * @return Formatted time string
     */
    @Override
    public String formatTime(long milliseconds) {
        long hours = milliseconds / (1000 * 60 * 60);
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long seconds = (milliseconds / 1000) % 60;
        long millis = milliseconds % 1000;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
        } else {
            return String.format("%02d:%02d.%03d", minutes, seconds, millis);
        }
    }
    
    /**
     * Formats a player name with colors
     * @param playerName The player name
     * @return Formatted player name
     */
    @Override
    public String formatPlayerName(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return "§7Unknown";
        }
        return "§b" + playerName + "§r";
    }
    
    /**
     * Sends a message to all online players
     * @param message The message to send
     */
    @Override
    public void broadcastMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            Bukkit.broadcastMessage(getPrefix() + message);
        }
    }
    
    /**
     * Logs a message to console
     * @param message The message to log
     */
    @Override
    public void logMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            Bukkit.getLogger().info("[Utils] " + message);
        }
    }
    
    /**
     * Gets a configuration value
     * @param key The configuration key
     * @return The configuration value
     */
    @Override
    public Object getConfigValue(String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }
        return configValues.get(key);
    }
    
    /**
     * Sets a configuration value
     * @param key The configuration key
     * @param value The value to set
     */
    @Override
    public void setConfigValue(String key, Object value) {
        if (key != null && !key.trim().isEmpty()) {
            configValues.put(key, value);
        }
    }
}


