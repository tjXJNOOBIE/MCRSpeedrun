package com.tjxjnoobie.speed;

import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.managers.*;
import com.tjxjnoobie.API.minecraft.Config;
import com.tjxjnoobie.API.velocity.managers.PunishManager;
import com.tjxjnoobie.enums.GameModeEnum;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.interfaces.*;
import com.tjxjnoobie.listeners.ChatListener;
import com.tjxjnoobie.speed.cache.LocationCache;
import com.tjxjnoobie.API.minecraft.utils.MCUtils;

import com.tjxjnoobie.API.minecraft.RankMC;
import com.tjxjnoobie.API.minecraft.managers.SoundManager;
import com.tjxjnoobie.API.utils.ReflectUtil;
import com.tjxjnoobie.API.velocity.RatingAPI;

import com.tjxjnoobie.listeners.BlockPlaceListener;
import com.tjxjnoobie.listeners.CoreJoinListener;
import com.tjxjnoobie.listeners.CoreQuitListener;
import com.tjxjnoobie.speed.Commands.*;
import com.tjxjnoobie.API.cache.SpeedrunStatsCache;
import com.tjxjnoobie.speed.Events.*;

import com.tjxjnoobie.speed.managers.*;
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
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;


public final class Main extends JavaPlugin implements PluginMessageListener, MainInterFace, Listener {


    private IGameState gameState;
    private IGameMode gameMode;
    private IGameManager gameManager;
    private IPlayerManager playerManager;
    private IUtils utils;
    private ISpeedrunStatsCache statsCache;
    private IWorldManager worldManager;
    private ILocationCache locationCache;
    private IJoinEvent joinEvent;
    private IQuitEvent quitEvent;
    private IMCUtils mcUtils;
    private IRatingCache ratingCache;
    private IRating rating;
    private IRatingAPI ratingAPI;
    private IRankMC rankMC;
    private IPlayerProfile playerProfile;
    private IRank rank;
    private IDebugger debugger;
    private ISoundManager soundManager;
    private IRankCache rankCache;
    private IRetentionManager retentionManager;
    private IRedis redis;
    private IDebug debug;
    private IVoting voting;
    private SpeedRunContext speedRunContext;
    private IGlobalContext globalContext;
    private InterfaceManager mainManager;
    private BlockPlaceListener blockPlaceHandler;
    private CoreJoinListener coreJoinListener;
    private static MainInterFace mainInterFace;
    private IBossBarManager bossBarManager;
    private IInventoryManager inventoryManager;
    private IProxyUtils proxyUtils;
    private IGameType gameType;
    private IStatsManager statsManager;
    private ILobbyStatsCache lobbyStatsCache;
    private ISpeedrunStatsCache speedrunStatsCache;
    private IPunishManager punishManager;
    private IPunishLog punishLog;
    private Plugin plugin;
    private static Main instance;
    private static final String CHANNEL = "factions:sync";


    @Override
    public void onEnable() {
        plugin = this;
        Config.createConfig();
        Config.loadConfig();
        MySQL.connect();
        ReflectUtil.loadLibs();
        try {
            Redis.connectToRedis();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        retentionManager = new RetentionManager();

        try {
            rankCache = new RankCache(rank);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        gameState = new GameState(utils);
        mcUtils = new MCUtils(plugin,globalContext);
        soundManager = new SoundManager(plugin);

        rankMC = new RankMC(rank, rankCache);
        redis = new Redis();

        InterfaceManager.setMainInterFace(this);

        mainInterFace = InterfaceManager.getMainInterFace();

        debug = new Debug(gameState, gameManager, utils, locationCache, worldManager, rankCache, soundManager, retentionManager, debugger, plugin);
        ratingAPI = new RatingAPI(utils);
        gameMode = new GameMode();
        gameType = new GameType();
        speedRunContext = new SpeedRunContext(plugin,gameMode,null,statsCache,gameManager,gameState,mcUtils,null,null,null,null,quitEvent,rankMC,ratingCache,rating,ratingAPI,playerProfile,rank,debugger,soundManager,rankCache,retentionManager,redis,debug,bossBarManager,null,null,gameType,statsManager,null);
        globalContext = new GlobalContext(gameMode,null,statsCache,gameState,mcUtils,null,rankMC,ratingCache,rating,ratingAPI,playerProfile,rank,debugger,soundManager,rankCache,retentionManager,redis,null,proxyUtils,null,null,null,null,punishLog);

        statsCache = new SpeedrunStatsCache(globalContext);
        punishManager = new PunishManager(globalContext);
        inventoryManager = new InventoryManager(speedRunContext);
        playerManager = new PlayerManager(speedRunContext);
        globalContext.setRank(rank);
        globalContext.setDebugger(debugger);
        globalContext.setRankCache(rankCache);
        globalContext.setLobbyStatsCache(lobbyStatsCache);
        globalContext.setUtils(utils);
        globalContext.setWorldManager(worldManager);
        globalContext.setPlayerProfile(playerProfile);
        globalContext.setInventoryManager(inventoryManager);
        speedRunContext.setPlayerManager(playerManager);
        speedRunContext.setInventoryManager(inventoryManager);
        speedRunContext.setJoinEvent(joinEvent);
        speedRunContext.setVoting(voting);
        speedRunContext.setGameManager(gameManager);
        speedRunContext.setGameType(gameType);
        speedRunContext.setWorldManager(worldManager);
        speedRunContext.setLocationCache(locationCache);
        speedRunContext.setUtils(utils);
        speedRunContext.setSRStatsCache(speedrunStatsCache);
        statsManager = new StatsManager();
        globalContext.setPunishManager(punishManager);
        globalContext.setWorldManager(worldManager);
        globalContext.setGameType(gameType);
        globalContext.setStatsManager(statsManager);
        globalContext.setLobbyStatsCache();
        globalContext.setUtils(utils);
        locationCache = new LocationCache(speedRunContext);



        try {
            InterfaceManager.setBlockPlaceHandler();
            InterfaceManager.setGlobalHandler(globalContext,"com.tjxjnoobie.kingdomFactions.Events.BlockPlace", "BlockPlaceHandler");
            InterfaceManager.setGlobalHandler(globalContext,"com.tjxjnoobie.core.Events.ChatEvent","ChatHandler");
            InterfaceManager.setGlobalHandler(globalContext,"com.tjxjnoobie.core.Events.CoreJoin","CoreJoinHandler");
            InterfaceManager.setGlobalHandler(globalContext,"com.tjxjnoobie.core.Events.CoreQuit","CoreQuitHandler");


        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
        utils.createServerID();
        utils.createGameID();

        String gameID = utils.getGameID();
        String serverID = utils.getServerID(globalContext);
        MobKill.blockDragonDeathSound(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);
        try {
            utils.setGameType();
            gameState.createServerID(serverID, gameID, "servers", "SPEEDRUN");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        registerEvents();
        registerCommand("debug", new Debug(gameState, gameManager, utils, locationCache, worldManager, rankCache, soundManager, retentionManager, debugger, plugin));
        registerCommand("addplayer", new AddPlayer(utils, gameManager, rankCache));
        registerCommand("createworld", new CreateWorld(worldManager, utils));
        registerCommand("changeworld", new ChangeWorldCMD(utils));
        registerCommand("world", new LoadWorld(utils, worldManager));
        registerCommand("loadseed", new Seed(utils, worldManager));
        registerCommand("setspawn", new SetSpawns(speedRunContext));
        registerCommand("spawndragon", new SpawnEnderDragon(utils, rankCache));
        registerCommand("debugger", new DebuggerCMD(debugger, utils, rankCache));
        registerCommand("vote", new Vote(speedRunContext));
        registerCommand("v", new Vote(speedRunContext));
        registerCommand("fireevent", new FireEvent(speedRunContext));



        try {
            getGameState().setGameState(GameStateEnum.STARTUP, getServerID());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        String serverID = utils.getServerID(globalContext);
        gameState.removeServerID(serverID);
    }


    public GameState getGameState() {
        return gameState;
    }

    public String getServerID() {
        return utils.serverID;
    }

    public static Main getInstance() {
        return instance;
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new WeatherChange(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryDrag(speedRunContext),this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryInteract(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new DropEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new InventoryMove(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new LoginEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new CoreJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new CoreQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(blockPlaceHandler), this);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new DeathEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new QuitEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new MobKill(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new HungerLevelChange(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPickup(gameManager, utils, mcUtils, gameState), this);
        Bukkit.getPluginManager().registerEvents(new DamageEvent(gameManager, gameState), this);
        Bukkit.getPluginManager().registerEvents(new ChangeWorld(gameManager, utils, mcUtils, gameMode, gameState), this);
        Bukkit.getPluginManager().registerEvents(new FairFight(speedRunContext, this), this);

    }

    public void registerCommand(String command, CommandExecutor executor) {
        Bukkit.getPluginCommand(command).setExecutor(executor);
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





