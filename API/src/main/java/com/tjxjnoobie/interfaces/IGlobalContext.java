package com.tjxjnoobie.interfaces;

import java.util.HashMap;

/**
 * Interface for GlobalContext to provide dependency injection capabilities
 */
public interface IGlobalContext {
    
    // Core getters using interfaces
    IGameMode getGameMode();
    IGameState getGameState();
    IUtils getUtils();
    ISpeedrunStatsCache getStatsCache();
    IMCUtils getMcUtils();
    IWorldManager getWorldManager();
    IRankMC getRankMC();
    IRatingCache getRatingCache();
    IRating getRating();
    IRatingAPI getRatingAPI();
    IPlayerProfile getPlayerProfile();
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
    IInventoryManager getInventoryManager();
    IVoting getVoting();
    
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
    IGlobalContext setGameType(IGameType gameType);
    IGlobalContext setProxyUtils(IProxyUtils proxyUtils);
    IGlobalContext setStatsManager(IStatsManager statsManager);
    IGlobalContext setLobbyStatsCache(ILobbyStatsCache lobbyStatsCache);
    IGlobalContext setSpeedrunStatsCache(ISpeedrunStatsCache speedrunStatsCache);
    IGlobalContext setPunishManager(IPunishManager punishManager);
    IGlobalContext setPunishLog(IPunishLog punishLog);
    IGlobalContext setInventoryManager(IInventoryManager inventoryManager);
    IGlobalContext setVoting(IVoting voting);
}