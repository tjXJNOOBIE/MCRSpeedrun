package com.tjxjnoobie.proxy;

import com.tjxjnoobie.api.dependency.annotations.DelegatesToInterface;
import com.tjxjnoobie.api.dependency.injection.helpers.DependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInterface;
import com.tjxjnoobie.api.platform.velocity.startup.VelocityEnabler;
import com.tjxjnoobie.api.platform.velocity.startup.interfaces.IVelocityEnabler;
import com.tjxjnoobie.api.platform.velocity.startup.interfaces.IVelocityMain;
import com.tjxjnoobie.proxy.Events.VelocityLoginEvent;
import com.tjxjnoobie.proxy.Events.VelocityPreLoginEvent;
import com.tjxjnoobie.proxy.commands.*;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(
    id = "velocitycore",
    name = "VelocityCore",
    version = "1.0"
)
@DelegatesToInterface(getLinkedInterface = IVelocityMain.class)
public class VelocityMain implements IDependencyInterface<IVelocityMain>, IVelocityMain{

    @com.google.inject.Inject private Logger logger;
    @com.google.inject.Inject private ProxyServer proxyServer;
    IDependencyInjectorHelper<?,?> injectionHelper = new DependencyInjectorHelper<>();
    IVelocityEnabler velocityEnabler = new VelocityEnabler();
    //TODO: Testing custom injection on a isolated redis instance to check of @PostConstruct can run
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws Throwable {
        velocityEnabler.onVelocityEnable(event);
        //TODO: Abstract SimpleCommand so we can move
        // these commands to a separate class??
       velocityEnabler.registerCommand("sim", new Sim(),proxyServer );
       velocityEnabler.registerCommand("rank", new RankCMD(),proxyServer );
       velocityEnabler.registerCommand("ban", new Ban(), proxyServer);
       velocityEnabler.registerCommand("kick", new Kick(),proxyServer );
       velocityEnabler.registerCommand("mute", new Mute(),proxyServer );
       velocityEnabler.registerCommand("warn", new Warn(),proxyServer );
       velocityEnabler.registerCommand("unban", new Unban(),proxyServer);
       proxyServer.getEventManager().register(this, new VelocityPreLoginEvent());
       proxyServer.getEventManager().register(this, new VelocityLoginEvent());
    }


}