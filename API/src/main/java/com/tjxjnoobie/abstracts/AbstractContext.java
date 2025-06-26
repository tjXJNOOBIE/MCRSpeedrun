package com.tjxjnoobie.abstracts;

import com.tjxjnoobie.interfaces.IContext;

import java.util.HashMap;

/**
 * Abstract base class for context implementations providing common dependency injection functionality
 * @param <T> The concrete context type
 */
public abstract class AbstractContext<T extends AbstractContext<T>> implements IContext<T> {
    
    protected final HashMap<Class<?>, Object> dependencyMap = new HashMap<>();
    
    @Override
    public HashMap<Class<?>, Object> getDependencyMap() {
        return dependencyMap;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <U> U get(Class<U> clazz) {
        // First, try a direct lookup
        Object dependency = dependencyMap.get(clazz);
        if (dependency != null) {
            return clazz.cast(dependency);
        }
        
        // Try to find an instance that is assignable
        for (Object obj : dependencyMap.values()) {
            if (clazz.isInstance(obj)) {
                return clazz.cast(obj);
            }
        }
        
        throw new IllegalArgumentException("No dependency found for " + clazz.getName());
    }
    
    @Override
    public <U> void register(Class<U> clazz, U instance) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        if (instance == null) {
            throw new IllegalArgumentException("Instance cannot be null");
        }
        dependencyMap.put(clazz, instance);
    }
    
    @Override
    public boolean isRegistered(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        
        // Check direct registration
        if (dependencyMap.containsKey(clazz)) {
            return true;
        }
        
        // Check if any registered instance is assignable to the class
        for (Object obj : dependencyMap.values()) {
            if (clazz.isInstance(obj)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public T getContext() {
        return (T) this;
    }
    
    /**
     * Registers multiple dependencies at once
     * @param dependencies Map of class to instance mappings
     */
    protected void registerAll(HashMap<Class<?>, Object> dependencies) {
        if (dependencies != null) {
            dependencyMap.putAll(dependencies);
        }
    }
    
    /**
     * Template method for subclasses to initialize their specific dependencies
     */
    protected abstract void initializeDependencies();
}