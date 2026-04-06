/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.velocity.startup;

import com.tjxjnoobie.api.dependency.injection.helpers.DependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.composition.domains.IInfrastructureDomain;
import com.tjxjnoobie.api.internal.utils.reflection.ReflectUtil;
import com.tjxjnoobie.api.managers.MySQL;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.utils.CustomRunnable;
import com.tjxjnoobie.api.platform.minecraft.Config;
import com.tjxjnoobie.api.platform.velocity.startup.interfaces.IVelocityEnabler;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

/**
 * Velocity startup entrypoint that boots the DI scanner and consumes dependencies through token lookup.
 */
@Plugin(
        id = "velocitycore",
        name = "VelocityCore",
        version = "1.0"
)
public class VelocityEnabler implements IVelocityEnabler, IInfrastructureDomain {

    IDependencyInjectorHelper<?, ?> injectorHelper = new DependencyInjectorHelper<>();

    @Subscribe
    @Override
    public void onVelocityEnable(ProxyInitializeEvent event) throws Throwable {
        ReflectUtil.loadLibs();
        Config.createConfig();
        Config.loadConfig();
        injectorHelper.setupDISystem();
        new CustomRunnable() {

            @Override
            public void run() {
                Log.warn("Trying to connect to redis via composed domain method...");
                connectToRedis();
            }
        }.runTaskLaterAsync(15000L);
        new CustomRunnable() {

            @Override
            public void run() {
                Log.warn("Trying to connect to redis via inherited domain default...");
                connectToRedis();
            }
        }.runTaskLaterAsync(15000L);
        new CustomRunnable() {

            @Override
            public void run() {
                Log.warn("Trying rank access via inherited composed method...");
                if (getRanks() == null) {
                    Log.error("IRank composition returned null ranks during startup test");
                    return;
                }
                Log.info("Resolved IRank via inherited composition. Cached rank count=" + getRanks().size());
            }
        }.runTaskLaterAsync(15000L);

        MySQL.connect();
    }

    @Override
    public void registerCommand(String command, Command commandClass, ProxyServer proxyServer, String... aliases) {
        CommandManager commandManager = proxyServer.getCommandManager();
        commandManager.register(commandManager.metaBuilder(command).aliases(aliases).build(), commandClass);
        Log.info("Registered command: /" + command
                + (aliases.length > 0 ? " (aliases: " + String.join(", ", aliases) + ")" : ""));
    }
}
