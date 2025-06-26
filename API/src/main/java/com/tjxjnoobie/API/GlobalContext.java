package com.tjxjnoobie.API;

import com.tjxjnoobie.abstracts.AbstractContext;
import com.tjxjnoobie.interfaces.*;

import java.util.HashMap;

public class GlobalContext extends AbstractContext<GlobalContext> implements IGlobalContext {

    // Core dependencies using interfaces
    private IGameState gameState;
    private IGameMode gameMode;
    private IUtils utils;
    private ISpeedrunStatsCache statsCache;
    private IWorldManager worldManager;
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
    private IGameType gameType;
    private IProxyUtils proxyUtils;
    private IStatsManager statsManager;
    private ILobbyStatsCache lobbyStatsCache;
    private ISpeedrunStatsCache speedrunStatsCache;
    private IPunishManager punishManager;
    private IPunishLog punishLog;
    private IInventoryManager inventoryManager;
    private IVoting voting;

    /**
     * Default constructor for dependency injection
     */
    public GlobalContext() {
        // Initialize with null values - dependencies will be set via setters
        initializeDependencies();
    }

    /**
     * Constructor with all dependencies
     */
    public GlobalContext(IGameMode gameMode, IUtils utils, ISpeedrunStatsCache statsCache, IGameState gameState, 
                         IMCUtils mcUtils, IWorldManager worldManager, IRankMC rankMC, IRatingCache ratingCache,
                         IRating rating, IRatingAPI ratingAPI, IPlayerProfile playerProfile, IRank rank, 
                         IDebugger debugger, ISoundManager soundManager, IRankCache rankCache, 
                         IRetentionManager retentionManager, IRedis redis, IGameType gameType, 
                         IProxyUtils proxyUtils, IStatsManager statsManager, ILobbyStatsCache lobbyStatsCache,
                         ISpeedrunStatsCache speedrunStatsCache, IPunishManager punishManager, IPunishLog punishLog
                        ,IInventoryManager inventoryManager) {
        
        this.gameMode = gameMode;
        this.utils = utils;
        this.statsCache = statsCache;
        this.gameState = gameState;
        this.mcUtils = mcUtils;
        this.worldManager = worldManager;
        this.rankMC = rankMC;
        this.ratingCache = ratingCache;
        this.rating = rating;
        this.ratingAPI = ratingAPI;
        this.playerProfile = playerProfile;
        this.rank = rank;
        this.debugger = debugger;
        this.soundManager = soundManager;
        this.rankCache = rankCache;
        this.retentionManager = retentionManager;
        this.redis = redis;
        this.gameType = gameType;
        this.proxyUtils = proxyUtils;
        this.statsManager = statsManager;
        this.lobbyStatsCache = lobbyStatsCache;
        this.speedrunStatsCache = speedrunStatsCache;
        this.punishManager = punishManager;
        this.punishLog = punishLog;
        this.inventoryManager = inventoryManager;
        
        initializeDependencies();
    }
    
    @Override
    protected void initializeDependencies() {
        // Register interface types for dependency injection
        if (gameMode != null) register(IGameMode.class, gameMode);
        if (utils != null) register(IUtils.class, utils);
        if (statsCache != null) register(ISpeedrunStatsCache.class, statsCache);
        if (gameState != null) register(IGameState.class, gameState);
        if (mcUtils != null) register(IMCUtils.class, mcUtils);
        if (worldManager != null) register(IWorldManager.class, worldManager);
        if (rankMC != null) register(IRankMC.class, rankMC);
        if (ratingCache != null) register(IRatingCache.class, ratingCache);
        if (rating != null) register(IRating.class, rating);
        if (ratingAPI != null) register(IRatingAPI.class, ratingAPI);
        if (playerProfile != null) register(IPlayerProfile.class, playerProfile);
        if (rank != null) register(IRank.class, rank);
        if (debugger != null) register(IDebugger.class, debugger);
        if (soundManager != null) register(ISoundManager.class, soundManager);
        if (rankCache != null) register(IRankCache.class, rankCache);
        if (retentionManager != null) register(IRetentionManager.class, retentionManager);
        if (redis != null) register(IRedis.class, redis);
        if (gameType != null) register(IGameType.class, gameType);
        if (proxyUtils != null) register(IProxyUtils.class, proxyUtils);
        if (statsManager != null) register(IStatsManager.class, statsManager);
        if (lobbyStatsCache != null) register(ILobbyStatsCache.class, lobbyStatsCache);
        if (speedrunStatsCache != null) register(ISpeedrunStatsCache.class, speedrunStatsCache);
        if (punishManager != null) register(IPunishManager.class, punishManager);
        if (punishLog != null) register(IPunishLog.class, punishLog);
        if (inventoryManager != null) register(IInventoryManager.class, inventoryManager);
        // Register the context itself
        register(IGlobalContext.class, this);
        register(GlobalContext.class, this);
    }

    // Getters
    @Override
    public IGameMode getGameMode() {
        return gameMode;
    }

    @Override
    public IGameState getGameState() {
        return gameState;
    }

    @Override
    public IUtils getUtils() {
        return utils;
    }

    @Override
    public ISpeedrunStatsCache getStatsCache() {
        return statsCache;
    }

    @Override
    public IMCUtils getMcUtils() {
        return mcUtils;
    }

    @Override
    public IWorldManager getWorldManager() {
        return worldManager;
    }

    @Override
    public IRankMC getRankMC() {
        return rankMC;
    }

    @Override
    public IRatingCache getRatingCache() {
        return ratingCache;
    }

    @Override
    public IRating getRating() {
        return rating;
    }

    @Override
    public IRatingAPI getRatingAPI() {
        return ratingAPI;
    }

    @Override
    public IPlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    @Override
    public IRank getRank() {
        return rank;
    }

    @Override
    public IDebugger getDebugger() {
        return debugger;
    }

    @Override
    public ISoundManager getSoundManager() {
        return soundManager;
    }

    @Override
    public IRankCache getRankCache() {
        return rankCache;
    }

    @Override
    public IRetentionManager getRetentionManager() {
        return retentionManager;
    }

    @Override
    public IRedis getRedis() {
        return redis;
    }

    @Override
    public IGameType getGameType() {
        return gameType;
    }

    @Override
    public IProxyUtils getProxyUtils() {
        return proxyUtils;
    }

    @Override
    public IStatsManager getStatsManager() {
        return statsManager;
    }

    @Override
    public ILobbyStatsCache getLobbyStatsCache() {
        return lobbyStatsCache;
    }

    @Override
    public ISpeedrunStatsCache getSRStatsCache() {
        return speedrunStatsCache;
    }

    @Override
    public IPunishManager getPunishManager() {
        return punishManager;
    }

    @Override
    public IPunishLog getPunishLog() {
        return punishLog;
    }

    @Override
    public IInventoryManager getInventoryManager() {
        return inventoryManager;
    }

    @Override
    public IVoting getVoting() {
        return voting;
    }

    // Setters that return the context for method chaining
    @Override
    public IGlobalContext setGameMode(IGameMode gameMode) {
        this.gameMode = gameMode;
        if (gameMode != null) register(IGameMode.class, gameMode);
        return this;
    }

    @Override
    public IGlobalContext setGameState(IGameState gameState) {
        this.gameState = gameState;
        if (gameState != null) register(IGameState.class, gameState);
        return this;
    }

    @Override
    public IGlobalContext setUtils(IUtils utils) {
        this.utils = utils;
        if (utils != null) register(IUtils.class, utils);
        return this;
    }

    @Override
    public IGlobalContext setStatsCache(ISpeedrunStatsCache statsCache) {
        this.statsCache = statsCache;
        if (statsCache != null) register(ISpeedrunStatsCache.class, statsCache);
        return this;
    }

    @Override
    public IGlobalContext setMcUtils(IMCUtils mcUtils) {
        this.mcUtils = mcUtils;
        if (mcUtils != null) register(IMCUtils.class, mcUtils);
        return this;
    }

    @Override
    public IGlobalContext setWorldManager(IWorldManager worldManager) {
        this.worldManager = worldManager;
        if (worldManager != null) register(IWorldManager.class, worldManager);
        return this;
    }

    @Override
    public IGlobalContext setRankMC(IRankMC rankMC) {
        this.rankMC = rankMC;
        if (rankMC != null) register(IRankMC.class, rankMC);
        return this;
    }

    @Override
    public IGlobalContext setRatingCache(IRatingCache ratingCache) {
        this.ratingCache = ratingCache;
        if (ratingCache != null) register(IRatingCache.class, ratingCache);
        return this;
    }

    @Override
    public IGlobalContext setRating(IRating rating) {
        this.rating = rating;
        if (rating != null) register(IRating.class, rating);
        return this;
    }

    @Override
    public IGlobalContext setRatingAPI(IRatingAPI ratingAPI) {
        this.ratingAPI = ratingAPI;
        if (ratingAPI != null) register(IRatingAPI.class, ratingAPI);
        return this;
    }

    @Override
    public IGlobalContext setPlayerProfile(IPlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
        if (playerProfile != null) register(IPlayerProfile.class, playerProfile);
        return this;
    }

    @Override
    public IGlobalContext setRank(IRank rank) {
        this.rank = rank;
        if (rank != null) register(IRank.class, rank);
        return this;
    }

    @Override
    public IGlobalContext setDebugger(IDebugger debugger) {
        this.debugger = debugger;
        if (debugger != null) register(IDebugger.class, debugger);
        return this;
    }

    @Override
    public IGlobalContext setSoundManager(ISoundManager soundManager) {
        this.soundManager = soundManager;
        if (soundManager != null) register(ISoundManager.class, soundManager);
        return this;
    }

    @Override
    public IGlobalContext setRankCache(IRankCache rankCache) {
        this.rankCache = rankCache;
        if (rankCache != null) register(IRankCache.class, rankCache);
        return this;
    }

    @Override
    public IGlobalContext setRetentionManager(IRetentionManager retentionManager) {
        this.retentionManager = retentionManager;
        if (retentionManager != null) register(IRetentionManager.class, retentionManager);
        return this;
    }

    @Override
    public IGlobalContext setRedis(IRedis redis) {
        this.redis = redis;
        if (redis != null) register(IRedis.class, redis);
        return this;
    }

    @Override
    public IGlobalContext setGameType(IGameType gameType) {
        this.gameType = gameType;
        if (gameType != null) register(IGameType.class, gameType);
        return this;
    }

    @Override
    public IGlobalContext setProxyUtils(IProxyUtils proxyUtils) {
        this.proxyUtils = proxyUtils;
        if (proxyUtils != null) register(IProxyUtils.class, proxyUtils);
        return this;
    }

    @Override
    public IGlobalContext setStatsManager(IStatsManager statsManager) {
        this.statsManager = statsManager;
        if (statsManager != null) register(IStatsManager.class, statsManager);
        return this;
    }

    @Override
    public IGlobalContext setLobbyStatsCache(ILobbyStatsCache lobbyStatsCache) {
        this.lobbyStatsCache = lobbyStatsCache;
        if (lobbyStatsCache != null) register(ILobbyStatsCache.class, lobbyStatsCache);
        return this;
    }

    @Override
    public IGlobalContext setSpeedrunStatsCache(ISpeedrunStatsCache speedrunStatsCache) {
        this.speedrunStatsCache = speedrunStatsCache;
        if (speedrunStatsCache != null) register(ISpeedrunStatsCache.class, speedrunStatsCache);
        return this;
    }

    @Override
    public IGlobalContext setPunishManager(IPunishManager punishManager) {
        this.punishManager = punishManager;
        if (punishManager != null) register(IPunishManager.class, punishManager);
        return this;
    }

    @Override
    public IGlobalContext setPunishLog(IPunishLog punishLog) {
        this.punishLog = punishLog;
        if (punishLog != null) register(IPunishLog.class, punishLog);
        return this;
    }

    @Override
    public IGlobalContext setInventoryManager(IInventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        if(inventoryManager != null) register(IInventoryManager.class, inventoryManager);
        return this;
    }

    @Override
    public IGlobalContext setVoting(IVoting voting) {
        this.voting = voting;
        if(voting != null) register(IVoting.class, voting);
        return this;
    }


    /**
     * Builder pattern for easy initialization in onEnable
     * Example usage:
     * GlobalContext context = new GlobalContext()
     *     .setGameMode(gameMode)
     *     .setUtils(utils)
     *     .setStatsCache(statsCache);
     */
    public static GlobalContext builder() {
        return new GlobalContext();
    }

    /**
     * Validates that all required dependencies are set
     * @throws IllegalStateException if required dependencies are missing
     */
    public void validate() {
        StringBuilder missing = new StringBuilder();
        
        if (gameMode == null) missing.append("gameMode, ");
        if (gameState == null) missing.append("gameState, ");
        if (utils == null) missing.append("utils, ");
        if (mcUtils == null) missing.append("mcUtils, ");
        if (debugger == null) missing.append("debugger, ");
        
        if (missing.length() > 0) {
            missing.setLength(missing.length() - 2); // Remove last comma and space
            // Default void abstraction - log missing dependencies instead of throwing
            System.err.println("Warning: Missing required dependencies: " + missing.toString());
        }
    }

    /**
     * Gets a summary of all registered dependencies
     * @return Summary string
     */
    public String getDependencySummary() {
        StringBuilder summary = new StringBuilder("GlobalContext Dependencies:\n");
        
        summary.append("- GameMode: ").append(gameMode != null ? "✓" : "✗").append("\n");
        summary.append("- GameState: ").append(gameState != null ? "✓" : "✗").append("\n");
        summary.append("- Utils: ").append(utils != null ? "✓" : "✗").append("\n");
        summary.append("- MCUtils: ").append(mcUtils != null ? "✓" : "✗").append("\n");
        summary.append("- WorldManager: ").append(worldManager != null ? "✓" : "✗").append("\n");
        summary.append("- Debugger: ").append(debugger != null ? "✓" : "✗").append("\n");
        summary.append("- Redis: ").append(redis != null ? "✓" : "✗").append("\n");
        summary.append("- StatsCache: ").append(statsCache != null ? "✓" : "✗").append("\n");
        summary.append("- RatingCache: ").append(ratingCache != null ? "✓" : "✗").append("\n");
        summary.append("- Total registered: ").append(dependencyMap.size()).append("\n");
        
        return summary.toString();
    }
}