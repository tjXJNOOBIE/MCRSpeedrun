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
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.annotations.AutoInjectAll;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.annotations.PreConstruct;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * DependencyInjectorHelper – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 10/12/2025
 */
public class DependencyInjectorHelper extends AbstractContext<IContext<?>> implements IDependencyInjectorHelper {

    private static final Set<String> SCANNED_PACKAGES = ConcurrentHashMap.newKeySet();
    private static final Set<Class<?>> AUTO_BOUND_TARGETS = ConcurrentHashMap.newKeySet();

    public final Queue<Class<?>> preConstructRetryQueue = new ConcurrentLinkedQueue<>();


    private Set<String> collectPackages(Class<?> targetClass) {
        LinkedHashSet<String> packages = new LinkedHashSet<>();

        if (targetClass != null) {
            collectPackagesFromTypeHierarchy(targetClass, packages);
            collectPackagesFromInjectMembers(targetClass, packages);
        }

        packages.addAll(getAllowedPackagePrefixes());
        packages.removeIf(pkg -> pkg == null || pkg.isBlank() || !shouldConsiderPackage(pkg));

        return packages;
    }

    private void collectPackagesFromTypeHierarchy(Class<?> type, Set<String> packages) {
        if (type == null || type == Object.class) {
            return;
        }

        addPackageCandidate(type.getPackage() != null ? type.getPackage().getName() : null, packages);

        for (Class<?> iface : type.getInterfaces()) {
            collectPackagesFromTypeHierarchy(iface, packages);
        }

        collectPackagesFromTypeHierarchy(type.getSuperclass(), packages);
    }

    private void collectPackagesFromInjectMembers(Class<?> type, Set<String> packages) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(AutoInjectAll.class)) {
                    addTypeHierarchyPackages(field.getType(), packages);
                    addGenericTypePackages(field.getGenericType(), packages);
                }
            }

            for (Method method : current.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Inject.class)) {
                    for (Class<?> paramType : method.getParameterTypes()) {
                        addTypeHierarchyPackages(paramType, packages);
                    }
                    Type[] genericParams = method.getGenericParameterTypes();
                    for (Type genericType : genericParams) {
                        addGenericTypePackages(genericType, packages);
                    }
                }
            }

            current = current.getSuperclass();
        }
    }

    private void addTypeHierarchyPackages(Class<?> type, Set<String> packages) {
        if (type == null) {
            return;
        }

        if (type.isArray()) {
            addTypeHierarchyPackages(type.getComponentType(), packages);
            return;
        }

        if (type.isPrimitive()) {
            return;
        }

        addPackageCandidate(type.getPackage() != null ? type.getPackage().getName() : null, packages);

        for (Class<?> iface : type.getInterfaces()) {
            addTypeHierarchyPackages(iface, packages);
        }

        addTypeHierarchyPackages(type.getSuperclass(), packages);
    }

    private void addGenericTypePackages(Type type, Set<String> packages) {
        if (type instanceof ParameterizedType parameterizedType) {
            for (Type arg : parameterizedType.getActualTypeArguments()) {
                if (arg instanceof Class<?> clazz) {
                    addTypeHierarchyPackages(clazz, packages);
                }
            }
        }
    }

    private void addPackageCandidate(String packageName, Set<String> packages) {
        if (packageName == null || packageName.isBlank()) {
            return;
        }

        if (!shouldConsiderPackage(packageName)) {
            return;
        }

        packages.add(packageName);
    }

    private boolean shouldConsiderPackage(String packageName) {
        if (packageName == null || packageName.isBlank()) {
            return false;
        }

        for (String excluded : getExcludedPackagePrefixes()) {
            if (packageName.startsWith(excluded)) {
                return false;
            }
        }

        Set<String> allowedPrefixes = getAllowedPackagePrefixes();
        if (allowedPrefixes.isEmpty()) {
            return true;
        }

        for (String allowed : allowedPrefixes) {
            if (packageName.startsWith(allowed)) {
                return true;
            }
        }

        return false;
    }

    private boolean ensurePackagesScanned(Set<String> packagesToScan) {
        boolean scannedNew = false;
        for (String pkg : packagesToScan) {
            if (pkg == null || pkg.isBlank()) {
                continue;
            }
            if (SCANNED_PACKAGES.add(pkg)) {
                Log.info("[AUTO-BIND] » scanning package: " + pkg);
                scanAndRegisterInjectableClasses(pkg);
                scannedNew = true;
            }
        }
        return scannedNew;
    }

    private void logTypeBinding(Class<?> contract, Object resolved, String origin) {
        String resolvedName = resolved != null ? resolved.getClass().getSimpleName() : "<null>";
        Log.info("[AUTO-BIND] Bound " + contract.getSimpleName() + " -> " + resolvedName + " (" + origin + ")");
    }

    private void assignFieldValue(Object target, Field field, Object value, String sourceDescription) throws IllegalAccessException {
        boolean isStatic = Modifier.isStatic(field.getModifiers());
        field.setAccessible(true);
        if (isStatic) {
            field.set(null, value);
        } else {
            field.set(target, value);
        }

        String owner = isStatic
                ? field.getDeclaringClass().getSimpleName()
                : (target != null ? target.getClass().getSimpleName() : field.getDeclaringClass().getSimpleName());
        String contract = field.getType().getSimpleName();
        String resolvedName = value != null ? value.getClass().getSimpleName() : "<null>";
        Log.info("[AUTO-BIND] Bound " + contract + " -> " + resolvedName + " for "
                + owner + "." + field.getName() + " (" + sourceDescription + ")");
    }

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
    @Override
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
     * <p>
     * This method registers the given class and instance as an important dependency,
     * meaning it will be processed during injection with high priority. It first
     * registers the dependency using the standard registration mechanism, then sets
     * metadata to associate the class with its dependency type and priority level.
     *
     * @param clazz    the class of the dependency to register
     * @param instance the instance object that represents this dependency
     * @param priority the priority value for injection; higher values indicate earlier processing during injection
     * @return null - this method does not return a meaningful value
     */
    @Override
    public void registerImportant(Class<?> clazz, Object instance, int priority) {
        registerDependency(clazz, instance);

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
    @Override
    public void injectFieldValue(Object target, Field field, Object value, boolean optional, boolean isStatic, Class<?> depClass, Class<?> clazz) {
        field.setAccessible(true);
        try {
            if (value != null) {
                if (isStatic) field.set(null, value);
                else field.set(target, value);
            } else if (!optional) {
                Log.error("[DI] ❌ Missing required dependency: " + depClass.getSimpleName() + " | Needed by: " + clazz.getSimpleName() + " | Field: " + field.getName() + " | Static: " + isStatic);
            }
        } catch (Exception e) {
            Log.error("[DI] Failed injecting " + depClass.getName() + " into " + clazz.getName());
            Log.exception(e);
        }
    }

    /**
     * Injects a value via method invocation with error handling.
     */
    @Override
    public void injectMethodValue(Object target, Method method, Object value, boolean optional, Class<?> depClass, Class<?> clazz) {
        try {
            if (value != null) method.invoke(target, value);
            else if (!optional)
                Log.error("[DI] ❌ Missing required dependency: " + depClass.getSimpleName() + " | Needed by: " + clazz.getSimpleName() + " | Method: " + method.getName());
        } catch (Exception e) {
            Log.error("[DI] Failed injecting via method " + method.getName() + " in " + clazz.getName());
            Log.exception(e);
        }
    }

    /**
     * Injects static fields of a class using the current owner's dependency map.
     * This is useful for utility/manager classes with static fields.
     */
    @Override
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
    @Override
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
    @Override
    public Field findField(Class<?> target, Class<?> depClass) {
        for (Field field : target.getDeclaredFields()) {
            if (field.getType().equals(depClass)) {
                return field;
            }
        }
        return null;
    }
    //TODO: Compare usage with calculateDepthFor in this class

    /**
     * Calculates the depth of a class based on its dependencies.
     */
    @Override
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
    @Override
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
                meta = new IDependencyMetaData() {
                };
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
     * Determines if a field should be injected based on annotations.
     */
    @Override
    public boolean shouldInjectField(Field field, boolean autoInject) {
        return autoInject || field.isAnnotationPresent(Inject.class);
    }

    /**
     * Determines if a method should be injected based on annotations.
     */
    @Override
    public boolean shouldInjectMethod(Method method, boolean autoInject) {
        return (autoInject || method.isAnnotationPresent(Inject.class)) && method.getParameterCount() == 1;
    }

    /**
     * Common field injection routine used by both static and instance injection paths.
     */
    @Override
    public void injectFieldsForClass(Object target, Class<?> clazz, boolean autoInject, boolean includeStatic, boolean includeInstance, Set<Class<?>> dependencies) {
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
    @Override
    public void injectMethodsForClass(Object target, Class<?> clazz, boolean autoInject, Set<Class<?>> dependencies) {
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
     * <p>
     * Iterates all LifecycleType values and records the first method found for each type.
     * This isolates lifecycle discovery so callers can execute them in a type-safe manner.
     *
     * @param clazz class to inspect for lifecycle annotations
     * @return mapping of LifecycleType to discovered Method; empty if none found
     */
    @Override
    public EnumMap<LifecycleType, Method> detectLifecycleForClass(Class<?> clazz) {
        EnumMap<LifecycleType, Method> map = new EnumMap<>(LifecycleType.class);
        for (LifecycleType lifecycle : LifecycleType.values()) {
            lifecycle.findIn(clazz).ifPresent(m -> map.put(lifecycle, m));
        }
        return map;
    }

    // ===== Top-level injection =====
    @Override
    public void injectAll() throws IllegalAccessException {
        dependencyGraph.keySet().stream().sorted(Comparator.comparingInt((Class<?> c) -> {
            IDependencyMetaData meta = dependencyGraph.get(c);
            return meta != null ? meta.getPriority() : 0;
        }).thenComparingInt(c -> {
            IDependencyMetaData meta = dependencyGraph.get(c);
            return meta != null ? meta.getDepth() : 0;
        })).forEach(this::injectAndRecordMetaData);
    }

    /**
     * Unified injection + metadata population for any target.
     * This method performs injection on the target's fields.
     * If called during Phase 1 (AutoBind), it will scan and register.
     * If called during Phase 4+ (Wave injection), dependencies should already be registered.
     */
    @SuppressWarnings("unchecked")
    @Override
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
            if (target == null) {
                return;
            }

            if (target instanceof Class<?>) {
                Class<?> type = (Class<?>) target;
                if (!AUTO_BOUND_TARGETS.add(type)) {
                    Log.info("[AUTO-BIND] Skipping duplicate type autoBind: " + type.getSimpleName());
                    return;
                }
                bindTypeInternal(type, "type-level request");
                return;
            }

            Class<?> targetClass = target.getClass();
            if (!AUTO_BOUND_TARGETS.add(targetClass)) {
                Log.info("[AUTO-BIND] Skipping duplicate autoBind for " + targetClass.getSimpleName());
                return;
            }

            Set<String> packagesToScan = collectPackages(targetClass);
            boolean scannedNew = ensurePackagesScanned(packagesToScan);

            if (scannedNew) {
                Log.info("[AUTO-BIND] Injecting registered classes after package scan (" + dependencyMap.getDependencyMapSize() + " entries)");
                injectAllRegisteredClasses();
            }

            Log.info("[AUTO-BIND] Binding fields for all registered classes (" + dependencyMap.getDependencyMapSize() + " entries)");
            bindFieldsForAllRegisteredClasses();

            Log.info("[AUTO-BIND] Binding fields from target object: " + targetClass.getSimpleName());
            bindFieldsFromTarget(target, targetClass);

            Log.info("[AUTO-BIND] AutoBind complete for " + targetClass.getSimpleName()
                    + ". Registered dependencies: " + dependencyMap.getDependencyMapSize());
        } catch (Exception e) {
            Log.exception(e);
        }
    }

    /**
     * Scans the entire project for injectable classes and registers them in the dependency map.
     * This scan runs regardless of whether dependencies are already registered — it ensures full coverage.
     *
     * @param basePackage The root package to begin scanning (e.g., com.tjxjnoobie.api)
     */
    @Override
    public void scanAndRegisterInjectableClasses(String basePackage) {
        Log.info("[AUTO-BIND] Scanning classpath for injectable classes in package: " + basePackage);

        try {
            Set<Class<?>> allClasses = findInjectableClasses(basePackage);

            if (allClasses.isEmpty()) {
                Log.info("[AUTO-BIND] No injectable classes discovered in package: " + basePackage);
                return;
            }

            for (Class<?> clazz : allClasses) {
                if (!clazz.isInterface() && !dependencyMap.isRegistered(clazz)) {
                    try {
                        // Instantiate the class to register
                        Object instance = clazz.getDeclaredConstructor().newInstance();

                        // Register with dependency map
                        dependencyMap.registerDependency(clazz, instance);

                        Log.info("[AUTO-BIND] Registered: " + clazz.getSimpleName());

                        // Optionally assign role based on dependencies
                        Set<Class<?>> deps = analyzeClassDependencies(clazz);
                        DependencyRole role = determineRoleFromDependencies(deps);
                        IDependencyMetaData meta = dependencyMap.getDependency(clazz);
                        if (meta != null) {
                            meta.setRole(role);
                            meta.setDependencies(deps);
                            Log.info("[AUTO-BIND] Assigned role " + role + " to " + clazz.getSimpleName());
                        }
                    } catch (Exception e) {
                        Log.warn("[AUTO-BIND] Could not instantiate class: " + clazz.getSimpleName() + " - Error: " + e.getMessage());
                    }
                } else {
                    Log.warn("[AUTO-BIND] Skipping already registered class: " + clazz.getSimpleName());
                }
            }

        } catch (Exception e) {
            Log.error("[AUTO-BIND] Failed to scan or register classes in package " + basePackage + ": " + e.getMessage());
        }
    }

    /**
     * Finds all classes in a given package that are potential candidates for dependency injection.
     * This includes classes with @Inject fields, methods, or interfaces.
     *
     * @param basePackage The root package to search (e.g., com.tjxjnoobie.api)
     * @return A set of classes found
     */
    //THIS ONE
    @Override
    public Set<Class<?>> findInjectableClasses(String basePackage) {
        Set<Class<?>> results = new HashSet<>();
        if (basePackage == null || basePackage.isBlank()) {
            return results;
        }

        String path = basePackage.replace('.', '/');
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resources = loader.getResources(path);
            if (!resources.hasMoreElements()) {
                Log.warn("[AUTO-BIND] No classpath entries found for package: " + basePackage);
            }

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();

                if ("file".equals(protocol)) {
                    File dir = new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
                    if (dir.exists() && dir.isDirectory()) {
                        walkDirectoryForInjectables(dir, basePackage, results, loader);
                    }
                } else if ("jar".equals(protocol)) {
                    scanJarResource(resource, path, results, loader);
                } else {
                    scanUnknownResource(resource, path, results, loader);
                }
            }
        } catch (IOException e) {
            Log.warn("[AUTO-BIND] Failed to access package " + path + ": " + e.getMessage());
        }

        return results;
    }


    /**
     * Injects fields for ALL registered classes in the DependencyMap.
     * This ensures every class gets its dependencies injected from the map.
     */
    @Override
    public void injectAllRegisteredClasses() {
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
     * Binds fields for ALL registered classes in the DependencyMap.
     * This ensures every class gets its fields bound aggressively from the map.
     */
    public void bindFieldsForAllRegisteredClasses() {
        // Get all registered metadata
        Collection<IDependencyMetaData> allMetadata = dependencyMap.getDependencyMapValues();

        Log.info("[AUTO-BIND] Binding fields for " + allMetadata.size() + " registered classes");

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

            // Bind fields for this instance
            bindFieldsFromTarget(instance, clazz);
        }
    }

    /**
     * Injects all @Inject fields for a specific instance using the DependencyMap.
     */
    @Override
    public void injectFieldsForInstance(Object instance, Class<?> clazz) {
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
                        Log.info("[AUTO-BIND] Injected " + fieldType.getSimpleName() + " into " + clazz.getSimpleName() + "." + field.getName());
                    } catch (Exception e) {
                        Log.error("[AUTO-BIND] Failed to inject " + fieldType.getSimpleName() + " into " + clazz.getSimpleName() + "." + field.getName() + ": " + e.getMessage());
                    }
                } else {
                    Inject inject = field.getAnnotation(Inject.class);
                    boolean optional = inject != null && inject.optional();
                    if (!optional) {
                        Log.warn("[AUTO-BIND] No instance found for required field: " + clazz.getSimpleName() + "." + field.getName() + " (" + fieldType.getSimpleName() + ")");
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
    @Override
    public void scanAndRegisterInjectableClasses(Class<?> targetClass) {
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
    @Override
    public Map<Class<?>, DependencyRole> analyzeAndAssignRoles(Set<Class<?>> injectableClasses) {
        Map<Class<?>, DependencyRole> roleMap = new HashMap<>();

        for (Class<?> clazz : injectableClasses) {
            if (!clazz.isInterface()) {
                Set<Class<?>> dependencies = analyzeClassDependencies(clazz);
                DependencyRole role = determineRoleFromDependencies(dependencies);
                roleMap.put(clazz, role);
                Log.info("[AUTO-BIND] Analyzed " + clazz.getSimpleName() + " -> Role: " + role + " (dependencies: " + dependencies.size() + ")");
            }
        }

        return roleMap;
    }

    /**
     * Registers all concrete classes with their pre-assigned roles.
     */
    @Override
    public void registerClassesWithRoles(Set<Class<?>> injectableClasses, Map<Class<?>, DependencyRole> roleMap) {
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
    @Override
    public void registerInterfaceImplementations(String basePackage, Set<Class<?>> injectableClasses) {
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
                        Log.info("[AUTO-BIND] Bound interface " + interfaceClass.getSimpleName() + " -> " + defaultImpl.getSimpleName() + " (global registry)");

                        // Log all implementations found
                        if (interfaceImpls.size() > 1) {
                            Log.info("[AUTO-BIND] Found " + interfaceImpls.size() + " implementations for " + interfaceClass.getSimpleName() + ": " + interfaceImpls.stream().map(Class::getSimpleName).reduce((a, b) -> a + ", " + b).orElse(""));
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
    @Override
    public void assignRolesToClasses(Set<Class<?>> implementations) {
        for (Class<?> clazz : implementations) {
            try {
                Set<Class<?>> dependencies = analyzeClassDependencies(clazz);
                DependencyRole role = determineRoleFromDependencies(dependencies);

                // Get or create metadata
                IDependencyMetaData meta = dependencyMap.getDependency(clazz);
                if (meta != null) {
                    meta.setRole(role);
                    meta.setDependencies(dependencies);
                    Log.info("[AUTO-BIND] Assigned role " + role + " to " + clazz.getSimpleName() + " (dependencies: " + dependencies.size() + ")");
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
    @Override
    public Set<Class<?>> analyzeClassDependencies(Class<?> clazz) {
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
    @Override
    public DependencyRole determineRoleFromDependencies(Set<Class<?>> dependencies) {
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
     * Recursively walks directory to find injectable classes.
     * TODO: Wire all DI classes with @Injectable annotation before re-enabling the annotation check
     * Currently discovers ALL classes and interfaces in the project package
     */
    @Override
    public void walkDirectoryForInjectables(File dir, String packageName, Set<Class<?>> results) {
        walkDirectoryForInjectables(dir, packageName, results, Thread.currentThread().getContextClassLoader());
    }

    private void walkDirectoryForInjectables(File dir, String packageName, Set<Class<?>> results, ClassLoader loader) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                walkDirectoryForInjectables(file, packageName + "." + file.getName(), results, loader);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replace(".class", "");
                handleDiscoveredClass(className, results, loader);
            }
        }
    }

    private void scanJarResource(URL resource, String packagePath, Set<Class<?>> results, ClassLoader loader) {
        try {
            URLConnection connection = resource.openConnection();
            if (connection instanceof JarURLConnection jarConnection) {
                try (JarFile jarFile = jarConnection.getJarFile()) {
                    scanJarEntries(jarFile, packagePath, results, loader);
                }
                return;
            }
        } catch (IOException e) {
            Log.warn("[AUTO-BIND] Failed to open jar resource " + resource + ": " + e.getMessage());
            return;
        }

        scanUnknownResource(resource, packagePath, results, loader);
    }

    private void scanUnknownResource(URL resource, String packagePath, Set<Class<?>> results, ClassLoader loader) {
        String file = resource.getFile();
        if (file == null) {
            return;
        }

        int separator = file.indexOf('!');
        if (separator == -1) {
            return;
        }

        String jarPath = file.substring(0, separator);
        if (jarPath.startsWith("file:")) {
            jarPath = jarPath.substring("file:".length());
        }

        try (JarFile jarFile = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
            scanJarEntries(jarFile, packagePath, results, loader);
        } catch (IOException e) {
            Log.warn("[AUTO-BIND] Failed to scan jar entries from " + jarPath + ": " + e.getMessage());
        }
    }

    private void scanJarEntries(JarFile jarFile, String packagePath, Set<Class<?>> results, ClassLoader loader) throws IOException {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                continue;
            }

            String name = entry.getName();
            if (!name.endsWith(".class") || !name.startsWith(packagePath)) {
                continue;
            }

            String className = name.replace('/', '.').replace(".class", "");
            handleDiscoveredClass(className, results, loader);
        }
    }

    private void handleDiscoveredClass(String className, Set<Class<?>> results, ClassLoader loader) {
        if (className == null || className.isBlank()) {
            return;
        }

        if (className.endsWith("package-info") || className.endsWith("module-info")) {
            return;
        }

        try {
            Class<?> clazz = Class.forName(className, false, loader);
            if (!shouldConsiderClass(clazz)) {
                return;
            }
            results.add(clazz);
        } catch (ClassNotFoundException | NoClassDefFoundError | UnsupportedClassVersionError ignored) {
            // Skip classes that cannot be loaded in the current runtime
        }
    }

    private boolean shouldConsiderClass(Class<?> clazz) {
        if (clazz == null || clazz.isSynthetic()) {
            return false;
        }

        Package pkg = clazz.getPackage();
        String packageName = pkg != null ? pkg.getName() : "";
        return shouldConsiderPackage(packageName);
    }



    /**
     * Binds fields from the target object using the now-populated dependency map.
     */
    public void bindFieldsFromTarget(Object target, Class<?> targetClass) {
        Log.info("[AUTO-BIND] Binding fields for: " + targetClass.getSimpleName());

        Class<?> clazz = targetClass;
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                bindField(target, field);
            }
            clazz = clazz.getSuperclass();
        }
    }

    private void bindTypeInternal(Class<?> type, String origin) throws Exception {
        if (type == null) {
            return;
        }

        if (!isEligibleForInjection(type)) {
            Log.info("[AUTO-BIND] Skipping ineligible type: " + type.getName());
            return;
        }

        IDependencyMetaData existingMeta = dependencyMap.getDependency(type);
        Object existingInstance = existingMeta != null ? dependencyMap.ensureAndGetInstance(existingMeta) : null;
        if (existingInstance != null) {
            logTypeBinding(type, existingInstance, origin + ", already registered");
            return;
        }

        IDependencyMetaData compatibleMeta = dependencyMap.findByAssignableType(type);
        Object compatibleInstance = compatibleMeta != null ? dependencyMap.ensureAndGetInstance(compatibleMeta) : null;
        if (compatibleInstance != null) {
            dependencyMap.registerDependency((Class<Object>) type, compatibleInstance);
            String source = compatibleMeta.getDependencyClass() != null
                    ? compatibleMeta.getDependencyClass().getSimpleName()
                    : compatibleInstance.getClass().getSimpleName();
            logTypeBinding(type, compatibleInstance, origin + ", matched " + source);
            return;
        }

        if (!type.isInterface()) {
            Object instance = type.getDeclaredConstructor().newInstance();
            dependencyMap.registerDependency((Class<Object>) type, instance);
            logTypeBinding(type, instance, origin + ", instantiated concrete type");
            return;
        }

        if (Arrays.stream(type.getMethods()).anyMatch(Method::isDefault)) {
            Object proxy = createSelfProxy(type);
            dependencyMap.registerDependency((Class<Object>) type, proxy);
            logTypeBinding(type, proxy, origin + ", self proxy");
            return;
        }

        Set<Class<?>> impls = findImplementations(type, type.getPackage() != null ? type.getPackage().getName() : "");
        if (!impls.isEmpty()) {
            Class<?> implClass = impls.iterator().next();
            Object instance = implClass.getDeclaredConstructor().newInstance();
            dependencyMap.registerDependency((Class<Object>) type, instance);
            logTypeBinding(type, instance, origin + ", discovered " + implClass.getSimpleName());
            return;
        }

        Object placeholder = createPlaceholderProxy(type);
        dependencyMap.registerDependency((Class<Object>) type, placeholder);
        logTypeBinding(type, placeholder, origin + ", placeholder proxy");
    }

    // --- Type-level binding (Class<?>) ---
    @Override
    public void bindType(Class<?> type) throws Exception {
        bindTypeInternal(type, "manual bind request");
    }

    // --- Field-level binding (instance) ---
    @Override
    public void bindField(Object target, Field field) {
        try {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();

            if (!isEligibleForInjection(fieldType)) {
                return;
            }

            boolean isStatic = Modifier.isStatic(field.getModifiers());
            Object currentValue = isStatic ? field.get(null) : field.get(target);
            if (currentValue != null) {
                return;
            }

            IDependencyMetaData exactMeta = dependencyMap.getDependency(fieldType);
            Object existingInstance = exactMeta != null ? dependencyMap.ensureAndGetInstance(exactMeta) : null;
            if (existingInstance != null) {
                assignFieldValue(target, field, existingInstance, "existing registration");
                return;
            }

            IDependencyMetaData compatibleMeta = dependencyMap.findByAssignableType(fieldType);
            Object compatibleInstance = compatibleMeta != null ? dependencyMap.ensureAndGetInstance(compatibleMeta) : null;
            if (compatibleInstance != null) {
                if (!dependencyMap.isRegistered(fieldType)) {
                    dependencyMap.registerDependency((Class<Object>) fieldType, compatibleInstance);
                }
                String source = compatibleMeta.getDependencyClass() != null
                        ? compatibleMeta.getDependencyClass().getSimpleName()
                        : compatibleInstance.getClass().getSimpleName();
                assignFieldValue(target, field, compatibleInstance, "matched " + source);
                return;
            }

            if (!fieldType.isInterface()) {
                Object instance = fieldType.getDeclaredConstructor().newInstance();
                dependencyMap.registerDependency((Class<Object>) fieldType, instance);
                assignFieldValue(target, field, instance, "instantiated " + fieldType.getSimpleName());
                return;
            }

            if (Arrays.stream(fieldType.getMethods()).anyMatch(Method::isDefault)) {
                Object proxy = createSelfProxy(fieldType);
                dependencyMap.registerDependency((Class<Object>) fieldType, proxy);
                assignFieldValue(target, field, proxy, "self proxy");
                return;
            }

            Set<Class<?>> impls = findImplementations(fieldType, fieldType.getPackage() != null ? fieldType.getPackage().getName() : "");
            if (!impls.isEmpty()) {
                Class<?> implClass = impls.iterator().next();
                Object instance = implClass.getDeclaredConstructor().newInstance();
                dependencyMap.registerDependency((Class<Object>) fieldType, instance);
                assignFieldValue(target, field, instance, "discovered " + implClass.getSimpleName());
                return;
            }

            Object placeholder = createPlaceholderProxy(fieldType);
            dependencyMap.registerDependency((Class<Object>) fieldType, placeholder);
            assignFieldValue(target, field, placeholder, "placeholder proxy");
        } catch (InaccessibleObjectException ignored) {
            //TODO: Add 'verbose' logging option
            // Log.info("Skipping inaccessible field: " + field.getName());
        } catch (Exception e) {
            Log.error("Error binding field " + field.getName() + ": " + e.getMessage());
        }
    }

    // --- Self-proxy creation helper ---
    @Override
    public Object createSelfProxy(Class<?> iface) throws Exception {
        return Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[]{iface}, (proxyObj, method, args) -> {
            if (method.isDefault()) {
                try {
                    MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(iface, MethodHandles.lookup());
                    return lookup.unreflectSpecial(method, iface).bindTo(proxyObj).invokeWithArguments(args);
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
    @Override

    public Object createPlaceholderProxy(Class<?> iface) {
        return Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[]{iface}, (p, m, a) -> {
            Log.warn("Called " + m.getName() + " on unimplemented interface: " + iface.getSimpleName());
            return null;
        });
    }


    // tryInvokeBuildMethod removed: contexts should use explicit registration APIs.
    @Override
    public Set<Class<?>> findImplementations(Class<?> interfaceType, String basePackage) {
        Set<Class<?>> implementations = new HashSet<>();
        if (interfaceType == null) {
            return implementations;
        }

        Set<String> searchPackages = collectImplementationSearchPackages(interfaceType, basePackage);
        for (String scanPackage : searchPackages) {
            Set<Class<?>> candidates = findInjectableClasses(scanPackage);
            for (Class<?> candidate : candidates) {
                if (candidate == null || candidate.isInterface()) {
                    continue;
                }

                if (interfaceType.isAssignableFrom(candidate)) {
                    implementations.add(candidate);
                    Log.info("Found concrete class: " + candidate.getSimpleName() + " implements " + interfaceType.getSimpleName() + "");
                }
            }
        }

        return implementations;
    }

    private Set<String> collectImplementationSearchPackages(Class<?> interfaceType, String basePackage) {
        LinkedHashSet<String> packages = new LinkedHashSet<>();

        if (basePackage != null && !basePackage.isBlank()) {
            packages.add(basePackage);
        }

        addTypeHierarchyPackages(interfaceType, packages);

        packages.addAll(getAllowedPackagePrefixes());
        packages.addAll(SCANNED_PACKAGES);

        packages.removeIf(pkg -> pkg == null || pkg.isBlank() || !shouldConsiderPackage(pkg));

        return packages;
    }

    @Override
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

