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
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.maps.DependencyMap;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyClass;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.console.style.LogColor;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * DependencyInjectorHelper – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @param <CLASS>    the type parameter
 * @param <INSTANCE> the type parameter
 * @author TJ
 * @since 10/12/2025
 */
public class DependencyInjectorHelper<CLASS, INSTANCE>
        implements IDependencyInjectorHelper<CLASS, INSTANCE> {


    private Set<Class<?>> LOADED_CLASSES = ConcurrentHashMap.newKeySet();
    private final Unsafe UNSAFE = getUnsafe();

    /**
     * The Base package.
     */
    String BASE_PACKAGE = "com.tjxjnoobie";

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
    @Override
    public void setupDISystem(IDependencyClass<?> entryPoint) throws Throwable {
        Log.warn("[DI] ===== DI System Initialization Started =====");

        Log.info("[DI] --- Phase 1: Class Scanning - Scanning classes with filter...");


        Set<Class<?>> scannedClasses = scanFromBasePackage(BASE_PACKAGE, entryPoint.getClass().getClassLoader());
        if (scannedClasses.isEmpty()) {
            Log.error("[DI] Phase 2: Class Scanning - scannedClasses is empty!");
            return;
        }

        Log.success("[DI] Success: Scanned " + scannedClasses.size() + " classes from base package");
        Log.info("[DI] --- Phase 2: Annotation Scanning & Dependency Injection/Registration Started");
        registerDependenciesViaAnnotation(LOADED_CLASSES);

        Log.warn("[DI] ===== DI System Initialization Ended =====");

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
    public void registerDependenciesViaAnnotation(Set<Class<?>> classesToScan) throws Throwable {
        IDependencyMap<?, ?> dependencyMap = new DependencyMap<>();
        int skipped = 0;

        Log.info("[DI-Helper] " + LogColor.YELLOW + "Starting dual-phase DI registration" + LogColor.RESET);

        if (classesToScan == null) {
            Log.warn("[DI-Helper] " + LogColor.YELLOW + "SKIP" + LogColor.RESET + " No classes to scan");
            return;
        }

        // ===== PHASE 1: CONCRETE ANNOTATION SCAN =====
        for (Class<?> scannedDependency : classesToScan) {
            if (!isClassLoadable(scannedDependency)) {
                continue;
            }
            if (scannedDependency == null) {
                Log.error("[DI-HELPER] Candidate classes are null in scan! ");
                continue;
            }

            // Skip if annotation is not present

            if (!scannedDependency.isAnnotationPresent(DelegatesToInterface.class)) {
                skipped++;
                continue;
            }

            // Get the annotation
            DelegatesToInterface dependencyAnnotation = scannedDependency.getAnnotation(DelegatesToInterface.class);
            if (dependencyAnnotation == null) {
                Log.error("[DI-Helper] dependencyAnnotation is null for " + scannedDependency.getSimpleName());
                continue;
            }

            // Skip if it's an interface - we want concrete classes with the annotation
            if (scannedDependency.isInterface()) {
                Log.warn("[DI-Helper] " + scannedDependency.getSimpleName() + " is an interface, but @DelegatesToInterface should be on concrete classes");
                continue;
            }
            // The scanned class is the concrete implementation

            // Get the interface it delegates to from the annotation

            Class<?> dependencyInterface = dependencyAnnotation.getClassForDelegation();

            Log.info("[DI-Helper] " + LogColor.YELLOW + "Discovered: dependency " + dependencyInterface.getSimpleName() + " with concrete class " + scannedDependency.getSimpleName());

            // Verify the concrete class implements the interface
            if (!dependencyInterface.isAssignableFrom(scannedDependency)) {
                Log.warn("[DI-Helper] " + LogColor.YELLOW + "SKIP" + LogColor.RESET +
                        " " + scannedDependency.getSimpleName() + " does not implement " + dependencyInterface.getSimpleName());
                continue;
            }
            Class<?> newConcreteInstance = createInterfaceInstance(dependencyInterface, scannedDependency);
//            IDependencyClass<?> dependencyClass = new DependencyClass<>(dependencyInterface);
            dependencyMap.registerDependency(dependencyInterface, newConcreteInstance);

//            IDependencyInstance<INSTANCE> dependencyInstance = new DependencyInstance<>(newConcreteInstance);
//               createSelfProxy2(dependencyInterface);
                createProxy(dependencyInterface,scannedDependency);
//            Log.info("[DI-Helper] " + LogColor.YELLOW + "Registering dependency " + dependencyInterface.getSimpleName() + " with concrete instance -> " + newConcreteInstance.getClass().getSimpleName());
            dependencyMap.registerDependency(dependencyInterface, newConcreteInstance);

//            dependencyMap.getDependencyMap().populateMetaData(dependencyClass, dependencyInstance);

            //TODO: Check if we even need to do proxying
            // createProxyFor(getDependencyClass());


            // propagateInstancesAcrossHierarchy();


        }
        // ===== PHASE 2: INTERFACE EXTENSION RECURSION =====
//        Set<Class<?>> registeredInterfaces = new LinkedHashSet<>(getDependencyMap().getDependencies());
//        for (Class<?> iface : registeredInterfaces) {
//            if (!iface.isInterface()) continue;
        // recursivelyLinkExtendedInterfaces(iface, 0);


//        }
        Log.info("[DI-Helper] " + LogColor.YELLOW + "SUMMARY" + LogColor.RESET +
                " Total registered: " + dependencyMap.getDependencyMapSize() + ", including extended interface chains");
        Log.warn("[DI-Helper] Skipped: " + skipped + " classes due to missing annotations or invalid implementations");
    }

    public Object createInstance(Class<?> concreteClass) {
        try {
            Constructor<?> ctor = concreteClass.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + concreteClass.getName(), e);
        }
    }

    @Override
    public boolean isClassLoadable(Class<?> dependencyClass) {
        try {
            Class.forName(dependencyClass.getName(), false, dependencyClass.getClassLoader());
            dependencyClass.getDeclaredMethods(); // will throw NoClassDefFoundError if deps missing
            return true;
        } catch (NoClassDefFoundError e) {
            Log.error("[SCAN] " + dependencyClass.getSimpleName() + " is not a loadable class in the runtime, skipping");

            return false;
        } catch (ClassNotFoundException e) {
            Log.exception(e);
        }
        return false;
    }

    /**
     * Gets all loaded classes.
     *
     * @param basePackage the base package
     * @return the all loaded classes
     */
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
                } catch (Throwable ignored) {
                }
            }
        } else {
            Log.warn("[DI-Scan] " + LogColor.YELLOW + "Non-URL classloader, using fallback package scan");
            LOADED_CLASSES.addAll(scanFromBasePackage(BASE_PACKAGE, loader));
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
                    if (!isClassLoadable(rawClass)) {
                        continue;
                    }
                    LOADED_CLASSES.add(rawClass);
                } catch (Throwable ignored) {
                }
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
                        Class<?> rawClass = Class.forName(className, false, loader);
                        if (!isClassLoadable(rawClass)) {
                            continue;
                        }
                        LOADED_CLASSES.add(rawClass);
                        Log.success("[DI-Scan] Found class " + className);
                    } catch (Throwable ignored) {
                    }
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
     * @param loader      The ClassLoader to use
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

    @SuppressWarnings("unchecked")
    private Class<?> createInterfaceInstance(Class<?> dependencyInterface, Class<?> dependencyConcrete) {
        try {
            // Safety check: does the concrete actually implement the interface?
            if (!dependencyInterface.isAssignableFrom(dependencyConcrete)) {
                throw new IllegalArgumentException(
                        dependencyConcrete.getName() + " does not implement " + dependencyInterface.getName()
                );
            }



            // Try normal public no-arg constructor
            Object instance = UNSAFE.allocateInstance(dependencyConcrete);
            dependencyInterface.cast(instance);
            Log.success("[DI-Instance] Successfully created instance: " + instance.getClass().getName());
            // Optional: sanity-check that the instance really is assignable
            if (!dependencyInterface.isInstance(instance)) {
                throw new IllegalStateException(
                        "Created instance does not implement interface: " + dependencyInterface.getName()
                );
            }

            // ✅ Return the actual runtime class of the instance
            return instance.getClass();

        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Failed to instantiate " + dependencyConcrete.getName(), ex);
        }
    }

    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create proxy for object.
     *
     * @param interfaceToProxy the interface to proxy
     * @return the object
     */
    @SuppressWarnings("unchecked")
    public Object createProxyFor(CLASS interfaceToProxy) {
        if (!interfaceToProxy.getClass().isInterface()) {
            Log.error("[PROXY] " + interfaceToProxy.getClass().getSimpleName() + " is not a interface, skipping");
            return false;
        }

//        if (!isClassLoadable(interfaceToProxy)) {
//            Log.error("[PROXY] " + interfaceToProxy.getSimpleName() + " is not a loadable class in the runtime, skipping");
//            return false;
//        }
        try {

            if (getDependencyInstance() == null) {
                Log.error("[PROXY] Failed to create proxy for " + interfaceToProxy.getClass().getSimpleName() + " - instance is null");
            }
            if (getDependencyInstance() != null && Proxy.isProxyClass(getDependencyInstance().getClass())) {
                Log.info("[PROXY] Reusing existing proxy for " + interfaceToProxy.getClass().getSimpleName());
                return getDependencyInstance();
            }
            if (getDependencyInstance() != null && interfaceToProxy.getClass().isInstance(getDependencyInstance())) {
                // It's already an instance for this interface; treat it as existing proxy/impl
                Log.info("[PROXY] Existing instance found for " + interfaceToProxy.getClass().getSimpleName() + ", skipping proxy creation");
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
                        if (args == null) {
                            Log.warn("[PROXY] Null argument passed to method " + method.getName() + ", creating dummy object...");
                            args = new Object[0]; // Make sure the below statement is not null
                        }

                        // Graceful handling for core Object methods to avoid CCEs
                        if (method.getDeclaringClass() == Object.class) {
                            switch (method.getName()) {
                                case "toString":
                                    return interfaceToProxy.getClass().getName() + "Proxy@";
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
                        } else {
                            Log.warn("[PROXY] Method " + method.getName() + " is not default in -> " + interfaceToProxy.getClass().getSimpleName());
                        }

                        // No impl, no default -> no-op
                        Log.info("[PROXY] No DI impl or default for " + interfaceToProxy.getClass().getSimpleName() + "." + method.getName());
                        return null;


                    } catch (InvocationTargetException ite) {
                        throw ite.getCause(); // unwrap nested
                    }

                }
        );
    }
    @SuppressWarnings("unchecked")
    public Object createProxy(Class<?> iface, Class<?> concreteInstance) {
        if (iface != null || concreteInstance != null) {
            return Proxy.newProxyInstance(
                    iface.getClassLoader(),
                    new Class<?>[]{iface},
                    (proxy, method, args) -> {
                        Log.warn("[PROXY] Trying to register proxy interface " + proxy.toString() + "-> " + proxy.getClass().getSimpleName() + " interface");
                        return invokeDefaultMethod(proxy, method, args);
                    }

            );
        }
        Log.error("[PROXY] Failed to create proxy: iface or concreteInstance are null");
        return null;
    }
    public void createSelfProxy(Class<?> iface) throws Exception {
        if (iface != null) {
            Log.info("[PROXY] Creating self proxy for " + iface.getSimpleName());
            Proxy.newProxyInstance(
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
    }
    public Object createSelfProxy2(Class<?> iface) throws Throwable {
        if(iface != null) {
            Log.info("[PROXY] Creating self proxy for " + iface.getSimpleName());
            return Proxy.newProxyInstance(
                    iface.getClassLoader(),
                    new Class<?>[]{iface},
                    (proxyObj, method, args) -> {
                        if (method.isDefault()) {
                            MethodHandles.Lookup lookup;
                            try {
                                Constructor<MethodHandles.Lookup> ctor =
                                        MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                                ctor.setAccessible(true);
                                lookup = ctor.newInstance(iface, MethodHandles.Lookup.PRIVATE);
                            } catch (NoSuchMethodException e) {
                                lookup = MethodHandles.lookup().in(iface);
                            }

                            return lookup.unreflectSpecial(method, iface)
                                    .bindTo(proxyObj)
                                    .invokeWithArguments(args);
                        }

                        throw new UnsupportedOperationException(
                                "Unimplemented interface call: " + method.getName() + " in " + iface.getSimpleName());
                    });
        }
        return null;
    }
    public Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        final Class<?> declaringClass = method.getDeclaringClass();
        // The "special" lookup lets us access private interface implementations
        return MethodHandles.lookup()
                .in(declaringClass)
                .findSpecial(declaringClass, method.getName(),
                        MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                        declaringClass)
                .bindTo(proxy)
                .invokeWithArguments(args);
    }




}

