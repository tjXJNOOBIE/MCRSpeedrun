package com.tjxjnoobie.interfaces;

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
    void addPlayer(UUID uuid, String name);
    void addInGame(UUID uuid, String name);
    void addWatching(UUID uuid, String name);
    void addInNether(UUID uuid, String name);
    void addInEnder(UUID uuid, String name);
    void addBlazeRod(UUID uuid, String name);
    void addEnderEye(UUID uuid, String name);
    void addEnderPearl(UUID uuid, String name);
    void addFinishedPlayer(UUID uuid);
    void addFinished();
    void setWinner(Player player);
    void setFinalTime(UUID uuid);
    
    // Game state queries
    int getCurrentPlayers();
    int getMinPlayers();
    int getMaxPlayers();
    int getPlayersRemaining();
    int getSpectatorsInt();
    int getPlayersInNetherInt();
    int getPlayersInEndInt();
    int getAllPlayersInt();
    int getPregameTime();
    int getLobbyCountDown();
    int getPlayerNeeded();
    int getFinished();
    
    // Player state checks
    boolean isSpectator(UUID uuid);
    boolean hasBlazeRod(UUID uuid);
    boolean hasEnderEye(UUID uuid);
    boolean hasEnderPearl(UUID uuid);
    boolean hasKilledBlaze(UUID uuid);
    boolean canSolo();
    boolean canMove();
    
    // Game data access
    HashMap<UUID, String> getPlaying();
    HashMap<UUID, String> getWatching();
    HashMap<UUID, String> getAllPlayersHash();
    HashMap<UUID, String> getBlazeRod();
    HashMap<UUID, String> getEnderPearl();
    HashMap<UUID, String> getEyeOfEnder();
    HashMap<UUID, String> getKilledBlaze();
    HashMap<UUID, String> getEnderDragon();
    HashMap<UUID, String> getInNetherHash();
    HashMap<UUID, String> getFinishedPlayers();
    HashMap<UUID, String> getInEnd();
    HashMap<UUID, String> getQuitPlayers();
    HashMap<UUID, Location> QuitLocation();
    
    // Time management
    long getSeed();
    long getTimeElapsedLong();
    long getFinalTime(UUID uuid);
    String getFinalTimeString(UUID uuid);
    String getCurrentTime();
    long getCurrentTimeLong();
    String getTimeElapsed();
    
    // Location management
    Location getSpawnFromSeed(long seed);
    Location getQuitLocation(UUID uuid);
    
    // Player retrieval
    UUID getPlayerUUID(String name);
    Player getWinner();
    Player getInGamePlayers();
    Player getPlayersWatching();
    Player getAllPlayers();
    
    // Game flow control
    void runCheckers();
    void startLobby();
    void startPreGame() throws SQLException;
    void startLobbyCountdown();
    void startGame() throws SQLException;
    void runGame() throws SQLException;
    void stopGame() throws SQLException;
    void runHotBarTimer();
    
    // World management
    void createWorlds(World.Environment environment);
    void createWorldsWithSeed(World.Environment environment, Long seed);
    void teleportPlayersToWorlds();
}