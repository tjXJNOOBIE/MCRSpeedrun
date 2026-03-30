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
import com.tjxjnoobie.api.dependency.metadata.DependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInterface;
import com.tjxjnoobie.api.interfaces.IRank;
import com.tjxjnoobie.api.interfaces.IRedis;
import com.tjxjnoobie.api.internal.utils.reflection.ReflectUtil;
import com.tjxjnoobie.api.managers.MySQL;
import com.tjxjnoobie.api.managers.Redis;
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
 * VelocityEnabler – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 11/8/2025
 */
@Plugin(
        id = "velocitycore",
        name = "VelocityCore",
        version = "1.0"
)
public class VelocityEnabler extends DependencyMetaData<IRedis,Redis> implements IVelocityEnabler, IInfrastructureDomain, IDependencyInterface<IVelocityEnabler> {

    IDependencyInjectorHelper<?,?> injectorHelper = new DependencyInjectorHelper<>();




    @Subscribe
    @Override
    public void onVelocityEnable(ProxyInitializeEvent event) throws Throwable {
        //TODO: Add main method logging
        //TODO: Remove from main method
        ReflectUtil.loadLibs();
        //TODO: Remove concrete call in favor of DI
        Config.createConfig();
        Config.loadConfig();
        injectorHelper.setupDISystem(this);
        new CustomRunnable() {

            @Override
            public void run() {
                Log.warn("Trying to connect to redis...");
                connectToRedis();
            }
        }.runTaskLaterAsync(15000L);
        new CustomRunnable() {

            @Override
            public void run() {
                Log.warn("Trying to connect to redis via token lookup...");
                getDependency(IRedis.class).connectToRedis();
            }
        }.runTaskLaterAsync(15000L);
        new CustomRunnable() {

            @Override
            public void run() {
                Log.warn("Trying to connect to redis via concrete...");
                getDependencyInstance().connectToRedis();
            }
        }.runTaskLaterAsync(15000L);
        new CustomRunnable() {

            @Override
            public void run() {
                Log.warn("Trying rank dependency via token lookup...");
                IRank rank = getDependency(IRank.class);
                if (rank == null) {
                    Log.error("IRank dependency was null during startup token lookup test");
                    return;
                }
                Log.info("Resolved IRank via token lookup. Cached rank count=" + rank.getRanks().size());
            }
        }.runTaskLaterAsync(15000L);

        //TODO: Delegate null check away from main init loop


        MySQL.connect();


        //TODO: Testing to see if @PostConstruct can run without direct redis class method delegation
//        redis = Redis.jedis;
//        redis.connect();



    }
    @Override
    public void registerCommand(String command, Command commandClass, ProxyServer proxyServer, String... aliases){
        CommandManager commandManager = proxyServer.getCommandManager();
        commandManager.register(commandManager.metaBuilder(command).aliases(aliases).build(), commandClass);
        Log.info("Registered command: /" + command + (aliases.length > 0 ? " (aliases: " + String.join(", ", aliases) + ")" : ""));
    }



}
