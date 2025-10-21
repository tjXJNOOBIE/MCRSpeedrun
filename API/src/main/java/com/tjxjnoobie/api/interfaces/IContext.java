package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.dependency.maps.DependencyMap;

import java.util.List;

/**
 * Base interface for context management providing dependency injection capabilities
 * @param <T> The type of context implementation
 */
public interface IContext<T> {


    /**
     * Gets the context instance itself
     *
     * @return The context instance
     */

    T getContext();
    /**
     * Gets the dependency map for direct access
     * @return HashMap containing all registered dependencies
     */
    DependencyMap getDependencyMap();


    /**
     * Checks if an object has any @Inject annotated fields
     * @param obj The object to check
     * @return true if the object has injectable fields, false otherwise
     */
    boolean hasInjectableFields(Object obj);
    


    /**
     * Returns a list of all registered context instances.
     * @return List of all IContext instances, never null.
     */
     List<IContext<?>> getAllContexts();
}