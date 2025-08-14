package com.tjxjnoobie.api.internal.utils;

import com.tjxjnoobie.api.contexts.GlobalContext;
import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.interfaces.IGameType;
import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IUtils;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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









    public Map<String, Object> getConfigValues() {
        return configValues;
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
            gameType.setGameType(GameTypeEnum.SPEED_RUN, serverID);

        }else if(directoryName.contains("lobby")){
            gameType.setGameType(GameTypeEnum.LOBBY, serverID);

        }else if(directoryName.contains("nexus")){
            gameType.setGameType(GameTypeEnum.NEXUS, serverID);
        }else if(directoryName.contains("kingdom")){
            gameType.setGameType(GameTypeEnum.KINGDOM, serverID);
        }else if(directoryName.contains("proxy")){
            gameType.setGameType(GameTypeEnum.PROXY, serverID);
        }else{
            gameType.setGameType(GameTypeEnum.DEV, serverID);

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
     * Sets a configuration value
     *
     * @param key           The configuration key
     * @param value         The value to set
     * @param globalContext
     */
    @Override
    public void setConfigValue(String key, Object value, IGlobalContext globalContext) {
        if (key != null && !key.trim().isEmpty()) {

            configValues.put(key, value);
        }
    }
}


