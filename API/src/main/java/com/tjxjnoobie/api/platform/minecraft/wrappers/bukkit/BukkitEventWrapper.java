package com.tjxjnoobie.api.platform.minecraft.wrappers.bukkit;

import com.tjxjnoobie.api.abstracts.AbstractEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

    public class BukkitEventWrapper extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final AbstractEvent wrappedEvent;

    public BukkitEventWrapper(AbstractEvent wrappedEvent) {
        this.wrappedEvent = wrappedEvent;
    }

    public AbstractEvent getWrappedEvent() {
        return wrappedEvent;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    }


