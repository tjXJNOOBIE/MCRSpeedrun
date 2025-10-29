/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.injection.helpers;

import com.tjxjnoobie.api.dependency.annotations.DelegatesToInterface;
import com.tjxjnoobie.api.dependency.contexts.abstracts.AbstractContext;
import com.tjxjnoobie.api.dependency.injection.enums.LifecycleType;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.console.style.LogColor;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    // private final Set<String> SCANNED_PACKAGES = ConcurrentHashMap.newKeySet();
  //  private final Set<Class<?>> AUTO_BOUND_TARGETS = ConcurrentHashMap.newKeySet();
    public final Set<Class<?>> LOADED_CLASSES = ConcurrentHashMap.newKeySet();

   // public final Queue<Class<?>> preConstructRetryQueue = new ConcurrentLinkedQueue<>();




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



    /**
     * Registers dependencies declared via @DelegatesToInterface annotations.
     * <p>
     * This scanner respects configured package allow/exclude lists and gracefully skips invalid entries.
     * It ensures the DependencyMap is populated with interface keys and concrete instances sourced from
     * the annotation metadata.
     * </p>
     */
    @Override
    public void registerDependenciesViaAnnotation(Set<Class<?>> classesToScan) {
        Log.info("[DI-Helper] " + LogColor.YELLOW + "Starting dual-phase DI registration" + LogColor.RESET);

        if (classesToScan == null || classesToScan.isEmpty()) {
            Log.warn("[DI-Helper] " + LogColor.YELLOW + "SKIP" + LogColor.RESET + " No classes to scan");
            return;
        }

        int registeredCount = 0;

        // ===== PHASE 1: CONCRETE ANNOTATION SCAN =====
        for (Class<?> candidate : classesToScan) {
            if (candidate == null || candidate.isInterface() || Modifier.isAbstract(candidate.getModifiers()))
                continue;

            DelegatesToInterface annotation = candidate.getAnnotation(DelegatesToInterface.class);
            if (annotation == null) continue;

            Class<?> targetInterface = annotation.value();
            if (targetInterface == null || targetInterface == Void.class || !targetInterface.isInterface()) {
                Log.warn("[DI-Helper] " + LogColor.YELLOW + "SKIP" + LogColor.RESET +
                        " Invalid @DelegatesToInterface on " + candidate.getName());
                continue;
            }

            if (!targetInterface.isAssignableFrom(candidate)) {
                Log.warn("[DI-Helper] " + LogColor.YELLOW + "SKIP" + LogColor.RESET +
                        " " + candidate.getSimpleName() + " does not implement " + targetInterface.getSimpleName());
                continue;
            }

            if (dependencyMap.isRegistered(targetInterface, false)) {
                Log.warn("[DI-Helper] " + LogColor.YELLOW + "SKIP" + LogColor.RESET +
                        " Interface " + targetInterface.getName() + " already registered");
                continue;
            }

            dependencyMap.registerDependency(targetInterface, candidate);
            Log.success("[DI-Helper] " + LogColor.GREEN + "REGISTERED " + LogColor.RESET +
                    targetInterface.getSimpleName() + " -> " + candidate.getSimpleName());
            registeredCount++;
        }

        // ===== PHASE 2: INTERFACE EXTENSION RECURSION =====
        Set<Class<?>> registeredInterfaces = new LinkedHashSet<>(dependencyMap.getDependencies());
        for (Class<?> iface : registeredInterfaces) {
            if (!iface.isInterface()) continue;
            recursivelyLinkExtendedInterfaces(iface, 0);
        }

        Log.info("[DI-Helper] " + LogColor.YELLOW + "SUMMARY" + LogColor.RESET +
                " Total registered: " + registeredCount + ", including extended interface chains");
    }


    /**
     * Recursively links extended interfaces and finds valid annotation paths.
     */
    @Override
    public void recursivelyLinkExtendedInterfaces(Class<?> iface, int depth) {
        if (iface == null || iface == Object.class) return;

        for (Class<?> subIface : getAllLoadedClasses("com.tjxjnoobie")) {
            if (!subIface.isInterface() || subIface.equals(iface)) continue;

            // If this interface extends our current one
            if (iface.isAssignableFrom(subIface)) {
                // Attempt to find a valid annotated implementation for this subinterface
                Set<Class<?>> validImpl = findMatchingConcreteForInterface(subIface);
                if (validImpl != null) {
                    if (!dependencyMap.isRegistered(subIface, false)) {
                        dependencyMap.registerDependency(subIface, validImpl);
                        Log.success("[DI-Helper] " + LogColor.GREEN + "REGISTERED " + LogColor.RESET +
                                subIface.getSimpleName() + " -> " + validImpl.getClass().getSimpleName() +
                                " (via parent " + iface.getSimpleName() + ")");
                    }
                } else {
                    // Recurse deeper to try superinterfaces
                    recursivelyLinkExtendedInterfaces(subIface, depth + 1);
                }
            }
        }
    }


    /**
     * Finds a concrete class implementing the given interface that has a matching @DelegatesToInterface annotation.
     */
    @Override
    public Set<Class<?>> findMatchingConcreteForInterface(Class<?> subIface) {
        Set<Class<?>> matches = new LinkedHashSet<>();

        for (Class<?> clazz : getAllLoadedClasses("com.tjxjnoobie")) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) continue;

            if (subIface.isAssignableFrom(clazz)) {
                DelegatesToInterface anno = clazz.getAnnotation(DelegatesToInterface.class);
                if (anno != null && anno.value().equals(subIface)) {
                    Log.success("[DI-Scan] Found concrete " + clazz.getSimpleName() +
                            " matching " + subIface.getSimpleName());
                    matches.add(clazz);
                }
            }
        }

        return matches;
    }

    public Set<Class<?>> getAllLoadedClasses(String basePackage) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader instanceof URLClassLoader urlLoader) {
            for (URL url : urlLoader.getURLs()) {
                Log.info("[DI-Scan] " + LogColor.YELLOW + "URL: " + url.toExternalForm());
                try {
                    File file = new File(url.toURI());
                    if (file.isDirectory()) {
                        scanDirectory("", file, loader);
                    } else if (file.getName().endsWith(".jar")) {
                        scanJar(file, basePackage, loader);
                    }
                } catch (Throwable ignored) { }
            }
        } else {
            Log.warn("[DI-Scan] " + LogColor.YELLOW + "Non-URL classloader, using fallback package scan");
            LOADED_CLASSES.addAll(scanFromBasePackage("com.tjxjnoobie", loader));
        }

        Log.info("[DI-Scan] " + LogColor.GRAY + "Discovered " + LOADED_CLASSES.size() + " classes from classloader");
        return LOADED_CLASSES;
    }
    @Override
    public void scanDirectory(String pkg, File dir, ClassLoader loader) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                scanDirectory(pkg + (pkg.isEmpty() ? "" : ".") + file.getName(), file, loader);
            } else if (file.getName().endsWith(".class")) {
                String name = pkg + (pkg.isEmpty() ? "" : ".") + file.getName().replace(".class", "");
                try {
                    LOADED_CLASSES.add(Class.forName(name, false, loader));
                } catch (Throwable ignored) { }
            }
        }
    }


    /**
     * Scans JAR files for .class entries under the given package path.
     */
    /**
     * Scans JAR files for .class entries under the given package path.
     */
    @Override
    public void scanJar(File jarFile, String basePackage, ClassLoader loader) {
        if (jarFile == null || !jarFile.exists()) return;
        String prefix = basePackage.replace('.', '/');
        Log.info("[DI-Scan] Scanning JAR " + jarFile.getName() + " for classes under " + prefix);
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                Log.info("[DI-Scan] Scanning JAR " + jarFile.getName() + " for class " + entry.getName());
                if (entry.getName().startsWith(prefix) && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.').replace(".class", "");

                    // Extract package name for filtering
                    String packageName = className.contains(".")
                            ? className.substring(0, className.lastIndexOf('.'))
                            : "";

                    // Apply package filtering using InjectionConfig
                    if (!shouldConsiderPackage(packageName)) {
                        Log.info("[DI-Scan] " + LogColor.YELLOW + "EXCLUDED" + LogColor.RESET
                                + " class " + className + " (filtered package: " + packageName + ")");
                        continue;
                    }

                    try {
                        LOADED_CLASSES.add(Class.forName(className, false, loader));
                        Log.success("[DI-Scan] Found class " + className);
                    } catch (Throwable ignored) { }
                }
            }
        } catch (Throwable e) {
            Log.error("[DI-Scan] Failed to scan JAR " + jarFile.getName());
            Log.exception(e);
        }
    }

    /**
     * Scans all .class resources under the given base package using the provided ClassLoader.
     * Works for both directories and JAR resources.
     *
     * @param basePackage The base package to scan (e.g., "com.tjxjnoobie")
     * @param loader The ClassLoader to use
     * @return A set of discovered classes
     */
    @Override
    public Set<Class<?>> scanFromBasePackage(String basePackage, ClassLoader loader) {

        if (basePackage == null || basePackage.isBlank()) return LOADED_CLASSES;
        String path = basePackage.replace('.', '/');

        try {
            Enumeration<URL> resources = loader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();

                if (protocol.equals("file")) {
                    File directory = new File(resource.toURI());
                    scanDirectory(basePackage, directory, loader);
                } else if (protocol.equals("jar")) {
                    String jarPath = resource.getPath();
                    if (jarPath.startsWith("file:")) jarPath = jarPath.substring(5, jarPath.indexOf("!"));
                    scanJar(new File(URLDecoder.decode(jarPath, StandardCharsets.UTF_8)), basePackage, loader);
                } else {
                    Log.warn("[DI-Scan] " + LogColor.YELLOW + "Unsupported protocol: " + protocol +
                            " for " + basePackage);
                }
            }
        } catch (Throwable e) {
            Log.error("[DI-Scan] Failed to scan package " + basePackage);
            Log.exception(e);
        }

        Log.info("[DI-Scan] " + LogColor.GRAY + "Discovered " + LOADED_CLASSES.size() +
                " classes under base package " + basePackage);
        return LOADED_CLASSES;
    }
    @Override
    public EnumMap<LifecycleType, Method> detectLifecycleForClass(Class<?> clazz) {
        EnumMap<LifecycleType, Method> map = new EnumMap<>(LifecycleType.class);
        for (LifecycleType lifecycle : LifecycleType.values()) {
            lifecycle.findIn(clazz).ifPresent(m -> map.put(lifecycle, m));
        }
        return map;
    }



    /**
     * Gets all instances from the map.
     *
     * @return Collection of all dependency instances
     */
    @Override
    //TODO: Move to DependencyMetaData
    public List<Object> getAllInstances() {
        return dependencyMap.getAllInstances();
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
    //TODO: Move method to DependencyMap
    @Override
    public void registerImportant(Class<?> clazz, Object instance, int priority) {
        registerDependency(clazz, instance);

        getMetaData(clazz).setDependencyClass(clazz);
        getMetaData(clazz).setPriority(priority);

    }
    //
    // TODO: Lots of unneeded/redundnat methods here, go through to keep useful ones

    //    /**
//     * Finds a field in a target class that matches the dependency class.
//     */
//    @Override
//    public Field findField(Class<?> target, Class<?> depClass) {
//        for (Field field : target.getDeclaredFields()) {
//            if (field.getType().equals(depClass)) {
//                return field;
//            }
//        }
//        return null;
//    }
    //    /**
//     * PreConstruct initialization method with highest priority (0).
//     * This ensures the DI system is initialized before any other components.
//     * Called automatically after dependency injection but before other classes.
//     */
//    @PreConstruct(priority = 0)
//    @Override
//    public void initializeDependencySystem() {
//        Log.info("[DI-Helper] ===== Initializing Dependency Injection System =====");
//        Log.info("[DI-Helper] Injectable classes count: " + dependencyGraph.size());
//
//        try {
//            // Build the dependency graph first
//            if (!dependencyGraph.isEmpty()) {
//                Log.info("[DI-Helper] Building dependency graph...");
//                buildDependencyGraph();
//                Log.info("[DI-Helper] Computing depth levels...");
//                computeDepthLevels();
//                Log.info("[DI-Helper] Dependency graph built successfully");
//            }
//
//            // Initialize the injection map
//            Log.info("[DI-Helper] Injection map initialized");
//
//            // Log summary
//            Log.success("[DI-Helper] Dependency Injection System initialized successfully");
//            Log.info("[DI-Helper] Graph nodes: " + dependencyGraph.size());
//            Log.info("[DI-Helper] Registered dependencies: " + dependencyMap.getDependencyMapSize());
//
//        } catch (Exception e) {
//            Log.critical("[DI-Helper] Failed to initialize DI system: " + e.getMessage());
//            throw new RuntimeException("DI System initialization failed", e);
//        }
//    }
    // Use injectionConfig methods in scanDirectoryForClasses
//    @Override
//    public Set<Class<?>> scanDirectoryForClasses() {
//        Set<Class<?>> discovered = new LinkedHashSet<>();
//        Set<String> packagesToScan = collectPackages(null);
//
//        if (packagesToScan.isEmpty()) {
//            Package currentPackage = getClass().getPackage();
//            if (currentPackage != null && currentPackage.getName() != null) {
//                packagesToScan.add(currentPackage.getName());
//            }
//        }
//
//        for (String basePackage : packagesToScan) {
//            if (basePackage == null || basePackage.isBlank()) {
//                continue;
//            }
//
//            Log.info("[DI-Helper] " + LogColor.YELLOW + "SCAN" + LogColor.RESET
//                    + " Searching package: " + basePackage);
//            Set<Class<?>> packageClasses = findInjectableClasses(basePackage);
//
//            // Apply package exclusion filtering using InjectionConfig
//            Set<Class<?>> filteredClasses = new LinkedHashSet<>();
//            for (Class<?> clazz : packageClasses) {
//                if (clazz != null && isEligibleForInjection(clazz)) {
//                    filteredClasses.add(clazz);
//                } else if (clazz != null) {
//                    String packageName = clazz.getPackage() != null ? clazz.getPackage().getName() : "";
//                    Log.info("[DI-Helper] " + LogColor.YELLOW + "EXCLUDED" + LogColor.RESET
//                            + " Class " + clazz.getSimpleName() + " from package " + packageName);
//                }
//            }
//
//            discovered.addAll(filteredClasses);
//        }
//
//        Log.info("[DI-Helper] " + LogColor.YELLOW + "SUMMARY" + LogColor.RESET
//                + " scanDirectoryForClasses located " + discovered.size() + " candidates");
//
//        return discovered;
//    }
//    /**
//     * Collects package prefixes that should be scanned for dependency injection targets.
//     *
//     * @param targetClass the class requesting auto-bind operations
//     * @return a set of package prefixes to scan
//     */
//    private Set<String> collectPackages(Class<?> targetClass) {
//        LinkedHashSet<String> packages = new LinkedHashSet<>();
//
//        if (targetClass != null) {
//            collectPackagesFromTypeHierarchy(targetClass, packages);
//            collectPackagesViaAnnotation(targetClass, packages);
//            Log.success("[DI-Helper] " + LogColor.GREEN + "Collected packages for " + targetClass.getSimpleName() + ": " + packages.size());
//        } else{
//            Log.warn("[DI-Helper] " + LogColor.YELLOW + "No target class provided to scan for dependencies");
//        }
//
//         packages.addAll(getAllowedPackagePrefixes());
//         packages.removeIf(pkg -> pkg == null || pkg.isBlank() || !shouldConsiderPackage(pkg));
//
//        return packages;
//    }
//
//    private void collectPackagesFromTypeHierarchy(Class<?> type, Set<String> packages) {
//        if (type == null || type == Object.class) {
//            return;
//        }
//
//        addPackageCandidate(type.getPackage() != null ? type.getPackage().getName() : null, packages);
//
//        for (Class<?> iface : type.getInterfaces()) {
//            collectPackagesFromTypeHierarchy(iface, packages);
//        }
//
//        collectPackagesFromTypeHierarchy(type.getSuperclass(), packages);
//    }

//    private void collectPackagesViaAnnotation(Class<?> type, Set<String> packages) {
//        Class<?> current = type;
//        while (current != null && current != Object.class) {
//                if (type.isAnnotationPresent(DelegatesToInterface.class)) {
//                    addTypeHierarchyPackages(type, packages);
//                }
//
//
////            for (Method method : current.getDeclaredMethods()) {
////                if (method.isAnnotationPresent(DelegatesToInterface.class)) {
////                    for (Class<?> paramType : method.getParameterTypes()) {
////                        addTypeHierarchyPackages(paramType, packages);
////                    }
////                    Type[] genericParams = method.getGenericParameterTypes();
////                    for (Type genericType : genericParams) {
////                        addGenericTypePackages(genericType, packages);
////                    }
////                }
////            }
//
//            current = current.getSuperclass();
//        }
//    }

//    private void addTypeHierarchyPackages(Class<?> type, Set<String> packages) {
//        if (type == null) {
//            return;
//        }
//
//        if (type.isArray()) {
//            addTypeHierarchyPackages(type.getComponentType(), packages);
//            return;
//        }
//
//        if (type.isPrimitive()) {
//            return;
//        }
//
//        addPackageCandidate(type.getPackage() != null ? type.getPackage().getName() : null, packages);
//
//        for (Class<?> iface : type.getInterfaces()) {
//            addTypeHierarchyPackages(iface, packages);
//        }
//
//        addTypeHierarchyPackages(type.getSuperclass(), packages);
//    }

//    private void addGenericTypePackages(Type type, Set<String> packages) {
//        if (type instanceof ParameterizedType parameterizedType) {
//            for (Type arg : parameterizedType.getActualTypeArguments()) {
//                if (arg instanceof Class<?> clazz) {
//                    addTypeHierarchyPackages(clazz, packages);
//                }
//            }
//        }
//    }

//    private void addPackageCandidate(String packageName, Set<String> packages) {
//        if (packageName == null || packageName.isBlank()) {
//            return;
//        }
//
//        if (!shouldConsiderPackage(packageName)) {
//            return;
//        }
//
//        packages.add(packageName);
//    }
    /**
     * Initializes the dependency graph, resolves injection order, and processes pending hooks.
     *
     * @throws Exception when graph construction or injection fails
     */
    //TODO: Investigate method responsibility
//    public void initialize() throws Exception {
//
//        Set<Class<?>> scannedClasses = scanDirectoryForClasses();
//        registerDependenciesViaAnnotation(scannedClasses);
//
//        // Step 1: Build the dependency graph of registered components.
//        buildDependencyGraph();
//        // Step 2: Compute depth levels to determine initialization order.
//        computeDepthLevels();
//        // Step 3: Perform dependency injection for all registered metadata entries.
//        injectAll();
//        // Step 4: Process any deferred @PreConstruct executions.
//        processPreConstructRetryQueue();
//    }

    // ===== Helper methods for DI System ===== \\


//    /**
//     * Injects a value into a field with error handling.
//     */
//    @Override
//    public void injectFieldValue(Object target, Field field, Object value, boolean optional, boolean isStatic, Class<?> depClass, Class<?> clazz) {
//        field.setAccessible(true);
//        try {
//            if (value != null) {
//                if (isStatic) field.set(null, value);
//                else field.set(target, value);
//            } else if (!optional) {
//                Log.error("[DI] ❌ Missing required dependency: " + depClass.getSimpleName() + " | Needed by: " + clazz.getSimpleName() + " | Field: " + field.getName() + " | Static: " + isStatic);
//            }
//        } catch (Exception e) {
//            Log.error("[DI] Failed injecting " + depClass.getName() + " into " + clazz.getName());
//            Log.exception(e);
//        }
//    }
//
//    /**
//     * Injects a value via method invocation with error handling.
//     */
//    @Override
//    public void injectMethodValue(Object target, Method method, Object value, boolean optional, Class<?> depClass, Class<?> clazz) {
//        try {
//            if (value != null) method.invoke(target, value);
//            else if (!optional)
//                Log.error("[DI] ❌ Missing required dependency: " + depClass.getSimpleName() + " | Needed by: " + clazz.getSimpleName() + " | Method: " + method.getName());
//        } catch (Exception e) {
//            Log.error("[DI] Failed injecting via method " + method.getName() + " in " + clazz.getName());
//            Log.exception(e);
//        }
//    }
//
//    /**
//     * Injects static fields of a class using the current owner's dependency map.
//     * This is useful for utility/manager classes with static fields.
//     */
//    @Override
//    public void injectStaticFields(Class<?> clazz) {
//        injectStaticFields(clazz, false);
//    }
//
//    public void injectStaticFields(Class<?> clazz, boolean autoInject) {
//        if (clazz == null) {
//            Log.warn("[DI] Cannot inject static fields of null class");
//            return;
//        }
//        // TODO: Wire all DI classes with @Injectable annotation before re-enabling this check
//        // Currently commented out to allow injection without @Injectable requirement
//        // if (isRequireInjectableAnnotation() && !clazz.isAnnotationPresent(Injectable.class)) {
//        //     Log.info("[DI] Skipping static injection for non-@Injectable class: " + clazz.getName());
//        //     return;
//        // }
//        injectFieldsForClass(null, clazz, autoInject, true, false, null);
//    }

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
     * Determines the role of a component based on its dependencies.
     */
    @Override
    public DependencyRole determineRole(Set<Class<?>> dependencies) {
        if (dependencies.isEmpty()) return DependencyRole.BASE;
        if (dependencies.size() == 1) return DependencyRole.INTERMEDIATE;
        return DependencyRole.ISOLATED;
    }

    // ===== Graph-based dependency analysis =====
//    public void buildDependencyGraph() {
//        for (Class<?> clazz : dependencyGraph.keySet()) {
//            Set<Class<?>> dependencies = new HashSet<>();
//            // Fields
//            for (Field field : clazz.getDeclaredFields()) {
//                if (field.isAnnotationPresent(Inject.class) && dependencyGraph.containsKey(field.getType())) {
//                    dependencies.add(field.getType());
//                }
//            }
//            // Superclass
//            Class<?> superClass = clazz.getSuperclass();
//            if (superClass != null && dependencyGraph.containsKey(superClass)) dependencies.add(superClass);
//            // Interfaces
//            for (Class<?> iface : clazz.getInterfaces()) {
//                if (dependencyGraph.containsKey(iface)) dependencies.add(iface);
//            }
//
//            IDependencyMetaData meta = dependencyGraph.get(clazz);
//            if (meta == null) {
//                meta = new IDependencyMetaData() {
//                };
//                dependencyGraph.put(clazz, meta);
//            }
//            meta.setDependencyClass(clazz);
//            meta.setDependencies(dependencies);
//            meta.setRole(dependencies.isEmpty() ? DependencyRole.BASE : DependencyRole.ISOLATED);
//        }
//    }





//    /**
//     * Checks if an object has injectable fields.
//     * Supports both @Inject annotation and @AutoInjectAll annotation.
//     *
//     * @param obj The object to check
//     * @return true if the object has injectable fields, false otherwise
//     */
//    @Override
//    public boolean hasInjectableFields(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//
//        Class<?> clazz = obj.getClass();
//        boolean auto = clazz.isAnnotationPresent(AutoInjectAll.class);
//
//        // If @AutoInjectAll is present, check for any non-static fields
//        if (auto) {
//            while (clazz != null && clazz != Object.class) {
//                for (Field field : clazz.getDeclaredFields()) {
//                    if (!Modifier.isStatic(field.getModifiers())) {
//                        return true;
//                    }
//                }
//                clazz = clazz.getSuperclass();
//            }
//            return false;
//        }
//
//        // Otherwise, check for @Inject annotated fields or methods
//        clazz = obj.getClass();
//        while (clazz != null && clazz != Object.class) {
//            for (Field field : clazz.getDeclaredFields()) {
//                if (field.isAnnotationPresent(Inject.class)) {
//                    return true;
//                }
//            }
//
//            for (Method method : clazz.getDeclaredMethods()) {
//                if (method.isAnnotationPresent(Inject.class)) {
//                    return true;
//                }
//            }
//
//            clazz = clazz.getSuperclass();
//        }
//
//        return false;
//    }

//    /**
//     * Determines if a field should be injected based on annotations.
//     */
//    @Override
//    public boolean shouldInjectField(Field field, boolean autoInject) {
//        return autoInject || field.isAnnotationPresent(Inject.class);
//    }
//
//    /**
//     * Determines if a method should be injected based on annotations.
//     */
//    @Override
//    public boolean shouldInjectMethod(Method method, boolean autoInject) {
//        return (autoInject || method.isAnnotationPresent(Inject.class)) && method.getParameterCount() == 1;
//    }

//    /**
//     * Common field injection routine used by both static and instance injection paths.
//     */
//    @Override
//    public void injectFieldsForClass(Object target, Class<?> clazz, boolean autoInject, boolean includeStatic, boolean includeInstance, Set<Class<?>> dependencies) {
//        if (clazz == null) return;
//        Field[] fields;
//        try {
//            fields = clazz.getDeclaredFields();
//        } catch (LinkageError e) {
//            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
//            Log.info("[DI] Skipping field processing for class <yellow>" + clazz.getSimpleName() + "</yellow> due to an error: <red>" + errorMessage + "</red>");
//            Log.exception(e);
//            return;
//        }
//
//        for (Field field : fields) {
//            try {
//                if (!shouldInjectField(field, autoInject)) continue;
//
//                boolean isStatic = Modifier.isStatic(field.getModifiers());
//                if ((isStatic && !includeStatic) || (!isStatic && !includeInstance)) continue;
//
//                Inject inject = field.getAnnotation(Inject.class);
//                boolean optional = inject != null && inject.optional();
//                Class<?> depClass = field.getType();
//
//                if (dependencies != null) {
//                    dependencies.add(depClass);
//                }
//
//                Object value = resolveDependency(depClass);
//                injectFieldValue(target, field, value, optional, isStatic, depClass, clazz);
//            } catch (Throwable e) {
//                Log.error("[DI] Failed to inject field '" + field.getName() + "' in class '" + clazz.getName() + "'.");
//                Log.exception(e);
//            }
//        }
//    }

//    // Method injection helper for a single class
//    @Override
//    public void injectMethodsForClass(Object target, Class<?> clazz, boolean autoInject, Set<Class<?>> dependencies) {
//        if (clazz == null) return;
//        for (Method method : clazz.getDeclaredMethods()) {
//            if (!shouldInjectMethod(method, autoInject)) continue;
//
//            Inject inject = method.getAnnotation(Inject.class);
//            boolean optional = inject != null && inject.optional();
//            Class<?> depClass = method.getParameterTypes()[0];
//            if (dependencies != null) {
//                dependencies.add(depClass);
//            }
//
//            Object value = resolveDependency(depClass);
//            injectMethodValue(target, method, value, optional, depClass, clazz);
//        }
//    }

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


    // ===== Top-level injection =====
//    @Override
//    public void injectAll() throws IllegalAccessException {
//        dependencyGraph.keySet().stream().sorted(Comparator.comparingInt((Class<?> c) -> {
//            IDependencyMetaData meta = dependencyGraph.get(c);
//            return meta != null ? meta.getPriority() : 0;
//        }).thenComparingInt(c -> {
//            IDependencyMetaData meta = dependencyGraph.get(c);
//            return meta != null ? meta.getDepth() : 0;
//        })).forEach(this::injectAndRecordMetaData);
//    }

//    /**
//     * Unified injection + metadata population for any target.
//     * This method performs injection on the target's fields.
//     * If called during Phase 1 (AutoBind), it will scan and register.
//     * If called during Phase 4+ (Wave injection), dependencies should already be registered.
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public void injectAndRecordMetaData(Object target) {
//        if (target == null || target instanceof Class) return;
//
//        Class<?> rootClass = target.getClass();
//
//        boolean auto = rootClass.isAnnotationPresent(AutoInjectAll.class);
//        // Get metadata for this class
//        IDependencyMetaData meta = dependencyMap.getDependency(rootClass);
//        if (meta == null) {
//            // Fallback: create metadata if not found
//            meta = new DependencyMetaData(rootClass);
//        }
//
//        // track discovered dependencies & lifecycle methods
//        Set<Class<?>> dependencies = new HashSet<>();
//        Method preConstruct = null;
//        Method postConstruct = null;
//
//        Class<?> clazz = rootClass;
//        while (clazz != null && clazz != Object.class) {
////            // Inject static fields for this class via helper to keep this method clean
////            injectStaticFields(clazz, auto);
////
////            // === Field injection (instance fields only; static handled above) ===
////            injectFieldsForClass(target, clazz, auto, false, true, dependencies);
////
////            // === Method injection ===
////            injectMethodsForClass(target, clazz, auto, dependencies);
//
//            // === Lifecycle detection ===
//            EnumMap<LifecycleType, Method> lifecycle = detectLifecycleForClass(clazz);
//            Method pre = lifecycle.get(LifecycleType.PRE_CONSTRUCT);
//            Method post = lifecycle.get(LifecycleType.POST_CONSTRUCT);
//            if (pre != null) preConstruct = pre;
//            if (post != null) postConstruct = post;
//
//            clazz = clazz.getSuperclass();
//        }
//
//        // Update metadata for this component
//        meta.setDependencies(dependencies);
//        meta.setPreConstruct(preConstruct);
//        meta.setPostConstruct(postConstruct);
//        meta.setDepth(calculateDepth(rootClass, dependencies));
//        meta.setRole(determineRole(dependencies));
//
//        // Store in graph
//        dependencyGraph.put(rootClass, meta);
//
//        // === Execute PreConstruct ===
//        if (preConstruct != null) {
//            executeLifecycleMethod(preConstruct, target, rootClass, "PreConstruct"); //TODO: Type-safe lifecycleType
//        }
//
//        // === Execute PostConstruct ===
//        if (postConstruct != null) {
//            executeLifecycleMethod(postConstruct, target, rootClass, "PostConstruct"); //TODO: Type-safe lifecycleType
//        }
//    }


//    public void processPreConstructRetryQueue() throws InterruptedException {
//        while (!preConstructRetryQueue.isEmpty()) {
//            Class<?> clazz = preConstructRetryQueue.poll();
//            Object instance = dependencyMap.getDependencyInstance(clazz);
//            if (instance != null) injectAndRecordMetaData(instance);
//            // Thread.sleep(10); // TODO: Add proper loop
//        }
//    }

//    @Override
//    public void autoBind(Object target) {
//        try {
//            if (target == null) {
//                return;
//            }
//
//            if (target instanceof Class<?>) {
//                Class<?> type = (Class<?>) target;
//                if (!AUTO_BOUND_TARGETS.add(type)) {
//                    Log.info("[AUTO-BIND] Skipping duplicate type autoBind: " + type.getSimpleName());
//                    return;
//                }
//                bindTypeInternal(type, "type-level request");
//                return;
//            }
//
//            Class<?> targetClass = target.getClass();
//            if (!AUTO_BOUND_TARGETS.add(targetClass)) {
//                Log.info("[AUTO-BIND] Skipping duplicate autoBind for " + targetClass.getSimpleName());
//                return;
//            }
//
//            Set<String> packagesToScan = collectPackages(targetClass);
//            boolean scannedNew = ensurePackagesScanned(packagesToScan);
//
//            if (scannedNew) {
//                Log.info("[AUTO-BIND] Injecting registered classes after package scan (" + dependencyMap.getDependencyMapSize() + " entries)");
//                injectAllRegisteredClasses();
//            }
//
//            Log.info("[AUTO-BIND] Binding fields for all registered classes (" + dependencyMap.getDependencyMapSize() + " entries)");
//            bindFieldsForAllRegisteredClasses();
//
//            Log.info("[AUTO-BIND] Binding fields from target object: " + targetClass.getSimpleName());
//            bindFieldsFromTarget(target, targetClass);
//
//            Log.info("[AUTO-BIND] AutoBind complete for " + targetClass.getSimpleName()
//                    + ". Registered dependencies: " + dependencyMap.getDependencyMapSize());
//        } catch (Exception e) {
//            Log.exception(e);
//        }
//    }
//
//    /**
//     * Scans the entire project for injectable classes and registers them in the dependency map.
//     * This scan runs regardless of whether dependencies are already registered — it ensures full coverage.
//     *
//     * @param basePackage The root package to begin scanning (e.g., com.tjxjnoobie.api)
//     */
//    @Override
//    public void scanAndRegisterInjectableClasses(String basePackage) {
//        Log.info("[AUTO-BIND] Scanning classpath for injectable classes in package: " + basePackage);
//
//        try {
//            // Step 1: Discover candidate classes that could participate in dependency injection.
//            Set<Class<?>> allClasses = findInjectableClasses(basePackage);
//
//            // Step 2: Stop early when no candidates exist for the requested package.
//            if (allClasses.isEmpty()) {
//                Log.info("[AUTO-BIND] No injectable classes discovered in package: " + basePackage);
//                return;
//            }
//
//            // Step 3: Track newly registered classes for summary reporting.
//            Set<Class<?>> newlyRegistered = new HashSet<>();
//
//            for (Class<?> clazz : allClasses) {
//                Log.info("[AUTO-BIND] Scanned for injection: " + clazz.getName());
//
//                // Step 4: Avoid re-registering classes that already exist in the dependency map.
//                if (dependencyMap.isRegistered(clazz)) {
//                    Log.info("[AUTO-BIND] Skipping already registered class: " + clazz.getSimpleName());
//                    continue;
//                }
//
//                try {
//                    // Step 5: Route interfaces through the type binding pipeline.
//                    if (clazz.isInterface()) {
//                        bindTypeInternal(clazz, "package scan");
//                        if (dependencyMap.isRegistered(clazz)) {
//                            newlyRegistered.add(clazz);
//                            refreshMetadataForType(clazz, clazz);
//                        }
//                        continue;
//                    }
//
//                    // Step 6: Register abstract classes without instantiating them so metadata is available.
//                    if (Modifier.isAbstract(clazz.getModifiers())) {
//                        registerInstanceAndMetadata(clazz, null, clazz);
//                        newlyRegistered.add(clazz);
//                        Log.info("[AUTO-BIND] Registered abstract class metadata: " + clazz.getSimpleName());
//                        continue;
//                    }
//
//                    // Step 7: Instantiate concrete classes and bind them directly into the dependency map.
//                    Object instance = clazz.getDeclaredConstructor().newInstance();
//                    registerInstanceAndMetadata(clazz, instance, clazz);
//                    newlyRegistered.add(clazz);
//
//                    // Step 8: Capture dependency metadata and role information for the new registration.
//                    IDependencyMetaData meta = dependencyMap.getDependency(clazz);
//                    DependencyRole role = meta != null && meta.getRole() != null
//                            ? meta.getRole()
//                            : determineRoleFromDependencies(analyzeClassDependencies(clazz));
//                    Log.info("[AUTO-BIND] Registered: " + clazz.getSimpleName() + " (role=" + role + ")");
//                } catch (Exception e) {
//                    // Step 9: Warn when any individual class fails to prepare for dependency usage.
//                    Log.warn("[AUTO-BIND] Could not prepare class: " + clazz.getSimpleName() + " - Error: " + e.getMessage());
//                }
//            }
//
//            // Step 10: Emit a summary when new registrations were added during the scan.
//            if (!newlyRegistered.isEmpty()) {
//                Log.info("[AUTO-BIND] Newly registered classes: " + newlyRegistered.size());
//            }
//
//        } catch (Exception e) {
//            // Step 11: Capture unexpected exceptions that occurred during the scan.
//            Log.error("[AUTO-BIND] Failed to scan or register classes in package " + basePackage + ": " + e.getMessage());
//        }
//    }

//    /**
//     * Finds all classes in a given package that are potential candidates for dependency injection.
//     * This includes classes with @Inject fields, methods, or interfaces.
//     *
//     * @param basePackage The root package to search (e.g., com.tjxjnoobie.api)
//     * @return A set of classes found
//     */
//    //THIS ONE
//    @Override
//    public Set<Class<?>> findInjectableClasses(String basePackage) {
//        Set<Class<?>> results = new HashSet<>();
//        if (basePackage == null || basePackage.isBlank()) {
//            return results;
//        }
//
//        String path = basePackage.replace('.', '/');
//        ClassLoader loader = Thread.currentThread().getContextClassLoader();
//
//        try {
//            Enumeration<URL> resources = loader.getResources(path);
//            if (!resources.hasMoreElements()) {
//                Log.warn("[AUTO-BIND] No classpath entries found for package: " + basePackage);
//            }
//
//            while (resources.hasMoreElements()) {
//                URL resource = resources.nextElement();
//                String protocol = resource.getProtocol();
//
//                if ("file".equals(protocol)) {
//                    File dir = new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
//                    if (dir.exists() && dir.isDirectory()) {
//                        walkDirectoryForInjectables(dir, basePackage, results, loader);
//                    }
//                } else if ("jar".equals(protocol)) {
//                    scanJarResource(resource, path, results, loader);
//                } else {
//                    scanUnknownResource(resource, path, results, loader);
//                }
//            }
//        } catch (IOException e) {
//            Log.warn("[AUTO-BIND] Failed to access package " + path + ": " + e.getMessage());
//        }
//
//        return results;
//    }


//    /**
//     * Injects fields for ALL registered classes in the DependencyMap.
//     * This ensures every class gets its dependencies injected from the map.
//     */
//    @Override
//    public void injectAllRegisteredClasses() {
//        // Step 1: Retrieve all registered dependency metadata entries.
//        Collection<IDependencyMetaData> allMetadata = dependencyMap.getDependencyMapValues();
//
//        Log.info("[AUTO-BIND] Injecting fields for " + allMetadata.size() + " registered classes");
//
//        for (IDependencyMetaData meta : allMetadata) {
//            // Step 2: Skip metadata entries that do not reference a concrete class.
//            if (meta == null || meta.getDependencyClass() == null) {
//                continue;
//            }
//
//            Class<?> clazz = meta.getDependencyClass();
//            Object instance = meta.ensureAndGetInstance(meta);
//
//            // Step 3: Warn and continue when no instance can be resolved for the metadata.
//            if (instance == null) {
//                Log.warn("[AUTO-BIND] No instance available for: " + clazz.getSimpleName());
//                continue;
//            }
//
//            // Step 4: Inject fields for the resolved instance.
//            injectFieldsForInstance(instance, clazz);
//        }
//    }

//    /**
//     * Binds fields for ALL registered classes in the DependencyMap.
//     * This ensures every class gets its fields bound aggressively from the map.
//     */
//    public void bindFieldsForAllRegisteredClasses() {
//        // Step 1: Retrieve all registered dependency metadata entries.
//        Collection<IDependencyMetaData> allMetadata = dependencyMap.getDependencyMapValues();
//
//        Log.info("[AUTO-BIND] Binding fields for " + allMetadata.size() + " registered classes");
//
//        for (IDependencyMetaData meta : allMetadata) {
//            // Step 2: Skip metadata entries lacking a dependency type.
//            if (meta == null || meta.getDependencyClass() == null) {
//                continue;
//            }
//
//            Class<?> clazz = meta.getDependencyClass();
//            Object instance = meta.ensureAndGetInstance(meta);
//
//            // Step 3: Continue when no instance is available to bind against.
//            if (instance == null) {
//                Log.warn("[AUTO-BIND] No instance available for: " + clazz.getSimpleName());
//                continue;
//            }
//
//            // Step 4: Bind fields on the resolved instance using the dependency map.
//            bindFieldsFromTarget(instance, clazz);
//        }
//    }

//    /**
//     * Injects all @Inject fields for a specific instance using the DependencyMap.
//     */
//    @Override
//    public void injectFieldsForInstance(Object instance, Class<?> clazz) {
//        Class<?> currentClass = clazz;
//
//        while (currentClass != null && currentClass != Object.class) {
//            // Step 1: Evaluate each declared field for injection requirements.
//            for (Field field : currentClass.getDeclaredFields()) {
//                if (!field.isAnnotationPresent(Inject.class)) {
//                    continue;
//                }
//
//                // Skip static fields (handled separately)
//                if (Modifier.isStatic(field.getModifiers())) {
//                    continue;
//                }
//
//                Class<?> fieldType = field.getType();
//
//                // Step 2: Attempt to resolve the dependency directly from the map.
//                Object value = dependencyMap.getDependencyInstance(fieldType);
//
//                if (value == null) {
//                    // Step 3: Search for assignable types when a direct match is unavailable.
//                    IDependencyMetaData compatibleMeta = dependencyMap.findByAssignableType(fieldType);
//                    if (compatibleMeta != null) {
//                        value = compatibleMeta.ensureAndGetInstance(compatibleMeta);
//                    }
//                }
//
//                if (value != null) {
//                    try {
//                        field.setAccessible(true);
//                        field.set(instance, value);
//                        Log.info("[AUTO-BIND] Injected " + fieldType.getSimpleName() + " into " + clazz.getSimpleName() + "." + field.getName());
//                    } catch (Exception e) {
//                        Log.error("[AUTO-BIND] Failed to inject " + fieldType.getSimpleName() + " into " + clazz.getSimpleName() + "." + field.getName() + ": " + e.getMessage());
//                    }
//                } else {
//                    Inject inject = field.getAnnotation(Inject.class);
//                    boolean optional = inject != null && inject.optional();
//                    if (!optional) {
//                        Log.warn("[AUTO-BIND] No instance found for required field: " + clazz.getSimpleName() + "." + field.getName() + " (" + fieldType.getSimpleName() + ")");
//                    }
//                }
//            }
//
//            currentClass = currentClass.getSuperclass();
//        }
//    }


//    /**
//     * Analyzes a class to find all its @Inject dependencies.
//     * Walks the class hierarchy to find all injected fields.
//     */
//    @Override
//    public Set<Class<?>> analyzeClassDependencies(Class<?> clazz) {
//        Set<Class<?>> dependencies = new HashSet<>();
//
//        Class<?> current = clazz;
//        while (current != null && current != Object.class) {
//            collectInjectDependencies(current, dependencies);
//            current = current.getSuperclass();
//        }
//
//        return dependencies;
//    }

//    /**
//     * Updates metadata with dependency and lifecycle information for the supplied type.
//     *
//     * @param metadataKey the type whose metadata should be updated
//     * @param scanTarget the concrete class to analyze for dependency information
//     */
//    private void refreshMetadataForType(Class<?> metadataKey, Class<?> scanTarget) {
//        // Step 1: Ensure both the metadata key and scan target are available.
//        if (metadataKey == null || scanTarget == null) {
//            return;
//        }
//
//        // Step 2: Resolve the metadata entry associated with the key.
//        IDependencyMetaData meta = dependencyMap.getDependency(metadataKey);
//        if (meta == null) {
//            return;
//        }
//
//        // Step 3: Analyze dependencies declared on the scan target.
//        Set<Class<?>> deps = analyzeClassDependencies(scanTarget);
//
//        // Step 4: Persist dependency relationships and inferred role information.
//        meta.setDependencies(deps);
//        meta.setRole(determineRoleFromDependencies(deps));
//
//        // Step 5: Populate metadata with lifecycle hooks sourced from the scan target.
//        meta.populateMetaData(scanTarget);
//    }

//    /**
//     * Registers an instance (or metadata-only entry) and refreshes dependency metadata accordingly.
//     *
//     * @param type           the dependency type being registered
//     * @param instance       the resolved instance, which may be null for metadata-only entries
//     * @param metadataSource the class to scan for metadata; falls back to the dependency type when null
//     */
//    private void registerInstanceAndMetadata(Class<?> type, Object instance, Class<?> metadataSource) {
//        // Step 1: Validate that a dependency type has been provided.
//        if (type == null) {
//            return;
//        }
//
//        // Step 2: Persist the dependency registration with the supplied instance (if any).
//        dependencyMap.registerDependency(type, instance);
//
//        // Step 3: Ensure metadata retains a reference to the instance when one exists.
//        IDependencyMetaData meta = dependencyMap.getDependency(type);
//        if (meta != null && instance != null) {
//            meta.setInstance(instance);
//        }
//
//        // Step 4: Choose the class that should drive metadata population.
//        Class<?> target = metadataSource != null ? metadataSource : (instance != null ? instance.getClass() : type);
//
//        // Step 5: Refresh the metadata to include dependency and lifecycle details.
//        refreshMetadataForType(type, target);
//    }

//    private void collectInjectDependencies(Class<?> type, Set<Class<?>> dependencies) {
//        Arrays.stream(type.getDeclaredFields())
//                .filter(field -> field.isAnnotationPresent(Inject.class))
//                .map(Field::getType)
//                .forEach(dependencies::add);
//
//        Arrays.stream(type.getDeclaredMethods())
//                .filter(method -> method.isAnnotationPresent(Inject.class) && method.getParameterCount() == 1)
//                .map(method -> method.getParameterTypes()[0])
//                .forEach(dependencies::add);
//    }

//    /**
//     * Determines the role of a class based on its dependency count.
//     * - BASE: 0 dependencies (no injections needed)
//     * - INTERMEDIATE: 1-2 dependencies (simple dependencies)
//     * - ISOLATED: 3+ dependencies (complex dependencies)
//     */
//    @Override
//    public DependencyRole determineRoleFromDependencies(Set<Class<?>> dependencies) {
//        int depCount = dependencies.size();
//
//        if (depCount == 0) {
//            return DependencyRole.BASE;
//        } else if (depCount <= 2) {
//            return DependencyRole.INTERMEDIATE;
//        } else {
//            return DependencyRole.ISOLATED;
//        }
//    }


//    /**
//     * Recursively walks directory to find injectable classes.
//     * TODO: Wire all DI classes with @Injectable annotation before re-enabling the annotation check
//     * Currently discovers ALL classes and interfaces in the project package
//     */
//    @Override
//    public void walkDirectoryForInjectables(File dir, String packageName, Set<Class<?>> results) {
//        walkDirectoryForInjectables(dir, packageName, results, Thread.currentThread().getContextClassLoader());
//    }
//
//    private void walkDirectoryForInjectables(File dir, String packageName, Set<Class<?>> results, ClassLoader loader) {
//        File[] files = dir.listFiles();
//        // Step 1: Abort traversal when the directory is empty or inaccessible.
//        if (files == null) return;
//
//        for (File file : files) {
//            if (file.isDirectory()) {
//                walkDirectoryForInjectables(file, packageName + "." + file.getName(), results, loader);
//            } else if (file.getName().endsWith(".class")) {
//                String className = packageName + '.' + file.getName().replace(".class", "");
//                handleDiscoveredClass(className, results, loader);
//            }
//        }
//    }

//    private void scanJarResource(URL resource, String packagePath, Set<Class<?>> results, ClassLoader loader) {
//        try {
//            URLConnection connection = resource.openConnection();
//            if (connection instanceof JarURLConnection jarConnection) {
//                try (JarFile jarFile = jarConnection.getJarFile()) {
//                    scanJarEntries(jarFile, packagePath, results, loader);
//                }
//                return;
//            }
//        } catch (IOException e) {
//            Log.warn("[AUTO-BIND] Failed to open jar resource " + resource + ": " + e.getMessage());
//            return;
//        }
//
//        scanUnknownResource(resource, packagePath, results, loader);
//    }

//    private void scanUnknownResource(URL resource, String packagePath, Set<Class<?>> results, ClassLoader loader) {
//        String file = resource.getFile();
//        if (file == null) {
//            return;
//        }
//
//        int separator = file.indexOf('!');
//        if (separator == -1) {
//            return;
//        }
//
//        String jarPath = file.substring(0, separator);
//        if (jarPath.startsWith("file:")) {
//            jarPath = jarPath.substring("file:".length());
//        }
//
//        try (JarFile jarFile = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
//            scanJarEntries(jarFile, packagePath, results, loader);
//        } catch (IOException e) {
//            Log.warn("[AUTO-BIND] Failed to scan jar entries from " + jarPath + ": " + e.getMessage());
//        }
//    }

//    private void scanJarEntries(JarFile jarFile, String packagePath, Set<Class<?>> results, ClassLoader loader) throws IOException {
//        Enumeration<JarEntry> entries = jarFile.entries();
//        while (entries.hasMoreElements()) {
//            JarEntry entry = entries.nextElement();
//            if (entry.isDirectory()) {
//                continue;
//            }
//
//            String name = entry.getName();
//            if (!name.endsWith(".class") || !name.startsWith(packagePath)) {
//                continue;
//            }
//
//            String className = name.replace('/', '.').replace(".class", "");
//            handleDiscoveredClass(className, results, loader);
//        }
//    }
//
//    private void handleDiscoveredClass(String className, Set<Class<?>> results, ClassLoader loader) {
//        if (className == null || className.isBlank()) {
//            return;
//        }
//
//        if (className.endsWith("package-info") || className.endsWith("module-info")) {
//            return;
//        }
//
//        try {
//            Class<?> clazz = Class.forName(className, false, loader);
//            if (!shouldConsiderClass(clazz)) {
//                return;
//            }
//            results.add(clazz);
//        } catch (ClassNotFoundException | NoClassDefFoundError | UnsupportedClassVersionError ignored) {
//            // Skip classes that cannot be loaded in the current runtime
//        }
//    }

//    private boolean shouldConsiderClass(Class<?> clazz) {
//        if (clazz == null || clazz.isSynthetic()) {
//            return false;
//        }
//
//        Package pkg = clazz.getPackage();
//        String packageName = pkg != null ? pkg.getName() : "";
//        return shouldConsiderPackage(packageName);
//    }



//    /**
//     * Binds fields from the target object using the now-populated dependency map.
//     */
//    public void bindFieldsFromTarget(Object target, Class<?> targetClass) {
//        Log.info("[AUTO-BIND] Binding fields for: " + targetClass.getSimpleName());
//
//        // Step 1: Begin traversal at the provided target class.
//        Class<?> clazz = targetClass;
//        while (clazz != null && clazz != Object.class) {
//            // Step 2: Inject each declared field on the current class.
//            for (Field field : clazz.getDeclaredFields()) {
//                bindField(target, field);
//            }
//            // Step 3: Continue walking up the inheritance hierarchy.
//            clazz = clazz.getSuperclass();
//        }
//    }

//    /**
//     * Binds a class or interface into the dependency map, creating instances or proxies as needed.
//     *
//     * @param type   the type to bind
//     * @param origin the origin description for logging purposes
//     * @throws Exception when instantiation or proxy creation fails
//     */
//    private void bindTypeInternal(Class<?> type, String origin) throws Exception {
//        // Step 1: Ensure a target type was provided for binding.
//        if (type == null) {
//            return;
//        }
//
//        // Step 2: Skip types that are not eligible for injection.
//        if (!isEligibleForInjection(type)) {
//            Log.info("[AUTO-BIND] Skipping ineligible type: " + type.getName());
//            return;
//        }
//
//        // Step 3: Reuse an already registered instance when available.
//        IDependencyMetaData existingMeta = dependencyMap.getDependency(type);
//        Object existingInstance = existingMeta != null ? dependencyMap.ensureAndGetInstance(existingMeta) : null;
//        if (existingInstance != null) {
//            logTypeBinding(type, existingInstance, origin + ", already registered");
//            return;
//        }
//
//        // Step 4: Attempt to reuse an assignable dependency instance.
//        IDependencyMetaData compatibleMeta = dependencyMap.findByAssignableType(type);
//        Object compatibleInstance = compatibleMeta != null ? dependencyMap.ensureAndGetInstance(compatibleMeta) : null;
//        if (compatibleInstance != null) {
//            registerInstanceAndMetadata(type, compatibleInstance, compatibleMeta.getDependencyClass());
//            String source = compatibleMeta.getDependencyClass() != null
//                    ? compatibleMeta.getDependencyClass().getSimpleName()
//                    : compatibleInstance.getClass().getSimpleName();
//            logTypeBinding(type, compatibleInstance, origin + ", matched " + source);
//            return;
//        }
//
//        // Step 5: Handle abstract classes by binding metadata and reusing compatible subclasses.
//        if (!type.isInterface() && Modifier.isAbstract(type.getModifiers())) {
//            registerInstanceAndMetadata(type, null, type);
//
//            Set<Class<?>> impls = findImplementations(type, type.getPackage() != null ? type.getPackage().getName() : "");
//            if (!impls.isEmpty()) {
//                Class<?> implClass = impls.iterator().next();
//                Object instance = implClass.getDeclaredConstructor().newInstance();
//                registerInstanceAndMetadata(type, instance, type);
//                logTypeBinding(type, instance, origin + ", resolved abstract via " + implClass.getSimpleName());
//                return;
//            }
//
//            logTypeBinding(type, null, origin + ", registered abstract metadata");
//            return;
//        }
//
//        // Step 6: Instantiate and register concrete classes directly.
//        if (!type.isInterface()) {
//            Object instance = type.getDeclaredConstructor().newInstance();
//            registerInstanceAndMetadata(type, instance, type);
//            logTypeBinding(type, instance, origin + ", instantiated concrete type");
//            return;
//        }
//
//        // Step 7: Create self proxies for interfaces with default methods.
//        if (Arrays.stream(type.getMethods()).anyMatch(Method::isDefault)) {
//            Object proxy = createSelfProxy(type);
//            registerInstanceAndMetadata(type, proxy, type);
//            logTypeBinding(type, proxy, origin + ", self proxy");
//            return;
//        }
//
//        // Step 8: Attempt to bind discovered implementations for interfaces.
//        Set<Class<?>> impls = findImplementations(type, type.getPackage() != null ? type.getPackage().getName() : "");
//        if (!impls.isEmpty()) {
//            Class<?> implClass = impls.iterator().next();
//            Object instance = implClass.getDeclaredConstructor().newInstance();
//            registerInstanceAndMetadata(type, instance, implClass);
//            logTypeBinding(type, instance, origin + ", discovered " + implClass.getSimpleName());
//            return;
//        }
//
//        // Step 9: Use a placeholder proxy when no implementation could be located.
//        Object placeholder = createPlaceholderProxy(type);
//        registerInstanceAndMetadata(type, placeholder, type);
//        logTypeBinding(type, placeholder, origin + ", placeholder proxy");
//    }

//    /**
//     * Binds the provided type into the dependency map using the manual binding pipeline.
//     *
//     * @param type the class to register within the dependency map
//     * @throws Exception when type binding fails
//     */
//    @Override
//    public void bindType(Class<?> type) throws Exception {
//        // Delegate to the shared binding routine with a manual origin label.
//        bindTypeInternal(type, "manual bind request");
//    }
//
//    /**
//     * Resolves and assigns a dependency for the provided field on the target instance.
//     *
//     * @param target the instance whose field should be populated
//     * @param field  the field requiring injection
//     */
//    @Override
//    public void bindField(Object target, Field field) {
//        try {
//            // Step 1: Ensure the field is accessible for reflection-based assignment.
//            field.setAccessible(true);
//
//            // Step 2: Determine the type to inject and short-circuit when ineligible.
//            Class<?> fieldType = field.getType();
//            if (!isEligibleForInjection(fieldType)) {
//                return;
//            }
//
//            // Step 3: Skip binding when a value is already present on the field.
//            boolean isStatic = Modifier.isStatic(field.getModifiers());
//            Object currentValue = isStatic ? field.get(null) : field.get(target);
//            if (currentValue != null) {
//                return;
//            }
//
//            // Step 4: Track optional injection semantics for later fallback handling.
//            Inject injectAnnotation = field.getAnnotation(Inject.class);
//            boolean optional = injectAnnotation != null && injectAnnotation.optional();
//
//            // Step 5: Prefer an exact dependency match from the registry when available.
//            IDependencyMetaData exactMeta = dependencyMap.getDependency(fieldType);
//            Object existingInstance = exactMeta != null ? dependencyMap.ensureAndGetInstance(exactMeta) : null;
//            if (existingInstance != null) {
//                assignFieldValue(target, field, existingInstance, "existing registration");
//                return;
//            }
//
//            // Step 6: Attempt to reuse an assignable dependency instance.
//            IDependencyMetaData compatibleMeta = dependencyMap.findByAssignableType(fieldType);
//            Object compatibleInstance = compatibleMeta != null ? dependencyMap.ensureAndGetInstance(compatibleMeta) : null;
//            if (compatibleInstance != null) {
//                registerInstanceAndMetadata(fieldType, compatibleInstance, compatibleMeta.getDependencyClass());
//                String source = compatibleMeta.getDependencyClass() != null
//                        ? compatibleMeta.getDependencyClass().getSimpleName()
//                        : compatibleInstance.getClass().getSimpleName();
//                assignFieldValue(target, field, compatibleInstance, "matched " + source);
//                return;
//            }
//
//            // Step 7: Resolve any instance directly recorded within the dependency map.
//            Object resolved = dependencyMap.getDependencyInstance(fieldType);
//            if (resolved != null) {
//                assignFieldValue(target, field, resolved, "resolved from registry");
//                return;
//            }
//
//            // Step 8: Handle abstract classes by locating a concrete subclass when possible.
//            if (!fieldType.isInterface() && Modifier.isAbstract(fieldType.getModifiers())) {
//                registerInstanceAndMetadata(fieldType, null, fieldType);
//                Set<Class<?>> impls = findImplementations(fieldType, fieldType.getPackage() != null ? fieldType.getPackage().getName() : "");
//                if (!impls.isEmpty()) {
//                    Class<?> implClass = impls.iterator().next();
//                    Object instance = implClass.getDeclaredConstructor().newInstance();
//                    registerInstanceAndMetadata(fieldType, instance, fieldType);
//                    assignFieldValue(target, field, instance, "resolved abstract via " + implClass.getSimpleName());
//                    return;
//                }
//            }
//
//            // Step 9: Bind existing metadata entries before instantiating a concrete class.
//            if (!fieldType.isInterface()) {
//                if (dependencyMap.isRegistered(fieldType)) {
//                    Object existing = dependencyMap.getDependencyInstance(fieldType);
//                    if (existing != null) {
//                        assignFieldValue(target, field, existing, "existing registration");
//                        return;
//                    }
//                }
//                Object instance = fieldType.getDeclaredConstructor().newInstance();
//                registerInstanceAndMetadata(fieldType, instance, fieldType);
//                assignFieldValue(target, field, instance, "instantiated " + fieldType.getSimpleName());
//                return;
//            }
//
//            // Step 10: Create default-method proxies for interfaces that can self-handle invocations.
//            if (Arrays.stream(fieldType.getMethods()).anyMatch(Method::isDefault)) {
//                if (dependencyMap.isRegistered(fieldType)) {
//                    Object existing = dependencyMap.getDependencyInstance(fieldType);
//                    if (existing != null) {
//                        assignFieldValue(target, field, existing, "existing registration");
//                        return;
//                    }
//                }
//                Object proxy = createSelfProxy(fieldType);
//                registerInstanceAndMetadata(fieldType, proxy, fieldType);
//                assignFieldValue(target, field, proxy, "self proxy");
//                return;
//            }
//
//            // Step 11: Discover and instantiate concrete implementations for interface injections.
//            Set<Class<?>> impls = findImplementations(fieldType, fieldType.getPackage() != null ? fieldType.getPackage().getName() : "");
//            if (!impls.isEmpty()) {
//                Class<?> implClass = impls.iterator().next();
//                if (dependencyMap.isRegistered(fieldType)) {
//                    Object existing = dependencyMap.getDependencyInstance(fieldType);
//                    if (existing != null) {
//                        assignFieldValue(target, field, existing, "existing registration");
//                        return;
//                    }
//                }
//                Object instance = implClass.getDeclaredConstructor().newInstance();
//                registerInstanceAndMetadata(fieldType, instance, implClass);
//                assignFieldValue(target, field, instance, "discovered " + implClass.getSimpleName());
//                return;
//            }
//
//            // Step 12: Reuse any late-registered instance before falling back to a placeholder.
//            if (dependencyMap.isRegistered(fieldType)) {
//                Object existing = dependencyMap.getDependencyInstance(fieldType);
//                if (existing != null) {
//                    assignFieldValue(target, field, existing, "existing registration");
//                    return;
//                }
//            }
//
//            // Step 13: Provide placeholder proxies for required but unresolved dependencies.
//            if (!optional) {
//                Object placeholder = createPlaceholderProxy(fieldType);
//                registerInstanceAndMetadata(fieldType, placeholder, fieldType);
//                assignFieldValue(target, field, placeholder, "placeholder proxy");
//            } else {
//                // Step 14: Leave optional dependencies unset when no suitable binding was resolved.
//                Log.info("[AUTO-BIND] Optional dependency left unset for " + field.getDeclaringClass().getSimpleName()
//                        + "." + field.getName());
//            }
//        } catch (InaccessibleObjectException ignored) {
//            //TODO: Add 'verbose' logging option
//            // Step 15: Field is inaccessible due to module boundaries; ignore silently for now.
//        } catch (Exception e) {
//            // Step 16: Report unexpected binding failures for diagnostic purposes.
//            Log.error("Error binding field " + field.getName() + ": " + e.getMessage());
//        }
//    }

//    /**
//     * Creates a proxy that routes interface calls to default method implementations when available.
//     *
//     * @param iface the interface requiring a self proxy
//     * @return a proxy capable of invoking default methods or logging unimplemented calls
//     * @throws Exception when reflective invocation of default methods fails
//     */
//    @Override
//    public Object createSelfProxy(Class<?> iface) throws Exception {
//        // Step 1: Construct a proxy that delegates to default methods when present.
//        return Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[]{iface}, (proxyObj, method, args) -> {
//            if (method.isDefault()) {
//                try {
//                    MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(iface, MethodHandles.lookup());
//                    return lookup.unreflectSpecial(method, iface).bindTo(proxyObj).invokeWithArguments(args);
//                } catch (Throwable t) {
//                    // Step 2: Surface failures during default-method invocation.
//                    Log.error("Failed to invoke default method " + method.getName() + " on " + iface.getSimpleName() + ": " + t.getMessage());
//                    throw t;
//                }
//            }
//            // Step 3: Warn when the interface lacks an implementation path.
//            Log.warn("Unimplemented interface call: " + method.getName() + " in " + iface.getSimpleName());
//            return null;
//        });
//    }
//
//    /**
//     * Creates a placeholder proxy that logs calls to unresolved dependencies.
//     *
//     * @param iface the interface missing an implementation
//     * @return a proxy that logs invocation attempts
//     */
//    @Override
//    public Object createPlaceholderProxy(Class<?> iface) {
//        // Produce a proxy that reports unimplemented method usage.
//        return Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[]{iface}, (p, m, a) -> {
//            Log.warn("Called " + m.getName() + " on unimplemented interface: " + iface.getSimpleName());
//            return null;
//        });
//    }


//    // tryInvokeBuildMethod removed: contexts should use explicit registration APIs.
//    /**
//     * Finds implementations or subclasses for the provided type within the given package.
//     *
//     * @param interfaceType the type to search implementations for
//     * @param basePackage   the base package to scan
//     * @return a set of assignable, non-abstract implementations
//     */
//    @Override
//    public Set<Class<?>> findImplementations(Class<?> interfaceType, String basePackage) {
//        Set<Class<?>> implementations = new HashSet<>();
//        if (interfaceType == null) {
//            return implementations;
//        }
//
//        Set<String> searchPackages = collectImplementationSearchPackages(interfaceType, basePackage);
//        for (String scanPackage : searchPackages) {
//            Set<Class<?>> candidates = findInjectableClasses(scanPackage);
//            for (Class<?> candidate : candidates) {
//                if (candidate == null || candidate.isInterface()) {
//                    continue;
//                }
//
//                if (interfaceType.isAssignableFrom(candidate)) {
//                    implementations.add(candidate);
//                    Log.info("Found concrete class: " + candidate.getSimpleName() + " implements " + interfaceType.getSimpleName() + "");
//                }
//            }
//        }
//
//        return implementations;
//    }

//    private Set<String> collectImplementationSearchPackages(Class<?> interfaceType, String basePackage) {
//        LinkedHashSet<String> packages = new LinkedHashSet<>();
//
//        if (basePackage != null && !basePackage.isBlank()) {
//            packages.add(basePackage);
//        }
//
//        addTypeHierarchyPackages(interfaceType, packages);
//
//        packages.addAll(getAllowedPackagePrefixes());
//        packages.addAll(SCANNED_PACKAGES);
//
//        packages.removeIf(pkg -> pkg == null || pkg.isBlank() || !shouldConsiderPackage(pkg));
//
//        return packages;
//    }

//    @Override
//    public void walkDirectory(Class<?> interfaceType, File dir, String packageName, Set<Class<?>> results) {
//        File[] files = dir.listFiles();
//        if (files == null) return;
//
//        for (File file : files) {
//            if (file.isDirectory()) {
//                // Step 1: Recurse into subdirectories to continue discovery.
//                walkDirectory(interfaceType, file, packageName + "." + file.getName(), results);
//            } else if (file.getName().endsWith(".class")) {
//                String className = packageName + '.' + file.getName().replace(".class", "");
//                try {
//                    Class<?> clazz = Class.forName(className);
//                    // Step 2: Record concrete, non-abstract classes assignable to the target type.
//                    if (!clazz.isInterface()
//                            && interfaceType.isAssignableFrom(clazz)
//                            && !Modifier.isAbstract(clazz.getModifiers())
//                            && !clazz.equals(interfaceType)) {
//                        results.add(clazz);
//                        Log.info("Found concrete class: " + clazz.getSimpleName() + " implements " + interfaceType.getSimpleName());
//                    }
//                } catch (Throwable ignored) {
//                    // Step 3: Ignore classes that fail to load during scanning.
//                }
//            }
//        }
//    }
}

