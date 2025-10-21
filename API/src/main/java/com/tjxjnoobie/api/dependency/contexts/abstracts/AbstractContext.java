package com.tjxjnoobie.api.dependency.contexts.abstracts;

import com.tjxjnoobie.api.dependency.injection.enums.LifecycleType;
import com.tjxjnoobie.api.dependency.injection.helpers.ContextInjectionHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IContextInjectionHelper;
import com.tjxjnoobie.api.dependency.maps.DependencyGraphMap;
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
import java.lang.reflect.*;
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
    
    // Use custom dependency maps instead of plain Maps
    protected final DependencyGraphMap dependencyGraph = new DependencyGraphMap();
    protected final IContextInjectionHelper contextInjectorHelper = new ContextInjectionHelper();
    
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




    @Override
    public void injectAllDependencies() throws IllegalAccessException {
        injectAllDependencies(1); // Default to 1 pass
    }

    @Override
    public int injectAllDependencies(int maxPasses) throws IllegalAccessException {
        Set<Object> allDeps = new HashSet<>(dependencyMap.getAllInstances());
        Set<Object> injected = new HashSet<>();
        int totalInjected = 0;

        Log.info("[DI] ===== Starting multi-pass injection for " + this.getClass().getSimpleName() + " =====");
        Log.info("[DI] Total dependencies to inject: " + allDeps.size());

        for (int pass = 0; pass < maxPasses; pass++) {
            int injectedThisPass = 0;

            Log.info("[DI] --- Pass " + (pass + 1) + " ---");

            for (Object dep : allDeps) {
                if (dep == null || dep == this || injected.contains(dep)) {
                    continue;
                }

                // Check if this dependency has injectable fields
                if (hasInjectableFields(dep)) {
                    Log.info("[DI] Injecting into: " + dep.getClass().getSimpleName());
                    injectAndRecordMetaData(dep);
                    injected.add(dep);
                    injectedThisPass++;
                    totalInjected++;
                } else {
                    // No injectable fields, mark as done
                    injected.add(dep);
                }
            }

            Log.info("[DI] Pass " + (pass + 1) + " completed: " + injectedThisPass + " objects injected");

            // If no progress was made, we're done
            if (injectedThisPass == 0) {
                Log.info("[DI] No progress in pass " + (pass + 1) + ", stopping early");
                break;
            }
        }

        Log.info("[DI] ===== Multi-pass injection complete: " + totalInjected + " total injections =====");
        return totalInjected;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void injectFieldsFromContext(Object target, IContext<?> context) {
        contextInjectorHelper.injectFieldsFromContext(target, context);
    }

    @Override
    public void injectFieldsFromContexts(Object target, List<IContext<?>> contexts) {
        contextInjectorHelper.injectFieldsFromContexts(target, contexts);
    }

    @Override
    public void injectAllFromContexts(List<IContext<?>> contexts) throws IllegalAccessException {
        if (contexts == null || contexts.isEmpty()) {
            Log.warn("[DI] No contexts provided for injection");
            return;
        }

        Log.info("[DI] ===== Starting multi-context injection =====");
        Log.info("[DI] Contexts to process: " + contexts.size());

        // First, inject dependencies within each context
        for (IContext<?> context : contexts) {
            if (context != null) {
                Log.info("[DI] Processing context: " + context.getClass().getSimpleName());
                context.injectAllDependencies();
                context.injectFieldsFromContext(this, context);
            }
        }

        Log.info("[DI] ===== Multi-context injection complete =====");
    }


    /**
     * Injects fields into a target object using a merged dependency map.
     * This allows cross-context dependency resolution.
     *
     * @param target             The object to inject into
     * @param mergedDependencies The merged dependency map from all contexts
     */

    
    /**
     * Injects static fields of a class using the current dependency map.
     * This is useful for injecting into utility classes or managers with static fields.
     *
     * @param clazz The class whose static fields should be injected
     */
    //TODO: Merge with regular injection method
    public void injectStaticFields(Class<?> clazz) {
        contextInjectorHelper.injectStaticFields(clazz);
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

    /**
     * Generates a comprehensive dependency report for debugging.
     * Call this method anytime to see the current state of all dependencies.
     */
    public void generateDependencyReport() {
        generateInjectableReport(); // Call the missing method
    }

    /**
     * Generates a report of injectable classes and their metadata.
     */
    public void generateInjectableReport() {
        Log.info("[DI-REPORT] ========== COMPREHENSIVE DEPENDENCY REPORT ==========");
        Log.info("[DI-REPORT] Generated at: " + new Date());
        Log.info("[DI-REPORT]");

        // Context overview
        Log.info("[DI-REPORT] === CONTEXT OVERVIEW ===");
        Log.info("[DI-REPORT] Total contexts registered: " + contextRegistry.size());

        int totalDeps = 0;
        for (IContext<?> context : contextRegistry) {
            if (context != null) {
                totalDeps += context.getDependencyMap().size();
                Log.info("[DI-REPORT]   • " + context.getClass().getSimpleName() +
                        " (" + context.getDependencyMap().size() + " deps)");
            }
        }
        Log.info("[DI-REPORT] Total dependencies: " + totalDeps);
        Log.info("[DI-REPORT]");

        // Detailed per-context breakdown
        for (IContext<?> context : contextRegistry) {
            if (context == null) continue;

            String contextName = context.getClass().getSimpleName();
            Log.info("[DI-REPORT] === " + contextName + " ===");

            DependencyMap depMap = context.getDependencyMap();
            if (depMap.isEmpty()) {
                Log.warn("[DI-REPORT]   (No dependencies registered)");
                continue;
            }

            // Group by package for better readability
            Map<String, List<String>> byPackage = new HashMap<>();

            for (Map.Entry<Class<?>, IDependencyMetaData> entry : depMap.entrySet()) {
                Class<?> type = entry.getKey();
                Object instance = entry.getValue();

                String packageName = type.getPackage() != null ?
                        type.getPackage().getName() : "(default)";

                String info = type.getSimpleName() + " → " +
                        (instance != null ? instance.getClass().getSimpleName() : "NULL") +
                        (instance == null ? " ⚠" : " ✓");

                byPackage.computeIfAbsent(packageName, k -> new ArrayList<>()).add(info);
            }

            // Print grouped dependencies
            for (Map.Entry<String, List<String>> pkgEntry : byPackage.entrySet()) {
                Log.info("[DI-REPORT]   Package: " + pkgEntry.getKey());
                for (String dep : pkgEntry.getValue()) {
                    Log.info("[DI-REPORT]     • " + dep);
                }
            }
            Log.info("[DI-REPORT]");
        }

        Log.info("[DI-REPORT] ========================================================");

        // Add metadata summary using built-in IAbstractClassMetaData methods
        Log.info("[DI-REPORT] === METADATA SUMMARY ===");
        try {
            Log.info("[DI-REPORT] Total classes tracked: " + getMetadataRegistrySize());
            Log.info("[DI-REPORT] Registered classes: " + getRegisteredClasses());
            Log.info("[DI-REPORT] Instantiated classes: " + getInstantiatedClasses());
            Log.info("[DI-REPORT] " + getFormattedStatsSummary());
        } catch (Exception e) {
            Log.warn("[DI-REPORT] Metadata not available: " + e.getMessage());
        }
        Log.info("[DI-REPORT] ========================================================");
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

    /**
     * Checks if an object has no @Inject annotated fields.
     * Used to identify leaf dependencies in wave-based injection.
     *
     * @param obj The object to check
     * @return true if the object has no @Inject fields, false otherwise
     */
    public boolean hasNoInjectFields(Object obj) {
        return contextInjectorHelper.hasNoInjectFields(obj);
    }



    public Object invokeDefault(Object proxy, Method method, Object[] args) throws Throwable {
        final Class<?> declaringClass = method.getDeclaringClass();
        // bypass access checks
        Constructor<MethodHandles.Lookup> constructor =
                MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
        constructor.setAccessible(true);

        return constructor
                .newInstance(declaringClass,
                        MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                                | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
                .unreflectSpecial(method, declaringClass)
                .bindTo(proxy)
                .invokeWithArguments(args == null ? new Object[0] : args);
    }


    /**
     * Resolves a lifecycle method for the given class using the LifecycleType enum.
     * Prefers metadata if present, falling back to scanning the class via LifecycleType.findIn().
     */
    public Optional<Method> getLifecycleMethod(Class<?> clazz, LifecycleType type) {
        if (clazz == null || type == null) return Optional.empty();
        IDependencyMetaData meta = dependencyGraph.get(clazz);
        if (meta != null) {
            Method m = (type == LifecycleType.PRE_CONSTRUCT) ? meta.getPreConstruct() : meta.getPostConstruct();
            if (m != null) return Optional.of(m);
        }
        return type.findIn(clazz);
    }

    /**
     * Executes a lifecycle method (PRE_CONSTRUCT or POST_CONSTRUCT) with error handling.
     * Records PRE_CONSTRUCT success/failure in metadata when available.
     */
    public void executeLifecycle(Object target, LifecycleType type) {
        if (target == null || type == null) return;
        Class<?> clazz = (target instanceof Class) ? (Class<?>) target : target.getClass();
        Optional<Method> maybe = getLifecycleMethod(clazz, type);
        if (maybe.isEmpty()) return;

        Method method = maybe.get();
        try {
            method.setAccessible(true);
            if (Modifier.isStatic(method.getModifiers())) {
                method.invoke(null);
            } else {
                if (target instanceof Class) {
                    Log.warn("[DI] Skipping " + type + " for " + clazz.getSimpleName() + " (no instance)");
                    return;
                }
                method.invoke(target);
            }
            IDependencyMetaData meta = dependencyGraph.get(clazz);
            if (meta != null && type == LifecycleType.PRE_CONSTRUCT) {
                meta.setPreConstructSuccess(true);
            }
        } catch (Exception e) {
            IDependencyMetaData meta = dependencyGraph.get(clazz);
            if (meta != null && type == LifecycleType.PRE_CONSTRUCT) {
                meta.setPreConstructSuccess(false);
                meta.incrementRetryCount();
            }
            Log.critical("[DI] " + type + " failed for " + clazz.getSimpleName() + " :: " + e.getMessage());
        }
    }


}
