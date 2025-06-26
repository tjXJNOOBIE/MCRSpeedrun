package com.tjxjnoobie.events.internal;

import com.tjxjnoobie.abstracts.AbstractEvent;
import com.tjxjnoobie.annotations.ModuleScope;
import com.tjxjnoobie.annotations.SubscribeEvent;
import com.tjxjnoobie.enums.EventCapability;
import com.tjxjnoobie.enums.EventDomain;
import com.tjxjnoobie.enums.EventStatus;
import com.tjxjnoobie.events.interfaces.EventListener;
import com.tjxjnoobie.wrappers.ListenerWrapper;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EventBus {
    private final Map<Class<?>, List<ListenerWrapper>> registry = new HashMap<>();
    private final List<AbstractEvent.EventListener<?>> listeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<AbstractEvent>> middleware = new CopyOnWriteArrayList<>();

    // Async executor for async-capable events
    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "EventBus-AsyncExecutor");
        t.setDaemon(true);
        return t;
    });
    public void register(AbstractEvent.EventListener<?> listener) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubscribeEvent.class)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1 || !AbstractEvent.class.isAssignableFrom(params[0])) continue;

                SubscribeEvent sub = method.getAnnotation(SubscribeEvent.class);
                ModuleScope scope = method.getAnnotation(ModuleScope.class);

                method.setAccessible(true);
                Class<?> eventClass = params[0];

                ListenerWrapper wrapper = new ListenerWrapper(
                        listener,
                        method,
                        sub.priority(),
                        sub.async(),
                        scope != null ? scope.value() : EventDomain.GLOBAL
                );

                registry.computeIfAbsent(eventClass, c -> new ArrayList<>()).add(wrapper);
                registry.get(eventClass).sort(Comparator.comparingInt(w -> -w.priority));
            }
        }
    }

    public void unregister(AbstractEvent.EventListener<?> listener) {
        listeners.remove(listener);
        for (List<ListenerWrapper> wrappers : registry.values()) {
            wrappers.removeIf(w -> w.instance.equals(listener));
        }
    }

    public void post(AbstractEvent event, EventDomain eventDomain) {
        List<ListenerWrapper> listeners = registry.get(event.getClass());
        if (listeners == null) return;

        for (ListenerWrapper wrapper : listeners) {
            if (wrapper.eventDomain != EventDomain.GLOBAL && wrapper.eventDomain != eventDomain)
                continue;
            if (event.isCancelled()) break;

            if (wrapper.async) {
                new Thread(() -> wrapper.invoke(event)).start();
            } else {
                wrapper.invoke(event);
            }
        }
    }
    // Listeners registered to this bus




    /**
     * Unregisters an event listener.
     * @param listener The listener to unregister.
     */


    /**
     * Registers middleware that executes before event handlers.
     * Middleware can modify or log events.
     * @param mw Middleware consumer.
     */
    public void registerMiddleware(Consumer<AbstractEvent> mw) {
        if (!middleware.contains(mw)) {
            middleware.add(mw);
        }
    }

    /**
     * Unregisters middleware.
     * @param mw Middleware consumer to remove.
     */
    public void unregisterMiddleware(Consumer<AbstractEvent> mw) {
        middleware.remove(mw);
    }

    public static <T extends AbstractEvent> void fire(T event) {
        if (!event.hasCapability(EventCapability.FIREABLE)) {
            throw new IllegalStateException("Event is not marked as FIREABLE: " + event.getClass().getSimpleName());
        }

        long start = System.nanoTime();

        event.setStatus(EventStatus.FIRED); //changed
        event.setStatus(EventStatus.RUNNING);
        event.beforeFire();
        event.applyMiddleware();

        Runnable dispatch = () -> {
            try {
                for (AbstractEvent.EventListener<?> listener : listeners) {
                    if (listener.supports(event)) {
                        ((EventListener<T>) listener).handle(event);
                    }
                }
                event.setStatus(EventStatus.SUCCESS);
            } catch (Throwable ex) {
                event.logException(ex);
                event.setStatus(EventStatus.FAILED);
            } finally {
                event.completed = true;
                event.completedAt = System.currentTimeMillis();
                long duration = System.nanoTime() - start;
                System.out.println("[Event Timer] " + event.getClass().getSimpleName() + " took " + duration + " ns");
                event.onFire();
            }
        };

        if (event.hasCapability(EventCapability.ASYNC)) {
            asyncExecutor.execute(dispatch);
        } else {
            dispatch.run();
        }
    }
    public void post(AbstractEvent event) {
        post(event, EventDomain.GLOBAL);
    }
}


