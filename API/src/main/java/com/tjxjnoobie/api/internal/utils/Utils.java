package com.tjxjnoobie.api.internal.utils;

import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.interfaces.IGameType;
import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IUtils;
import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.console.Log;

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


public class Utils implements IUtils<IGlobalContext> {

    public final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public String prefix ="§6§lNovus§8§l »»§f ";
    public String staffPrefix = "§4§lNovus §8§l»»§c ";
    public String serverID;
    public String gameID;
    
    @Inject private IGlobalContext globalContext;
    @Inject private ILocalServerMetaData localServerMetaData;
    @Inject private IGameType gameType;

    // Configuration storage
    //TODO: Make AbstractConfig system
    private final Map<String, Object> configValues = new HashMap<>();

    @Override
    public String getServerID() {
        if (localServerMetaData == null) {
            return this.serverID;
        }
        return localServerMetaData.getServerID();
    }

    @Override
    public String getGameID() {
        if (localServerMetaData == null) {
            return this.gameID;
        }
        return localServerMetaData.getGameID();
    }


    @Override
    public IGlobalContext getGlobalContext(){
        return globalContext;
    }

    @Override
    public void setConfigValue(String key, Object value, IGlobalContext globalContext) {
        if (key != null && !key.trim().isEmpty()) {
            configValues.put(key, value);
        }
    }

    @Override
    public void createServerID() {
        Log.info("[ID] Creating new server ID...");
        if (localServerMetaData == null) {
            Log.error("[ID] Failed to create server ID: LocalServerMetaData is null");
            return;
        }
        localServerMetaData.setServerID(generateRandomID(5));
    }

    @Override
    public void createGameID() {
        Log.info("[ID] Creating new game ID...");
        if (localServerMetaData == null) {
            Log.error("[ID] Failed to create game ID: LocalServerMetaData is null");
            return;
        }
        localServerMetaData.setGameID(generateRandomID(6));
    }

    @Override
    public String generateRandomID(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder id = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            id.append(CHARACTERS.charAt(index));
        }
        Log.info("[ID] Generated ID: " + id.toString());
        return id.toString();
    }

    @Override
    public Map<String, Object> getConfigValues() {
        return configValues;
    }

    @Override
    public void setGameType() throws SQLException {
        // Get the current working directory as a string
        String currentDir = System.getProperty("user.dir");
        // Create a File object with the directory path
        File directory = new File(currentDir);
        // Retrieve the directory name
        String directoryName = directory.getName();
        if(directoryName.contains("speed")){
            gameType.setGameType(GameTypeEnum.SPEED_RUN, getServerID());

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

    @Override
    public String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm");
        return sdf.format(cal.getTime());
    }

    @Override
    public String getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");
        return sdf.format(cal.getTime());
    }

    @Override
    public String getAdvancedTime() {
        TimeZone timeZone = Calendar.getInstance().getTimeZone();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(cal.getTime()) + " " + timeZone.getDisplayName(false, 0);
    }

    @Override
    public boolean parseBoolean(String value) {
        if (value == null) {
            return false;
        }
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            return false;
        }
    }

    @Override
    public String formatTimestamp(Timestamp timestamp, DateTimeFormatter formatter) {
        return (timestamp != null) ? timestamp.toLocalDateTime().format(formatter) : null;
    }

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

    @Override
    public Map<String, Object> getConfigValues(IGlobalContext globalContext) {
        //TODO: Remove context from parameters in favor of custom DI approach
        if (globalContext == null) {
            Log.error("[Config] Failed to get config values: GlobalContext is null");
            return configValues;
        }
        return globalContext.getUtils().getConfigValues(globalContext);
    }



    @Override
    public Object getConfigValue(IGlobalContext globalContext, String key) {
        if (globalContext == null) {
            Log.error("[Config] Failed to get config value: GlobalContext is null");
            return null;
        }
        if (key == null || key.trim().isEmpty()) {
            return null;
        }
        return globalContext.getUtils().getConfigValues(globalContext).get(key);
    }

    @Override
    public void setGameType(IGlobalContext globalContext, GameTypeEnum gameTypeEnum) throws SQLException {
        if (globalContext == null) {
            Log.error("[GameType] Failed to set game type: GlobalContext is null");
            return;
        }
        globalContext.getGameType().setGameType(gameTypeEnum, getServerID());
    }
}


