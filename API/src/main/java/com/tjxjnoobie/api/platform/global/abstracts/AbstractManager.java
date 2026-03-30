package com.tjxjnoobie.api.platform.global.abstracts;

import com.tjxjnoobie.api.platform.global.annotations.Inject;

/**
 * Abstract base class for all managers providing common functionality
 * @param <T> The context type this manager operates with
 */
public abstract class AbstractManager<T>  {

    @Inject protected T context;
    protected volatile boolean initialized = false;

    protected AbstractManager() {

    }
    protected AbstractManager(T context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
    }
    
    /**
     * Gets the context this manager operates with
     * @return The context instance
     */
    public T getContext() {
        return context;
    }
    
    /**
     * Initializes the manager. This method should be called after construction.
     * Subclasses should override doInitialize() to provide specific initialization logic.
     */
    public final void initialize() {
        if (initialized) {
            return;
        }
        try {
            doInitialize();
            initialized = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize " + getClass().getSimpleName(), e);
        }
    }
    
    /**
     * Checks if the manager has been initialized
     * @return true if initialized, false otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Ensures the manager is initialized before performing operations
     * @throws IllegalStateException if not initialized
     */
    protected void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException(getClass().getSimpleName() + " has not been initialized");
        }
    }
    
    /**
     * Template method for subclasses to implement their specific initialization logic
     * @throws Exception if initialization fails
     */
    protected abstract void doInitialize() throws Exception;
    
    /**
     * Cleanup method called when the manager is being destroyed
     * Subclasses should override this to provide cleanup logic
     */
    public void cleanup() {
        initialized = false;
    }
    
    /**
     * Gets the manager name for logging and debugging purposes
     * @return The manager name
     */
    public String getManagerName() {
        return getClass().getSimpleName();
    }
}