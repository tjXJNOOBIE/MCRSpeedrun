/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.injection.helpers;

import com.tjxjnoobie.api.dependency.contexts.abstracts.AbstractContext;
import com.tjxjnoobie.api.dependency.injection.enums.LifecycleType;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.metadata.DependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyGraphMap;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.annotations.AutoInjectAll;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.annotations.Injectable;
import com.tjxjnoobie.api.platform.global.annotations.PreConstruct;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * DependencyInjectorHelper – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 10/12/2025
 */
public class DependencyInjectorHelper extends AbstractContext<IContext<?>> implements IDependencyInjectorHelper {


    private final Queue<Class<?>> preConstructRetryQueue = new ConcurrentLinkedQueue<>();


    /**
     * Gets all instances from the map.
     *
     * @return Collection of all dependency instances
     */
    @Override
    public List<Object> getAllInstances() {
        return dependencyMap.getAllInstances();
    }
    /**
     * PreConstruct initialization method with highest priority (0).
     * This ensures the DI system is initialized before any other components.
     * Called automatically after dependency injection but before other classes.
     */
    @PreConstruct(priority = 0)
    public void initializeDependencySystem() {
        Log.info("[DI-Helper] ===== Initializing Dependency Injection System =====");
        Log.info("[DI-Helper] Injectable classes count: " + dependencyGraph.size());
        
        try {
            // Build the dependency graph first
            if (!dependencyGraph.isEmpty()) {
                Log.info("[DI-Helper] Building dependency graph...");
                buildDependencyGraph();
                Log.info("[DI-Helper] Computing depth levels...");
                computeDepthLevels();
                Log.info("[DI-Helper] Dependency graph built successfully");
            }
            
            // Initialize the injection map
            Log.info("[DI-Helper] Injection map initialized");
            
            // Log summary
            Log.success("[DI-Helper] Dependency Injection System initialized successfully");
            Log.info("[DI-Helper] Graph nodes: " + dependencyGraph.size());
            Log.info("[DI-Helper] Registered dependencies: " + dependencyMap.getDependencyMapSize());

        } catch (Exception e) {
            Log.critical("[DI-Helper] Failed to initialize DI system: " + e.getMessage());
            throw new RuntimeException("DI System initialization failed", e);
        }
    }


    // ===== Fluent priority builder =====

    /**
     * Registers a dependency instance as important with a specified priority.
     *
     * This method registers the given class and instance as an important dependency,
     * meaning it will be processed during injection with high priority. It first
     * registers the dependency using the standard registration mechanism, then sets
     * metadata to associate the class with its dependency type and priority level.
     *
     * @param clazz the class of the dependency to register
     * @param instance the instance object that represents this dependency
     * @param priority the priority value for injection; higher values indicate earlier processing during injection
     * @return null - this method does not return a meaningful value
     */
    @Override
    public void registerImportant(Class<?> clazz, Object instance, int priority) {
        registerDependency(clazz,instance);

        getMetaData(clazz).setDependencyClass(clazz);
        getMetaData(clazz).setPriority(priority);

    }

    public void initialize() throws Exception {
        buildDependencyGraph();
        computeDepthLevels();
        injectAll();
        processPreConstructRetryQueue();
    }

    // ===== Helper methods for DI System ===== \\


    /**
     * Injects a value into a field with error handling.
     */
    public void injectFieldValue(Object target, Field field, Object value, boolean optional, 
                                   boolean isStatic, Class<?> depClass, Class<?> clazz) {
        field.setAccessible(true);
        try {
            if (value != null) {
                if (isStatic) field.set(null, value);
                else field.set(target, value);
            } else if (!optional) {
                Log.error("[DI] ❌ Missing required dependency: " + depClass.getSimpleName() 
                        + " | Needed by: " + clazz.getSimpleName() 
                        + " | Field: " + field.getName()
                        + " | Static: " + isStatic);
            }
        } catch (Exception e) {
            Log.error("[DI] Failed injecting " + depClass.getName() + " into " + clazz.getName());
            Log.exception(e);
        }
    }

    /**
     * Injects a value via method invocation with error handling.
     */
    public void injectMethodValue(Object target, Method method, Object value, boolean optional,
                                    Class<?> depClass, Class<?> clazz) {
        try {
            if (value != null) method.invoke(target, value);
            else if (!optional)
                Log.error("[DI] ❌ Missing required dependency: " + depClass.getSimpleName() 
                        + " | Needed by: " + clazz.getSimpleName() 
                        + " | Method: " + method.getName());
        } catch (Exception e) {
            Log.error("[DI] Failed injecting via method " + method.getName() + " in " + clazz.getName());
            Log.exception(e);
        }
    }
    /**
     * Injects static fields of a class using the current owner's dependency map.
     * This is useful for utility/manager classes with static fields.
     */
    public void injectStaticFields(Class<?> clazz) {
        injectStaticFields(clazz, false);
    }

    public void injectStaticFields(Class<?> clazz, boolean autoInject) {
        if (clazz == null) {
            Log.warn("[DI] Cannot inject static fields of null class");
            return;
        }
        // TODO: Wire all DI classes with @Injectable annotation before re-enabling this check
        // Currently commented out to allow injection without @Injectable requirement
        // if (isRequireInjectableAnnotation() && !clazz.isAnnotationPresent(Injectable.class)) {
        //     Log.info("[DI] Skipping static injection for non-@Injectable class: " + clazz.getName());
        //     return;
        // }
        injectFieldsForClass(null, clazz, autoInject, true, false, null);
    }
    /**
     * Executes a lifecycle method (PreConstruct or PostConstruct) with error handling.
     */
    public void executeLifecycleMethod(Method method, Object target, Class<?> clazz, String lifecycleType) {
        try {
            method.setAccessible(true);
            method.invoke(target);
        } catch (Exception e) {
            Log.error("[DI] " + lifecycleType + " failed for " + clazz.getSimpleName());
            Log.exception(e);
        }
    }

    /**
     * Finds a field in a target class that matches the dependency class.
     */
    public Field findField(Class<?> target, Class<?> depClass) {
        for (Field field : target.getDeclaredFields()) {
            if (field.getType().equals(depClass)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Calculates the depth of a class based on its dependencies.
     */
    public int calculateDepth(Class<?> clazz, Set<Class<?>> dependencies) {
        if (dependencies.isEmpty()) return 0;
        int maxDepth = 0;
        for (Class<?> dep : dependencies) {
            IDependencyMetaData depMeta = dependencyGraph.get(dep);
            if (depMeta != null) {
                maxDepth = Math.max(maxDepth, depMeta.getDepth() + 1);
            }
        }
        return maxDepth;
    }

    /**
     * Determines the role of a component based on its dependencies.
     */
    public DependencyRole determineRole(Set<Class<?>> dependencies) {
        if (dependencies.isEmpty()) return DependencyRole.BASE;
        if (dependencies.size() == 1) return DependencyRole.INTERMEDIATE;
        return DependencyRole.ISOLATED;
    }

    // ===== Graph-based dependency analysis =====
    public void buildDependencyGraph() {
        for (Class<?> clazz : dependencyGraph.keySet()) {
            Set<Class<?>> dependencies = new HashSet<>();
            // Fields
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class) && dependencyGraph.containsKey(field.getType())) {
                    dependencies.add(field.getType());
                }
            }
            // Superclass
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && dependencyGraph.containsKey(superClass)) dependencies.add(superClass);
            // Interfaces
            for (Class<?> iface : clazz.getInterfaces()) {
                if (dependencyGraph.containsKey(iface)) dependencies.add(iface);
            }

            IDependencyMetaData meta = dependencyGraph.get(clazz);
            if (meta == null) {
                meta = new IDependencyMetaData() {};
                dependencyGraph.put(clazz, meta);
            }
            meta.setDependencyClass(clazz);
            meta.setDependencies(dependencies);
            meta.setRole(dependencies.isEmpty() ? DependencyRole.BASE : DependencyRole.ISOLATED);
        }
    }

    public void computeDepthLevels() {
        Set<Class<?>> visited = new HashSet<>();
        for (Class<?> clazz : dependencyGraph.keySet()) computeDepthFor(clazz, visited, new HashSet<>());
    }

    public int computeDepthFor(Class<?> clazz, Set<Class<?>> visited, Set<Class<?>> stack) {
        IDependencyMetaData meta = dependencyGraph.get(clazz);
        if (meta != null && meta.getDepth() > 0) return meta.getDepth();
        if (stack.contains(clazz)) throw new RuntimeException("Cyclic dependency detected: " + clazz.getName());
        stack.add(clazz);

        int maxDepDepth = 0;
        if (meta != null) {
            for (Class<?> dep : meta.getDependencies()) {
                maxDepDepth = Math.max(maxDepDepth, computeDepthFor(dep, visited, stack));
            }
        }
        stack.remove(clazz);

        int depth = maxDepDepth + 1;
        if (meta != null) {
            meta.setDepth(depth);
        }
        visited.add(clazz);
        return depth;
    }

    /**
     * Checks if an object has injectable fields.
     * Supports both @Inject annotation and @AutoInjectAll annotation.
     *
     * @param obj The object to check
     * @return true if the object has injectable fields, false otherwise
     */
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
     * Determines if a field should be injected based on annotations.
     */
    public boolean shouldInjectField(Field field, boolean autoInject) {
        return autoInject || field.isAnnotationPresent(Inject.class);
    }

    /**
     * Determines if a method should be injected based on annotations.
     */
    public boolean shouldInjectMethod(Method method, boolean autoInject) {
        return (autoInject || method.isAnnotationPresent(Inject.class)) && method.getParameterCount() == 1;
    }

    /**
     * Common field injection routine used by both static and instance injection paths.
     */
    private void injectFieldsForClass(Object target, Class<?> clazz, boolean autoInject,
                                      boolean includeStatic, boolean includeInstance,
                                      Set<Class<?>> dependencies) {
        if (clazz == null) return;
        for (Field field : clazz.getDeclaredFields()) {
            if (!shouldInjectField(field, autoInject)) continue;

            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if ((isStatic && !includeStatic) || (!isStatic && !includeInstance)) continue;

            Inject inject = field.getAnnotation(Inject.class);
            boolean optional = inject != null && inject.optional();
            Class<?> depClass = field.getType();

            if (dependencies != null) {
                dependencies.add(depClass);
            }

            Object value = resolveDependency(depClass);
            injectFieldValue(target, field, value, optional, isStatic, depClass, clazz);
        }
    }

    // Method injection helper for a single class
    private void injectMethodsForClass(Object target, Class<?> clazz, boolean autoInject, Set<Class<?>> dependencies) {
        if (clazz == null) return;
        for (Method method : clazz.getDeclaredMethods()) {
            if (!shouldInjectMethod(method, autoInject)) continue;

            Inject inject = method.getAnnotation(Inject.class);
            boolean optional = inject != null && inject.optional();
            Class<?> depClass = method.getParameterTypes()[0];
            if (dependencies != null) {
                dependencies.add(depClass);
            }

            Object value = resolveDependency(depClass);
            injectMethodValue(target, method, value, optional, depClass, clazz);
        }
    }

    // Lifecycle detection helper for a single class using enum types


    /**
     * Detects lifecycle methods declared on the given class.
     *
     * Iterates all LifecycleType values and records the first method found for each type.
     * This isolates lifecycle discovery so callers can execute them in a type-safe manner.
     *
     * @param clazz class to inspect for lifecycle annotations
     * @return mapping of LifecycleType to discovered Method; empty if none found
     */
    public EnumMap<LifecycleType, Method> detectLifecycleForClass(Class<?> clazz) {
        EnumMap<LifecycleType, Method> map = new EnumMap<>(LifecycleType.class);
        for (LifecycleType lifecycle : LifecycleType.values()) {
            lifecycle.findIn(clazz).ifPresent(m -> map.put(lifecycle, m));
        }
        return map;
    }

    // ===== Top-level injection =====
    public void injectAll() throws IllegalAccessException {
        dependencyGraph.keySet().stream()
                .sorted(Comparator.comparingInt((Class<?> c) -> {
                    IDependencyMetaData meta = dependencyGraph.get(c);
                    return meta != null ? meta.getPriority() : 0;
                }).thenComparingInt(c -> {
                    IDependencyMetaData meta = dependencyGraph.get(c);
                    return meta != null ? meta.getDepth() : 0;
                }))
                .forEach(this::injectAndRecordMetaData);
    }

    /**
     * Unified injection + metadata population for any target.
     * This method performs injection on the target's fields.
     * If called during Phase 1 (AutoBind), it will scan and register.
     * If called during Phase 4+ (Wave injection), dependencies should already be registered.
     */
    @SuppressWarnings("unchecked")
    public void injectAndRecordMetaData(Object target) {
        if (target == null || target instanceof Class) return;

        Class<?> rootClass = target.getClass();

        boolean auto = rootClass.isAnnotationPresent(AutoInjectAll.class);
        // Get metadata for this class
        IDependencyMetaData meta = dependencyMap.getDependency(rootClass);
        if (meta == null) {
            // Fallback: create metadata if not found
            meta = new DependencyMetaData(rootClass);
        }

        // track discovered dependencies & lifecycle methods
        Set<Class<?>> dependencies = new HashSet<>();
        Method preConstruct = null;
        Method postConstruct = null;

        Class<?> clazz = rootClass;
        while (clazz != null && clazz != Object.class) {
            // Inject static fields for this class via helper to keep this method clean
            injectStaticFields(clazz, auto);

            // === Field injection (instance fields only; static handled above) ===
            injectFieldsForClass(target, clazz, auto, false, true, dependencies);

            // === Method injection ===
            injectMethodsForClass(target, clazz, auto, dependencies);

            // === Lifecycle detection ===
            EnumMap<LifecycleType, Method> lifecycle = detectLifecycleForClass(clazz);
            Method pre = lifecycle.get(LifecycleType.PRE_CONSTRUCT);
            Method post = lifecycle.get(LifecycleType.POST_CONSTRUCT);
            if (pre != null) preConstruct = pre;
            if (post != null) postConstruct = post;

            clazz = clazz.getSuperclass();
        }

        // Update metadata for this component
        meta.setDependencies(dependencies);
        meta.setPreConstruct(preConstruct);
        meta.setPostConstruct(postConstruct);
        meta.setDepth(calculateDepth(rootClass, dependencies));
        meta.setRole(determineRole(dependencies));

        // Store in graph
        dependencyGraph.put(rootClass, meta);

        // === Execute PreConstruct ===
        if (preConstruct != null) {
            executeLifecycleMethod(preConstruct, target, rootClass, "PreConstruct"); //TODO: Type-safe lifecycleType
        }

        // === Execute PostConstruct ===
        if (postConstruct != null) {
            executeLifecycleMethod(postConstruct, target, rootClass, "PostConstruct"); //TODO: Type-safe lifecycleType
        }
    }





    public void processPreConstructRetryQueue() throws InterruptedException {
        while (!preConstructRetryQueue.isEmpty()) {
            Class<?> clazz = preConstructRetryQueue.poll();
            Object instance = dependencyMap.getDependencyInstance(clazz);
            if (instance != null) injectAndRecordMetaData(instance);
            // Thread.sleep(10); // TODO: Add proper loop
        }
    }
    @Override
    public void autoBind(Object target) {
        try {
            // Determine if we're binding a type or an instance
            if (target instanceof Class<?>) {
                bindType((Class<?>) target);
            } else {
                Class<?> targetClass = target.getClass();
                Log.info("[AUTO-BIND] Starting autoBind for: " + targetClass.getSimpleName());
                
                // STEP 1: Scan and register all injectable classes at runtime
                Log.info("[AUTO-BIND] Step 1: Scanning and registering all classes...");
                scanAndRegisterInjectableClasses(targetClass);
                Log.info("[AUTO-BIND] Step 1 complete. Total registered: " + dependencyMap.getDependencyMapSize());
                
                // STEP 2: Inject fields for ALL registered classes from DependencyMap
                Log.info("[AUTO-BIND] Step 2: Injecting fields for all registered classes...");
                injectAllRegisteredClasses();
                Log.info("[AUTO-BIND] Step 2 complete");
                
                // STEP 3: Bind fields from target specifically
                Log.info("[AUTO-BIND] Step 3: Binding target fields...");
                bindFieldsFromTarget(target, targetClass);
                
                Log.info("[AUTO-BIND] AutoBind completed for: " + targetClass.getSimpleName());
            }
        } catch (Exception e) {
            Log.exception(e);
        }
    }

    /**
     * Injects fields for ALL registered classes in the DependencyMap.
     * This ensures every class gets its dependencies injected from the map.
     */
    private void injectAllRegisteredClasses() {
        // Get all registered metadata
        Collection<IDependencyMetaData> allMetadata = dependencyMap.getDependencyMapValues();
        
        Log.info("[AUTO-BIND] Injecting fields for " + allMetadata.size() + " registered classes");
        
        for (IDependencyMetaData meta : allMetadata) {
            if (meta == null || meta.getDependencyClass() == null) {
                continue;
            }
            
            Class<?> clazz = meta.getDependencyClass();
            Object instance = meta.ensureAndGetInstance(meta);
            
            if (instance == null) {
                Log.warn("[AUTO-BIND] No instance available for: " + clazz.getSimpleName());
                continue;
            }
            
            // Inject fields for this instance
            injectFieldsForInstance(instance, clazz);
        }
    }

    /**
     * Injects all @Inject fields for a specific instance using the DependencyMap.
     */
    private void injectFieldsForInstance(Object instance, Class<?> clazz) {
        Class<?> currentClass = clazz;
        
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Inject.class)) {
                    continue;
                }
                
                // Skip static fields (handled separately)
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                
                Class<?> fieldType = field.getType();
                
                // Try to get instance from DependencyMap
                Object value = dependencyMap.getDependencyInstance(fieldType);
                
                if (value == null) {
                    // Try to find by assignable type
                    IDependencyMetaData compatibleMeta = dependencyMap.findByAssignableType(fieldType);
                    if (compatibleMeta != null) {
                        value = compatibleMeta.ensureAndGetInstance(compatibleMeta);
                    }
                }
                
                if (value != null) {
                    try {
                        field.setAccessible(true);
                        field.set(instance, value);
                        Log.info("[AUTO-BIND] Injected " + fieldType.getSimpleName() + 
                                " into " + clazz.getSimpleName() + "." + field.getName());
                    } catch (Exception e) {
                        Log.error("[AUTO-BIND] Failed to inject " + fieldType.getSimpleName() + 
                                " into " + clazz.getSimpleName() + "." + field.getName() + ": " + e.getMessage());
                    }
                } else {
                    Inject inject = field.getAnnotation(Inject.class);
                    boolean optional = inject != null && inject.optional();
                    if (!optional) {
                        Log.warn("[AUTO-BIND] No instance found for required field: " + 
                                clazz.getSimpleName() + "." + field.getName() + " (" + fieldType.getSimpleName() + ")");
                    }
                }
            }
            
            currentClass = currentClass.getSuperclass();
        }
    }

    /**
     * Scans the classpath for injectable classes and registers them in the dependency map.
     * This ensures the dependency map is populated before field binding occurs.
     * Also discovers and registers implementations for interfaces.
     */
    private void scanAndRegisterInjectableClasses(Class<?> targetClass) {
        Log.info("[AUTO-BIND] Scanning classpath for injectable classes...");
        String basePackage = targetClass.getPackage() != null ? targetClass.getPackage().getName() : "";
        
        try {
            Set<Class<?>> injectableClasses = findInjectableClasses(basePackage);
            Log.info("[AUTO-BIND] Found " + injectableClasses.size() + " injectable classes");
            
            // First pass: Analyze roles and assign them BEFORE registration
            Log.info("[AUTO-BIND] Analyzing class dependencies to determine roles...");
            Map<Class<?>, DependencyRole> roleMap = analyzeAndAssignRoles(injectableClasses);
            
            // Second pass: Register all concrete classes with their assigned roles
            Log.info("[AUTO-BIND] Registering classes with assigned roles...");
            registerClassesWithRoles(injectableClasses, roleMap);
            
            // Third pass: Find and register implementations for interfaces
            Log.info("[AUTO-BIND] Scanning for interface implementations...");
            registerInterfaceImplementations(basePackage, injectableClasses);
            
        } catch (Exception e) {
            Log.error("[AUTO-BIND] Error scanning for injectable classes: " + e.getMessage());
        }
    }

    /**
     * Analyzes all injectable classes and assigns roles based on their dependencies.
     * Returns a map of class to assigned role for use in registration.
     */
    private Map<Class<?>, DependencyRole> analyzeAndAssignRoles(Set<Class<?>> injectableClasses) {
        Map<Class<?>, DependencyRole> roleMap = new HashMap<>();
        
        for (Class<?> clazz : injectableClasses) {
            if (!clazz.isInterface()) {
                Set<Class<?>> dependencies = analyzeClassDependencies(clazz);
                DependencyRole role = determineRoleFromDependencies(dependencies);
                roleMap.put(clazz, role);
                Log.info("[AUTO-BIND] Analyzed " + clazz.getSimpleName() + " -> Role: " + role + 
                        " (dependencies: " + dependencies.size() + ")");
            }
        }
        
        return roleMap;
    }

    /**
     * Registers all concrete classes with their pre-assigned roles.
     */
    private void registerClassesWithRoles(Set<Class<?>> injectableClasses, Map<Class<?>, DependencyRole> roleMap) {
        for (Class<?> clazz : injectableClasses) {
            if (!clazz.isInterface() && !dependencyMap.isRegistered(clazz)) {
                try {
                    // Instantiate the class
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    
                    // Register with metadata
                    dependencyMap.registerDependency(clazz, instance);
                    
                    // Get metadata and set the pre-assigned role
                    IDependencyMetaData meta = dependencyMap.getDependency(clazz);
                    if (meta != null) {
                        DependencyRole role = roleMap.getOrDefault(clazz, DependencyRole.ISOLATED);
                        meta.setRole(role);
                        meta.setDependencies(analyzeClassDependencies(clazz));
                        Log.info("[AUTO-BIND] Registered: " + clazz.getSimpleName() + " with role " + role);
                    }
                } catch (Exception e) {
                    Log.warn("[AUTO-BIND] Could not instantiate " + clazz.getSimpleName() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Finds all implementations for interfaces and registers them.
     * This allows a single DI class to register the entire system.
     * Also automatically determines and assigns roles based on dependencies.
     */
    private void registerInterfaceImplementations(String basePackage, Set<Class<?>> injectableClasses) {
        Set<Class<?>> interfaces = new HashSet<>();
        Set<Class<?>> implementations = new HashSet<>();
        
        // Separate interfaces from implementations
        for (Class<?> clazz : injectableClasses) {
            if (clazz.isInterface()) {
                interfaces.add(clazz);
            } else {
                implementations.add(clazz);
            }
        }
        
        Log.info("[AUTO-BIND] Found " + interfaces.size() + " interfaces and " + implementations.size() + " implementations");
        
        // Analyze and assign roles based on dependencies
        Log.info("[AUTO-BIND] Analyzing class dependencies to determine roles...");
        assignRolesToClasses(implementations);
        
        // For each interface, find and register its implementations
        for (Class<?> interfaceClass : interfaces) {
            Set<Class<?>> interfaceImpls = new HashSet<>();
            
            for (Class<?> implClass : implementations) {
                // Check if this class implements the interface
                if (interfaceClass.isAssignableFrom(implClass)) {
                    interfaceImpls.add(implClass);
                }
            }
            
            if (!interfaceImpls.isEmpty()) {
                // Register the first implementation as the default for this interface
                Class<?> defaultImpl = interfaceImpls.iterator().next();
                
                try {
                    Object instance = dependencyMap.getDependencyInstance(defaultImpl);
                    if (instance != null) {
                        // Register the interface to point to the implementation instance
                        dependencyMap.registerDependency((Class<Object>) interfaceClass, instance);
                        Log.info("[AUTO-BIND] Bound interface " + interfaceClass.getSimpleName() + 
                                " -> " + defaultImpl.getSimpleName());
                        
                        // Log all implementations found
                        if (interfaceImpls.size() > 1) {
                            Log.info("[AUTO-BIND] Found " + interfaceImpls.size() + " implementations for " + 
                                    interfaceClass.getSimpleName() + ": " + 
                                    interfaceImpls.stream().map(Class::getSimpleName).reduce((a, b) -> a + ", " + b).orElse(""));
                        }
                    }
                } catch (Exception e) {
                    Log.warn("[AUTO-BIND] Could not bind interface " + interfaceClass.getSimpleName() + ": " + e.getMessage());
                }
            } else {
                Log.info("[AUTO-BIND] No implementations found for interface: " + interfaceClass.getSimpleName());
            }
        }
    }

    /**
     * Automatically analyzes class dependencies and assigns roles.
     * - BASE: No @Inject fields (leaf dependencies)
     * - INTERMEDIATE: 1-2 @Inject fields (depends on a few things)
     * - ISOLATED: 3+ @Inject fields (complex dependencies)
     */
    private void assignRolesToClasses(Set<Class<?>> implementations) {
        for (Class<?> clazz : implementations) {
            try {
                Set<Class<?>> dependencies = analyzeClassDependencies(clazz);
                DependencyRole role = determineRoleFromDependencies(dependencies);
                
                // Get or create metadata
                IDependencyMetaData meta = dependencyMap.getDependency(clazz);
                if (meta != null) {
                    meta.setRole(role);
                    meta.setDependencies(dependencies);
                    Log.info("[AUTO-BIND] Assigned role " + role + " to " + clazz.getSimpleName() + 
                            " (dependencies: " + dependencies.size() + ")");
                }
            } catch (Exception e) {
                Log.warn("[AUTO-BIND] Could not analyze dependencies for " + clazz.getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Analyzes a class to find all its @Inject dependencies.
     * Walks the class hierarchy to find all injected fields.
     */
    private Set<Class<?>> analyzeClassDependencies(Class<?> clazz) {
        Set<Class<?>> dependencies = new HashSet<>();
        
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                // Check for @Inject annotation
                if (field.isAnnotationPresent(Inject.class)) {
                    dependencies.add(field.getType());
                }
            }
            
            // Check for @Inject methods
            for (Method method : current.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Inject.class) && method.getParameterCount() == 1) {
                    dependencies.add(method.getParameterTypes()[0]);
                }
            }
            
            current = current.getSuperclass();
        }
        
        return dependencies;
    }

    /**
     * Determines the role of a class based on its dependency count.
     * - BASE: 0 dependencies (no injections needed)
     * - INTERMEDIATE: 1-2 dependencies (simple dependencies)
     * - ISOLATED: 3+ dependencies (complex dependencies)
     */
    private DependencyRole determineRoleFromDependencies(Set<Class<?>> dependencies) {
        int depCount = dependencies.size();
        
        if (depCount == 0) {
            return DependencyRole.BASE;
        } else if (depCount <= 2) {
            return DependencyRole.INTERMEDIATE;
        } else {
            return DependencyRole.ISOLATED;
        }
    }

    /**
     * Finds all injectable classes in the given package.
     * Looks for classes with @Injectable annotation or interface implementations.
     */
    private Set<Class<?>> findInjectableClasses(String basePackage) {
        Set<Class<?>> injectableClasses = new HashSet<>();
        String path = basePackage.replace('.', '/');

        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File dir = new File(resource.getFile());
                if (dir.exists() && dir.isDirectory()) {
                    walkDirectoryForInjectables(dir, basePackage, injectableClasses);
                }
            }
        } catch (IOException e) {
            Log.warn("[AUTO-BIND] Failed to scan package " + basePackage + ": " + e.getMessage());
        }
        
        return injectableClasses;
    }

    /**
     * Recursively walks directory to find injectable classes.
     * TODO: Wire all DI classes with @Injectable annotation before re-enabling the annotation check
     * Currently discovers ALL classes and interfaces in the project package
     */
    private void walkDirectoryForInjectables(File dir, String packageName, Set<Class<?>> results) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                walkDirectoryForInjectables(file, packageName + "." + file.getName(), results);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    // TODO: Re-enable @Injectable check after all classes are properly annotated
                    // TODO: Remove multiple checks of Injectable.java in multiple methods
                    // Currently accepting ALL classes and interfaces in project packages
                    // if (clazz.isAnnotationPresent(Injectable.class) || 
                    //     (clazz.isInterface() && !clazz.getName().startsWith("java."))) {
                    //     results.add(clazz);
                    // }
                    
                    // Temporary: Accept all non-java/third-party classes
                    if (!clazz.getName().startsWith("java.") && 
                        !clazz.getName().startsWith("javax.") &&
                        !clazz.getName().startsWith("org.bukkit.") &&
                        !clazz.getName().startsWith("com.velocitypowered.") &&
                        !clazz.getName().startsWith("org.slf4j.") &&
                        !clazz.getName().startsWith("sun.")) {
                        results.add(clazz);
                    }

                } catch (Throwable ignored) {
                    // Skip classes that can't be loaded
                }
            }
        }
    }

    /**
     * Binds fields from the target object using the now-populated dependency map.
     */
    private void bindFieldsFromTarget(Object target, Class<?> targetClass) {
        Log.info("[AUTO-BIND] Binding fields for: " + targetClass.getSimpleName());
        
        Class<?> clazz = targetClass;
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                bindField(target, field);
            }
            clazz = clazz.getSuperclass();
        }
    }

    // --- Type-level binding (Class<?>) ---
    public void bindType(Class<?> type) throws Exception {
        if (!type.isInterface() || !isEligibleForInjection(type)) {
            Log.info("[AUTO-BIND] Skipping ineligible or non-interface type: " + type.getName());
            return;
        }

        // 1️⃣ Already registered
        if (dependencyMap.isRegistered(type)) {
            Log.info("[AUTO-BIND] Skipping exact dependency registration for " + type.getSimpleName());
            return;
        }

        // 2️⃣ Compatible registered implementation
        Object compat = dependencyMap.findByAssignableType(type);
        if (compat != null && !dependencyMap.isRegistered(type)) {
            dependencyMap.registerDependency((Class<Object>) type, compat);
            Log.info("[AUTO-BIND] Auto-bound " + type.getSimpleName() + " to existing compatible dependency " + compat.getClass().getSimpleName());
            return;
        }

        // 3️⃣ Default methods -> self-proxy
        if (Arrays.stream(type.getMethods()).anyMatch(Method::isDefault)) {
            Object proxy = createSelfProxy(type);
            dependencyMap.registerDependency((Class<Object>) type, proxy);
            Log.info("[AUTO-BIND] Bound " + type.getSimpleName() + " to self via proxy instance.");
            return;
        }

        // 4️⃣ Discover implementation in same package tree
        Set<Class<?>> impls = findImplementations(type, type.getPackage() != null ? type.getPackage().getName() : "");
        if (!impls.isEmpty()) {
            Class<?> implClass = impls.iterator().next();
            Object instance = implClass.getDeclaredConstructor().newInstance();
            dependencyMap.registerDependency((Class<Object>) type, instance);
            Log.info("[AUTO-BIND] Discovered and registered concrete class for " + type.getSimpleName() + " -> " + implClass.getSimpleName());
            return;
        }

        // 5️⃣ Fallback placeholder proxy
        Object placeholder = createPlaceholderProxy(type);
        dependencyMap.registerDependency((Class<Object>) type, placeholder);
        Log.warn("[AUTO-BIND] Bound " + type.getSimpleName() + " to fallback placeholder.");
    }

    // --- Field-level binding (instance) ---
    public void bindField(Object target, Field field) {
        try {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();

            if (!fieldType.isInterface() || !isEligibleForInjection(fieldType)) return;
            if (field.get(target) != null) return;

            // 1️⃣ Exact match
            if (dependencyMap.isRegistered(fieldType)) { Object existing = dependencyMap.getDependencyInstance(fieldType); field.set(target, existing); return; }

            // 2️⃣ Compatible implementation
            Object compat = dependencyMap.findByAssignableType(fieldType);
            if (compat != null) {
                field.set(target, compat);
                dependencyMap.registerDependency((Class<Object>) fieldType, compat);
                return;
            }

            // 3️⃣ Default method interface -> self proxy
            if (Arrays.stream(fieldType.getMethods()).anyMatch(Method::isDefault)) {
                Object proxy = createSelfProxy(fieldType);
                field.set(target, proxy);
                dependencyMap.registerDependency((Class<Object>) fieldType, proxy);
                return;
            }

            // 4️⃣ Discover implementation
            Set<Class<?>> impls = findImplementations(fieldType, fieldType.getPackage() != null ? fieldType.getPackage().getName() : "");
            if (!impls.isEmpty()) {
                Class<?> implClass = impls.iterator().next();
                Object instance = implClass.getDeclaredConstructor().newInstance();
                field.set(target, instance);
                dependencyMap.registerDependency((Class<Object>) fieldType, instance);
                return;
            }

            // 5️⃣ Placeholder fallback
            Object placeholder = createPlaceholderProxy(fieldType);
            field.set(target, placeholder);
            dependencyMap.registerDependency((Class<Object>) fieldType, placeholder);

        } catch (InaccessibleObjectException ignored) {
            //TODO: Add 'verbose' logging option
           // Log.info("Skipping inaccessible field: " + field.getName());
        } catch (Exception e) {
            Log.error("Error binding field " + field.getName() + ": " + e.getMessage());
        }
    }

    // --- Self-proxy creation helper ---
    public Object createSelfProxy(Class<?> iface) throws Exception {
        return Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class<?>[]{iface},
                (proxyObj, method, args) -> {
                    if (method.isDefault()) {
                        try {
                            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(iface, MethodHandles.lookup());
                            return lookup.unreflectSpecial(method, iface)
                                    .bindTo(proxyObj)
                                    .invokeWithArguments(args);
                        } catch (Throwable t) {
                            Log.error("Failed to invoke default method " + method.getName() + " on " + iface.getSimpleName() + ": " + t.getMessage());
                            throw t;
                        }
                    }
                    Log.warn("Unimplemented interface call: " + method.getName() + " in " + iface.getSimpleName());
                    return null;
                });
    }

    // --- Placeholder proxy helper ---
    public Object createPlaceholderProxy(Class<?> iface) {
        return Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class<?>[]{iface},
                (p, m, a) -> {
                    Log.warn("Called " + m.getName() + " on unimplemented interface: " + iface.getSimpleName());
                    return null;
                });
    }

    // tryInvokeBuildMethod removed: contexts should use explicit registration APIs.
    public Set<Class<?>> findImplementations(Class<?> interfaceType, String basePackage) {
        Set<Class<?>> implementations = new HashSet<>();
        String path = basePackage.replace('.', '/');

        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File dir = new File(resource.getFile());
                if (dir.exists() && dir.isDirectory()) {
                    walkDirectory(interfaceType, dir, basePackage, implementations);
                }
            }
        } catch (IOException e) {
            Log.error("Failed to walk package tree for " + interfaceType.getSimpleName() + ": " + e.getMessage());
        }
        return implementations;
    }

    public void walkDirectory(Class<?> interfaceType, File dir, String packageName, Set<Class<?>> results) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                walkDirectory(interfaceType, file, packageName + "." + file.getName(), results);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    if (!clazz.isInterface() && interfaceType.isAssignableFrom(clazz)) {
                        results.add(clazz);
                        Log.info("Found concrete class: " + clazz.getSimpleName() + " implements " + interfaceType.getSimpleName());
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }
}

