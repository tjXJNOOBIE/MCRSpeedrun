package com.tjxjnoobie.api.interfaces;

import java.util.List;

/**
 * Base interface for context management providing dependency injection capabilities
 *
 * @param <T> The type of context implementation
 */
public interface IContext<T> extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {


    /**
     * Gets the context instance itself
     *
     * @return The context instance
     */

    T getContext();

    /**
     * Returns a list of all registered context instances.
     *
     * @return List of all IContext instances, never null.
     */
    List<IContext<?>> getAllContexts();
}
