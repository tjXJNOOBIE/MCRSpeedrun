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
import com.tjxjnoobie.api.dependency.metadata.DependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.DependencyMetaDataHelper;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaDataHelper;
import com.tjxjnoobie.api.dependency.metadata.wrappers.DependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.wrappers.DependencyInterface;
import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInterface;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.console.style.LogColor;
import com.tjxjnoobie.api.platform.global.utils.CustomRunnable;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;


/**
 * DependencyInjectorHelper – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @param <INTERFACE>    the type parameter
 * @param <INSTANCE>> the type parameter
 * @author TJ
 * @since 10/12/2025
 */
public class DependencyInjectorHelper<INTERFACE, INSTANCE>
        implements IDependencyInjectorHelper<INTERFACE, INSTANCE> {

    //TODO: Extract file helper methods to a concerned domain
    private final Set<Class<? extends INTERFACE>> LOADED_INTERFACES = ConcurrentHashMap.newKeySet();

    private final Set<Class<? extends  INSTANCE>> LOADED_CONCRETES = ConcurrentHashMap.newKeySet();
    private final IDependencyMetaDataHelper<INTERFACE,INSTANCE> dependencyMetaDataHelper = new DependencyMetaDataHelper<INTERFACE,INSTANCE>();
//    private DependencyMap<INTERFACE,INSTANCE> dependencyMap = new DependencyMap<>();


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
    public void setupDISystem(IDependencyInterface<?> entryPoint) {
        Log.warn("[DI] ===== DI System Initialization Started =====");
        Log.info(" %YELLOW% [DI] --- Phase 1: Class Scanning ");
        scanPackage(BASE_PACKAGE,entryPoint.getClass().getClassLoader());
        Log.info("[DI] --- Phase 2: Annotation Scanning & Map Registration ");
        new CustomRunnable(){

            @Override
            public void run() {
                try {
                    registerDependenciesViaAnnotation();
                } catch (Throwable e) {
                    Log.exception(e);
                }
            }
        }.runTaskLater(5000L);


        Log.warn("[DI] ===== DI System Initialization Ended =====");

    }



    public void registerDependenciesViaAnnotation() {

        Log.warn("[DI-Helper] ====== Beginning annotation-driven DI registration ======");

        int skipped = 0;
        int registeredBindings = 0;

        for (Class<? extends INSTANCE> rawScannedConcrete : LOADED_CONCRETES) {

            //Grab all concretes with @DelegatesToInterface annotation
            DelegatesToInterface concreteAnnotation = rawScannedConcrete.getAnnotation(DelegatesToInterface.class);
            if(concreteAnnotation == null){
                //Concrete without annotation
                continue;
            }

            Set<Class<? extends INTERFACE>> validInterfaces = new LinkedHashSet<>();
            for (Class<?> linkedInterface : resolveLinkedInterfaces(concreteAnnotation)) {
                if (!doesConcreteImplementInterface(rawScannedConcrete, linkedInterface)) {
                    continue;
                }
                validInterfaces.add((Class<? extends INTERFACE>) linkedInterface);
            }

            if (validInterfaces.isEmpty()) {
                Log.warn("[DI-Helper] No valid delegated interfaces found for " + rawScannedConcrete.getSimpleName() + ", skipping...");
                skipped++;
                continue;
            }

            Class<? extends INTERFACE> primaryInterface = validInterfaces.iterator().next();
            IDependencyInterface<INTERFACE> wrappedLinkedInterface =
                    new DependencyInterface<>(primaryInterface);
            IDependencyInstance<INSTANCE> wrappedLinkedConcrete = new DependencyInstance<>(rawScannedConcrete);
            IDependencyMetaData<INTERFACE, INSTANCE> dependencyMetaData = new DependencyMetaData<>();
            dependencyMetaDataHelper.populateMetaData(dependencyMetaData, wrappedLinkedInterface, wrappedLinkedConcrete);

            for (Class<? extends INTERFACE> linkedInterface : validInterfaces) {
                DependencyMap.getDependencyMap().registerDependency(linkedInterface, dependencyMetaData);
                Object registeredInstance = DependencyMap.getDependencyMap().getInstance((Class<Object>) linkedInterface);
                if (registeredInstance != null) {
                    Log.critical("" + registeredInstance.getClass().getSimpleName());
                }
                registeredBindings++;
            }

            // If we reach here, we failed linkage or wrapping for rawScannedConcrete, skip.
        }



        Log.warn("[DI-Helper] Finished annotation DI registration, skipped classes: " + skipped
                + ", registered interface bindings: " + registeredBindings);
    }

    private Set<Class<?>> resolveLinkedInterfaces(DelegatesToInterface concreteAnnotation) {
        Set<Class<?>> linkedInterfaces = new LinkedHashSet<>();
        if (concreteAnnotation == null) {
            return linkedInterfaces;
        }

        addLinkedInterface(linkedInterfaces, concreteAnnotation.getLinkedInterface());
        Arrays.stream(concreteAnnotation.getLinkedInterfaces())
                .forEach(linkedInterface -> addLinkedInterface(linkedInterfaces, linkedInterface));
        return linkedInterfaces;
    }

    private void addLinkedInterface(Set<Class<?>> linkedInterfaces, Class<?> linkedInterface) {
        if (linkedInterface == null || linkedInterface == Void.class) {
            return;
        }
        linkedInterfaces.add(linkedInterface);
    }



    public boolean doesConcreteImplementInterface(Class<?> concrete,
            Class<?> targetInterface) {

        if (concrete == null || targetInterface == null) {
            Log.error("[DI-Helper] doesConcreteImplementInterface received null parameters");
            return false;
        }

        boolean result = targetInterface.isAssignableFrom(concrete);

        if (result) {
            Log.success("[DI-Helper] " + concrete.getSimpleName()
                    + " implements/extends " + targetInterface.getSimpleName());
        } else {
            Log.warn("[DI-Helper] " + concrete.getSimpleName()
                    + " does NOT implement/extend " + targetInterface.getSimpleName() + " skipping...");
        }

        return result;
    }



    public boolean isClassLoadable(Class<?> dependencyClass) {
        if (dependencyClass == null) {
            Log.warn("[DI-Helper] dependencyClass is null");
            return false;
        }
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
    public boolean isClassLoadable(IDependencyInterface<INTERFACE> dependencyInterface) {
        return isClassLoadable(dependencyInterface.getClass());
    }
    public boolean isClassLoadable(IDependencyInstance<INSTANCE> dependencyInstance) {
        return isClassLoadable(dependencyInstance.getClass());
    }




    private void processDependencyClasses(String className, ClassLoader loader) {
        try {
            Class<?> rawClass = Class.forName(className, false, loader);

            if (!isClassLoadable(rawClass)) return;

            if (rawClass.isInterface()) {
                LOADED_INTERFACES.add((Class<? extends INTERFACE>) rawClass);

                Log.success("[DI-Scan] Loaded interface: " + rawClass.getSimpleName());
            } else if (isConcrete(rawClass)) {
                LOADED_CONCRETES.add((Class<? extends INSTANCE>) rawClass);
                Log.success("[DI-Scan] Loaded concrete: " + rawClass.getSimpleName());
            }

        } catch (Throwable ignored) {
        }
    }
//    private Supplier<INSTANCE> createFactory(Class<?> concrete) {
//        return () -> {
//            try {
//                Log.warn("[DI-Helper] Creating instance of " + concrete.getSimpleName());
//                concrete.getDeclaredConstructor().setAccessible(true);
//                return (INSTANCE) concrete.getDeclaredConstructor().newInstance();
//            } catch (Exception e) {
//                //Fallback to UNSAFE if no no-args constructor exists
//                Log.exception(e);
//
//            }
//            return null;
//        };
//    }
    public void scanDirectory(String pkg, File dir, ClassLoader loader) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {

            if (file.isDirectory()) {
                scanDirectory(pkg + (pkg.isEmpty() ? "" : ".") + file.getName(), file, loader);
                continue;
            }

            if (!file.getName().endsWith(".class")) continue;

            String className =
                    pkg + (pkg.isEmpty() ? "" : ".") + file.getName().replace(".class", "");

            processDependencyClasses(className, loader);
        }
    }

    public void scanJar(File jarFile, String basePackage, ClassLoader loader) {
        if (jarFile == null || !jarFile.exists()) return;

        String prefix = basePackage.replace('.', '/') + "/";

        try (JarFile jar = new JarFile(jarFile)) {

            jar.stream()
                    .filter(e -> !e.isDirectory())
                    .filter(e -> e.getName().endsWith(".class"))
                    .filter(e -> e.getName().startsWith(prefix))
                    .forEach(entry -> {
                        String className = entry.getName()
                                .replace('/', '.')
                                .replace(".class", "");

                        processDependencyClasses(className, loader);
                    });

        } catch (Throwable e) {
            Log.error("[DI-Scan] Failed to scan JAR " + jarFile.getName());
            Log.exception(e);
        }
    }
    public  boolean isConcrete(Class<?> clazz) {
        int mods = clazz.getModifiers();
        return !clazz.isInterface()
                && !Modifier.isAbstract(mods)
                && !clazz.isEnum()
                && !clazz.isAnnotation();
    }

public void scanPackage(String basePackage, ClassLoader loader) {

    if (basePackage == null || basePackage.isBlank()) return;

    String path = basePackage.replace('.', '/');

    try {
        Enumeration<URL> resources = loader.getResources(path);

        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String protocol = url.getProtocol();

            switch (protocol) {

                case "file": {
                    File directory = new File(url.toURI());
                    scanDirectory(basePackage, directory, loader);
                    break;
                }

                case "jar": {
                    File jar = extractJarFile(url);
                    if (jar != null) scanJar(jar, basePackage, loader);
                    break;
                }

                // Zip or war-style archives
                case "zip":
                case "wsjar":
                case "war":
                case "zipfs":
                case "vfs":
                case "vfszip":
                case "bundleresource":
                case "bundle": {
                    File jar = extractJarFile(url);
                    if (jar != null) scanJar(jar, basePackage, loader);
                    break;
                }

                // Java module system (Java 9+)
                case "jrt": {
                    scanModulePath(basePackage, loader);
                    break;
                }

                default:
                    Log.warn("[DI-Scan] Unsupported protocol: " + protocol + " @ " + url);
                    break;
            }
        }

    } catch (Throwable e) {
        Log.error("[DI-Scan] Failed to scan package " + basePackage);
        Log.exception(e);
    }

    Log.info("[DI-Scan] " + LogColor.GRAY +
            "Loaded " + LOADED_INTERFACES.size() + " interfaces and " +
            LOADED_CONCRETES.size() + " concretes for package " + basePackage);
}
    private void scanModulePath(String basePackage, ClassLoader loader) {
        try {
            ModuleLayer layer = ModuleLayer.boot();

            for (Module module : layer.modules()) {
                // only scan modules that EXPORT the package you’re looking for
                if (module.isNamed() && module.getPackages().contains(basePackage)) {

                    try (InputStream in = module.getResourceAsStream(basePackage.replace('.', '/') + "/")) {
                        if (in == null) continue;

                        // JRT cannot list directories directly
                        // you'd need jrtfs for true scanning, we skip detailed module scanning for now
                        Log.warn("[DI-Scan] JRT scanning is not fully implemented for: " + basePackage);
                    }
                }
            }
        } catch (Throwable e) {
            Log.error("[DI-Scan] Failed to scan JRT module path for " + basePackage);
            Log.exception(e);
        }
    }
    private File extractJarFile(URL url) {
        try {
            String external = url.toExternalForm();

            // Handles: jar:file:/..., zip:file:/..., bundle:/..., wsjar:file:/...
            if (external.startsWith("jar:")) external = external.substring(4);

            // Extract before "!/"
            int idx = external.indexOf("!/");
            if (idx != -1) external = external.substring(0, idx);

            if (external.startsWith("file:")) {
                external = external.substring(5);
            }

            return new File(URLDecoder.decode(external, StandardCharsets.UTF_8));
        } catch (Throwable e) {
            Log.error("[DI-Scan] Failed to extract JAR path from URL: " + url);
            Log.exception(e);
            return null;
        }
    }




}
