package com.tjxjnoobie.speed.managers;

import com.tjxjnoobie.abstracts.AbstractContext;
import com.tjxjnoobie.interfaces.*;
import org.bukkit.plugin.Plugin;

public class SpeedRunContext extends AbstractContext<SpeedRunContext>  {

    // Core dependencies using interfaces
    private Plugin plugin;
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
    private IBossBarManager bossBarManager;
    private IVoting voting;
    private IInventoryManager inventoryManager;
    private IGameType gameType;
    private IStatsManager statsManager;
    private ISpeedrunStatsCache speedrunStatsCache;
    private IFairFight fairFight;

    /**
     * Default constructor for dependency injection
     */
    public SpeedRunContext() {
        // Initialize with null values - dependencies will be set via setters
        initializeDependencies();
    }

    /**
     * Constructor with all dependencies
     */
    public SpeedRunContext(Plugin plugin, IGameMode gameMode, IUtils utils, ISpeedrunStatsCache statsCache,
                           IGameManager gameManager, IGameState gameState, IMCUtils mcUtils,
                           IPlayerManager playerManager, IWorldManager worldManager,
                           ILocationCache locationCache, IJoinEvent joinEvent, IQuitEvent quitEvent, 
                           IRankMC rankMC, IRatingCache ratingCache, IRating rating, IRatingAPI ratingAPI, 
                           IPlayerProfile playerProfile, IRank rank, IDebugger debugger, 
                           ISoundManager soundManager, IRankCache rankCache, IRetentionManager retentionManager, 
                           IRedis redis, IDebug debug, IBossBarManager bossBarManager, IVoting voting, 
                           IInventoryManager inventoryManager, IGameType gameType, IStatsManager statsManager, 
                           ISpeedrunStatsCache speedrunStatsCache, IFairFight fairFight) {
        
        this.plugin = plugin;
        this.gameMode = gameMode;
        this.utils = utils;
        this.statsCache = statsCache;
        this.gameManager = gameManager;
        this.gameState = gameState;
        this.mcUtils = mcUtils;
        this.playerManager = playerManager;
        this.worldManager = worldManager;
        this.locationCache = locationCache;
        this.joinEvent = joinEvent;
        this.quitEvent = quitEvent;
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
        this.debug = debug;
        this.bossBarManager = bossBarManager;
        this.voting = voting;
        this.inventoryManager = inventoryManager;
        this.gameType = gameType;
        this.statsManager = statsManager;
        this.speedrunStatsCache = speedrunStatsCache;
        this.fairFight = fairFight;
        
        initializeDependencies();
    }

     
    protected void initializeDependencies() {
        // Register interface types for dependency injection
        if (plugin != null) register(Plugin.class, plugin);
        if (gameMode != null) register(IGameMode.class, gameMode);
        if (gameState != null) register(IGameState.class, gameState);
        if (utils != null) register(IUtils.class, utils);
        if (statsCache != null) register(ISpeedrunStatsCache.class, statsCache);
        if (gameManager != null) register(IGameManager.class, gameManager);
        if (playerManager != null) register(IPlayerManager.class, playerManager);
        if (mcUtils != null) register(IMCUtils.class, mcUtils);
        if (worldManager != null) register(IWorldManager.class, worldManager);
        if (locationCache != null) register(ILocationCache.class, locationCache);
        if (joinEvent != null) register(IJoinEvent.class, joinEvent);
        if (quitEvent != null) register(IQuitEvent.class, quitEvent);
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
        if (debug != null) register(IDebug.class, debug);
        if (bossBarManager != null) register(IBossBarManager.class, bossBarManager);
        if (voting != null) register(IVoting.class, voting);
        if (inventoryManager != null) register(IInventoryManager.class, inventoryManager);
        if (gameType != null) register(IGameType.class, gameType);
        if (statsManager != null) register(IStatsManager.class, statsManager);
        if (speedrunStatsCache != null) register(ISpeedrunStatsCache.class, speedrunStatsCache);
        if (fairFight != null) register(IFairFight.class, fairFight);
        
        // Register the context itself
        register(SpeedRunContext.class, this);
        register(SpeedRunContext.class, this);
    }

    // Getters
     
    public Plugin getPlugin() {
        return plugin;
    }

    public IGameMode getGameMode() {
        return gameMode;
    }

    public IGameState getGameState() {
        return gameState;
    }

    public IUtils getUtils() {
        return utils;
    }

    public ISpeedrunStatsCache getStatsCache() {
        return statsCache;
    }

    public IGameManager getGameManager() {
        return gameManager;
    }

    public IPlayerManager getPlayerManager() {
        return playerManager;
    }

    public IMCUtils getMcUtils() {
        return mcUtils;
    }

    public IWorldManager getWorldManager() {
        return worldManager;
    }

    public ILocationCache getLocationCache() {
        return locationCache;
    }

    public IJoinEvent getJoinEvent() {
        return joinEvent;
    }

    public IQuitEvent getQuitEvent() {
        return quitEvent;
    }

    public IRankMC getRankMC() {
        return rankMC;
    }

    public IRatingCache getRatingCache() {
        return ratingCache;
    }

     

    public IRating getRating() {
        return rating;
    }

     
    public IRatingAPI getRatingAPI() {
        return ratingAPI;
    }

     
    public IPlayerProfile getPlayerProfile() {
        return playerProfile;
    }

     
    public IRank getRank() {
        return rank;
    }

     
    public IDebugger getDebugger() {
        return debugger;
    }

     
    public ISoundManager getSoundManager() {
        return soundManager;
    }

     
    public IRankCache getRankCache() {
        return rankCache;
    }

     
    public IRetentionManager getRetentionManager() {
        return retentionManager;
    }

     
    public IRedis getRedis() {
        return redis;
    }

     
    public IDebug getDebug() {
        return debug;
    }

     
    public IBossBarManager getBossBarManager() {
        return bossBarManager;
    }

     
    public IVoting getVoting() {
        return voting;
    }

     
    public IInventoryManager getInventoryManager() {
        return inventoryManager;
    }

     
    public IGameType getGameType() {
        return gameType;
    }

     
    public IStatsManager getStatsManager() {
        return statsManager;
    }

     
    public ISpeedrunStatsCache getSRStatsCache() {
        return speedrunStatsCache;
    }

     
    public IFairFight getFairFight() {
        return fairFight;
    }

    // Setters that return the context for method chaining
     
    public SpeedRunContext setPlugin(Plugin plugin) {
        this.plugin = plugin;
        if (plugin != null) register(Plugin.class, plugin);
        return this;
    }

     
    public SpeedRunContext setGameMode(IGameMode gameMode) {
        this.gameMode = gameMode;
        if (gameMode != null) register(IGameMode.class, gameMode);
        return this;
    }

     
    public SpeedRunContext setGameState(IGameState gameState) {
        this.gameState = gameState;
        if (gameState != null) register(IGameState.class, gameState);
        return this;
    }

     
    public SpeedRunContext setUtils(IUtils utils) {
        this.utils = utils;
        if (utils != null) register(IUtils.class, utils);
        return this;
    }

     
    public SpeedRunContext setStatsCache(ISpeedrunStatsCache statsCache) {
        this.statsCache = statsCache;
        if (statsCache != null) register(ISpeedrunStatsCache.class, statsCache);
        return this;
    }

     
    public SpeedRunContext setGameManager(IGameManager gameManager) {
        this.gameManager = gameManager;
        if (gameManager != null) register(IGameManager.class, gameManager);
        return this;
    }

     
    public SpeedRunContext setPlayerManager(IPlayerManager playerManager) {
        this.playerManager = playerManager;
        if (playerManager != null) register(IPlayerManager.class, playerManager);
        return this;
    }

     
    public SpeedRunContext setMcUtils(IMCUtils mcUtils) {
        this.mcUtils = mcUtils;
        if (mcUtils != null) register(IMCUtils.class, mcUtils);
        return this;
    }

     
    public SpeedRunContext setWorldManager(IWorldManager worldManager) {
        this.worldManager = worldManager;
        if (worldManager != null) register(IWorldManager.class, worldManager);
        return this;
    }

     
    public SpeedRunContext setLocationCache(ILocationCache locationCache) {
        this.locationCache = locationCache;
        if (locationCache != null) register(ILocationCache.class, locationCache);
        return this;
    }

     
    public SpeedRunContext setJoinEvent(IJoinEvent joinEvent) {
        this.joinEvent = joinEvent;
        if (joinEvent != null) register(IJoinEvent.class, joinEvent);
        return this;
    }

     
    public SpeedRunContext setQuitEvent(IQuitEvent quitEvent) {
        this.quitEvent = quitEvent;
        if (quitEvent != null) register(IQuitEvent.class, quitEvent);
        return this;
    }

     
    public SpeedRunContext setRankMC(IRankMC rankMC) {
        this.rankMC = rankMC;
        if (rankMC != null) register(IRankMC.class, rankMC);
        return this;
    }

     
    public SpeedRunContext setRatingCache(IRatingCache ratingCache) {
        this.ratingCache = ratingCache;
        if (ratingCache != null) register(IRatingCache.class, ratingCache);
        return this;
    }

     
    public SpeedRunContext setRating(IRating rating) {
        this.rating = rating;
        if (rating != null) register(IRating.class, rating);
        return this;
    }

     
    public SpeedRunContext setRatingAPI(IRatingAPI ratingAPI) {
        this.ratingAPI = ratingAPI;
        if (ratingAPI != null) register(IRatingAPI.class, ratingAPI);
        return this;
    }

     
    public SpeedRunContext setPlayerProfile(IPlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
        if (playerProfile != null) register(IPlayerProfile.class, playerProfile);
        return this;
    }

     
    public SpeedRunContext setRank(IRank rank) {
        this.rank = rank;
        if (rank != null) register(IRank.class, rank);
        return this;
    }

     
    public SpeedRunContext setDebugger(IDebugger debugger) {
        this.debugger = debugger;
        if (debugger != null) register(IDebugger.class, debugger);
        return this;
    }

     
    public SpeedRunContext setSoundManager(ISoundManager soundManager) {
        this.soundManager = soundManager;
        if (soundManager != null) register(ISoundManager.class, soundManager);
        return this;
    }

     
    public SpeedRunContext setRankCache(IRankCache rankCache) {
        this.rankCache = rankCache;
        if (rankCache != null) register(IRankCache.class, rankCache);
        return this;
    }

     
    public SpeedRunContext setRetentionManager(IRetentionManager retentionManager) {
        this.retentionManager = retentionManager;
        if (retentionManager != null) register(IRetentionManager.class, retentionManager);
        return this;
    }

     
    public SpeedRunContext setRedis(IRedis redis) {
        this.redis = redis;
        if (redis != null) register(IRedis.class, redis);
        return this;
    }

     
    public SpeedRunContext setDebug(IDebug debug) {
        this.debug = debug;
        if (debug != null) register(IDebug.class, debug);
        return this;
    }

     
    public SpeedRunContext setBossBarManager(IBossBarManager bossBarManager) {
        this.bossBarManager = bossBarManager;
        if (bossBarManager != null) register(IBossBarManager.class, bossBarManager);
        return this;
    }

     
    public SpeedRunContext setVoting(IVoting voting) {
        this.voting = voting;
        if (voting != null) register(IVoting.class, voting);
        return this;
    }

     
    public SpeedRunContext setInventoryManager(IInventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        if (inventoryManager != null) register(IInventoryManager.class, inventoryManager);
        return this;
    }

     
    public SpeedRunContext setGameType(IGameType gameType) {
        this.gameType = gameType;
        if (gameType != null) register(IGameType.class, gameType);
        return this;
    }

     
    public SpeedRunContext setStatsManager(IStatsManager statsManager) {
        this.statsManager = statsManager;
        if (statsManager != null) register(IStatsManager.class, statsManager);
        return this;
    }

     
    public SpeedRunContext setSRStatsCache(ISpeedrunStatsCache speedrunStatsCache) {
        this.speedrunStatsCache = speedrunStatsCache;
        if (speedrunStatsCache != null) register(ISpeedrunStatsCache.class, speedrunStatsCache);
        return this;
    }

     
    public SpeedRunContext setFairFight(IFairFight fairFight) {
        this.fairFight = fairFight;
        if (fairFight != null) register(IFairFight.class, fairFight);
        return this;
    }

    /**
     * Builder pattern for easy initialization in onEnable
     * Example usage:
     * SpeedRunContext context = SpeedRunContext.builder()
     *     .setPlugin(this)
     *     .setGameMode(gameMode)
     *     .setUtils(utils)
     *     .setStatsCache(statsCache);
     */
    public static SpeedRunContext builder() {
        return new SpeedRunContext();
    }

    /**
     * Validates that all required dependencies are set
     * @throws IllegalStateException if required dependencies are missing
     */
    public void validate() {
        StringBuilder missing = new StringBuilder();
        
        if (plugin == null) missing.append("plugin, ");
        if (gameMode == null) missing.append("gameMode, ");
        if (gameState == null) missing.append("gameState, ");
        if (utils == null) missing.append("utils, ");
        if (mcUtils == null) missing.append("mcUtils, ");
        if (debugger == null) missing.append("debugger, ");
        
        if (missing.length() > 0) {
            missing.setLength(missing.length() - 2); // Remove last comma and space
            throw new IllegalStateException("Missing required dependencies: " + missing.toString());
        }
    }

    /**
     * Gets a summary of all registered dependencies
     * @return Summary string
     */
    public String getDependencySummary() {
        StringBuilder summary = new StringBuilder("SpeedRunContext Dependencies:\n");
        
        summary.append("- Plugin: ").append(plugin != null ? "✓" : "✗").append("\n");
        summary.append("- GameMode: ").append(gameMode != null ? "✓" : "✗").append("\n");
        summary.append("- GameState: ").append(gameState != null ? "✓" : "✗").append("\n");
        summary.append("- Utils: ").append(utils != null ? "✓" : "✗").append("\n");
        summary.append("- MCUtils: ").append(mcUtils != null ? "✓" : "✗").append("\n");
        summary.append("- GameManager: ").append(gameManager != null ? "✓" : "✗").append("\n");
        summary.append("- PlayerManager: ").append(playerManager != null ? "✓" : "✗").append("\n");
        summary.append("- WorldManager: ").append(worldManager != null ? "✓" : "✗").append("\n");
        summary.append("- Debugger: ").append(debugger != null ? "✓" : "✗").append("\n");
        summary.append("- Total registered: ").append(dependencyMap.size()).append("\n");
        
        return summary.toString();
    }
}