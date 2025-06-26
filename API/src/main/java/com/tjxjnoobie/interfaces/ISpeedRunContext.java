package com.tjxjnoobie.interfaces;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * Interface for SpeedRunContext to provide dependency injection capabilities
 */
public abstract class ISpeedRunContext {
    
    // Core getters using interfaces
    protected abstract Plugin getPlugin();
    abstract IGameMode getGameMode();
    abstract IGameState getGameState();
    abstract IUtils getUtils();
    abstract ISpeedrunStatsCache getStatsCache();
    abstract IGameManager getGameManager();
    abstract IPlayerManager getPlayerManager();
    abstract IMCUtils getMcUtils();
    abstract IWorldManager getWorldManager();
    abstract ILocationCache getLocationCache();
    abstract IJoinEvent getJoinEvent();
    abstract IQuitEvent getQuitEvent();
    abstract IRankMC getRankMC();
    abstract IRatingCache getRatingCache();
    abstract IRating getRating();
    abstract IRatingAPI getRatingAPI();
    abstract IPlayerProfile getPlayerProfile();
    abstract  IRank getRank();
    abstract  IDebugger getDebugger();
    abstract  ISoundManager getSoundManager();
    abstract  IRankCache getRankCache();
    abstract  IRetentionManager getRetentionManager();
    abstract  IRedis getRedis();
    abstract  IDebug getDebug();
    abstract  IBossBarManager getBossBarManager();
    abstract  IVoting getVoting();
    abstract  IInventoryManager getInventoryManager();
    abstract IGameType getGameType();
    abstract IStatsManager getStatsManager();
    abstract ISpeedrunStatsCache getSRStatsCache();
    abstract  IFairFight getFairFight();
    
    // Dependency map access
    abstract HashMap<Class<?>, Object> getDependencyMap();
    abstract  <T> T get(Class<T> clazz);
    
    // Setters that return the context for method chaining
    abstract ISpeedRunContext setPlugin(Plugin plugin);
    abstract ISpeedRunContext setGameMode(IGameMode gameMode);
    abstract ISpeedRunContext setGameState(IGameState gameState);
    abstract  ISpeedRunContext setUtils(IUtils utils);
    abstract ISpeedRunContext setStatsCache(ISpeedrunStatsCache statsCache);
    abstract ISpeedRunContext setGameManager(IGameManager gameManager);
    abstract ISpeedRunContext setPlayerManager(IPlayerManager playerManager);
    abstract  ISpeedRunContext setMcUtils(IMCUtils mcUtils);
    abstract  ISpeedRunContext setWorldManager(IWorldManager worldManager);
    abstract ISpeedRunContext setLocationCache(ILocationCache locationCache);
    abstract  ISpeedRunContext setJoinEvent(IJoinEvent joinEvent);
    abstract  ISpeedRunContext setQuitEvent(IQuitEvent quitEvent);
    abstract  ISpeedRunContext setRankMC(IRankMC rankMC);
    abstract  ISpeedRunContext setRatingCache(IRatingCache ratingCache);
    abstract  ISpeedRunContext setRating(IRating rating);
    abstract   ISpeedRunContext setRatingAPI(IRatingAPI ratingAPI);
    abstract   ISpeedRunContext setPlayerProfile(IPlayerProfile playerProfile);
    abstract   ISpeedRunContext setRank(IRank rank);
    abstract   ISpeedRunContext setDebugger(IDebugger debugger);
    abstract   ISpeedRunContext setSoundManager(ISoundManager soundManager);
    abstract  ISpeedRunContext setRankCache(IRankCache rankCache);
    abstract   ISpeedRunContext setRetentionManager(IRetentionManager retentionManager);
    abstract   ISpeedRunContext setRedis(IRedis redis);
    abstract  ISpeedRunContext setDebug(IDebug debug);
    abstract  ISpeedRunContext setBossBarManager(IBossBarManager bossBarManager);
    abstract    ISpeedRunContext setVoting(IVoting voting);
    abstract    ISpeedRunContext setInventoryManager(IInventoryManager inventoryManager);
    abstract   ISpeedRunContext setGameType(IGameType gameType);
    abstract   ISpeedRunContext setStatsManager(IStatsManager statsManager);
    abstract   ISpeedRunContext setSRStatsCache(ISpeedrunStatsCache speedrunStatsCache);
    abstract  ISpeedRunContext setFairFight(IFairFight fairFight);
}