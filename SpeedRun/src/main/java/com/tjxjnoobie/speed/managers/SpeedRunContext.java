package com.tjxjnoobie.speed.managers;

import com.tjxjnoobie.api.dependency.contexts.abstracts.AbstractContext;
import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.plugin.Plugin;


public class SpeedRunContext extends AbstractContext<ISpeedRunContext> implements ISpeedRunContext, IContext<ISpeedRunContext> {

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
    private IBossBarManager bossBarManager;
    private IVoting voting;
    private IInventoryManager inventoryManager;
    private IGameType gameType;
    private IStatsManager statsManager;
    private ISpeedrunStatsCache speedrunStatsCache;
    private IFairFight fairFight;
    private ISpeedRunContext speedRunContext = this;

    // Helper to register both interface and concrete implementation classes
 

    /**
     * Default constructor for dependency injection
     */
    public SpeedRunContext() {
        super();
        this.setContext(this);
        // Initialize with null values - dependencies will be set via setters
        

    }

    /**
     * Constructor with all dependencies
     */
    public SpeedRunContext(Plugin plugin, IGameMode gameMode, IUtils utils, ISpeedrunStatsCache statsCache, IGameManager gameManager, IGameState gameState, IMCUtils mcUtils, IPlayerManager playerManager, IWorldManager worldManager, ILocationCache locationCache, ISpeedRunJoinEvent joinEvent, IQuitEvent quitEvent, IRankMC rankMC, IRatingCache ratingCache, IRating rating, IRatingAPI ratingAPI, IPlayerProfile playerProfile, IRank rank, IDebugger debugger, ISoundManager soundManager, IRankCache rankCache, IRetentionManager retentionManager, IRedis redis, IDebug debug, IBossBarManager bossBarManager, IVoting voting, IInventoryManager inventoryManager, IGameType gameType, IStatsManager statsManager, ISpeedrunStatsCache speedrunStatsCache, IFairFight fairFight
    ,ISpeedRunContext speedrunContext) {


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
        this.speedRunContext = speedrunContext;
    }

    // Fallback method for dependency injection
    public void initializeDependencies() {
        // Register interface and concrete types for dependency injection
        if (plugin != null) registerDependency(Plugin.class, plugin, this::getPlugin, this);
        if (gameMode != null) registerDependency(IGameMode.class, gameMode, this::getGameMode, this);
        if (gameState != null) registerDependency(IGameState.class, gameState, this::getGameState, this);
        if (utils != null) registerDependency(IUtils.class, utils, this::getUtils, this);
        if (statsCache != null) registerDependency(ISpeedrunStatsCache.class, statsCache, this::getStatsCache, this);
        if (gameManager != null) registerDependency(IGameManager.class, gameManager, this::getGameManager, this);
        if (playerManager != null) registerDependency(IPlayerManager.class, playerManager, this::getPlayerManager, this);
        if (mcUtils != null) registerDependency(IMCUtils.class, mcUtils, this::getMcUtils, this);
        if (worldManager != null) registerDependency(IWorldManager.class, worldManager, this::getWorldManager, this);
        if (locationCache != null) registerDependency(ILocationCache.class, locationCache, this::getLocationCache, this);
        if (joinEvent != null) registerDependency(ISpeedRunJoinEvent.class, joinEvent, this::getJoinEvent, this);
        if (quitEvent != null) registerDependency(IQuitEvent.class, quitEvent, this::getQuitEvent, this);
        if (rankMC != null) registerDependency(IRankMC.class, rankMC, this::getRankMC, this);
        if (ratingCache != null) registerDependency(IRatingCache.class, ratingCache, this::getRatingCache, this);
        if (rating != null) registerDependency(IRating.class, rating, this::getRating, this);
        if (ratingAPI != null) registerDependency(IRatingAPI.class, ratingAPI, this::getRatingAPI, this);
        if (playerProfile != null) registerDependency(IPlayerProfile.class, playerProfile, this::getPlayerProfile, this);
        if (rank != null) registerDependency(IRank.class, rank, this::getRank, this);
        if (debugger != null) registerDependency(IDebugger.class, debugger, this::getDebugger, this);
        if (soundManager != null) registerDependency(ISoundManager.class, soundManager, this::getSoundManager, this);
        if (rankCache != null) registerDependency(IRankCache.class, rankCache, this::getRankCache, this);
        if (retentionManager != null) registerDependency(IRetentionManager.class, retentionManager, this::getRetentionManager, this);
        if (redis != null) registerDependency(IRedis.class, redis, this::getRedis, this);
        if (debug != null) registerDependency(IDebug.class, debug, this::getDebug, this);
        if (bossBarManager != null) registerDependency(IBossBarManager.class, bossBarManager, this::getBossBarManager, this);
        if (voting != null) registerDependency(IVoting.class, voting, this::getVoting, this);
        if (inventoryManager != null) registerDependency(IInventoryManager.class, inventoryManager, this::getInventoryManager, this);
        if (gameType != null) registerDependency(IGameType.class, gameType, this::getGameType, this);
        if (statsManager != null) registerDependency(IStatsManager.class, statsManager, this::getStatsManager, this);
        if (speedrunStatsCache != null) registerDependency(ISpeedrunStatsCache.class, speedrunStatsCache, this::getSRStatsCache, this);
        if (fairFight != null) registerDependency(IFairFight.class, fairFight, this::getFairFight, this);

        // Register the context itself
        registerDependency(ISpeedRunContext.class, this, () -> this, this);
        registerDependency(ISpeedRunContext.class, this, this::getContext, this);
       // register(SpeedRunContext.class, this, this::getContext);
    }

    // Getters
    @Override
    public ISpeedRunContext getSpeedRunContext(){
        return speedRunContext;
    }
    @Override
    public Plugin getPlugin() {
        return plugin;
    }

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
    public IGameManager getGameManager() {
        return gameManager;
    }

    @Override
    public IPlayerManager getPlayerManager() {
        return playerManager;
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
    public ILocationCache getLocationCache() {
        return locationCache;
    }

    @Override
    public ISpeedRunJoinEvent getJoinEvent() {
        return joinEvent;
    }

    @Override
    public IQuitEvent getQuitEvent() {
        return quitEvent;
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

    public IDebug getDebug() {
        return debug;
    }

    @Override

    public IBossBarManager getBossBarManager() {
        return bossBarManager;
    }

    @Override

    public IVoting getVoting() {
        return voting;
    }

    @Override

    public IInventoryManager getInventoryManager() {
        return inventoryManager;
    }

    @Override

    public IGameType getGameType() {
        return gameType;
    }

    @Override

    public IStatsManager getStatsManager() {
        return statsManager;
    }

    @Override

    public ISpeedrunStatsCache getSRStatsCache() {
        return speedrunStatsCache;
    }

    @Override
    public IFairFight getFairFight() {
        return fairFight;
    }

    // Setters that return the context for method chaining
    @Override
    public ISpeedRunContext setPlugin(Plugin plugin) {
        this.plugin = plugin;
        if (plugin != null) registerDependency(Plugin.class, plugin, this::getPlugin, this);
        return this;
    }

    @Override

    public SpeedRunContext setGameMode(IGameMode gameMode) {
        this.gameMode = gameMode;
        if (gameMode != null) registerDependency(IGameMode.class, gameMode, this::getGameMode, this);
        return this;
    }

    @Override

    public SpeedRunContext setGameState(IGameState gameState) {
        this.gameState = gameState;
        if (gameState != null) registerDependency(IGameState.class, gameState, this::getGameState, this);
        return this;
    }

    @Override

    public SpeedRunContext setUtils(IUtils utils) {
        this.utils = utils;
        if (utils != null) registerDependency(IUtils.class, utils, this::getUtils, this);
        return this;
    }

    @Override

    public SpeedRunContext setStatsCache(ISpeedrunStatsCache statsCache) {
        this.statsCache = statsCache;
        if (statsCache != null) registerDependency(ISpeedrunStatsCache.class, statsCache, this::getStatsCache, this);
        return this;
    }

    @Override

    public SpeedRunContext setGameManager(IGameManager gameManager) {
        this.gameManager = gameManager;
        if (gameManager != null) registerDependency(IGameManager.class, gameManager, this::getGameManager, this);
        return this;
    }

    @Override

    public SpeedRunContext setPlayerManager(IPlayerManager playerManager) {
        this.playerManager = playerManager;
        if (playerManager != null) registerDependency(IPlayerManager.class, playerManager, this::getPlayerManager, this);
        return this;
    }

    @Override

    public SpeedRunContext setMcUtils(IMCUtils mcUtils) {
        this.mcUtils = mcUtils;
        if (mcUtils != null) registerDependency(IMCUtils.class, mcUtils, this::getMcUtils, this);
        return this;
    }

    @Override

    public SpeedRunContext setWorldManager(IWorldManager worldManager) {
        this.worldManager = worldManager;
        if (worldManager != null) registerDependency(IWorldManager.class, worldManager, this::getWorldManager, this);
        return this;
    }

    @Override

    public SpeedRunContext setLocationCache(ILocationCache locationCache) {
        this.locationCache = locationCache;
        if (locationCache != null) registerDependency(ILocationCache.class, locationCache, this::getLocationCache, this);
        return this;
    }

    @Override

    public SpeedRunContext setJoinEvent(ISpeedRunJoinEvent joinEvent) {
        this.joinEvent = joinEvent;
        if (joinEvent != null) registerDependency(ISpeedRunJoinEvent.class, joinEvent, this::getJoinEvent, this);
        return this;
    }

    @Override

    public SpeedRunContext setQuitEvent(IQuitEvent quitEvent) {
        this.quitEvent = quitEvent;
        if (quitEvent != null) registerDependency(IQuitEvent.class, quitEvent, this::getQuitEvent, this);
        return this;
    }

    @Override

    public SpeedRunContext setRankMC(IRankMC rankMC) {
        this.rankMC = rankMC;
        if (rankMC != null) registerDependency(IRankMC.class, rankMC, this::getRankMC, this);
        return this;
    }

    @Override

    public SpeedRunContext setRatingCache(IRatingCache ratingCache) {
        this.ratingCache = ratingCache;
        if (ratingCache != null) registerDependency(IRatingCache.class, ratingCache, this::getRatingCache, this);
        return this;
    }

    @Override

    public SpeedRunContext setRating(IRating rating) {
        this.rating = rating;
        if (rating != null) registerDependency(IRating.class, rating, this::getRating, this);
        return this;
    }

    @Override

    public SpeedRunContext setRatingAPI(IRatingAPI ratingAPI) {
        this.ratingAPI = ratingAPI;
        if (ratingAPI != null) registerDependency(IRatingAPI.class, ratingAPI, this::getRatingAPI, this);
        return this;
    }

    @Override

    public SpeedRunContext setPlayerProfile(IPlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
        if (playerProfile != null) registerDependency(IPlayerProfile.class, playerProfile, this::getPlayerProfile, this);
        return this;
    }

    @Override

    public SpeedRunContext setRank(IRank rank) {
        this.rank = rank;
        if (rank != null) registerDependency(IRank.class, rank, this::getRank, this);
        return this;
    }

    @Override

    public SpeedRunContext setDebugger(IDebugger debugger) {
        this.debugger = debugger;
        if (debugger != null) registerDependency(IDebugger.class, debugger, this::getDebugger, this);
        return this;
    }

    @Override

    public SpeedRunContext setSoundManager(ISoundManager soundManager) {
        this.soundManager = soundManager;
        if (soundManager != null) registerDependency(ISoundManager.class, soundManager, this::getSoundManager, this);
        return this;
    }

    @Override

    public SpeedRunContext setRankCache(IRankCache rankCache) {
        this.rankCache = rankCache;
        if (rankCache != null) registerDependency(IRankCache.class, rankCache, this::getRankCache, this);
        return this;
    }

    @Override

    public SpeedRunContext setRetentionManager(IRetentionManager retentionManager) {
        this.retentionManager = retentionManager;
        if (retentionManager != null) registerDependency(IRetentionManager.class, retentionManager, this::getRetentionManager, this);
        return this;
    }

    @Override

    public SpeedRunContext setRedis(IRedis redis) {
        this.redis = redis;
        if (redis != null) registerDependency(IRedis.class, redis, this::getRedis, this);
        return this;
    }

    @Override

    public SpeedRunContext setDebug(IDebug debug) {
        this.debug = debug;
        if (debug != null) registerDependency(IDebug.class, debug, this::getDebug, this);
        return this;
    }

    @Override

    public SpeedRunContext setBossBarManager(IBossBarManager bossBarManager) {
        this.bossBarManager = bossBarManager;
        if (bossBarManager != null) registerDependency(IBossBarManager.class, bossBarManager, this::getBossBarManager, this);
        return this;
    }

    @Override

    public SpeedRunContext setVoting(IVoting voting) {
        this.voting = voting;
        if (voting != null) registerDependency(IVoting.class, voting, this::getVoting, this);
        return this;
    }

    @Override

    public SpeedRunContext setInventoryManager(IInventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        if (inventoryManager != null) registerDependency(IInventoryManager.class, inventoryManager, this::getInventoryManager, this);
        return this;
    }

    @Override

    public SpeedRunContext setGameType(IGameType gameType) {
        this.gameType = gameType;
        if (gameType != null) registerDependency(IGameType.class, gameType, this::getGameType, this);
        return this;
    }

    @Override

    public SpeedRunContext setStatsManager(IStatsManager statsManager) {
        this.statsManager = statsManager;
        if (statsManager != null) registerDependency(IStatsManager.class, statsManager, this::getStatsManager, this);
        return this;
    }

    @Override

    public SpeedRunContext setSRStatsCache(ISpeedrunStatsCache speedrunStatsCache) {
        this.speedrunStatsCache = speedrunStatsCache;
        if (speedrunStatsCache != null) registerDependency(ISpeedrunStatsCache.class, speedrunStatsCache, this::getSRStatsCache, this);
        return this;
    }
    @Override
    public ISpeedRunContext setSpeedrunContext(ISpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
        if (speedRunContext != null) {
            registerDependency(ISpeedRunContext.class, this, () -> this, this);
            // Also register the concrete SpeedRunContext class for dependencies that need it
            registerDependency(SpeedRunContext.class, this, () -> (SpeedRunContext) this, this);
        }
        return this;
    }

    @Override
    public SpeedRunContext setFairFight(IFairFight fairFight) {
        this.fairFight = fairFight;
        if (fairFight != null) registerDependency(IFairFight.class, fairFight, this::getFairFight, this);
        return this;
    }

    /**
     * Builder pattern for easy initialization in onEnable
     * Example usage:
     * SpeedRunContext context = SpeedRunContext.builder()
     * .setPlugin(this)
     * .setGameMode(gameMode)
     * .setUtils(utils)
     * .setStatsCache(statsCache);
     */
    @Override
    public ISpeedRunContext builder() {
        return new SpeedRunContext();
    }

    @Override
    public void buildSpeedRunContext() {
                setSpeedrunContext(speedRunContext).
                setPlugin(plugin).
                setRedis(redis).
                setRank(rank).
                setRatingAPI(ratingAPI).
                setDebugger(debugger).
                setRankCache(rankCache).
                setUtils(utils).
                setWorldManager(worldManager).
                setPlayerProfile(playerProfile).
                setInventoryManager(inventoryManager).
                setSoundManager(soundManager).
                setMcUtils(mcUtils).
                setRankMC(rankMC).
                setRetentionManager(retentionManager).
                setStatsCache(statsCache).
                setGameState(gameState).
                setGameType(gameType).
                setGameMode(gameMode).
                setStatsManager(statsManager).
                setRatingCache(ratingCache).
                setJoinEvent(joinEvent).
                setDebug(debug).
                setQuitEvent(quitEvent).
                setGameManager(gameManager).
                setLocationCache(locationCache).
                setSRStatsCache(speedrunStatsCache).
                setFairFight(fairFight).
                setPlayerManager(playerManager).
                setRetentionManager(retentionManager).
                setRankCache(rankCache);

    }


    /**
     * Validates that all required dependencies are set
     *
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
     *
     * @return Summary string
     */
    public String getDependencySummary() {
        // TODO: Update using dependencyMap and loop
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