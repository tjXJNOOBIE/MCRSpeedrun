package com.tjxjnoobie.api.platform.velocity.cache;

import com.tjxjnoobie.api.platform.minecraft.Config;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;


public class PunishCache {

    public HashMap<UUID, Boolean> banned = new HashMap<>();
    public HashMap<UUID, Boolean> muted = new HashMap<>();
    public HashMap<UUID, Boolean> ipBanned = new HashMap<>();
    private static Jedis jedis;

    public static void initializeCache() {
        jedis = new Jedis(Config.redis_host, Integer.parseInt(Config.redis_port));
        Set<String> bannedPlayerKeys = jedis.keys("bans:*"); // Assuming ban data is stored with keys like 'ban:<UUID>'
        for (String key : bannedPlayerKeys) {
            // Retrieve ban information from Redis and populate the cache
            // Deserialize the data as per your storage format
        }
    }

    public boolean isMuted (UUID uuid){
        return muted.containsKey(uuid);

    }


    public boolean isBanned(UUID uuid) {
        return banned.containsKey(uuid);
    }

    public boolean isIPBanned(UUID uuid) {
        return ipBanned.containsKey(uuid);
    }

    public void setBanned(UUID uuid, boolean isBanned) {
        banned.put(uuid, isBanned);
    }
    public void setIPBanned(UUID uuid, boolean isIPBanned) {
        ipBanned.put(uuid, isIPBanned);
    }


    public void setMuted(UUID uuid, boolean isMuted) {
        muted.put(uuid, isMuted);
    }

    public HashMap<UUID, Boolean> getBanned() {
        return banned;
    }

    public HashMap<UUID, Boolean> getMuted() {
        return muted;
    }

    public HashMap<UUID, Boolean> getIpBanned() {
        return ipBanned;
    }
}
