package com.tjxjnoobie.api.dependency.contexts.abstracts;

import com.tjxjnoobie.api.dependency.maps.DependencyMap;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyGraphMap;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.annotations.AutoInjectAll;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.metadata.interfaces.IAbstractClassMetaData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Supplier;

/**
 * Abstract base class for context implementations providing common dependency injection functionality.
 * Focuses on context management and dependency resolution.
 * Injection orchestration is delegated to DependencyInjectorHelper and ContextInjectionHelper.
 *
 * @param <T> The interface context type
 */
public abstract class AbstractContext<T> implements IContext<T>, IAbstractClassMetaData<T>, IDependencyMap, IDependencyGraphMap{

    // Static registry to track all context instances
    private final List<IContext<?>> contextRegistry = new ArrayList<>();
    // InjectionConfig implementation
    private final Set<String> allowedPackages = new HashSet<>();
    private final Set<String> excludedPackages = new ConcurrentSkipListSet<>();
    private final HashMap<Class<?>, Boolean> eligibilityCache = new HashMap<>();
    
    private T context;

    /**
     * Default constructor that initializes the context with default configuration values.
     */
    public AbstractContext() {
        initializeDefaults();
    }
    
    /**
     * Initializes default configuration values for allowed and excluded packages.
     * Sets up the default package whitelist and blacklist for dependency injection eligibility.
     */
    public void initializeDefaults() {
        // Add default allowed packages
        allowedPackages.add("com.tjxjnoobie");
        
        // Add default excluded packages  
        excludedPackages.add("java.");
        excludedPackages.add("javax.");
        excludedPackages.add("sun.");
        excludedPackages.add("com.sun.");
    }

    // ===== REGISTER/BASE FUNCTIONS =====







    /**
     * Registers a factory supplier for a dependency class.
     * The factory is wrapped to record instantiation metadata when invoked.
     *
     * @param <U> The type of the dependency
     * @param clazz The class type to register the factory for
     * @param factory The supplier that creates instances of the dependency
     */
    public <U> void registerFactory(Class<U> clazz, Supplier<U> factory) {
        // Create a wrapper factory that includes metadata recording
        Supplier<U> wrappedFactory = () -> {
            U instance = factory.get();
            if (instance != null) {
                try {
                    recordDiInstantiation(instance.getClass(), this.getClass());
                } catch (Exception e) {
                    Log.warn("[DI-Factory] Failed to record instantiation for " + instance.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
            return instance;
        };
        
        // Register with null instance but with factory
        registerDependency(clazz, null, wrappedFactory, this);
        registerDependencyToGraph(clazz);
        
        Log.info("[DI-Factory] Registered factory for: " + clazz.getSimpleName());
    }

    /**
     * Updates an existing dependency binding with a new instance.
     * Re-registers the dependency in both the dependency map and graph.
     *
     * @param <U> The type of the dependency
     * @param clazz The class type to rebind
     * @param instance The new instance to bind
     */
    public <U> void rebind(Class<U> clazz, U instance) {
        registerDependency(clazz, instance);
        registerDependencyToGraph(clazz);
    }

    /**
     * Updates an existing factory binding with a new factory supplier.
     * Delegates to registerFactory to handle the update.
     *
     * @param <U> The type of the dependency
     * @param clazz The class type to rebind
     * @param factory The new factory supplier
     */
    public <U> void rebindFactory(Class<U> clazz, Supplier<U> factory) {
        registerFactory(clazz, factory);
    }

    /**
     * Removes a dependency from the dependency map and graph.
     * Cleans up all references to the specified class type.
     *
     * @param <U> The type of the dependency
     * @param clazz The class type to unbind
     */
    public <U> void unbind(Class<U> clazz) {
        removeDependency(clazz);
        dependencyGraph.remove(clazz);
        Log.info("[DI] Unbound dependency: " + clazz.getSimpleName());
    }

    /**
     * Reloads a dependency by creating a new instance from the factory and re-registering it.
     * Replaces both the instance and factory bindings.
     *
     * @param <U> The type of the dependency
     * @param clazz The class type to reload
     * @param factory The factory supplier to create the new instance
     */
    public <U> void reload(Class<U> clazz, Supplier<U> factory) {
        U instance = factory.get();
        registerDependency(clazz, instance, factory, this);
        registerDependencyToGraph(clazz);
    }

    /**
     * Performs automatic binding of dependencies for a target object.
     * This is a no-op in the base class and should be overridden by subclasses to provide implementation.
     *
     * @param target The object to perform auto-binding on
     */
    public void autoBind(Object target) {
        // Default no-op - subclasses can override
        Log.warn("[DI] autoBind not implemented in AbstractContext - override in subclass if needed");
    }

    /**
     * Resolves a dependency by attempting multiple resolution strategies.
     * First checks for direct instance, then factory, then assignable type.
     *
     * @param dependencyClass The class type to resolve
     * @return The resolved instance, or null if not found
     */
    @Override
    public Object resolveDependency(Class<?> dependencyClass) {
        // Use custom map's getInstance method
        Object instance = getDependency(dependencyClass);
        if (instance != null) {
            return instance;
        }
        
        // Check if there's metadata with a factory
        IDependencyMetaData metaData = getDependency(dependencyClass);
        if (metaData != null && metaData.getFactory() != null) {
            Object created = metaData.getFactory().get();
            if (created != null) {
                return created;
            }
        }
        
        // Try to find by assignable type
        Object assignable = findByAssignableType(dependencyClass);
        if (assignable != null) {
            return assignable;
        }
        
        return null;
    }

    // ===== GETTERS/SETTERS =====

    /**
     * Returns a type-safe reference to this context instance.
     * Used internally for casting and type safety.
     *
     * @return This context instance cast to type T
     */
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    /**
     * Retrieves this context instance.
     * Provides explicit access to the context for external consumers.
     *
     * @return This context instance
     */
    @Override
    public T getContext() {
        return self();
    }

    /**
     * Retrieves the dependency map associated with this context.
     * The map contains all registered dependencies and their metadata.
     *
     * @return The dependency map
     */
    public DependencyMap getDependencyMap() {
        return dependencyMap;
    }

    /**
     * Retrieves a dependency instance using its factory supplier.
     * Throws an exception if no factory is found for the specified class.
     *
     * @param <U> The type of the dependency
     * @param clazz The class type to retrieve
     * @return The instance created by the factory
     * @throws RuntimeException if no factory binding is found
     */
    public <U> U getWithFactory(Class<U> clazz) {
        IDependencyMetaData metaData = getDependency(clazz);
        if (metaData != null && metaData.getFactory() != null) {
            Object instance = metaData.getFactory().get();
            if (instance != null) {
                return clazz.cast(instance);
            }
        }
        throw new RuntimeException("No factory binding found for " + clazz.getName());
    }

    /**
     * Retrieves all registered contexts in the global registry.
     * Returns a copy of the registry to prevent external modification.
     *
     * @return A list of all registered context instances
     */
    public List<IContext<?>> getAllContexts() {
        return new ArrayList<>(contextRegistry);
    }

    /**
     * Sets the context instance and registers it in the global registry.
     * Logs the total number of contexts after registration.
     *
     * @param context The context instance to set
     */
    public void setContext(T context) {
        this.context = context;
        registerContext(this);
        Log.info("[DI] Total contexts in registry: " + contextRegistry.size());
    }

    // ===== CONTEXT REGISTRY MANAGEMENT =====

    /**
     * Registers a context in the global registry.
     * Prevents duplicate registrations and logs the registration event.
     * Logs a critical error if the context is null.
     *
     * @param context The context to register
     */
    public void registerContext(IContext<?> context) {
        if(context == null){
            Log.critical("[DI] Context is null");
        }
        if (!contextRegistry.contains(context)) {
            contextRegistry.add(context);
            Log.info("[DI] Registered context: " + context.getClass().getSimpleName());
        }
        Log.warn("[DI] Context already registered, skipping");
    }

    /**
     * Unregisters a context from the global registry.
     * Removes the context if it exists and logs the unregistration event.
     *
     * @param context The context to unregister
     */
    public void unregisterContext(IContext<?> context) {
        if (contextRegistry.remove(context)) {
            Log.info("[DI] Unregistered context: " + context.getClass().getSimpleName());
        }
    }

    /**
     * Clears all contexts from the global registry.
     * Useful for testing and cleanup operations.
     */
    public void clearContextRegistry() {
        contextRegistry.clear();
        Log.info("[DI] Cleared global context registry");
    }

 




}
