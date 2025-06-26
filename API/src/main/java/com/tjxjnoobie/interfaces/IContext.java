package com.tjxjnoobie.interfaces;

import java.util.HashMap;

/**
 * Base interface for context management providing dependency injection capabilities
 * @param <T> The type of context implementation
 */
public interface IContext<T> {
    
    /**
     * Gets the dependency map for direct access
     * @return HashMap containing all registered dependencies
     */
    HashMap<Class<?>, Object> getDependencyMap();
    
    /**
     * Gets a dependency by its class type
     * @param clazz The class type to retrieve
     * @param <U> The type parameter
     * @return The dependency instance
     * @throws IllegalArgumentException if dependency is not found
     */
    <U> U get(Class<U> clazz);
    
    /**
     * Registers a dependency in the context
     * @param clazz The class type to register
     * @param instance The instance to register
     * @param <U> The type parameter
     */
    <U> void register(Class<U> clazz, U instance);
    
    /**
     * Checks if a dependency is registered
     * @param clazz The class type to check
     * @return true if registered, false otherwise
     */
    boolean isRegistered(Class<?> clazz);
    
    /**
     * Gets the context instance itself
     * @return The context instance
     */
    T getContext();
}