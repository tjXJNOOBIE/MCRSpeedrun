package com.tjxjnoobie.proxy;

import org.tavall.dependency.annotations.DelegatesToInterface;
import org.tavall.dependency.injection.helpers.DependencyInjectorHelper;
import org.tavall.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.managers.PlayerProfile;
import com.tjxjnoobie.api.platform.cache.RankCache;
import com.tjxjnoobie.api.platform.velocity.Rank;
import com.tjxjnoobie.api.platform.velocity.startup.VelocityEnabler;
import com.tjxjnoobie.api.platform.velocity.startup.interfaces.IVelocityEnabler;
import com.tjxjnoobie.api.platform.velocity.startup.interfaces.IVelocityMain;
import com.tjxjnoobie.proxy.Events.VelocityLoginEvent;
import com.tjxjnoobie.proxy.Events.VelocityPreLoginEvent;
import com.tjxjnoobie.proxy.commands.*;
import com.tjxjnoobie.proxy.store.PlayerStoreHistoryService;
import com.tjxjnoobie.proxy.store.ProxySchemaBootstrap;
import com.tjxjnoobie.proxy.store.PurchasedRankApplier;
import com.tjxjnoobie.proxy.store.StoreBackendClient;
import com.tjxjnoobie.proxy.store.StoreFulfillmentPoller;
import com.tjxjnoobie.proxy.store.StoreIntegrationConfig;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.time.Duration;

@Plugin(
    id = "velocitycore",
    name = "VelocityCore",
    version = "1.0"
)
@DelegatesToInterface(getLinkedInterface = IVelocityMain.class)
public class VelocityMain implements IVelocityMain{

    @com.google.inject.Inject private Logger logger;
    @com.google.inject.Inject private ProxyServer proxyServer;
    private final IDependencyInjectorHelper<?, ?> injectionHelper = new DependencyInjectorHelper<>();
    IVelocityEnabler velocityEnabler = new VelocityEnabler();
    //TODO: Testing custom injection on a isolated redis instance to check of @PostConstruct can run
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws Throwable {
        velocityEnabler.onVelocityEnable(event);
        new ProxySchemaBootstrap(logger).ensureInitialized();
        Rank rankService = new Rank();
        RankCache rankCache = new RankCache(rankService);
        PlayerProfile playerProfile = new PlayerProfile();
        //TODO: Abstract SimpleCommand so we can move
        // these commands to a separate class??
       velocityEnabler.registerCommand("sim", new Sim(),proxyServer );
       velocityEnabler.registerCommand("rank", new RankCMD(),proxyServer );
       velocityEnabler.registerCommand("ban", new Ban(), proxyServer);
       velocityEnabler.registerCommand("kick", new Kick(),proxyServer );
       velocityEnabler.registerCommand("mute", new Mute(),proxyServer );
       velocityEnabler.registerCommand("warn", new Warn(),proxyServer );
       velocityEnabler.registerCommand("unban", new Unban(),proxyServer);
       registerStoreRuntime(rankService, rankCache, playerProfile);
       proxyServer.getEventManager().register(this, new VelocityPreLoginEvent());
       proxyServer.getEventManager().register(this, new VelocityLoginEvent());
    }

    private void registerStoreRuntime(Rank rankService, RankCache rankCache, PlayerProfile playerProfile) {
        StoreIntegrationConfig storeConfig = StoreIntegrationConfig.fromEnvironment();
        StoreBackendClient backendClient = new StoreBackendClient(storeConfig);
        PlayerStoreHistoryService historyService = new PlayerStoreHistoryService(backendClient);
        PurchasedRankApplier purchasedRankApplier = new PurchasedRankApplier(
                rankService,
                rankCache,
                playerProfile,
                proxyServer,
                storeConfig.getFallbackRank()
        );
        velocityEnabler.registerCommand("store", new StoreCommand(rankService, historyService), proxyServer);

        if (!storeConfig.isEnabled()) {
            logger.info("Store integration disabled. Set NOVUS_STORE_BASE_URL and NOVUS_STORE_BOOTSTRAP_SECRET to enable polling.");
            return;
        }

        StoreFulfillmentPoller poller = new StoreFulfillmentPoller(backendClient, purchasedRankApplier, logger);
        proxyServer.getScheduler()
                .buildTask(this, poller::pollOnce)
                .delay(Duration.ofSeconds(5))
                .repeat(storeConfig.getPollInterval())
                .schedule();
        logger.info("Store fulfillment polling enabled against {}", storeConfig.getBaseUrl());
    }


}
