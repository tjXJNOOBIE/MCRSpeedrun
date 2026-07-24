package com.tjxjnoobie.api.managers;

import org.tavall.logging.Log;
import com.tjxjnoobie.api.platform.minecraft.Config;

import java.sql.*;
import java.util.Arrays;

public class MySQL {

    public static String username = Config.username;
    public static String password = Config.password;
    public static String database = Config.database;
    public static String host = Config.host;
    public static String port = Config.port;
    public static Connection connection = Config.connection;


    public static void connect() {
        if (!isConnected()) {
            System.out.println("Trying to connect to " + database);
            try
            {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username,
                        password);
                System.out.println("Connected to " + database + " database");
            }
            catch (SQLException e) {
                System.out.println("Could not connect to MySQL Database");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public static void close() {

        if (isConnected()) {
            try
            {
                connection.close();
                System.out.println("Safely disconnected from" + database + "database");
            }
            catch (SQLException e) {
                System.out.println("Error disconnecting from " + database);

            }
        }
    }





    public static boolean isConnected() {
        return connection != null;
    }

    public static void executePreparedStatement(String sql, Object... params) throws SQLException {
        System.out.println("Attempting to execute query: " + sql);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
                Log.info("Setting parameter " + i + " to " + params[i]);
            }
            stmt.executeUpdate();
            System.out.println("Query executed: " +sql+ " with " + Arrays.toString(params));
        }
    }


    public static ResultSet getResult(String qry, Object... params) {
        ResultSet rs = null;
        try
        {
            PreparedStatement st = connection.prepareStatement(qry);
            for(int i = 0; i < params.length; i++){
            st.setObject(i+1, params[i]);
            }
            rs = st.executeQuery();
            System.out.println("Executed query: " + qry);

        }
        catch (SQLException e) {
            System.err.println("Could not execute query: + " +qry);
        }

        return rs;

    }




}


