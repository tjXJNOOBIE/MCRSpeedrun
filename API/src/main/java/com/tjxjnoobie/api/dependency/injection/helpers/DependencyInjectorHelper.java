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
import com.tjxjnoobie.api.dependency.injection.enums.LifecycleType;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyClass;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.console.style.LogColor;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
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
public class DependencyInjectorHelper<CLASS extends IDependencyClass<?>,
        INSTANCE extends IDependencyInstance<?>>
        implements IDependencyInjectorHelper<CLASS,INSTANCE>, IDependencyMap<CLASS,INSTANCE>{

    IDependencyInstance<INSTANCE> dependencyInstance;

    public Set<Class<? extends CLASS>> LOADED_CLASSES = ConcurrentHashMap.newKeySet();





    //TODO: Determine method relevance
//    @Override
//    public void propagateInstancesAcrossHierarchy() {
//        for (CLASS propagateCandidate : getDependencyMap().getSubDependenciesForBase()) {
//            Log.info("[DI-Propagate] Propagating instances across hierarchy");
//
//            Object instance = getDependencyInstance(propagateCandidate);
//
//            if (instance == null) Log.error("[DI-Propagate] " + LogColor.YELLOW + "NULL INSTANCE" + LogColor.RESET);
//
//            // Share instance with any class that implements or extends this interface
//            for (Class<?> candidate : getDependencyMap().getSubDependenciesForBase()) {
//                if (candidate == null || candidate.isInterface() || Modifier.isAbstract(candidate.getModifiers()))
//                    Log.error("[DI-Propagate] " + LogColor.YELLOW + "SKIP" + LogColor.RESET + " Invalid candidate: " + candidate.getName());
//
//                // Candidate implements or extends the same hierarchy as the interface’s impl
//                if (propagateCandidate.isAssignableFrom(candidate) || candidate.isAssignableFrom(propagateCandidate)) {
//                    Log.info("[DI-Propagate] " + LogColor.YELLOW + "Propagating " + propagateCandidate.getSimpleName() + " to " + candidate.getSimpleName());
//                    // Avoid overwriting an existing meta
//                    setInstance(instance);
//                    getDependencyMap().registerDependency(candidate, sharedMeta);
//
//                    Log.success("[DI-Propagate] Shared instance of "
//                            + propagateCandidate.getSimpleName() + " to " + candidate.getSimpleName());
//                }
//            }
//        }
//    }


    /**
     * Registers dependencies declared via @DelegatesToInterface annotations.
     * <p>
     * This scanner respects configured package allow/exclude lists and gracefully skips invalid entries.
     * It ensures the DependencyMap is populated with interface keys and concrete instances sourced from
     * the annotation metadata.
     * </p>
     */
    @Override
    public void registerDependenciesViaAnnotation(Set<Class<? extends CLASS>> classesToScan) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Log.info("[DI-Helper] " + LogColor.YELLOW + "Starting dual-phase DI registration" + LogColor.RESET);

        if (classesToScan == null || classesToScan.isEmpty()) {
            Log.warn("[DI-Helper] " + LogColor.YELLOW + "SKIP" + LogColor.RESET + " No classes to scan");
            return;
        }

        int registeredCount = 0;

        // ===== PHASE 1: CONCRETE ANNOTATION SCAN =====
        for (Class<? extends CLASS> dependencyClass : classesToScan) {
            if (dependencyClass == null)
                Log.error("[DI-HELPER] Candidate classes are null in scan! ");

            DelegatesToInterface delegatesAnnotation = dependencyClass.getAnnotation(DelegatesToInterface.class);
            if (delegatesAnnotation == null) { Log.error("[DI-Helper] " + LogColor.YELLOW + "SKIP " + LogColor.RESET + "No @DelegatesToInterface delegatesAnnotation found on " + dependencyClass.getName());
                continue;
            }

            IDependencyClass<CLASS> targetInterface = getDependencyClass();
            IDependencyInstance<INSTANCE> targetInstance = getDependencyInstance();
            if (!targetInterface.isInterface()){ Log.error("[DI-Helper] targetInterface is not a interface");
            continue;
        }
            if (targetInterface == null) { Log.error("[DI-Helper] targetInterface is null ");
                continue;
            }


            if (!targetInterface.isAssignableFrom(dependencyClass.getClass())) {
                Log.warn("[DI-Helper] " + LogColor.YELLOW + "SKIP" + LogColor.RESET +

                        " " + dependencyClass.getSimpleName() + " does not implement " + targetInterface.getSimpleName());
                continue;
            }
            //TODO: May be redundant due to needing duplicate keys with diffrent values
//            if (getDependencyMap().isRegistered(targetInterface, false)) {
//                Log.warn("[DI-Helper] " + LogColor.YELLOW + "SKIP" + LogColor.RESET +
//                        " Interface " + targetInterface.getName() + " already registered");
//                continue;
//            }
//            Object instance = dependencyClass.getDeclaredConstructor().newInstance();
           // Object proxy = createProxyFor(targetInterface);
//            IDependencyClass<CLASS> redisProxy = createProxyFor(IRedis.class);
//            getDependencyMap().registerDependency(IRedis.class, redisProxy);
            getDependencyMap().registerDependency(targetInterface, targetInstance);
            createProxyFor(targetInterface);

            // propagateInstancesAcrossHierarchy();

//            Log.success("[DI-Helper] " + LogColor.GREEN + "REGISTERED " + LogColor.RESET +
//                    targetInterface.getSimpleName() + " -> " + dependencyClass.getSimpleName());
            registeredCount++;
        }
        // ===== PHASE 2: INTERFACE EXTENSION RECURSION =====
//        Set<Class<?>> registeredInterfaces = new LinkedHashSet<>(getDependencyMap().getDependencies());
//        for (Class<?> iface : registeredInterfaces) {
//            if (!iface.isInterface()) continue;
           // recursivelyLinkExtendedInterfaces(iface, 0);



//        }
        Log.info("[DI-Helper] " + LogColor.YELLOW + "SUMMARY" + LogColor.RESET +
                " Total registered: " + registeredCount + ", including extended interface chains");
        getDependencyMetaDataSummary();
    }
//TODO: Potentially redundant code
//
//    /**
//     * Recursively links extended interfaces and finds valid annotation paths.
//     */
//    @Override
//    public void recursivelyLinkExtendedInterfaces(Class<?> iface, int depth) {
//        if (iface == null || iface == Object.class || !iface.isInterface()) Log.error("[DI-Link] " + LogColor.YELLOW + "SKIP" + LogColor.RESET + "Invalid interface: " + iface.getName());
//
//        // Indentation for pretty recursive logging
//        String prefix = "  ".repeat(depth);
//
//        Log.info(prefix + "[DI-Link] " + LogColor.GRAY + "Scanning interface: " + iface.getName());
//
//        // Iterate through project-scoped loaded classes
//        for (Class<?> candidate : getDependencyMap().getDependencies()) {
//            if (candidate == null) continue;
//
//            // Skip interfaces and abstracts, we only want potential concretes
//            if (candidate.isInterface() || Modifier.isAbstract(candidate.getModifiers())) continue;
//
//            // Only consider classes implementing this interface
//            if (!iface.isAssignableFrom(candidate)) continue;
//
//            // Check for @DelegatesToInterface annotation
//            DelegatesToInterface annotation = candidate.getAnnotation(DelegatesToInterface.class);
//            if (annotation != null && annotation.getClassForDelegation().equals(iface)) {
//                // Found the correct annotated implementation
//                    getDependencyMap().registerDependency(iface, candidate);
//                    Log.success(prefix + "[DI-Link] " + LogColor.GREEN + "BOUND " + LogColor.RESET +
//                            iface.getSimpleName() + " -> " + candidate.getSimpleName());
//
//                return; // stop further recursion, we found our concrete
//            }
//        }
//    }

    /**
     * Finds a concrete class implementing the given interface that has a matching @DelegatesToInterface annotation.
     */
    @Override
    public Set<Class<? extends CLASS>> findMatchingConcreteForInterface(IDependencyClass<CLASS> subIface) {
        Set<Class<?extends CLASS>> matches = new LinkedHashSet<>();

        for (Class<? extends CLASS> dependencyClass : getAllLoadedClasses("com.tjxjnoobie")) {
            if (dependencyClass.isInterface() || Modifier.isAbstract(dependencyClass.getClass().getModifiers())) continue;

            if (subIface.isAssignableFrom(dependencyClass.getClass())) {
                DelegatesToInterface anno = dependencyClass.getAnnotation(DelegatesToInterface.class);
                if (anno != null && anno.getClassForDelegation().equals(subIface)) {
                    Log.success("[DI-Scan] Found concrete " + dependencyClass.getSimpleName() +
                            " matching " + subIface.getSimpleName());
                    matches.add(dependencyClass);
                }
            }
        }

        return matches;
    }

    public Set<Class<? extends CLASS>> getAllLoadedClasses(String basePackage) {
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
                    Class<?> rawClass = Class.forName(name, false, loader);
                    //This cast shouldn't matter, we can always return a class from generic types
                    LOADED_CLASSES.add((Class<? extends CLASS>) Class.forName(name, false, loader));
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
//                    if (!shouldConsiderPackage(packageName)) {
//                        Log.info("[DI-Scan] " + LogColor.YELLOW + "EXCLUDED" + LogColor.RESET
//                                + " class " + className + " (filtered package: " + packageName + ")");
//                        continue;
//                    }

                    try {
                        //Cast check should be okay so we can get classes from raw generic types
                        LOADED_CLASSES.add((Class<? extends CLASS>) Class.forName(className, false, loader));
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
    public Set<Class<? extends CLASS>> scanFromBasePackage(String basePackage, ClassLoader loader) {

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
    @SuppressWarnings("unchecked")
    public Object createProxyFor(IDependencyClass<CLASS> interfaceToProxy) {
        if(!interfaceToProxy.isInterface()) {
            Log.error("[PROXY] " + interfaceToProxy.getSimpleName() + " is not a interface, skipping");
            return false;
        }

        if(!isClassLoadable(interfaceToProxy)){Log.error("[PROXY] " + interfaceToProxy.getSimpleName() + " is not a loadable class in the runtime, skipping");
        return false;
        }
        try {

            if (getDependencyInstance() == null) {
                Log.error("[PROXY] Failed to create proxy for " + interfaceToProxy.getSimpleName() + " - instance is null");
            }
            if (getDependencyInstance() != null && Proxy.isProxyClass(dependencyInstance.getClass())) {
                Log.info("[PROXY] Reusing existing proxy for " + interfaceToProxy.getSimpleName());
                return getDependencyInstance();
            }
            if (getDependencyInstance() != null && interfaceToProxy.isInstance(dependencyInstance)) {
                // It's already an instance for this interface; treat it as existing proxy/impl
                Log.info("[PROXY] Existing instance found for " + interfaceToProxy.getSimpleName() + ", skipping proxy creation");
                return getDependencyInstance();
            }
        } catch (Throwable t) {
            // Non-fatal: continue to create a new proxy if lookup fails
            Log.exception(t);
        }
        return Proxy.newProxyInstance(
                interfaceToProxy.getClass().getClassLoader(),
                new Class<?>[]{interfaceToProxy.getClass()},
                (proxy, method, args) -> {
                    try {
                        // Log.info("[PROXY] Trying to register proxy interface " + proxy.toString() + "-> " + proxy.getClass().getSimpleName() + " interface");
                        if(args==null){
                            Log.warn("[PROXY] Null argument passed to method " + method.getName() + ", creating dummy object...");
                            args = new Object[0]; // Make sure the below statement is not null
                        }

                        // Graceful handling for core Object methods to avoid CCEs
                        if (method.getDeclaringClass() == Object.class) {
                            switch (method.getName()) {
                                case "toString":
                                    return interfaceToProxy.getName() + "Proxy@";
                                case "hashCode":
                                    return 0;
                                case "equals":
                                    return proxy == (args.length > 0 ? args[0] : null);
                                default:
                                    // fall through; continue to default handling below
                                    break;
                            }
                        }

                        if (method.isDefault()) {
                        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(interfaceToProxy.getClass(), MethodHandles.lookup());
                        return lookup
                                .findSpecial(interfaceToProxy.getClass(), method.getName(),
                                        MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                                        interfaceToProxy.getClass())
                                .bindTo(proxy)
                                .invokeWithArguments(args);
                    } else{
                            Log.warn("[PROXY] Method " + method.getName() + " is not default in -> " + interfaceToProxy.getSimpleName());
                        }

                    // No impl, no default -> no-op
                    Log.info("[PROXY] No DI impl or default for " + interfaceToProxy.getSimpleName() + "." + method.getName());
                    return null;


                    } catch (InvocationTargetException ite) {
                        throw ite.getCause(); // unwrap nested
                    }

                }
        );
    }

    private boolean isClassLoadable(IDependencyClass<CLASS> clazz) {
        try {
            clazz.getDeclaredMethods(); // will throw NoClassDefFoundError if deps missing
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }
    public Object createSelfProxy(Class<?> interfaceToProxy){
        if(!interfaceToProxy.isInterface()) {
            Log.error("[PROXY] " + interfaceToProxy.getSimpleName() + " is not a interface, skipping");
            return false;
        }
        try {
            Log.warn("[PROXY] Trying to create proxy for " + interfaceToProxy.getSimpleName() );
            return Proxy.newProxyInstance(interfaceToProxy.getClassLoader(),
                    new Class<?>[]{interfaceToProxy}, (proxyObj, method, args) -> {
                        if (method.isDefault()) {
                            return MethodHandles.lookup()
                                    .findSpecial(
                                            interfaceToProxy,
                                            method.getName(),
                                            MethodType.methodType(
                                                    method.getReturnType(),
                                                    method.getParameterTypes()
                                            ),
                                            interfaceToProxy
                                    )
                                    .bindTo(proxyObj)
                                    .invokeWithArguments(args);
                        }
                        Log.success("[PROXY] Method " + method.getName() + " called on proxy object");
                        return method.invoke(interfaceToProxy,args);

                    });
        } catch (Exception e) {
            Log.error("[PROXY] Failed to create proxy for " + interfaceToProxy.getSimpleName() );
            Log.exception(e);
        }


        return null;
    }


            /**
             * Gets all instances from the map.
             *
             * @return Collection of all dependency instances
             */
    @Override
    //TODO: Move to DependencyMetaData. Method will throw a StackOverflowError
    public List<Object> getAllInstances() {
        return getDependencyMap().getAllInstances();
    }





    //TODO: Compare usage with calculateDepthFor in this class


    public int computeDepthFor(IDependencyClass<CLASS> dependencyClass, Set<IDependencyClass<CLASS>> visitedClasses
            , Set<IDependencyClass<CLASS>> stackClasses) {

        if(getDependencyMetaData(dependencyClass) == null) {Log.warn("[DI] Dependency metadata for class " + dependencyClass.getName() + " not found. Skipping computation.");
            return 0;
        }
        //TODO: Make a method to get depeenecy from graph
        if (getDepth() > 0) return getDepth();
        if (stackClasses.contains(dependencyClass)) throw new RuntimeException("Cyclic dependency detected: " + dependencyClass.getName());
        stackClasses.add(dependencyClass);

        int maxDepDepth = 0;
            for (IDependencyClass<CLASS> dep : getSubDependenciesForBase()) {
                maxDepDepth = Math.max(maxDepDepth, computeDepthFor(dependencyClass, visitedClasses, stackClasses));
            }

        stackClasses.remove(dependencyClass);
        int depth = maxDepDepth + 1;
        setDepth(depth);
        visitedClasses.add(dependencyClass);
        return depth;
    }


}

