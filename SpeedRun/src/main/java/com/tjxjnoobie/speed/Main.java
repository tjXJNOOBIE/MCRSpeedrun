package com.tjxjnoobie.speed;

import com.tjxjnoobie.api.annotations.AutoInjectAll;
import com.tjxjnoobie.api.enums.GameModeEnum;
import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.internal.utils.reflection.ReflectUtil;
import com.tjxjnoobie.api.listeners.BlockPlaceListener;
import com.tjxjnoobie.api.listeners.ChatListener;
import com.tjxjnoobie.api.listeners.CoreJoinListener;
import com.tjxjnoobie.api.listeners.CoreQuitListener;
import com.tjxjnoobie.api.managers.MySQL;
import com.tjxjnoobie.api.platform.minecraft.Config;
import com.tjxjnoobie.speed.Commands.*;
import com.tjxjnoobie.speed.Events.FairFight;
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
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

@AutoInjectAll
public final class Main extends JavaPlugin implements PluginMessageListener, MainInterFace, Listener, IUtils {


    private IGameState gameState;
    private IGameMode gameMode;
    private IGameManager gameManager;
    private IPlayerManager playerManager;
    private IUtils utils;
    private ISpeedrunStatsCache statsCache;
    private IWorldManager worldManager;
    private ILocationCache locationCache;
    private ISpeedRunJoinEvent joinEvent;
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
    private ISpeedRunContext speedRunContext;
    private IGlobalContext globalContext;
    private InterfaceManager mainManager;
    private BlockPlaceListener blockPlaceHandler;
    private CoreJoinListener coreJoinListener;
    private static MainInterFace mainInterFace;
    private IBossBarManager bossBarManager;
    private IInventoryBuilder inventoryBuilder;
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

        InterfaceManager.setMainInterFace(this);
        mainInterFace = InterfaceManager.getMainInterFace();
        globalContext.buildGlobalContext();
        speedRunContext.buildSpeedRunContext();
        initializeDependencies();


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
        utils.createServerID(globalContext);
        utils.createGameID(globalContext);

        String gameID = getGameID();
        String serverID = utils.getServerID(globalContext);
        SpeedRunMobKill.blockDragonDeathSound(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);
        try {
            gameType.setGameType(GameTypeEnum.SPEED_RUN, serverID);
            gameState.createServerID(serverID, gameID, "servers", "SPEEDRUN");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        registerEvents();
        registerCommand("debug", new Debug(globalContext,speedRunContext));
        registerCommand("addplayer", new AddPlayer(globalContext,speedRunContext));
        registerCommand("createworld", new CreateWorld(globalContext));
        registerCommand("changeworld", new ChangeWorldCMD(globalContext));
        registerCommand("world", new LoadWorld(globalContext));
        registerCommand("loadseed", new Seed(globalContext));
        registerCommand("setspawn", new SetSpawns(speedRunContext));
        registerCommand("spawndragon", new SpawnEnderDragon(globalContext));
        registerCommand("debugger", new DebuggerCMD(globalContext));
        registerCommand("vote", new Vote(speedRunContext));
        registerCommand("v", new Vote(speedRunContext));
        registerCommand("fireevent", new FireEvent(speedRunContext));



        try {
            getGameState().setGameState(GameStateEnum.STARTUP, serverID);
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


    public IGameState getGameState() {
        return gameState;
    }



    public static Main getInstance() {
        return instance;
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunWeatherChange(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryDrag(speedRunContext),this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryInteract(speedRunContext,globalContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunDropEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryMove(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunInventoryClick(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunLoginEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new CoreJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new CoreQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(blockPlaceHandler), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunJoinEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunDeathEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunQuitEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunMobKill(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunHungerLevelChange(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunPlayerPickup(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunDamageEvent(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new SpeedRunChangeWorld(speedRunContext), this);
        Bukkit.getPluginManager().registerEvents(new FairFight(speedRunContext, this), this);

    }

    public void registerCommand(String command, CommandExecutor executor) {
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

    @Override
    public void initializeDependencies() {

    }
}





