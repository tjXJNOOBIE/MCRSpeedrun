package com.tjxjnoobie.proxy;

import com.tjxjnoobie.api.dependency.contexts.GlobalContext;
import com.tjxjnoobie.api.dependency.injection.helpers.ContextInjectionHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.DependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IContextInjectionHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.internal.utils.reflection.ReflectUtil;
import com.tjxjnoobie.api.managers.MySQL;
import com.tjxjnoobie.api.managers.Redis;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.minecraft.Config;
import com.tjxjnoobie.proxy.Commands.*;
import com.tjxjnoobie.proxy.Events.VelocityLoginEvent;
import com.tjxjnoobie.proxy.Events.VelocityPreLoginEvent;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.sql.SQLException;

@Plugin(
    id = "velocitycore",
    name = "VelocityCore",
    version = "1.0"
)
public class VelocityMain  {

    @com.google.inject.Inject private Logger logger;
    @com.google.inject.Inject
    private ProxyServer proxyServer;
    @Inject private IRank rank;
    @Inject private IUtils utils;
    @Inject private IProxyUtils proxyUtils;
    @Inject private IRating rating;
    @Inject private IPlayerProfile playerProfile;
    @Inject private IGlobalContext globalContext;
    @Inject private IRankCache rankCache;
    @Inject private IPunishManager punishManager;
    @Inject private IPunishLog punishLog;
    @Inject private Redis redis;




    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws SQLException, ClassNotFoundException {
        //TODO: Add main method logging
        //TODO: Remove from main method
        IContextInjectionHelper injectionHelper = new ContextInjectionHelper();
        IDependencyInjectorHelper dependencyInjectorHelper = new DependencyInjectorHelper();
        dependencyInjectorHelper.autoBind(this);
        //TODO: Remove concrete call in favor of DI
        globalContext = new GlobalContext();
        ReflectUtil.loadLibs();
        Config.createConfig();
        Config.loadConfig();
        globalContext.setProxyUtils(proxyUtils);

        MySQL.connect();
        globalContext.setRank(rank).
                setProxyUtils(proxyUtils).
                setPlayerProfile(playerProfile).
                setUtils(utils).
                setPunishManager(punishManager).
                setRankCache(rankCache).
                setPunishLog(punishLog).
                setRedis(redis);
        redis.connectToRedis();


        registerCommand("sim", new Sim());
        registerCommand("rank", new RankCMD(globalContext));
        registerCommand("ban", new Ban(globalContext,proxyServer));
        registerCommand("kick", new Kick(globalContext,proxyServer));
        registerCommand("mute", new Mute());
        registerCommand("warn", new Warn(globalContext,proxyServer));
        registerCommand("unban", new Unban(globalContext,proxyServer));
        proxyServer.getEventManager().register(this, new VelocityPreLoginEvent(globalContext));
        proxyServer.getEventManager().register(this, new VelocityLoginEvent(globalContext));

    }

    public void registerCommand(String command, Command commandClass, String... aliases){
        CommandManager commandManager = proxyServer.getCommandManager();
        commandManager.register(commandManager.metaBuilder(command).aliases(aliases).build(), commandClass);
        logger.info("Registered command: /" + command + (aliases.length > 0 ? " (aliases: " + String.join(", ", aliases) + ")" : ""));
    }
}
