package com.tjxjnoobie.abstracts;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Abstract factory for creating and managing manager instances
 * @param <T> The context type
 * @param <M> The base manager type
 */
public abstract class AbstractManagerFactory<T, M extends AbstractManager<T>> {
    
    protected final T context;
    private final ConcurrentHashMap<Class<? extends M>, M> managers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends M>, Supplier<M>> factories = new ConcurrentHashMap<>();
    
    protected AbstractManagerFactory(T context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        registerFactories();
    }
    
    /**
     * Gets or creates a manager instance
     * @param managerClass The manager class
     * @return The manager instance
     */
    @SuppressWarnings("unchecked")
    public <U extends M> U getManager(Class<U> managerClass) {
        if (managerClass == null) {
            throw new IllegalArgumentException("Manager class cannot be null");
        }
        
        M manager = managers.get(managerClass);
        if (manager != null) {
            return (U) manager;
        }
        
        // Create new instance
        synchronized (this) {
            // Double-check locking
            manager = managers.get(managerClass);
            if (manager != null) {
                return (U) manager;
            }
            
            Supplier<M> factory = factories.get(managerClass);
            if (factory == null) {
                throw new IllegalArgumentException("No factory registered for " + managerClass.getName());
            }
            
            manager = factory.get();
            if (manager == null) {
                throw new RuntimeException("Factory returned null for " + managerClass.getName());
            }
            
            // Initialize the manager
            manager.initialize();
            
            managers.put(managerClass, manager);
            return (U) manager;
        }
    }
    
    /**
     * Checks if a manager is registered
     * @param managerClass The manager class
     * @return true if registered
     */
    public boolean isRegistered(Class<? extends M> managerClass) {
        return factories.containsKey(managerClass);
    }
    
    /**
     * Checks if a manager instance exists
     * @param managerClass The manager class
     * @return true if instance exists
     */
    public boolean hasInstance(Class<? extends M> managerClass) {
        return managers.containsKey(managerClass);
    }
    
    /**
     * Registers a factory for a manager type
     * @param managerClass The manager class
     * @param factory The factory supplier
     */
    protected void registerFactory(Class<? extends M> managerClass, Supplier<M> factory) {
        if (managerClass == null) {
            throw new IllegalArgumentException("Manager class cannot be null");
        }
        if (factory == null) {
            throw new IllegalArgumentException("Factory cannot be null");
        }
        
        factories.put(managerClass, factory);
    }
    
    /**
     * Unregisters a manager factory
     * @param managerClass The manager class
     */
    protected void unregisterFactory(Class<? extends M> managerClass) {
        factories.remove(managerClass);
        
        // Also remove any existing instance
        M manager = managers.remove(managerClass);
        if (manager != null) {
            manager.cleanup();
        }
    }
    
    /**
     * Gets the context
     * @return The context instance
     */
    public T getContext() {
        return context;
    }
    
    /**
     * Initializes all registered managers
     */
    public void initializeAll() {
        for (Class<? extends M> managerClass : factories.keySet()) {
            getManager(managerClass);
        }
    }
    
    /**
     * Cleans up all manager instances
     */
    public void cleanup() {
        for (M manager : managers.values()) {
            try {
                manager.cleanup();
            } catch (Exception e) {
                System.err.println("Error cleaning up manager " + manager.getClass().getName() + ": " + e.getMessage());
            }
        }
        managers.clear();
    }
    
    /**
     * Gets the number of registered factories
     * @return The factory count
     */
    public int getFactoryCount() {
        return factories.size();
    }
    
    /**
     * Gets the number of created manager instances
     * @return The instance count
     */
    public int getInstanceCount() {
        return managers.size();
    }
    
    /**
     * Template method for subclasses to register their specific factories
     */
    protected abstract void registerFactories();
}