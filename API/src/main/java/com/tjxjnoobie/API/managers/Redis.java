package com.tjxjnoobie.API.managers;

import com.tjxjnoobie.API.minecraft.Config;
import com.tjxjnoobie.API.minecraft.HandleBlocks;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class Redis {
    public static Jedis jedis;
    public static String host = Config.redis_host;
    public static String port = Config.redis_port;
    public static String password = Config.redis_password;
    private static Thread subscriberThread;
    public static String REDIS_CHANNEL = "global:all_data";
    private static HandleBlocks handleBlocks;

    public static void connectToRedis() throws ClassNotFoundException {
        Class.forName("redis.clients.jedis.Jedis");

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
    public static void disconnectFromRedis() {
        if (jedis != null) {
            jedis.close();
            System.out.println("Disconnected from Redis");
        }
    }
    private void publishToRedis(String message) {
        try (Jedis pubJedis = new Jedis("localhost", 6379)) {
            pubJedis.publish(REDIS_CHANNEL, message);
        }
    }
    public static void publishRedisUpdate(String message) {
        jedis.publish(REDIS_CHANNEL, message);
        System.out.println("Published message: " + message + " to channel: " + REDIS_CHANNEL);
    }
    public static void handleRedisMessage(String message) {
        String[] parts = message.split(",");
        if (parts.length < 2) return;

        String type = parts[0];

        switch (type) {
            case "BLOCK_PLACE":
                handleBlocks.handleBlockPlace(parts);
                break;

        }
    }
    private static class RedisSubscriber extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            System.out.println("Received message: " + message + " from channel: " + channel);

            if (REDIS_CHANNEL.equals(channel)) {
                handleRedisMessage(message);
            }
        }
    }
}
