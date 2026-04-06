/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.velocity.startup.interfaces;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;

public interface IVelocityMain extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {


    @Subscribe
    default void onProxyInitialization(ProxyInitializeEvent event) throws Throwable {
    }

    default void registerCommand(String command, Command commandClass, ProxyServer proxyServer, String... aliases){

    }
}
