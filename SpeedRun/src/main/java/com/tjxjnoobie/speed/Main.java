package com.tjxjnoobie.speed;

import org.tavall.dependency.injection.helpers.DependencyInjectorHelper;
import org.tavall.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.enums.GameModeEnum;
import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.interfaces.*;
import org.tavall.internal.utils.reflection.ReflectUtil;
import com.tjxjnoobie.api.listeners.BlockPlaceListener;
import com.tjxjnoobie.api.listeners.ChatListener;
import com.tjxjnoobie.api.listeners.CoreJoinListener;
import com.tjxjnoobie.api.listeners.CoreQuitListener;
import com.tjxjnoobie.api.managers.MySQL;
import org.tavall.logging.Log;
import com.tjxjnoobie.api.platform.minecraft.Config;
import com.tjxjnoobie.api.platform.minecraft.managers.FairFight;
import com.tjxjnoobie.speed.Commands.*;
import com.tjxjnoobie.speed.Events.bukkit.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

public class Main extends JavaPlugin implements PluginMessageListener, Listener, IUtils {



      private IGameState gameState;
      private IGameMode gameMode;
      private IGameManager gameManager;
      private IDebugger debugger;
      private IRetentionManager retentionManager;
      private BlockPlaceListener blockPlaceHandler;
      private IGameType gameType;
     public static Plugin plugin;
     private static Main instance;
     private static final String CHANNEL = "factions:sync";

    @Override
    public void onEnable() {
        plugin = this;
        IDependencyInjectorHelper<?,?> injectionHelper = new DependencyInjectorHelper<>();

        //TODO: Delegate this temp fix to a helper method
        //TODO: Use injection helper classes thru implementations instead of instancing
        // ===== PHASE 0: AutoBind Main class FIRST (before anything else) =====
        // This ensures Main's fields are scanned and registered before contexts are created
        // We create a temporary helper just to run autoBind on Main
        try {
            injectionHelper.setupDISystem();
        } catch (Throwable e) {
            Log.exception(e);
        }
        // ===== PHASE 1: Pre-DI Setup (No dependencies needed) =====
        Log.info("[Main] ===== Phase 1: Pre-DI Setup =====");
        Config.createConfig();
        Config.loadConfig();
        MySQL.connect();
        ReflectUtil.loadLibs();
        
        // ===== PHASE 2: DI Initialization =====
        Log.info("[Main] ===== Phase 2: DI Initialization =====");

        // Register Plugin in both contexts so it's available everywhere


        Log.success("[Main] ===== DI Initialization Complete =====");
        
        // ===== PHASE 3: Post-DI Setup (Dependencies now available) =====
        Log.info("[Main] ===== Phase 3: Post-DI Setup =====");
        
        // Interface Manager setup
//        InterfaceManager.setMainInterFace(this);
//        mainInterFace = InterfaceManager.getMainInterFace();
//
//        try {
//            InterfaceManager.setBlockPlaceHandler();
//            InterfaceManager.setGlobalHandler("com.tjxjnoobie.kingdomFactions.Events.BlockPlace", "BlockPlaceHandler");
//            InterfaceManager.setGlobalHandler("com.tjxjnoobie.core.Events.ChatEvent","ChatHandler");
//            InterfaceManager.setGlobalHandler("com.tjxjnoobie.core.Events.CoreJoin","CoreJoinHandler");
//            InterfaceManager.setGlobalHandler("com.tjxjnoobie.core.Events.CoreQuit","CoreQuitHandler");
//        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
//                 IllegalAccessException e) {
//            Log.exception(e);
//        }
        
        // Game setup (uses injected dependencies)
        Log.info("[Main] Setting up game systems...");
        gameMode.setGameMode(GameModeEnum.NORMAL);
        retentionManager.loadMockPlayers(10);
        
        new BukkitRunnable() {
            public void run() {
                try {
                    retentionManager.loadRetentionCache();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskLater(this, 20 * 10);
        
        debugger.loadDebuggersCache();
        gameManager.runCheckers();
        
        // Server setup
        Log.info("[Main] Setting up server metadata...");
        createServerID();
        createGameID();
        String gameID = getGameID();
        String serverID = getServerID();
        
        SpeedRunMobKill.blockDragonDeathSound(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);
        
        try {
            gameType.setGameType(GameTypeEnum.SPEED_RUN, serverID);
            gameState.createServerID(serverID, gameID, "servers", "SPEEDRUN");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Register events and commands
        Log.info("[Main] Registering events and commands...");
        registerEvents();
        registerCommand("debug", new Debug());
        registerCommand("addplayer", new AddPlayer());
        registerCommand("createworld", new CreateWorld());
        registerCommand("changeworld", new ChangeWorldCMD());
        registerCommand("world", new LoadWorld());
        registerCommand("loadseed", new Seed());
        registerCommand("setspawn", new SetSpawns());
        registerCommand("spawndragon", new SpawnEnderDragon());
        registerCommand("debugger", new DebuggerCMD());
        registerCommand("vote", new Vote());
        registerCommand("v", new Vote());
        registerCommand("fireevent", new FireEvent());

        // Set initial game state
        try {
            getGameState().setGameState(GameStateEnum.STARTUP, serverID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        Log.success("[Main] ===== Plugin Enabled Successfully =====");
    }


    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        String serverID = getServerID();
        gameState.removeServerID(serverID);
    }
    

    public static Plugin getPlugin() {
        return plugin;
    }

    public IGameState getGameState() {
        return gameState;
    }

    // getContext() is now provided by ContextAccess default implementation
    // It automatically finds the  IGlobalContext globalContext field


    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunWeatherChange(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryDrag(),this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryInteract(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunDropEvent(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryMove(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunLoginEvent(), this);
        Bukkit.getPluginManager().registerEvents(new CoreJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new CoreQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(blockPlaceHandler), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunJoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunDeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunQuitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunMobKill(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunHungerLevelChange(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunPlayerPickup(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunDamageEvent(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunChangeWorld(), this);
        Bukkit.getPluginManager().registerEvents(new FairFight(), this);

    }

    public void registerCommand(String command, CommandExecutor executor) {
        Log.info(command + " has been registered with " + executor.toString());
        if (Bukkit.getPluginCommand(command) == null) {
            Log.error("Command '" + command + "' is not registered in plugin.yml!");
            return;
        }
        Objects.requireNonNull(Bukkit.getPluginCommand(command)).setExecutor(executor);
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(CHANNEL)) return;
        System.out.println("Plugin message received with data " + Arrays.toString(message));

        try (DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(message))) {
            String type = dataIn.readUTF();

            switch (type) {
                case "BLOCK_PLACE":
                    blockPlaceHandler.handleBlockPlace(dataIn);
                    System.out.print("Handling Block Place Data for type " + type + " with data " + dataIn.toString());
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public void sendPluginMessage(byte[] data) {
        System.out.println("Plugin message sent with data " + Arrays.toString(data));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendPluginMessage(this, CHANNEL, data);
        }
    }



}
