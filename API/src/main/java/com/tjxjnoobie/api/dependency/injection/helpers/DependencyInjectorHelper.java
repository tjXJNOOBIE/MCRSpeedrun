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

    /**
     * Injects all dependencies with a single pass.
     * Default to 1 pass for standard injection.
     *
     * @throws IllegalAccessException if field access fails
     */
    public void injectAllDependencies() throws IllegalAccessException {
        injectAllDependencies(1);
    }

    /**
     * Injects all dependencies with multiple passes for complex dependency chains.
     * Each pass injects dependencies that have injectable fields, continuing until
     * no more progress is made or maxPasses is reached.
     *
     * @param maxPasses maximum number of injection passes to perform
     * @return total number of objects injected across all passes
     * @throws IllegalAccessException if field access fails
     */
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
                System.err.println("[DI] Missing required dependency: " + depClass.getSimpleName());
            }
        } catch (Exception e) {
            System.err.println("[DI] Failed injecting " + depClass.getName() + " into " + clazz.getName());
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
                System.err.println("[DI] Missing required dependency: " + depClass.getSimpleName());
        } catch (Exception e) {
            System.err.println("[DI] Failed injecting via method " + method.getName() + " in " + clazz.getName());
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
        if (isRequireInjectableAnnotation() && !clazz.isAnnotationPresent(Injectable.class)) {
            Log.info("[DI] Skipping static injection for non-@Injectable class: " + clazz.getName());
            return;
        }
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
            System.err.println("[DI] " + lifecycleType + " failed for " + clazz.getSimpleName());
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
     */
    @SuppressWarnings("unchecked")
    public void injectAndRecordMetaData(Object target) {
        if (target == null || target instanceof Class) return;

        Class<?> rootClass = target.getClass();
        autoBind(target); // existing registration logic

        boolean auto = rootClass.isAnnotationPresent(AutoInjectAll.class);

        // Create or fetch IDependencyMetaData for this class
        IDependencyMetaData meta = dependencyMap.registerDependency(rootClass, DependencyMetaData::new);
        meta.setDependencyClass(rootClass);

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

    public void autoBind(Object target) {
        try {
            // Determine if we're binding a type or an instance
            if (target instanceof Class<?>) {
                bindType((Class<?>) target);
            } else {
                Class<?> targetClass = target.getClass();
                if (isRequireInjectableAnnotation() && !targetClass.isAnnotationPresent(Injectable.class)) {
                    Log.info("Skipping autoBind for non-@Injectable target: " + targetClass.getName());
                    return;
                }

                // Walk all fields for instance binding
                for (Field field : targetClass.getDeclaredFields()) {
                    bindField(target, field);
                }
            }
        } catch (Exception e) {
            Log.exception(e);
        }
    }

    // --- Type-level binding (Class<?>) ---
    public void bindType(Class<?> type) throws Exception {
        if (!type.isInterface() || !isEligibleForInjection(type)) {
            Log.info("Skipping ineligible or non-interface type: " + type.getName());
            return;
        }

        // 1️⃣ Already registered
        if (dependencyMap.isRegistered(type)) {
            Log.info("Skipping exact dependency registration for " + type.getSimpleName());
            return;
        }

        // 2️⃣ Compatible registered implementation
        Object compat = dependencyMap.findByAssignableType(type);
        if (compat != null && !dependencyMap.isRegistered(type)) {
            dependencyMap.registerDependency((Class<Object>) type, compat);
            Log.info("Auto-bound " + type.getSimpleName() + " to existing compatible dependency " + compat.getClass().getSimpleName());
            return;
        }

        // 3️⃣ Default methods -> self-proxy
        if (Arrays.stream(type.getMethods()).anyMatch(Method::isDefault)) {
            Object proxy = createSelfProxy(type);
            dependencyMap.registerDependency((Class<Object>) type, proxy);
            Log.info("Bound " + type.getSimpleName() + " to self via proxy instance.");
            return;
        }

        // 4️⃣ Discover implementation in same package tree
        Set<Class<?>> impls = findImplementations(type, type.getPackage() != null ? type.getPackage().getName() : "");
        if (!impls.isEmpty()) {
            Class<?> implClass = impls.iterator().next();
            Object instance = implClass.getDeclaredConstructor().newInstance();
            dependencyMap.registerDependency((Class<Object>) type, instance);
            Log.info("Discovered and registered concrete class for " + type.getSimpleName() + " -> " + implClass.getSimpleName());
            return;
        }

        // 5️⃣ Fallback placeholder proxy
        Object placeholder = createPlaceholderProxy(type);
        dependencyMap.registerDependency((Class<Object>) type, placeholder);
        Log.warn("Bound " + type.getSimpleName() + " to fallback placeholder.");
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
            Log.info("Skipping inaccessible field: " + field.getName());
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
                        Constructor<MethodHandles.Lookup> ctor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                        ctor.setAccessible(true);
                        return ctor.newInstance(iface, MethodHandles.Lookup.PRIVATE)
                                .unreflectSpecial(method, iface)
                                .bindTo(proxyObj)
                                .invokeWithArguments(args);
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

