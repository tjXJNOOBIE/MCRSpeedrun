package com.tjxjnoobie.proxy.Events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;

import java.sql.SQLException;

public class VelocityPreLoginEvent {



    @Subscribe
    public void onPreLogin(PreLoginEvent e) throws SQLException {
        String name = e.getUsername();




    }



}