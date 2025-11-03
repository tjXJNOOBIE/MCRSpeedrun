package com.tjxjnoobie.api.dependency.contexts.abstracts;

import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.metadata.interfaces.IAbstractClassMetaData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Abstract base class for context implementations providing common dependency injection functionality.
 * Focuses on context management and dependency resolution.
 * Injection orchestration is delegated to DependencyInjectorHelper and ContextInjectionHelper.
 *
 * @param <T> The interface context type
 */
public abstract class AbstractContext<T> implements IContext<T>, IAbstractClassMetaData<T> {
    //TODO: Rename class to be concenred or move methods out to restart with a better context system
    // Static registry to track all context instances
    // InjectionConfig implementation
    List<IContext<?>> contextRegistry = new ArrayList<>(); //TODO: Move into a interface within the registry system (undone)

    //TODO: Convert Context System to auto build context based on runtime that we determine at
    // dev time so its ready at compile time. This will allow for a more dynamic and flexible context management system.
    private final Set<String> allowedPackages = new HashSet<>();
    private final Set<String> excludedPackages = new ConcurrentSkipListSet<>();
    
    private T context;
    

    /**
     * Default constructor that initializes the context with default configuration values.
     */
    public AbstractContext() {


        // Only register helpers if we're not already initializing them (prevents circular dependency)
        //TODO: We're currently insacning interfaces themselves for this process for testing
        // This system should be replaced with a automatic injection system
//        if (!INITIALIZING_HELPERS.get()) {
//            INITIALIZING_HELPERS.set(true);
//            try {
//                dependencyMap.registerImportant(IDependencyInjectorHelper.class, new DependencyInjectorHelper(), 0);
//                dependencyMap.registerImportant(IContextInjectionHelper.class, new ContextInjectionHelper(), 0);
//            } finally {
//                INITIALIZING_HELPERS.set(false);
//            }
//        }
        //TODO: Check use for 'initializeDefaults()' method, package scanning itself scans the base package
        // and negates the use for exclusion or inclusion

        // initializeDefaults();
    }
    


    // ===== CORE RESOLUTION (Highest Priority) =====

//    /**
//     * Resolves a dependency by attempting multiple resolution strategies.
//     * First checks for direct instance, then factory, then assignable type.
//     * This is the primary method for dependency resolution.
//     *
//     * @param dependencyClass The class type to resolve
//     * @return The resolved instance, or null if not found
//     */
//    @Override
//    public Object resolveDependency(Class<?> dependencyClass) {
//        if (dependencyClass == null) {
//            return null;
//        }
//
//        IDependencyMetaData metaData = dependencyMap.getDependency(dependencyClass);
//        Object instance = metaData != null ? dependencyMap.ensureAndGetInstance(metaData) : null;
//
//        if (instance != null) {
//            return instance;
//        }
//
//        IDependencyMetaData compatible = dependencyMap.findByAssignableType(dependencyClass);
//        if (compatible != null) {
//            instance = dependencyMap.ensureAndGetInstance(compatible);
//            if (instance != null) {
//                return instance;
//            }
//        }
//
//        return null;
//    }

    // ===== REGISTER/BASE FUNCTIONS =====

//    /**
//     * Registers a factory supplier for a dependency class.
//     * The factory is wrapped to record instantiation metadata when invoked.
//     *
//     * @param <U> The type of the dependency
//     * @param clazz The class type to register the factory for
//     * @param factory The supplier that creates instances of the dependency
//     */
//    //TODO: Remove factory method in favor of the one in IDependencyInstance
//    public <U> void registerFactory(Class<U> clazz, Supplier<U> factory) {
//        // Create a wrapper factory that includes metadata recording
//        Supplier<U> wrappedFactory = () -> {
//            U instance = factory.get();
//            if (instance != null) {
//                try {
//                    recordDiInstantiation(instance.getClass(), this.getClass());
//                } catch (Exception e) {
//                    Log.warn("[DI-Factory] Failed to record instantiation for " + instance.getClass().getSimpleName() + ": " + e.getMessage());
//                }
//            }
//            return instance;
//        };
//
//        // Register with null instance but with factory
//        registerDependency(clazz, null, wrappedFactory, this);
//        registerDependencyToGraph(clazz);
//
//        Log.info("[DI-Factory] Registered factory for: " + clazz.getSimpleName());
//    }
//
//    /**
//     * Reloads a dependency by creating a new instance from the factory and re-registering it.
//     * Replaces both the instance and factory bindings.
//     *
//     * @param <U> The type of the dependency
//     * @param clazz The class type to reload
//     * @param factory The factory supplier to create the new instance
//     */
//    //TODO: Hook reload method with new supplier system from IDependencyInstance
//    public <U> void reload(Class<U> clazz, Supplier<U> factory) {
//
//
//        U instance = factory.get();
//        registerDependency(clazz, instance, factory, this);
//        registerDependencyToGraph(clazz);
//    }
//
//    /**
//     * Updates an existing dependency binding with a new instance.
//     * Re-registers the dependency in both the dependency map and graph.
//     *
//     * @param <U> The type of the dependency
//     * @param clazz The class type to rebind
//     * @param instance The new instance to bind
//     */
//    //TODO: Hook rebind method with new supplier system from IDependencyInstance
//
//    public <U> void rebind(Class<U> clazz, U instance) {
//        registerDependency(clazz, instance);
//        registerDependencyToGraph(clazz);
//    }
//
//    /**
//     * Updates an existing factory binding with a new factory supplier.
//     * Delegates to registerFactory to handle the update.
//     *
//     * @param <U> The type of the dependency
//     * @param clazz The class type to rebind
//     * @param factory The new factory supplier
//     */
//    //TODO: Hook reload method with new supplier system from IDependencyInstance
//
//    public <U> void rebindFactory(Class<U> clazz, Supplier<U> factory) {
//        registerFactory(clazz, factory);
//    }
//
//    /**
//     * Removes a dependency from the dependency map and graph.
//     * Cleans up all references to the specified class type.
//     *
//     * @param <U> The type of the dependency
//     * @param clazz The class type to unbind
//     */
//    //TODO: Hook unbind method with new supplier system from IDependencyInstance
//
//    public <U> void unbind(Class<U> clazz) {
//        removeDependency(clazz);
//        dependencyGraph.remove(clazz);
//        Log.info("[DI] Unbound dependency: " + clazz.getSimpleName());
//    }



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


    //TODO: Move method to concerned InjectionConfig class
    /**
     * Provides read access to the configured allowed package prefixes.
     *
     * @return live set of allowed package prefixes
     */
    protected Set<String> getAllowedPackagePrefixes() {
        return allowedPackages;
    }

    /**
     * Provides read access to the configured excluded package prefixes.
     *
     * @return live set of excluded package prefixes
     */
    //TODO: Move method to concerned InjectionConfig class

    protected Set<String> getExcludedPackagePrefixes() {
        return excludedPackages;
    }


    /**
     * Retrieves a list containing all registered context instances from the global registry.
     * This method returns a copy of the internal context registry to prevent external modification.
     *
     * @return A list of all IContext<?> instances currently registered in the system, never null.
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
