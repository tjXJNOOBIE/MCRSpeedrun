package com.tjxjnoobie.proxy.Events;

import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;

import java.sql.SQLException;

public class VelocityPreLoginEvent {
    private final IGlobalContext globalContext;

    public VelocityPreLoginEvent(IGlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Subscribe
    public void onPreLogin(PreLoginEvent e) throws SQLException {
        String name = e.getUsername();




    }



}
