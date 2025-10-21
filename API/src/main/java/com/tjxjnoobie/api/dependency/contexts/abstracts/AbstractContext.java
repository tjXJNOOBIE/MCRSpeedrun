package com.tjxjnoobie.api.dependency.contexts.abstracts;

import com.tjxjnoobie.api.dependency.injection.enums.LifecycleType;
import com.tjxjnoobie.api.dependency.maps.DependencyMap;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyGraphMap;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.annotations.AutoInjectAll;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.metadata.interfaces.IAbstractClassMetaData;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Supplier;

/**
 * Abstract base class for context implementations providing common dependency injection functionality
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
     * Default constructor
     */
    public AbstractContext() {
        initializeDefaults();
    }
    
    /**
     * Initialize default configuration values
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



    /**
     * Factory retrieval with explicit factory check
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

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    /**
     * Explicit context retrieval: returns this context instance.
     */
    @Override
    public T getContext() {
        return self();
    }
    public DependencyMap getDependencyMap() {
        return dependencyMap;
    }

    public void setContext(T context) {
        this.context = context;
        registerContext(this);
        Log.info("[DI] Total contexts in registry: " + contextRegistry.size());
    }







    /**
     * Factory registration - stores factory in metadata
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
     * Rebind - updates existing dependency
     */
    public <U> void rebind(Class<U> clazz, U instance) {
        registerDependency(clazz, instance);
        registerDependencyToGraph(clazz);
    }

    /**
     * Factory rebinding - updates existing factory
     */
    public <U> void rebindFactory(Class<U> clazz, Supplier<U> factory) {
        registerFactory(clazz, factory);
    }

    /**
     * Unbind - removes dependency from custom maps
     */
    public <U> void unbind(Class<U> clazz) {
        removeDependency(clazz);
        dependencyGraph.remove(clazz);
        Log.info("[DI] Unbound dependency: " + clazz.getSimpleName());
    }

    /**
     * Reload - replaces both instance and factory
     */
    public <U> void reload(Class<U> clazz, Supplier<U> factory) {
        U instance = factory.get();
        registerDependency(clazz, instance, factory, this);
        registerDependencyToGraph(clazz);
    }



    /**
     * Auto-binding is not implemented in base class
     * Subclasses can override to provide implementation
     */
    public void autoBind(Object target) {
        // Default no-op - subclasses can override
        Log.warn("[DI] autoBind not implemented in AbstractContext - override in subclass if needed");
    }




    /**
     * Basic dependency resolution using custom maps
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
//    public void buildDependencyGraph(Set<Class<?>> injectables) {
//        dependencyGraph.clear();
//        reverseDependencies.clear();
//
//        // Step 1: Fill direct dependencies for each class
//        for (Class<?> clazz : injectables) {
//            Set<Class<?>> dependencies = new HashSet<>();
//
//            // Walk fields
//            for (Field field : clazz.getDeclaredFields()) {
//                if (field.getType().isInterface() || injectables.contains(field.getType())) {
//                    dependencies.add(field.getType());
//                }
//            }
//
//            // Walk superclasses/interfaces
//            Class<?> superClass = clazz.getSuperclass();
//            if (superClass != null && injectables.contains(superClass)) {
//                dependencies.add(superClass);
//            }
//
//            for (Class<?> iface : clazz.getInterfaces()) {
//                if (injectables.contains(iface)) {
//                    dependencies.add(iface);
//                }
//            }
//
//            dependencyGraph.put(clazz, dependencies);
//        }
//
//        // Step 2: Build reverse dependency map
//        for (Map.Entry<Class<?>, Set<Class<?>>> entry : dependencyGraph.entrySet()) {
//            Class<?> clazz = entry.getKey();
//            for (Class<?> dep : entry.getValue()) {
//                reverseDependencies.computeIfAbsent(dep, k -> new HashSet<>()).add(clazz);
//            }
//        }
//
//        // Step 3: Calculate depth levels and roles
//        calculateDepthLevels();
//        classifyDependencyRoles();
//    }
//
//    public void classifyDependencyRoles() {
//        for (Class<?> clazz : dependencyGraph.keySet()) {
//            Set<Class<?>> deps = dependencyGraph.getOrDefault(clazz, Set.of());
//            Set<Class<?>> revs = reverseDependencies.getOrDefault(clazz, Set.of());
//
//            DependencyRole role;
//            if (deps.isEmpty() && revs.isEmpty()) role = DependencyRole.ISOLATED;
//            else if (deps.isEmpty()) role = DependencyRole.BASE;
//            else if (revs.isEmpty()) role = DependencyRole.TERMINAL;
//            else role = DependencyRole.INTERMEDIATE;
//
//            int priority = 0;
//            if (clazz.isAnnotationPresent(Priority.class)) {
//                priority = clazz.getAnnotation(Priority.class).value();
//            }
//
//            int depth = depthLevels.getOrDefault(clazz, 0);
//            dependencyMetaDataMap.put(clazz, new DependencyMetaData(role, depth, priority, deps, revs));
//        }
//    }
//    public void calculateDepthLevels() {
//        Map<Class<?>, Integer> depthLevels = new HashMap<>();
//        for (Class<?> base : dependencyGraph.keySet()) {
//            if (dependencyRoles.get(base) == DependencyRole.BASE) {
//                assignDepth(base, 0, depthLevels);
//            }
//        }
//    }
//
//    public void assignDepth(Class<?> clazz, int depth, Map<Class<?>, Integer> map) {
//        map.put(clazz, depth);
//        for (Class<?> dependent : reverseDependencies.getOrDefault(clazz, Set.of())) {
//            assignDepth(dependent, depth + 1, map);
//        }
//    }



    /**
     * Gets all registered contexts globally
     *
     * @return List of all context instances
     */
    public List<IContext<?>> getAllContexts() {
        return new ArrayList<>(contextRegistry);
    }

    /**
     * Registers a context in the global registry.
     * Useful when context registration needs to happen explicitly.
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
     * Unregisters a context from the global registry
     *
     * @param context The context to unregister
     */
    public void unregisterContext(IContext<?> context) {
        if (contextRegistry.remove(context)) {
            Log.info("[DI] Unregistered context: " + context.getClass().getSimpleName());
        }
    }

    /**
     * Clears the global context registry (useful for testing)
     */
    public void clearContextRegistry() {
        contextRegistry.clear();
        Log.info("[DI] Cleared global context registry");
    }


    @Override
    public boolean hasInjectableFields(Object obj) {
        if (obj == null) {
            return false;
        }

        Class<?> clazz = obj.getClass();
        boolean auto = clazz.isAnnotationPresent(AutoInjectAll.class);

        // If @AutoInjectAll is present, check for any non-static fields
        if (auto) {
            while (clazz != null && clazz != Object.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (!Modifier.isStatic(field.getModifiers())) {
                        return true;
                    }
                }
                clazz = clazz.getSuperclass();
            }
            return false;
        }

        // Otherwise, check for @Inject annotated fields or methods
        clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    return true;
                }
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Inject.class)) {
                    return true;
                }
            }

            clazz = clazz.getSuperclass();
        }

        return false;
    }





}
