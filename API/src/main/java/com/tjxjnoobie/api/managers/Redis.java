package com.tjxjnoobie.api.managers;

import com.tjxjnoobie.api.dependency.IDependencyInjectableConcrete;
import com.tjxjnoobie.api.dependency.annotations.DelegatesToInterface;
import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.interfaces.IRedis;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.minecraft.Config;
import com.tjxjnoobie.api.platform.minecraft.HandleBlocks;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@DelegatesToInterface(getLinkedInterface = IRedis.class)
public class Redis implements IRedis, IDependencyInjectableConcrete, IDependencyInstance<Redis> {

    public static Jedis jedis;
    public String host = Config.redis_host;
    public String port = Config.redis_port;
    public String password = Config.redis_password;
    public Thread subscriberThread;
    public String REDIS_CHANNEL = "global:all_data";
    private HandleBlocks handleBlocks;

    public Redis(){

    }

    //TODO: Testing method fire without annotation

    @Override
    public void connectToRedis() {
        Log.info("Connecting to Redis...");

        jedis = new Jedis(host, Integer.parseInt(port)); // Change this if your Redis server is different
        jedis.auth(password);

        subscriberThread = new Thread(() -> {
            try (Jedis subJedis = new Jedis(host, 6379)) {
                subJedis.auth(password);
                subJedis.subscribe(new RedisSubscriber() {
                }, REDIS_CHANNEL);
            }
        });
        subscriberThread.start();
        Log.success("Connected to Redis on Thread " + subscriberThread.getName());
    }

    @Override
    public void disconnectFromRedis() {
        if (jedis != null) {
            jedis.close();
            System.out.println("Disconnected from Redis");
        }
    }

    @Override
    public void publishToRedis(String message) {
        try (Jedis pubJedis = new Jedis("localhost", 6379)) {
            pubJedis.publish(REDIS_CHANNEL, message);
        }
    }

    @Override
    public void publishRedisUpdate(String message) {
        jedis.publish(REDIS_CHANNEL, message);
        System.out.println("Published message: " + message + " to channel: " + REDIS_CHANNEL);
    }

    @Override
    public void handleRedisMessage(String message) {
        String[] parts = message.split(",");
        if (parts.length < 2) return;

        String type = parts[0];

        switch (type) {
            case "BLOCK_PLACE":
                handleBlocks.handleBlockPlace(parts);
                break;

        }
    }


    protected void doInitialize() throws Exception {
        connectToRedis();
    }

    //TODO: Remove inner class and imporve sub functions
    private class RedisSubscriber extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            System.out.println("Received message: " + message + " from channel: " + channel);

            if (REDIS_CHANNEL.equals(channel)) {
                handleRedisMessage(message);
            }
        }
    }
}
