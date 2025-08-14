package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.annotations.AutoInjectAll;
import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;
import com.tjxjnoobie.api.platform.global.utils.interfaces.IConfigUtils;
import com.tjxjnoobie.api.platform.global.utils.interfaces.ITimeUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * Interface for GlobalContext to provide dependency injection capabilities
 */
@AutoInjectAll
public interface IGlobalContext  {


    // Core getters using interfaces
    Plugin getPlugin();
    IGameMode getGameMode();
    IGameState getGameState();
    IUtils getUtils();
    ISpeedrunStatsCache getSpeedrunStatsCache();
    IMCUtils getMcUtils();
    IWorldManager getWorldManager();
    IRankMC getRankMC();
    IRatingCache getRatingCache();
    IRating getRating();
    IRatingAPI getRatingAPI();
    IPlayerProfile getPlayerProfile();

    IMCUtils getMCUtils();

    IRank getRank();
    IDebugger getDebugger();
    ISoundManager getSoundManager();
    IRankCache getRankCache();
    IRetentionManager getRetentionManager();
    IRedis getRedis();
    IGameType getGameType();
    IProxyUtils getProxyUtils();
    IStatsManager getStatsManager();
    ILobbyStatsCache getLobbyStatsCache();
    ISpeedrunStatsCache getSRStatsCache();
    IPunishManager getPunishManager();
    IPunishLog getPunishLog();
    IInventoryBuilder getInventoryBuilder();
    IInventoryManager getInventoryManager();
    IVoting getVoting();
    IConfigUtils getConfigUtils();
    ITimeUtils getTimeUtils();
    ILocalServerMetaData getLocalServerMetaData();

    
    // Dependency map access
    HashMap<Class<?>, Object> getDependencyMap();
    <T> T get(Class<T> clazz);
    
    // Setters that return the context for method chaining
    IGlobalContext setGameMode(IGameMode gameMode);
    IGlobalContext setGameState(IGameState gameState);
    IGlobalContext setUtils(IUtils utils);
    IGlobalContext setStatsCache(ISpeedrunStatsCache statsCache);
    IGlobalContext setMcUtils(IMCUtils mcUtils);
    IGlobalContext setWorldManager(IWorldManager worldManager);
    IGlobalContext setRankMC(IRankMC rankMC);
    IGlobalContext setRatingCache(IRatingCache ratingCache);
    IGlobalContext setRating(IRating rating);
    IGlobalContext setRatingAPI(IRatingAPI ratingAPI);
    IGlobalContext setPlayerProfile(IPlayerProfile playerProfile);
    IGlobalContext setRank(IRank rank);
    IGlobalContext setDebugger(IDebugger debugger);
    IGlobalContext setSoundManager(ISoundManager soundManager);
    IGlobalContext setRankCache(IRankCache rankCache);
    IGlobalContext setRetentionManager(IRetentionManager retentionManager);
    IGlobalContext setRedis(IRedis redis);
    IGlobalContext setPlugin(Plugin plugin);
    IGlobalContext setGameType(IGameType gameType);
    IGlobalContext setProxyUtils(IProxyUtils proxyUtils);
    IGlobalContext setStatsManager(IStatsManager statsManager);
    IGlobalContext setLobbyStatsCache(ILobbyStatsCache lobbyStatsCache);
    IGlobalContext setSpeedrunStatsCache(ISpeedrunStatsCache speedrunStatsCache);
    IGlobalContext setPunishManager(IPunishManager punishManager);
    IGlobalContext setPunishLog(IPunishLog punishLog);
    IGlobalContext setInventoryManager(IInventoryManager inventoryManager);
    IGlobalContext setVoting(IVoting voting);
    IGlobalContext setInventoryBuilder(IInventoryBuilder inventoryBuilder);
    IGlobalContext setConfigUtils(IConfigUtils configUtils);
    IGlobalContext setTimeUtils(ITimeUtils timeUtils);
    IGlobalContext setLocalServerMetaData(ILocalServerMetaData localServerMetaData);
    IGlobalContext setPlugin(JavaPlugin plugin);

    void buildGlobalContext();
}