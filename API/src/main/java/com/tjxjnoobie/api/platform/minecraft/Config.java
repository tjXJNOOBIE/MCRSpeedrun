package com.tjxjnoobie.api.platform.minecraft;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static String username;
    public static String password;
    public static String database;
    public static String host;
    public static String port;
    public static String redis_host;
    public static String redis_port;
    public static String redis_password;
    public static Connection connection;

    public static void createConfig() {
        // Define the path for the configuration file
        File configFile = new File("plugins/X/database.yml");

        // Check if the file exists
        if (!configFile.exists()) {
            try {
                // Create the config file
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();

                // Define default values for the MySQL configuration
                Map<String, Object> mysqlConfig = new HashMap<>();
                mysqlConfig.put("host", "localhost");
                mysqlConfig.put("port", "3306");
                mysqlConfig.put("database", "all_data");
                mysqlConfig.put("username", "root");
                mysqlConfig.put("password", "your_password");
                mysqlConfig.put("redis_host", "localhost");
                mysqlConfig.put("redis_port","6379");
                mysqlConfig.put("redis_password", "your_password");

                // Wrap the MySQL configuration inside a parent map
                Map<String, Object> config = new HashMap<>();
                config.put("mysql", mysqlConfig);

                // Create a YAML object to write the data to the file
                Yaml yaml = new Yaml();
                FileWriter writer = new FileWriter(configFile);
                yaml.dump(config, writer);

                // Notify the console that the configuration file was created
                System.out.println("Created default database.yml with default values.");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to create database.yml!");
            }
        } else {
            System.out.println("database.yml already exists.");
        }
    }
    public static void loadConfig() {
        File configFile = new File("plugins/X/database.yml");
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                Yaml yaml = new Yaml();
                Map<String, Object> config = yaml.load(fis);

                // Load the MySQL configuration values
                Map<String, Object> mysqlConfig = (Map<String, Object>) config.get("mysql");
                host = (String) mysqlConfig.get("host");
                port = (String) mysqlConfig.get("port");
                database = (String) mysqlConfig.get("database");
                username = (String) mysqlConfig.get("username");
                password = (String) mysqlConfig.get("password");
                redis_host = (String) mysqlConfig.get("redis_host");
                redis_port = (String) mysqlConfig.get("redis_port");
                redis_password = (String) mysqlConfig.get("redis_password");

                System.out.println("Loaded MySQL configuration from database.yml");

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Could not load MySQL configuration from database.yml");
            }
        } else {
            System.err.println("No database.yml found! Please create a database.yml file.");
        }
    }
}
