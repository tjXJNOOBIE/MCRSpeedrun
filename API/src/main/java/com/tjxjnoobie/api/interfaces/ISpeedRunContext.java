package com.tjxjnoobie.api.interfaces;

import org.bukkit.plugin.Plugin;

/**
 * Interface for SpeedRunContext to provide dependency injection capabilities
 */
public interface ISpeedRunContext {

    // Getters
    ISpeedRunContext getSpeedRunContext();

    // Interface based getters for dependency injection for SpeedRun Module
    Plugin getPlugin();

    IGameMode getGameMode();

    IGameState getGameState();

    IUtils getUtils();

    ISpeedrunStatsCache getStatsCache();

    IGameManager getGameManager();

    IPlayerManager getPlayerManager();

    IMCUtils getMcUtils();

    IWorldManager getWorldManager();

    ILocationCache getLocationCache();

    ISpeedRunJoinEvent getJoinEvent();

    IQuitEvent getQuitEvent();

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

    IDebug getDebug();

    IBossBarManager getBossBarManager();

    IVoting getVoting();

    IInventoryManager getInventoryManager();

    IGameType getGameType();

    IStatsManager getStatsManager();

    ISpeedrunStatsCache getSRStatsCache();

    IFairFight getFairFight();
    


    // Setters that return the context for method chaining
    ISpeedRunContext setGameMode(IGameMode gameMode);

    ISpeedRunContext setGameState(IGameState gameState);

    ISpeedRunContext setUtils(IUtils utils);

    ISpeedRunContext setStatsCache(ISpeedrunStatsCache statsCache);

    ISpeedRunContext setGameManager(IGameManager gameManager);

    ISpeedRunContext setPlayerManager(IPlayerManager playerManager);

    ISpeedRunContext setMcUtils(IMCUtils mcUtils);

    ISpeedRunContext setWorldManager(IWorldManager worldManager);

    ISpeedRunContext setLocationCache(ILocationCache locationCache);

    ISpeedRunContext setJoinEvent(ISpeedRunJoinEvent joinEvent);

    ISpeedRunContext setQuitEvent(IQuitEvent quitEvent);

    ISpeedRunContext setRankMC(IRankMC rankMC);

    ISpeedRunContext setRatingCache(IRatingCache ratingCache);

    ISpeedRunContext setRating(IRating rating);

    ISpeedRunContext setRatingAPI(IRatingAPI ratingAPI);

    ISpeedRunContext setPlayerProfile(IPlayerProfile playerProfile);

    ISpeedRunContext setRank(IRank rank);

    ISpeedRunContext setDebugger(IDebugger debugger);

    ISpeedRunContext setSoundManager(ISoundManager soundManager);

    ISpeedRunContext setRankCache(IRankCache rankCache);

    ISpeedRunContext setRetentionManager(IRetentionManager retentionManager);

    ISpeedRunContext setRedis(IRedis redis);

    ISpeedRunContext setDebug(IDebug debug);

    ISpeedRunContext setBossBarManager(IBossBarManager bossBarManager);

    ISpeedRunContext setVoting(IVoting voting);

    ISpeedRunContext setInventoryManager(IInventoryManager inventoryManager);

    ISpeedRunContext setGameType(IGameType gameType);

    ISpeedRunContext setStatsManager(IStatsManager statsManager);

    ISpeedRunContext setSRStatsCache(ISpeedrunStatsCache speedrunStatsCache);

    ISpeedRunContext setSpeedrunContext(ISpeedRunContext speedRunContext);

    ISpeedRunContext setFairFight(IFairFight fairFight);

    ISpeedRunContext builder();

    void buildSpeedRunContext();
    void initializeDependencies();

    ISpeedRunContext setPlugin(Plugin plugin);


}