package com.tjxjnoobie.proxy;

import com.tjxjnoobie.api.dependency.contexts.GlobalContext;
import com.tjxjnoobie.api.dependency.injection.helpers.ContextInjectionHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IContextInjectionHelper;
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
import redis.clients.jedis.Jedis;

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
    private IContext<IGlobalContext> globalContext;
    @Inject private IRankCache rankCache;
    @Inject private IPunishManager punishManager;
    @Inject private IPunishLog punishLog;
    private Jedis redis;




    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws SQLException, ClassNotFoundException, IllegalAccessException {
        //TODO: Add main method logging
        //TODO: Remove from main method
        ReflectUtil.loadLibs();
        IContextInjectionHelper injectionHelper = new ContextInjectionHelper();
        //TODO: Remove concrete call in favor of DI
        globalContext = new GlobalContext();
        Config.createConfig();
        Config.loadConfig();
        injectionHelper.injectAllContextsGlobally(this);
        MySQL.connect();
        redis = Redis.jedis;
        redis.connect();


        registerCommand("sim", new Sim());
        registerCommand("rank", new RankCMD());
        registerCommand("ban", new Ban());
        registerCommand("kick", new Kick());
        registerCommand("mute", new Mute());
        registerCommand("warn", new Warn());
        registerCommand("unban", new Unban());
        proxyServer.getEventManager().register(this, new VelocityPreLoginEvent());
        proxyServer.getEventManager().register(this, new VelocityLoginEvent());

    }

    public void registerCommand(String command, Command commandClass, String... aliases){
        CommandManager commandManager = proxyServer.getCommandManager();
        commandManager.register(commandManager.metaBuilder(command).aliases(aliases).build(), commandClass);
        logger.info("Registered command: /" + command + (aliases.length > 0 ? " (aliases: " + String.join(", ", aliases) + ")" : ""));
    }
}
