package com.tjxjnoobie.velocityCore;

import com.google.inject.Inject;
import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.managers.*;
import com.tjxjnoobie.API.minecraft.Config;
import com.tjxjnoobie.API.utils.Rating;
import com.tjxjnoobie.API.utils.ReflectUtil;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.API.utils.ProxyUtils;
import com.tjxjnoobie.API.velocity.Rank;
import com.tjxjnoobie.API.velocity.managers.PunishManager;
import com.tjxjnoobie.velocityCore.Commands.*;
import com.tjxjnoobie.velocityCore.Events.VelocityLoginEvent;
import com.tjxjnoobie.velocityCore.Events.VelocityPreLoginEvent;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.sql.SQLException;

@Plugin(
    id = "velocitycore",
    name = "VelocityCore",
    version = "1.0"
)
public class VelocityCore {

    @Inject private Logger logger;
    @Inject private final ProxyServer proxyServer;
    private Rank rank;
    private Utils utils;
    private ProxyUtils proxyUtils;
    private Rating rating;
    private PlayerProfile playerProfile;
    private GlobalContext globalContext;
    private RankCache rankCache;
    private PunishManager punishManager;
    private PunishLog punishLog;


    @Inject
    public VelocityCore(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws SQLException, ClassNotFoundException {
        ReflectUtil.loadLibs();
        Config.createConfig();
        Config.loadConfig();
        MySQL.connect();
        Redis.connectToRedis();
        rank = new Rank();
        proxyUtils = new ProxyUtils(proxyServer);
        playerProfile = new PlayerProfile();
        globalContext = new GlobalContext(null,null,null,null,null,null,null,null,rating,null,playerProfile,rank,null,null,null,null,null,null,proxyUtils,null,null,null,null,punishLog);
        utils = new Utils(globalContext);
        globalContext.setUtils(utils);
        punishManager = new PunishManager(globalContext);
        rankCache = new RankCache(rank);

        globalContext.setRankCache(rankCache);
        globalContext.setPunishManager(punishManager);

        registerCommand("sim", new Sim(rating,utils));
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
