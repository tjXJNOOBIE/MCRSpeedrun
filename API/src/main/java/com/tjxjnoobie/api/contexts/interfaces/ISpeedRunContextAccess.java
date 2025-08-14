/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.contexts.interfaces;

import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.plugin.Plugin;

public interface ISpeedRunContextAccess {

    ISpeedRunContext getSpeedRunContext();
    default Plugin plugin() {
        return getSpeedRunContext().getPlugin();
    }

    default IGameMode gameMode() {
        return getSpeedRunContext().getGameMode();
    }

    default IGameState gameState() {
        return getSpeedRunContext().getGameState();
    }

    default IUtils utils() {
        return getSpeedRunContext().getUtils();
    }

    default ISpeedrunStatsCache statsCache() {
        return getSpeedRunContext().getStatsCache();
    }

    default IGameManager gameManager() {
        return getSpeedRunContext().getGameManager();
    }

    default IPlayerManager playerManager() {
        return getSpeedRunContext().getPlayerManager();
    }

    default IMCUtils mcUtils() {
        return getSpeedRunContext().getMcUtils();
    }

    default IWorldManager worldManager() {
        return getSpeedRunContext().getWorldManager();
    }

    default ILocationCache locationCache() {
        return getSpeedRunContext().getLocationCache();
    }

    default ISpeedRunJoinEvent joinEvent() {
        return getSpeedRunContext().getJoinEvent();
    }

    default IQuitEvent quitEvent() {
        return getSpeedRunContext().getQuitEvent();
    }

    default IRankMC rankMC() {
        return getSpeedRunContext().getRankMC();
    }

    default IRatingCache ratingCache() {
        return getSpeedRunContext().getRatingCache();
    }

    default IRating rating() {
        return getSpeedRunContext().getRating();
    }

    default IRatingAPI ratingAPI() {
        return getSpeedRunContext().getRatingAPI();
    }

    default IPlayerProfile playerProfile() {
        return getSpeedRunContext().getPlayerProfile();
    }

    default IRank rank() {
        return getSpeedRunContext().getRank();
    }

    default IDebugger debugger() {
        return getSpeedRunContext().getDebugger();
    }

    default ISoundManager soundManager() {
        return getSpeedRunContext().getSoundManager();
    }

    default IRankCache rankCache() {
        return getSpeedRunContext().getRankCache();
    }

    default IRetentionManager retentionManager() {
        return getSpeedRunContext().getRetentionManager();
    }

    default IRedis redis() {
        return getSpeedRunContext().getRedis();
    }

    default IDebug debug() {
        return getSpeedRunContext().getDebug();
    }

    default IBossBarManager bossBarManager() {
        return getSpeedRunContext().getBossBarManager();
    }

    default IVoting voting() {
        return getSpeedRunContext().getVoting();
    }

    default IInventoryManager inventoryManager() {
        return getSpeedRunContext().getInventoryManager();
    }

    default IGameType gameType() {
        return getSpeedRunContext().getGameType();
    }

    default IStatsManager statsManager() {
        return getSpeedRunContext().getStatsManager();
    }

    default ISpeedrunStatsCache srStatsCache() {
        return getSpeedRunContext().getSRStatsCache();
    }

    default IFairFight fairFight() {
        return getSpeedRunContext().getFairFight();
    }
}
