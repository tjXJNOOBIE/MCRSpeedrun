package com.tjxjnoobie.speed;

import com.tjxjnoobie.api.dependency.contexts.GlobalContext;
import com.tjxjnoobie.api.dependency.injection.helpers.ContextInjectionHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.DependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IContextInjectionHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.enums.GameModeEnum;
import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.internal.utils.reflection.ReflectUtil;
import com.tjxjnoobie.api.listeners.BlockPlaceListener;
import com.tjxjnoobie.api.listeners.ChatListener;
import com.tjxjnoobie.api.listeners.CoreJoinListener;
import com.tjxjnoobie.api.listeners.CoreQuitListener;
import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;
import com.tjxjnoobie.api.managers.MySQL;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.annotations.Injectable;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.minecraft.Config;
import com.tjxjnoobie.api.platform.minecraft.managers.FairFight;
import com.tjxjnoobie.speed.Commands.*;
import com.tjxjnoobie.speed.Events.bukkit.*;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

@Injectable("Main class for Minecraft Speedrun Module")
public class Main extends JavaPlugin implements PluginMessageListener, Listener, IUtils, MainInterFace {

    private IContext<IGlobalContext> iGlobalContext;
    private IContext<ISpeedRunContext> iSpeedContext;

     @Inject private IGameState gameState;
     @Inject private IGameMode gameMode;
     @Inject private IGameManager gameManager;
     @Inject private IPlayerManager playerManager;
     @Inject private IUtils utils;
     @Inject private ISpeedrunStatsCache statsCache;
     @Inject private IWorldManager worldManager;
     @Inject private ILocationCache locationCache;
     @Inject private ISpeedRunJoinEvent joinEvent;
     @Inject private IQuitEvent quitEvent;
     @Inject private IMCUtils mcUtils;
     @Inject private IRatingCache ratingCache;
     @Inject private IRating rating;
     @Inject private IRatingAPI ratingAPI;
     @Inject private IRankMC rankMC;
     @Inject private IPlayerProfile playerProfile;
     @Inject private IRank rank;
     @Inject private IDebugger debugger;
     @Inject private ISoundManager soundManager;
     @Inject private IRankCache rankCache;
     @Inject private IRetentionManager retentionManager;
     @Inject private IRedis redis;
     @Inject private IDebug debug;
     @Inject private IVoting voting;
     @Inject private ISpeedRunContext speedRunContext;
     @Inject private IGlobalContext globalContext;
     @Inject private BlockPlaceListener blockPlaceHandler;
     @Inject private CoreJoinListener coreJoinListener;
     @Inject private static MainInterFace mainInterFace;
     @Inject private IBossBarManager bossBarManager;
     @Inject private IInventoryBuilder inventoryBuilder;
     @Inject private IInventoryManager inventoryManager;
     @Inject private IProxyUtils proxyUtils;
     @Inject private IGameType gameType;
     @Inject private IStatsManager statsManager;
     @Inject private ILobbyStatsCache lobbyStatsCache;
     @Inject private ISpeedrunStatsCache speedrunStatsCache;
     @Inject private IPunishManager punishManager;
     @Inject private IPunishLog punishLog;
     @Inject private FireEvent fireEvent;
     @Inject private ILocalServerMetaData localServerMetaData;
     private Plugin plugin;
     private static Main instance;
     private static final String CHANNEL = "factions:sync";

    @Override
    public void onEnable() {
        plugin = this;
        IContextInjectionHelper injectionHelper = new ContextInjectionHelper();

        //TODO: Delegate this temp fix to a helper method
        //TODO: Use injection helper classes thru implementations instead of instancing
        IDependencyInjectorHelper dependencyInjectorHelper = new DependencyInjectorHelper();
        // ===== PHASE 0: AutoBind Main class FIRST (before anything else) =====
        Log.info("[Main] ===== Phase 0: Pre-AutoBind Main Class =====");
        // This ensures Main's fields are scanned and registered before contexts are created
        // We create a temporary helper just to run autoBind on Main
        try {
            // AutoBind Main class to scan its fields and prepare for injection
            dependencyInjectorHelper.autoBind(this);
            Log.info("[Main] Main class autoBind complete");
        } catch (Exception e) {
            Log.error("[Main] Failed to autoBind Main class: " + e.getMessage());
            throw new RuntimeException(e);
        }
        
        // ===== PHASE 1: Pre-DI Setup (No dependencies needed) =====
        Log.info("[Main] ===== Phase 1: Pre-DI Setup =====");
        Config.createConfig();
        Config.loadConfig();
        MySQL.connect();
        ReflectUtil.loadLibs();
        
        // ===== PHASE 2: DI Initialization =====
        Log.info("[Main] ===== Phase 2: DI Initialization =====");
        iGlobalContext = new GlobalContext();
        iSpeedContext = new SpeedRunContext();
        
        // Register Plugin in both contexts so it's available everywhere
        iSpeedContext.getContext().setPlugin(this);

        try {
            //TODO: Use injection helper classes thru implementations instead of instancing

            // Use the concrete ContextInjectionHelper implementation
            injectionHelper.injectAllContextsGlobally(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //TODO: Update logging to use entire context register size instead of one context
        Log.info("[Main] Registered contexts: " + iGlobalContext.getAllContexts().size());
        Log.success("[Main] ===== DI Initialization Complete =====");
        
        // ===== PHASE 3: Post-DI Setup (Dependencies now available) =====
        Log.info("[Main] ===== Phase 3: Post-DI Setup =====");
        
        // Interface Manager setup
        InterfaceManager.setMainInterFace(this);
        mainInterFace = InterfaceManager.getMainInterFace();

        try {
            InterfaceManager.setBlockPlaceHandler();
            InterfaceManager.setGlobalHandler("com.tjxjnoobie.kingdomFactions.Events.BlockPlace", "BlockPlaceHandler");
            InterfaceManager.setGlobalHandler("com.tjxjnoobie.core.Events.ChatEvent","ChatHandler");
            InterfaceManager.setGlobalHandler("com.tjxjnoobie.core.Events.CoreJoin","CoreJoinHandler");
            InterfaceManager.setGlobalHandler("com.tjxjnoobie.core.Events.CoreQuit","CoreQuitHandler");
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            Log.exception(e);
        }
        
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
        registerCommand("setspawn", new SetSpawns(speedRunContext));
        registerCommand("spawndragon", new SpawnEnderDragon(globalContext));
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
    
     @Inject public boolean hasNoInjectFields(Object obj) {
        if (obj == null) return true;
        
        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    return false;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return true;
    }


    public IGameState getGameState() {
        return gameState;
    }

    // getContext() is now provided by ContextAccess default implementation
    // It automatically finds the @Inject IGlobalContext globalContext field


    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunWeatherChange(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryDrag(speedRunContext),this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryInteract(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunDropEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryMove(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryClick(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunLoginEvent(), this);
        Bukkit.getPluginManager().registerEvents(new CoreJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new CoreQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(blockPlaceHandler), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunJoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunDeathEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunQuitEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunMobKill(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunHungerLevelChange(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunPlayerPickup(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunDamageEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunChangeWorld(speedRunContext), this);
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

    public Object getPluginInstance() {
        return this; // Return the plugin instance
    }


}





