package com.tjxjnoobie.api.internal.events.interfaces;

import com.tjxjnoobie.api.abstracts.AbstractEvent;

public interface EventListener<T extends AbstractEvent> {

    boolean supports(AbstractEvent event);

    void handle(T event);
}