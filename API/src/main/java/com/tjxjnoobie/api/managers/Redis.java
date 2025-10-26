package com.tjxjnoobie.api.managers;

import com.tjxjnoobie.api.abstracts.AbstractManager;
import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IRedis;
import com.tjxjnoobie.api.platform.minecraft.Config;
import com.tjxjnoobie.api.platform.minecraft.HandleBlocks;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class Redis extends AbstractManager<IGlobalContext> implements IRedis {
    public static Jedis jedis;
    public static String host = Config.redis_host;
    public static String port = Config.redis_port;
    public static String password = Config.redis_password;
    public static Thread subscriberThread;
    public static String REDIS_CHANNEL = "global:all_data";
    private static HandleBlocks handleBlocks;



    //TODO: Testing method fire without annotation
    // @PostConstruct
    @Override
    public void connectToRedis() throws ClassNotFoundException {

        jedis = new Jedis(host, Integer.parseInt(port)); // Change this if your Redis server is different
        jedis.auth(password);
        System.out.println("Connecting to Redis");

        subscriberThread = new Thread(() -> {
            try (Jedis subJedis = new Jedis(host, 6379)) {
                subJedis.auth(password);
                subJedis.subscribe(new RedisSubscriber() {
                    }, REDIS_CHANNEL);
            }
        });
        subscriberThread.start();
        System.out.println("Connected to Redis. Redis started on new thread: " +subscriberThread.getName());
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

    @Override
    protected void doInitialize() throws Exception {
        connectToRedis();
    }

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
