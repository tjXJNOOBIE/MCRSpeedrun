package com.tjxjnoobie.api.platform.velocity.wrappers;

import com.tjxjnoobie.api.abstracts.AbstractEvent;
import com.tjxjnoobie.api.console.Log;
import com.tjxjnoobie.api.internal.events.EventBus;
import com.tjxjnoobie.api.internal.events.interfaces.CancellableEvent;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.function.Function;

public class VelocityEventWrapper {
    private final ProxyServer proxy;
    private final EventBus eventBus;

    public VelocityEventWrapper(ProxyServer proxy, EventBus eventBus) {
        this.proxy = proxy;
        this.eventBus = eventBus;
    }

    /**
     * Registers a Velocity event class and wraps it with a custom event mapper
     *
     * @param velocityEventClass The Velocity event to listen to
     * @param adapter Maps the Velocity event into your AbstractEvent
     * @param <T> Type of Velocity event
     */
    public <T> void register(Class<T> velocityEventClass, Function<T, AbstractEvent> adapter) { //new
        EventManager manager = proxy.getEventManager();

        manager.register(this, velocityEventClass, new EventHandler<>() {
            @Override
            public void execute(T velocityEvent) {
                try {
                    AbstractEvent customEvent = adapter.apply(velocityEvent);

                    if (customEvent == null) {
                        Log.warn("Adapter returned null for event: " + velocityEventClass.getName());
                        return;
                    }

                    eventBus.post(customEvent);

                    // If both Velocity and custom event are cancellable, sync them
                    if (velocityEvent instanceof CancellableEvent
                            && customEvent.isCancellable()
                            && customEvent.isCancelled()) {
                        ((CancellableEvent) velocityEvent).setCancelled(true);
                    }

                } catch (Exception ex) {
                    Log.error("Error handling Velocity event: " + velocityEventClass.getName());
                }
            }
        });
    }
}
