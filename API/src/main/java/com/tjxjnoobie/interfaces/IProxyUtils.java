package com.tjxjnoobie.interfaces;

import java.util.UUID;

/**
 * Interface for proxy utility operations
 */
public interface IProxyUtils {
    
    /**
     * Sends a player to another server
     * @param playerId The player's UUID
     * @param serverName The target server name
     * @return true if transfer was initiated successfully
     */
    boolean sendPlayerToServer(UUID playerId, String serverName);
    
    /**
     * Gets the current server name
     * @return The server name
     */
    String getCurrentServerName();
    
    /**
     * Gets all available servers
     * @return Array of server names
     */
    String[] getAvailableServers();
    
    /**
     * Checks if a server is online
     * @param serverName The server name
     * @return true if server is online
     */
    boolean isServerOnline(String serverName);
    
    /**
     * Gets player count for a server
     * @param serverName The server name
     * @return Number of players on the server
     */
    int getServerPlayerCount(String serverName);
    
    /**
     * Gets total network player count
     * @return Total players across all servers
     */
    int getNetworkPlayerCount();
    
    /**
     * Sends a message to another server
     * @param serverName The target server name
     * @param channel The message channel
     * @param message The message to send
     */
    void sendServerMessage(String serverName, String channel, String message);
    
    /**
     * Broadcasts a message to all servers
     * @param channel The message channel
     * @param message The message to broadcast
     */
    void broadcastServerMessage(String channel, String message);
    
    /**
     * Gets server information
     * @param serverName The server name
     * @return Server information object
     */
    Object getServerInfo(String serverName);
    
    /**
     * Registers a message listener
     * @param channel The channel to listen on
     * @param listener The message listener
     */
    void registerMessageListener(String channel, Object listener);
    
    /**
     * Unregisters a message listener
     * @param channel The channel to stop listening on
     */
    void unregisterMessageListener(String channel);
    
    /**
     * Kicks a player from the network
     * @param playerId The player's UUID
     * @param reason The kick reason
     */
    void kickPlayer(UUID playerId, String reason);
    
    /**
     * Bans a player from the network
     * @param playerId The player's UUID
     * @param reason The ban reason
     * @param duration Ban duration in milliseconds (0 for permanent)
     */
    void banPlayer(UUID playerId, String reason, long duration);
    
    /**
     * Unbans a player
     * @param playerId The player's UUID
     */
    void unbanPlayer(UUID playerId);
    
    /**
     * Checks if a player is banned
     * @param playerId The player's UUID
     * @return true if player is banned
     */
    boolean isPlayerBanned(UUID playerId);
    
    /**
     * Gets player's current server
     * @param playerId The player's UUID
     * @return The server name or null if not found
     */
    String getPlayerServer(UUID playerId);
    
    /**
     * Checks if a player is online on the network
     * @param playerId The player's UUID
     * @return true if player is online
     */
    boolean isPlayerOnline(UUID playerId);
    
    /**
     * Gets network-wide player list
     * @return Array of online player UUIDs
     */
    UUID[] getNetworkPlayers();
    
    /**
     * Sends a private message between players across servers
     * @param senderId The sender's UUID
     * @param receiverId The receiver's UUID
     * @param message The message to send
     * @return true if message was sent successfully
     */
    boolean sendPrivateMessage(UUID senderId, UUID receiverId, String message);
    
    /**
     * Gets proxy version information
     * @return Version information string
     */
    String getProxyVersion();
}