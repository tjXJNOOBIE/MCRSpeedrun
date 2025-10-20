/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.injection.interfaces;

import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.machine.data.interfaces.ILocalServerMetaData;
import com.tjxjnoobie.api.platform.global.metadata.interfaces.IAbstractClassMetaData;
import com.tjxjnoobie.api.platform.global.utils.interfaces.IConfigUtils;
import com.tjxjnoobie.api.platform.global.utils.interfaces.ITimeUtils;
import com.tjxjnoobie.api.dependency.contexts.GlobalContext;
import org.bukkit.plugin.Plugin;

public interface IGlobalContextAccess {

    IGlobalContext getGlobalContext();
   
    
    default Plugin plugin() {
        return getGlobalContext().getPlugin();
    }

    default IGameMode gameMode() {
        return getGlobalContext().getGameMode();
    }

    default IGameState gameState() {
        return getGlobalContext().getGameState();
    }

    default IUtils utils() {
        return getGlobalContext().getUtils();
    }

    default ISpeedrunStatsCache speedrunStatsCache() {
        return getGlobalContext().getSpeedrunStatsCache();
    }

    default IMCUtils mcUtils() {
        return getGlobalContext().getMcUtils();
    }

    default IWorldManager worldManager() {
        return getGlobalContext().getWorldManager();
    }

    default IRankMC rankMC() {
        return getGlobalContext().getRankMC();
    }

    default IRatingCache ratingCache() {
        return getGlobalContext().getRatingCache();
    }

    default IRating rating() {
        return getGlobalContext().getRating();
    }

    default IRatingAPI ratingAPI() {
        return getGlobalContext().getRatingAPI();
    }

    default IPlayerProfile playerProfile() {
        return getGlobalContext().getPlayerProfile();
    }

    default IRank rank() {
        return getGlobalContext().getRank();
    }

    default IDebugger debugger() {
        return getGlobalContext().getDebugger();
    }

    default ISoundManager soundManager() {
        return getGlobalContext().getSoundManager();
    }

    default IRankCache rankCache() {
        return getGlobalContext().getRankCache();
    }

    default IRetentionManager retentionManager() {
        return getGlobalContext().getRetentionManager();
    }

    default IRedis redis() {
        return getGlobalContext().getRedis();
    }

    default IGameType gameType() {
        return getGlobalContext().getGameType();
    }

    default IProxyUtils proxyUtils() {
        return getGlobalContext().getProxyUtils();
    }

    default IStatsManager statsManager() {
        return getGlobalContext().getStatsManager();
    }

    default ILobbyStatsCache lobbyStatsCache() {
        return getGlobalContext().getLobbyStatsCache();
    }

    default ISpeedrunStatsCache srStatsCache() {
        return getGlobalContext().getSRStatsCache();
    }

    default IPunishManager punishManager() {
        return getGlobalContext().getPunishManager();
    }

    default IPunishLog punishLog() {
        return getGlobalContext().getPunishLog();
    }

    default IInventoryBuilder inventoryBuilder() {
        return getGlobalContext().getInventoryBuilder();
    }

    default IInventoryManager inventoryManager() {
        return getGlobalContext().getInventoryManager();
    }

    default IConfigUtils configUtils() {
        return getGlobalContext().getConfigUtils();
    }

    default ITimeUtils timeUtils() {
        return getGlobalContext().getTimeUtils();
    }

    default ILocalServerMetaData localServerMetaData() {
        return getGlobalContext().getLocalServerMetaData();
    }

    default InterfaceManager interfaceManager() {
        return getGlobalContext().getInterfaceManager();
    }

    // Metadata access methods
    default IAbstractClassMetaData<GlobalContext> metadataProvider() {
        return getGlobalContext().getMetadataProvider();
    }

    default Object abstractClassMetaDataInstance() {
        return getGlobalContext().getAbstractClassMetaDataInstance();
    }
}
