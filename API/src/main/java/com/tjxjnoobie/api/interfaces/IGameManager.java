package com.tjxjnoobie.api.interfaces;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Interface for game management operations
 */
public interface IGameManager {

    // Player management
    default void addPlayer(UUID uuid, String name) { }
    default void addInGame(UUID uuid, String name) { }
    default void addWatching(UUID uuid, String name) { }
    default void addInNether(UUID uuid, String name) { }
    default void addInEnder(UUID uuid, String name) { }
    default void addBlazeRod(UUID uuid, String name) { }
    default void addEnderEye(UUID uuid, String name) { }
    default void addEnderPearl(UUID uuid, String name) { }
    default void addFinishedPlayer(UUID uuid) { }
    default void addFinished() { }
    default void setWinner(Player player) { }
    default void setFinalTime(UUID uuid) { }

    // Game state queries
    default int getCurrentPlayers() { return 0; }
    default int getMinPlayers() { return 0; }
    default int getMaxPlayers() { return 0; }
    default int getPlayersRemaining() { return 0; }
    default int getSpectatorsInt() { return 0; }
    default int getPlayersInNetherInt() { return 0; }
    default int getPlayersInEndInt() { return 0; }
    default int getAllPlayersInt() { return 0; }
    default int getPregameTime() { return 0; }
    default int getLobbyCountDown() { return 0; }
    default int getPlayerNeeded() { return 0; }
    default int getFinished() { return 0; }

    // Player state checks
    default boolean isSpectator(UUID uuid) { return false; }
    default boolean hasBlazeRod(UUID uuid) { return false; }
    default boolean hasEnderEye(UUID uuid) { return false; }
    default boolean hasEnderPearl(UUID uuid) { return false; }
    default boolean hasKilledBlaze(UUID uuid) { return false; }
    default boolean canSolo() { return false; }
    default boolean canMove() { return false; }

    // Game data access
    default HashMap<UUID, String> getPlaying() { return new HashMap<>(); }
    default HashMap<UUID, String> getWatching() { return new HashMap<>(); }
    default HashMap<UUID, String> getAllPlayersHash() { return new HashMap<>(); }
    default HashMap<UUID, String> getBlazeRod() { return new HashMap<>(); }
    default HashMap<UUID, String> getEnderPearl() { return new HashMap<>(); }
    default HashMap<UUID, String> getEyeOfEnder() { return new HashMap<>(); }
    default HashMap<UUID, String> getKilledBlaze() { return new HashMap<>(); }
    default HashMap<UUID, String> getEnderDragon() { return new HashMap<>(); }
    default HashMap<UUID, String> getInNetherHash() { return new HashMap<>(); }
    default HashMap<UUID, String> getFinishedPlayers() { return new HashMap<>(); }
    default HashMap<UUID, String> getInEnd() { return new HashMap<>(); }
    default HashMap<UUID, String> getQuitPlayers() { return new HashMap<>(); }
    default HashMap<UUID, Location> QuitLocation() { return new HashMap<>(); }

    // Time management
    default long getSeed() { return 0L; }
    default long getTimeElapsedLong() { return 0L; }
    default long getFinalTime(UUID uuid) { return 0L; }
    default String getFinalTimeString(UUID uuid) { return ""; }
    default String getCurrentTime() { return ""; }
    default long getCurrentTimeLong() { return 0L; }
    default String getTimeElapsed() { return ""; }

    // Location management
    default Location getSpawnFromSeed(long seed) { return null; }
    default Location getQuitLocation(UUID uuid) { return null; }

    // Player retrieval
    default UUID getPlayerUUID(String name) { return null; }
    default Player getSpeedRunWinner() { return null; }
    default Player getInGamePlayers() { return null; }
    default Player getPlayersWatching() { return null; }
    default Player getAllPlayers() { return null; }

    // Game flow control
    default void runCheckers() { }
    default void startLobby() { }
    default void startPreGame() throws SQLException { }
    default void startLobbyCountdown() { }
    default void startGame() throws SQLException { }
    default void runGame() throws SQLException { }
    default void stopGame() throws SQLException { }
    default void runHotBarTimer() { }

    // World management
    default void createWorlds(World.Environment environment) { }
    default void createWorldsWithSeed(World.Environment environment, Long seed) { }
    default void teleportPlayersToWorlds() { }
}