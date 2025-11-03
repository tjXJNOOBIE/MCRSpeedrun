/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.velocity;

import com.tjxjnoobie.api.interfaces.IRedis;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface IVelocityMain extends IRedis {

    default void onVelocityEnable(){
        Log.info("[VelocityMain] Trying to enable velocity runtime...");
        connectToRedis();
        Log.success("[VelocityMain] Velocity runtime enabled successfully!");
    }
    @Subscribe
    default void onProxyInitialization(ProxyInitializeEvent event) throws SQLException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, InterruptedException {
    }

    default void registerCommand(String command, Command commandClass, String... aliases){

    }
}
