package com.tjxjnoobie.events.interfaces;

import com.tjxjnoobie.abstracts.AbstractEvent;

public interface EventListener<T extends AbstractEvent> {

    boolean supports(AbstractEvent event);

    void handle(T event);
}