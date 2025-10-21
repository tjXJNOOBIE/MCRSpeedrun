package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.dependency.maps.DependencyMap;

import java.util.List;
import java.util.Set;

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
     * Injects fields into all registered dependencies in this context
     * Performs multi-pass injection to handle nested dependencies
     */
    void injectAllDependencies() throws IllegalAccessException;
    
    /**
     * Injects fields into all registered dependencies with a maximum number of passes
     * @param maxPasses Maximum number of injection passes to perform
     * @return Number of objects successfully injected
     */
    int injectAllDependencies(int maxPasses) throws IllegalAccessException;
    
    /**
     * Injects fields from a specific context into a target object
     * @param target The target object to inject into
     * @param context The context to use for injection
     */
    void injectFieldsFromContext(Object target, IContext<?> context);
    
    /**
     * Injects fields from multiple contexts into a target object
     * @param target The target object to inject into
     * @param contexts List of contexts to use for injection
     */
    void injectFieldsFromContexts(Object target, List<IContext<?>> contexts);
    
    /**
     * Injects all dependencies from multiple contexts
     * @param contexts List of contexts to inject dependencies from
     */
    void injectAllFromContexts(List<IContext<?>> contexts) throws IllegalAccessException;



    /**
     * Checks if an object has any @Inject annotated fields
     * @param obj The object to check
     * @return true if the object has injectable fields, false otherwise
     */
    boolean hasInjectableFields(Object obj);
    

    /**
     * Checks if an object has no @Inject annotated fields.
     * Used to identify leaf dependencies in wave-based injection.
     * 
     * @param obj The object to check
     * @return true if the object has no @Inject fields, false otherwise
     */
    boolean hasNoInjectFields(Object obj);

    /**
     * Returns a list of all registered context instances.
     * @return List of all IContext instances, never null.
     */
     List<IContext<?>> getAllContexts();
}