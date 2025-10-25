package com.tjxjnoobie.proxy.Events;

import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;

import java.sql.SQLException;

public class VelocityPreLoginEvent {
    @Inject private IGlobalContext globalContext;



    @Subscribe
    public void onPreLogin(PreLoginEvent e) throws SQLException {
        String name = e.getUsername();




    }



}
