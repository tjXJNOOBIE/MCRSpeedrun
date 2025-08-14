package com.tjxjnoobie.api.abstracts;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Abstract base class for event handlers providing common functionality
 * @param <T> The context type this event handler operates with
 */
public abstract class AbstractEventHandler<T> implements Listener {
    
    protected final T context;
    protected final Plugin plugin;
    protected volatile boolean registered = false;
    
    protected AbstractEventHandler(T context, Plugin plugin) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        
        this.context = context;
        this.plugin = plugin;
    }
    
    /**
     * Gets the context this event handler operates with
     * @return The context instance
     */
    public T getContext() {
        return context;
    }
    
    /**
     * Gets the plugin instance
     * @return The plugin instance
     */
    public Plugin getPlugin() {
        return plugin;
    }
    
    /**
     * Registers this event handler with the plugin manager
     */
    public final void register() {
        if (!registered) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            registered = true;
            onRegister();
        }
    }
    
    /**
     * Checks if this event handler is registered
     * @return true if registered, false otherwise
     */
    public boolean isRegistered() {
        return registered;
    }
    
    /**
     * Unregisters this event handler
     */
    public void unregister() {
        if (registered) {
            // Bukkit doesn't provide a direct way to unregister specific listeners
            // This would typically be handled by the plugin manager during disable
            registered = false;
            onUnregister();
        }
    }
    
    /**
     * Called after the event handler is registered
     * Subclasses can override this for custom initialization
     */
    protected void onRegister() {
        // Default implementation does nothing
    }
    
    /**
     * Called after the event handler is unregistered
     * Subclasses can override this for custom cleanup
     */
    protected void onUnregister() {
        // Default implementation does nothing
    }
    
    /**
     * Gets the handler name for logging and debugging purposes
     * @return The handler name
     */
    public String getHandlerName() {
        return getClass().getSimpleName();
    }
    
    /**
     * Validates that the event handler is properly configured
     * Subclasses should override this to provide specific validation
     * @throws IllegalStateException if validation fails
     */
    protected void validate() throws IllegalStateException {
        if (context == null) {
            throw new IllegalStateException("Context is null");
        }
        if (plugin == null) {
            throw new IllegalStateException("Plugin is null");
        }
    }
}