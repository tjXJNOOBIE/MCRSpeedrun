package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.dependency.contexts.GlobalContext;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;
import com.tjxjnoobie.api.platform.global.annotations.Injectable;
import com.tjxjnoobie.api.platform.global.metadata.interfaces.IAbstractClassMetaData;
import com.tjxjnoobie.api.platform.global.utils.interfaces.IConfigUtils;
import com.tjxjnoobie.api.platform.global.utils.interfaces.ITimeUtils;

/**
 * Interface for GlobalContext to provide dependency injection capabilities and metadata operations.
 */
@Injectable("Global Context Interface")
public interface IGlobalContext extends IAbstractClassMetaData<IGlobalContext> {


    void initializeDependencies();

    IGlobalContext getGlobalContext();

    // Core getters using interfaces
    IGameMode getGameMode();
    IGameState getGameState();
    ISpeedrunStatsCache getSpeedrunStatsCache();
    IRatingCache getRatingCache();
    IRating getRating();
    IRatingAPI getRatingAPI();
    IPlayerProfile getPlayerProfile();
    IRank getRank();
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

    IConfigUtils getConfigUtils();
    ITimeUtils getTimeUtils();
    ILocalServerMetaData getLocalServerMetaData();

    
    // Dependency map access
    IDependencyMap getDependencyMap();

    
    // Setters that return the context for method chaining
    IGlobalContext setGameMode(IGameMode gameMode);
    IGlobalContext setGameState(IGameState gameState);

    IGlobalContext setStatsCache(ISpeedrunStatsCache statsCache);

    IGlobalContext setRatingCache(IRatingCache ratingCache);
    IGlobalContext setRating(IRating rating);
    IGlobalContext setRatingAPI(IRatingAPI ratingAPI);
    IGlobalContext setPlayerProfile(IPlayerProfile playerProfile);
    IGlobalContext setRank(IRank rank);
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
    IGlobalContext setConfigUtils(IConfigUtils configUtils);
    IGlobalContext setTimeUtils(ITimeUtils timeUtils);
    IGlobalContext setLocalServerMetaData(ILocalServerMetaData localServerMetaData);

    IGlobalContext setInterfaceManager(InterfaceManager interfaceManager);

    IGlobalContext setGlobalContext(IGlobalContext globalContext);
    InterfaceManager getInterfaceManager();

    void buildGlobalContext();

    /**
     * Get the metadata provider for delegation.
     * Implementations should return their composed IAbstractClassMetaData instance.
     * @return The metadata provider instance
     */
    IAbstractClassMetaData<GlobalContext> getMetadataProvider();
    
    /**
     * Get the AbstractClassMetaData instance for direct access.
     * @return The AbstractClassMetaData instance
     */
    default Object getAbstractClassMetaDataInstance() {
        return getMetadataProvider();
    }

}