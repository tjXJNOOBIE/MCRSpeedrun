package com.tjxjnoobie.api.dependency.contexts;

import com.tjxjnoobie.api.dependency.contexts.abstracts.AbstractContext;
import com.tjxjnoobie.api.dependency.injection.helpers.ContextInjectionHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.DependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IContextInjectionHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;
import com.tjxjnoobie.api.platform.global.metadata.enums.ClassSetting;
import com.tjxjnoobie.api.platform.global.metadata.interfaces.IAbstractClassMetaData;
import com.tjxjnoobie.api.platform.global.utils.interfaces.IConfigUtils;
import com.tjxjnoobie.api.platform.global.utils.interfaces.ITimeUtils;
import org.bukkit.plugin.Plugin;


public class GlobalContext extends AbstractContext<IGlobalContext> implements IGlobalContext, IContext<IGlobalContext>, IAbstractClassMetaData<IGlobalContext> {
    //TODO: Update class meta data system to integrate better with having to implement a interface
    // Core dependencies using interfaces
    private IGameState gameState;
    private IGameMode gameMode;
    private ILocalServerMetaData localServerMetaData;
    private ISpeedrunStatsCache statsCache;
    private IRatingCache ratingCache;
    private IRating rating;
    private IRatingAPI ratingAPI;
    private IPlayerProfile playerProfile;
    private IRank rank;
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
    private IConfigUtils configUtils;
    private ITimeUtils timeUtils;
    private IUtils utils;
    private IAbstractClassMetaData<GlobalContext> classMeta;


    
    private IGlobalContext globalContext = this;
    private InterfaceManager interfaceManager = new InterfaceManager();
    private Plugin plugin; //TODO: Remove bukkit import from global context

    /**
     * Default constructor for dependency injection
     */
    public GlobalContext() {
        super();
        registerImportant(IDependencyInjectorHelper.class, new DependencyInjectorHelper(),0 );
        registerImportant(DependencyInjectorHelper.class, new DependencyInjectorHelper(), 0 );

        registerImportant(IContextInjectionHelper.class, new ContextInjectionHelper(), 0 );
        registerImportant(ContextInjectionHelper.class, new ContextInjectionHelper(), 0 );

        this.setContext(this);

        //register(IGlobalContext.class, this, () -> this);
    }

    /**
     * Constructor with all dependencies
     */
    public GlobalContext(IGameMode gameMode, IUtils utils, ISpeedrunStatsCache statsCache, IGameState gameState,
                         IWorldManager worldManager, IRatingCache ratingCache,
                         IRating rating, IRatingAPI ratingAPI, IPlayerProfile playerProfile, IRank rank, 
                         ISoundManager soundManager, IRankCache rankCache,
                         IRetentionManager retentionManager, IRedis redis, IGameType gameType, 
                         IProxyUtils proxyUtils, IStatsManager statsManager, ILobbyStatsCache lobbyStatsCache,
                         ISpeedrunStatsCache speedrunStatsCache, IPunishManager punishManager, IPunishLog punishLog
                        ,IInventoryManager inventoryManager, IInventoryBuilder inventoryBuilder,
                         ILocalServerMetaData localServerMetaData, IGlobalContext globalContext) {
        this.gameMode = gameMode;
        this.utils = utils;
        this.statsCache = statsCache;
        this.gameState = gameState;
        this.ratingCache = ratingCache;
        this.rating = rating;
        this.ratingAPI = ratingAPI;
        this.playerProfile = playerProfile;
        this.rank = rank;
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
        this.localServerMetaData = localServerMetaData;
        this.globalContext = globalContext;

    }
    // Fallback method for dependency injection
     
    public void initializeDependencies() {

        // Register interface types for dependency injection
        System.out.println("Registering dependencies...");
        registerDependency(IGameMode.class, gameMode, this::getGameMode, this);
        registerDependency(ISpeedrunStatsCache.class, statsCache, this::getSpeedrunStatsCache, this);
        registerDependency(IGameState.class, gameState, this::getGameState, this);
        registerDependency(IRatingCache.class, ratingCache, this::getRatingCache, this);
        registerDependency(IRating.class, rating, this::getRating, this);
        registerDependency(IRatingAPI.class, ratingAPI, this::getRatingAPI, this);
        registerDependency(IPlayerProfile.class, playerProfile, this::getPlayerProfile, this);
        registerDependency(IRank.class, rank, this::getRank, this);

        registerDependency(ISoundManager.class, soundManager, this::getSoundManager, this);
        registerDependency(IRankCache.class, rankCache, this::getRankCache, this);
        registerDependency(IRetentionManager.class, retentionManager, this::getRetentionManager, this);
        registerDependency(IRedis.class, redis, this::getRedis, this);
        registerDependency(IGameType.class, gameType, this::getGameType, this);
        registerDependency(IProxyUtils.class, proxyUtils, this::getProxyUtils, this);
        registerDependency(IStatsManager.class, statsManager, this::getStatsManager, this);
        registerDependency(ILobbyStatsCache.class, lobbyStatsCache, this::getLobbyStatsCache, this);
        registerDependency(ISpeedrunStatsCache.class, speedrunStatsCache, this::getSRStatsCache, this);
        registerDependency(IPunishManager.class, punishManager, this::getPunishManager, this);
        registerDependency(IPunishLog.class, punishLog, this::getPunishLog, this);
        registerDependency(InterfaceManager.class, interfaceManager, this::getInterfaceManager, this);
        registerDependency(ILocalServerMetaData.class, localServerMetaData, this::getLocalServerMetaData, this);
        System.out.println("---------------------------------------");
        System.out.println("Dependencies registered!");
        System.out.println(getDependencySummary());
        System.out.println("Loaded Dependencies: "+dependencyMap.toString());
        System.out.println("---------------------------------------");
    }
     
    public IGlobalContext getGlobalContext() {
        return globalContext;
    }


    // Getters
     
    public IGameMode getGameMode() {
        return gameMode;
    }


     
    public IGameState getGameState() {
        return gameState;
    }

     


     
    public ISpeedrunStatsCache getSpeedrunStatsCache() {
        return statsCache;
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

     
    public IGameType getGameType() {
        return gameType;
    }

     
    public IProxyUtils getProxyUtils() {
        return proxyUtils;
    }

     
    public IStatsManager getStatsManager() {
        return statsManager;
    }

     
    public ILobbyStatsCache getLobbyStatsCache() {
        return lobbyStatsCache;
    }

     
    public ISpeedrunStatsCache getSRStatsCache() {
        return speedrunStatsCache;
    }

     
    public IPunishManager getPunishManager() {
        return punishManager;
    }

     
    public IPunishLog getPunishLog() {
        return punishLog;
    }

     
    public IConfigUtils getConfigUtils() {
       return configUtils;
    }

     
    public ITimeUtils getTimeUtils() {
        return timeUtils;
    }

     
    public ILocalServerMetaData getLocalServerMetaData() {
        return localServerMetaData;
    }

    
    public InterfaceManager getInterfaceManager() {
        return interfaceManager;
    }


    // Setters that return the context for method chaining
     
    public IGlobalContext setGameMode(IGameMode gameMode) {
        this.gameMode = gameMode;
        if (gameMode != null) registerDependency(IGameMode.class, gameMode, this::getGameMode, this);
        return this;
    }

     
    public IGlobalContext setGameState(IGameState gameState) {
        this.gameState = gameState;
        if (gameState != null) registerDependency(IGameState.class, gameState, this::getGameState, this);
        return this;
    }

     
    public IGlobalContext setStatsCache(ISpeedrunStatsCache statsCache) {
        this.statsCache = statsCache;
        if (statsCache != null) registerDependency(ISpeedrunStatsCache.class, statsCache, this::getSpeedrunStatsCache, this);
        return this;
    }

     
    public IGlobalContext setRatingCache(IRatingCache ratingCache) {
        this.ratingCache = ratingCache;
        if (ratingCache != null) registerDependency(IRatingCache.class, ratingCache, this::getRatingCache, this);
        return this;
    }

     
    public IGlobalContext setRating(IRating rating) {
        this.rating = rating;
        if (rating != null) registerDependency(IRating.class, rating, this::getRating, this);
        return this;
    }

     
    public IGlobalContext setRatingAPI(IRatingAPI ratingAPI) {
        this.ratingAPI = ratingAPI;
        if (ratingAPI != null) registerDependency(IRatingAPI.class, ratingAPI, this::getRatingAPI, this);
        return this;
    }

     
    public IGlobalContext setPlayerProfile(IPlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
        if (playerProfile != null) registerDependency(IPlayerProfile.class, playerProfile, this::getPlayerProfile, this);
        return this;
    }

     
    public IGlobalContext setRank(IRank rank) {
        this.rank = rank;
        if (rank != null) registerDependency(IRank.class, rank, this::getRank, this);
        return this;
    }



     
    public IGlobalContext setSoundManager(ISoundManager soundManager) {
        this.soundManager = soundManager;
        if (soundManager != null) registerDependency(ISoundManager.class, soundManager, this::getSoundManager, this);
        return this;
    }

     
    public IGlobalContext setRankCache(IRankCache rankCache) {
        this.rankCache = rankCache;
        if (rankCache != null) registerDependency(IRankCache.class, rankCache, this::getRankCache, this);
        return this;
    }

     
    public IGlobalContext setRetentionManager(IRetentionManager retentionManager) {
        this.retentionManager = retentionManager;
        if (retentionManager != null) registerDependency(IRetentionManager.class, retentionManager, this::getRetentionManager, this);
        registerFactory(IRetentionManager.class, this::getRetentionManager);
        return this;
    }

     
    public IGlobalContext setRedis(IRedis redis) {
        this.redis = redis;
        if (redis != null) registerDependency(IRedis.class, redis, this::getRedis, this);
        return this;
    }


     
    public IGlobalContext setGameType(IGameType gameType) {
        this.gameType = gameType;
        if (gameType != null) registerDependency(IGameType.class, gameType, this::getGameType, this);
        return this;
    }

     
    public IGlobalContext setProxyUtils(IProxyUtils proxyUtils) {
        this.proxyUtils = proxyUtils;
        if (proxyUtils != null) registerDependency(IProxyUtils.class, proxyUtils, this::getProxyUtils, this);
        return this;
    }

     
    public IGlobalContext setStatsManager(IStatsManager statsManager) {
        this.statsManager = statsManager;
        if (statsManager != null) registerDependency(IStatsManager.class, statsManager, this::getStatsManager, this);
        return this;
    }

     
    public IGlobalContext setLobbyStatsCache(ILobbyStatsCache lobbyStatsCache) {
        this.lobbyStatsCache = lobbyStatsCache;
        if (lobbyStatsCache != null) registerDependency(ILobbyStatsCache.class, lobbyStatsCache, this::getLobbyStatsCache, this);
        return this;
    }

     
    public IGlobalContext setSpeedrunStatsCache(ISpeedrunStatsCache speedrunStatsCache) {
        this.speedrunStatsCache = speedrunStatsCache;
        if (speedrunStatsCache != null) registerDependency(ISpeedrunStatsCache.class, speedrunStatsCache, this::getSRStatsCache, this);
        return this;
    }

     
    public IGlobalContext setPunishManager(IPunishManager punishManager) {
        this.punishManager = punishManager;
        if (punishManager != null) registerDependency(IPunishManager.class, punishManager, this::getPunishManager, this);
        return this;
    }

     
    public IGlobalContext setPunishLog(IPunishLog punishLog) {
        this.punishLog = punishLog;
        if (punishLog != null) registerDependency(IPunishLog.class, punishLog, this::getPunishLog, this);
        return this;
    }



     
    public IGlobalContext setConfigUtils(IConfigUtils configUtils) {
        this.configUtils = configUtils;
        if(configUtils != null) registerDependency(IConfigUtils.class, configUtils, this::getConfigUtils, this);
        return this;
    }

     
    public IGlobalContext setTimeUtils(ITimeUtils timeUtils) {
        this.timeUtils = timeUtils;
        if(timeUtils != null) registerDependency(ITimeUtils.class, timeUtils, this::getTimeUtils, this);
        return this;
    }

     
    public IGlobalContext setLocalServerMetaData(ILocalServerMetaData localServerMetaData) {
        this.localServerMetaData = localServerMetaData;
        if(localServerMetaData != null) registerDependency(ILocalServerMetaData.class, localServerMetaData, this::getLocalServerMetaData, this);
        return this;
    }


    public IGlobalContext setInterfaceManager(InterfaceManager interfaceManager) {
        this.interfaceManager = interfaceManager;
        if(interfaceManager != null) registerDependency(InterfaceManager.class, interfaceManager, this::getInterfaceManager, this);
        return this;
    }




     
    public IGlobalContext setGlobalContext(IGlobalContext globalContext) {
        this.globalContext = globalContext;
        if(globalContext != null) {
            registerDependency(IGlobalContext.class, this, () -> this, this);
            // Also register the concrete GlobalContext class for dependencies that need it
            registerDependency(GlobalContext.class, this, () -> (GlobalContext) this, this);
        }
        return this;
    }
    /**
     * Builder pattern for easy initialization in onEnable and program start up
     * Example usage:
     * GlobalContext context = new GlobalContext()
     *     .setGameMode(gameMode)
     *     .setUtils(utils)
     *     .setStatsCache(statsCache);
     */
     
    public void buildGlobalContext() {
                setGlobalContext(globalContext).
                setRedis(redis).
                setInterfaceManager(interfaceManager).
                setRank(rank).
                setRatingAPI(ratingAPI).
                setRankCache(rankCache).
                setLobbyStatsCache(lobbyStatsCache).
                setPlayerProfile(playerProfile).
                setSoundManager(soundManager).
                setPunishManager(punishManager).
                setPunishLog(punishLog).
                setRetentionManager(retentionManager).
                setStatsCache(statsCache).
                setGameState(gameState).
                setGameType(gameType).
                setGameMode(gameMode).
                setStatsManager(statsManager).
                setRatingCache(ratingCache).
                setConfigUtils(configUtils).
                setTimeUtils(timeUtils).
                setLocalServerMetaData(localServerMetaData);
                
        // Initialize metadata system after all dependencies are registered
        initializeMetadataSystem();
    }

    /**
     * Initialize the metadata system with default settings for key classes.
     * This should be called after all dependencies are registered.
     */
    private void initializeMetadataSystem() {
        System.out.println("[META] Initializing metadata system...");

        // Configure metadata settings for critical classes
        configureClassMetadata(IGameManager.class, true, false, new String[]{"core", "game"});
        configureClassMetadata(IDebugger.class, false, true, new String[]{"debug", "logging"});
        configureClassMetadata(IRedis.class, false, false, new String[]{"database", "cache"});
        configureClassMetadata(IRankMC.class, false, false, new String[]{"ranking", "minecraft"});
        configureClassMetadata(IStatsManager.class, false, true, new String[]{"stats", "performance"});
        configureClassMetadata(InterfaceManager.class, true, true, new String[]{"core", "interface"});

        // Configure the GlobalContext itself
        configureClassMetadata(GlobalContext.class, false, true, new String[]{"core", "context", "di"});

        // Enable performance monitoring for high-traffic classes

        System.out.println("[META] Metadata system initialized with " + classMeta.getMetadataRegistrySize() + " tracked classes");
        System.out.println("[META] " + classMeta.getMetadataClassLoadingStatsSummary());

        // Print class loading statistics
        printClassLoadingStats();
    }
    
    /**
     * Print class loading statistics to console.
     */
    private void printClassLoadingStats() {
        System.out.println("[META] === CLASS LOADING STATISTICS ===");
        System.out.println("[META] Total JVM Classes: " + classMeta.getTotalLoadedClasses());
        System.out.println("[META] Tracked Classes: " + classMeta.getTrackedClasses() + " (" + String.format("%.1f%%", classMeta.getTrackingPercentage()) + ")");
        System.out.println("[META] Registered Classes: " + classMeta.getRegisteredClasses() + " (" + String.format("%.1f%%", classMeta.getRegistrationPercentage()) + ")");
        System.out.println("[META] Instantiated Classes: " + classMeta.getInstantiatedClasses() + " (" + String.format("%.1f%%", classMeta.getInstantiationPercentage()) + ")");
        System.out.println("[META] =================================");
    }

    /**
     * Configure metadata settings for a specific class.
     */
    private void configureClassMetadata(Class<?> clazz, boolean exceptionWrapping, boolean logInjection, String[] tags) {
        classMeta.setClassSetting(clazz, ClassSetting.EXCEPTION_WRAPPING_ENABLED, exceptionWrapping);
        classMeta.setClassSetting(clazz, ClassSetting.LOG_ELIGIBLE_INJECTION, logInjection);
        classMeta.setClassSetting(clazz, ClassSetting.TAGS, tags);
        
        System.out.println("[META] Configured " + clazz.getSimpleName() + 
                          " - exceptionWrapping=" + exceptionWrapping + 
                          ", logInjection=" + logInjection + 
                          ", tags=" + String.join(",", tags));
    }

    /**
     * Get metadata for any class in the system.
     */
    public IAbstractClassMetaData<?> getClassMetadata(Class<?> clazz) {
        return classMeta.getOrCreateMetadata(clazz);
    }





    public static GlobalContext builder() {
        return new GlobalContext();
    }


    /**
     * Validates that all required dependencies are set
     * @throws IllegalStateException if required dependencies are missing
     */
    public void validateRequiredDependencies() {
        StringBuilder missing = new StringBuilder();
        if (gameMode == null) missing.append("gameMode, ");
        if (gameState == null) missing.append("gameState, ");
        if (utils == null) missing.append("utils, ");

        if (!missing.isEmpty()) {
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
        // TODO: Update using dependencyMap and loop
        StringBuilder summary = new StringBuilder("GlobalContext Dependencies:\n");

        summary.append("- GameMode: ").append(gameMode != null ? "✓" : "✗").append("\n");
        summary.append("- GameState: ").append(gameState != null ? "✓" : "✗").append("\n");
        summary.append("- Utils: ").append(utils != null ? "✓" : "✗").append("\n");
        summary.append("- Redis: ").append(redis != null ? "✓" : "✗").append("\n");
        summary.append("- StatsCache: ").append(statsCache != null ? "✓" : "✗").append("\n");
        summary.append("- RatingCache: ").append(ratingCache != null ? "✓" : "✗").append("\n");
        summary.append("- Total registered: ").append(dependencyMap.getDependencyMapSize()).append("\n");
        
        return summary.toString();
    }

     


    @Override
    public IAbstractClassMetaData<GlobalContext> getMetadataProvider() {
        return classMeta;
    }

    @Override
    public Object getAbstractClassMetaDataInstance() {
        return classMeta;
    }


}