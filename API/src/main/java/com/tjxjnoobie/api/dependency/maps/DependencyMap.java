/*
 * TJVD License (TJ Valentine's Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps;

import com.tjxjnoobie.api.dependency.annotations.DelegatesToInterface;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyClass;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.console.style.LogColor;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Custom map for storing dependency metadata with Class as key and IDependencyMetaData as value.
 * This map provides specialized operations for dependency injection and metadata management.
 * Delegates injection and bind operations to IDependencyInjectorHelper.
 *
 * @author TJ
 * @since 2025
 */
@DelegatesToInterface(getClassForDelegation = IDependencyMap.class) // Set up DI scanning for conrete/interface relationship
public class DependencyMap<CLASS extends IDependencyClass<?>, // Create & Extend variable CLASS to IDependencyClass interface
        INSTANCE extends IDependencyInstance<?>> // Create & Extend variable INSTANCE to IDependencyInstance
        extends ConcurrentHashMap<CLASS, INSTANCE> // Create a custom map via extension; Use extended variables CLASS AND INSTANCE in our extended MAP.
        implements IDependencyMap<CLASS, INSTANCE> { // Pass CLASS and INSTANCE variables to this classes interface so its usable and type safe by subclasses

    public IDependencyClass<CLASS> dependencyClass; // Our key for the extended map, should be a interface dependencyClass = getDependencyClass(); // Our key for the extended map, should be a interface
    public IDependencyMetaData<CLASS, INSTANCE> dependencyMetaData = getDependencyMetaData(dependencyClass); // Our metadata with shared class (key) and instance (value)
    public IDependencyInstance<INSTANCE>  dependencyInstance = getDependencyInstance();
    String dependencyInstanceClassName = dependencyInstance.getClass().getSimpleName();
    String dependencyClassName = dependencyClass.getSimpleName(); // This should be a interface


    @Override
    public void registerDependency() {

        // guards first
        if (dependencyClass == null) {
            Log.error("[DependencyMap] registerDependency: dependencyClass is null");
        }
        if (dependencyMetaData == null) {
            Log.error("[DependencyMap] registerDependency: dependencyMetaData is null");
        }
        if (dependencyInstance == null) {
            Log.warn("[DependencyMap] registerDependency: dependencyInstance is null (no factory bound)");
        }
        // use defaults on our types only; no redefinitions
        setDependencyClass(dependencyClass);
        setDependencyInstance(dependencyInstance);
        //TODO: Add a way to auto grab source context, perhaps from the scan
        // we still need to re work the context sustem though. Context could be
        // obsolete due to how dynamic our DI system is
        // setSourceContext();
        //TODO: Add scanned packages to setSubDependenciesForBase method
        // setSubDependenciesForBase();
        // persist typed key/value via map defaults
        // rely on IDependencyClass/IDependencyInstance defaults for class/raw access
        if (!isRegistered(dependencyClass)) {
            put((CLASS) dependencyClass, (INSTANCE) dependencyInstance); // Use put so we can have duplicate classes as keys with different instances
        }

        // populate metadata strictly via defaults/helpers
        Class<?> rawType = this.dependencyClass.getClass(); // raw class object available through our typed key
        populateMetaData();

        Log.success("[DependencyMap] Registered: " + dependencyClass.getSimpleName() + " -> " + dependencyInstanceClassName);
    }

    public void registerDependency(CLASS dependencyClass){

    }
    public void registerDependency(IDependencyClass<CLASS> dependencyClass, IDependencyInstance<INSTANCE> dependencyInstance){

    }
    @Override
    public DependencyMap<CLASS, INSTANCE> getDependencyMap() {
        return this;
    }
    //TODO: Make a new overload method so we can plugin in classes directly
//    /**
//     * Registers a dependency with minimal information.
//     * Factory and source context can be set later.
//     *
//     * @param clazz The class type of the dependency
//     * @param instance The actual instance of the dependency
//     */
//    @Override
//    public void registerDependency(IDependencyClass<CLASS> dependencyClass, IDependencyInstance<INSTANCE> dependencyInstance) {
//        registerDependency(dependencyClass, dependencyInstance, null, null);
//    }
//
//    /**
//     * Registers a dependency with optional factory but without a source context.
//     */
//    @Override
//    public void registerDependency(IDependencyMetaData clazz, Object instance, Supplier<?> factory) {
//        registerDependency(clazz, instance, factory, null);
//    }


    //TODO: Delegate this method to I/DepenencyFactory
//    /**
//     * Updates the instance for an existing dependency.
//     *
//     * @param clazz The class type
//     * @param instance The new instance
//     * @param <U> The type of the dependency
//     */
//    @Override
//    public <U> void updateInstance(Class<U> clazz, U instance) {
//        IDependencyMetaData metaData = get(clazz);
//        if (metaData != null) {
//            metaData.setInstance(instance);
//            Log.info("[DependencyMetaDataMap] Updated instance for: " + clazz.getSimpleName());
//        } else {
//            Log.warn("[DependencyMetaDataMap] Cannot update instance - no metadata found for: " + clazz.getSimpleName());
//        }
//    }


    /**
     * Retrieves all registered dependency metadata entries from this map.
     *
     * @return A collection containing the metadata for all registered dependencies
     */
    @Override
    public Collection<INSTANCE> getRawDependencyMapValues() {
        return values();
    }


    /**
     * Gets all dependencies with a specific role.
     *
     * @param role The dependency role to filter by
     * @return List of metadata with the specified role
     */
    @Override
    public List<INSTANCE> getByRole(DependencyRole role) {
        return values().stream()
                .filter(meta -> getDependencyRole() == role)
                .collect(Collectors.toList());
    }

    /**
     * Gets all dependencies from a specific context.
     *
     * @param context The source context to filter by
     * @return List of metadata from the specified context
     */
    @Override
    public List<INSTANCE> getByContext(IContext<?> context) {
        return values().stream()
                .filter(meta -> getSourceContext() == context)
                .collect(Collectors.toList());
    }

//    @Override
//    public IDependencyMetaData ensureAndGetInstance(IDependencyMetaData classToEnsure) {
//        if (classToEnsure == null) {
//            return null;
//        }
//
//        IDependencyMetaData instance = getDependencyMap().getDependencyInstance(getDependencyClass());
//        if (instance != null) {
//            return instance;
//        }
//
//        Supplier<?> factory = getDependencyMap().getDependency(classToEnsure.getClass()).getFactory();
//        if (factory != null) {
//            IDependencyMetaData created = (IDependencyMetaData) factory.get();
//            if (created != null) {
//                IDependencyMetaData ensuredInstance = new DependencyMetaData(classToEnsure.getClass());
//                ensuredInstance.setInstance(created);
//                return created;
//            }
//        }
//
//        return null;
//    }

//    /**
//     * Finds a dependency by assignable type.
//     * Searches for a registered class that is assignable from the given class.
//     *
//     * @param dependencyToFind The dependency class type to search for
//     * @return The instance if found, null otherwise
//     */
//    @Override
//    public Class<?> findByAssignableType(IDependencyMetaData<CLASS> dependencyToFind) {
//        if (dependencyToFind == null) {
//            return null;
//        }
//
//        Class<?> direct = getRawDependencyClass();
//        if (direct != null) {
//            return direct;
//        }
//
//        for (IDependencyMetaData dependency : getMetaData(dependencyToFind)) {
//            Class<?> registeredType = getDependency(dependency.getDependencyClass()).getDependencyClass();
//            Class<?> meta = getDependencyMap().getMetaData(getDe();
//            if (registeredType != null && dependencyToFind.isAssignableFrom(registeredType)) {
//                return meta;
//            }
//
//            Object instance = getDependencyMap().getDependencyInstance(meta);
//            if (dependencyToFind.isInstance(instance)) {
//                return meta;
//            }
//        }
//
//        return null;
//    }

//    @Override
//    public IDependencyMetaData getDependencyInstance(Class<?> clazz) {
//        if (clazz == null) {
//            return null;
//        }
//
//        Class<?> compatible = findByAssignableType(clazz);
//        return compatible != null ? ensureAndGetInstance(compatible) : null;
//    }


    @Override
    public void removeDependency(CLASS dependencyClass) {
        if (dependencyClass != null) {
            remove(dependencyClass);

            Log.info("[DependencyMap] Removed: " + dependencyClassName);
        }
    }

    /**
     * Gets statistics about the dependencies in this map.
     *
     * @return Map of statistic names to values
     */
//    @Override
//    public Map<String, Integer> getStatistics() {
//        Map<String, Integer> stats = new HashMap<>();
//        stats.put("total", size());
//        stats.put("withInstances", (int) values().stream()
//                .map(this::ensureAndGetInstance)
//                .filter(Objects::nonNull)
//                .count());
//        stats.put("withFactories", (int) values().stream()
//                .filter(m -> m.getFactory() != null)
//                .count());
//        stats.put("base", (int) values().stream()
//                .filter(m -> m.getRole() == DependencyRole.BASE)
//                .count());
//        stats.put("intermediate", (int) values().stream()
//                .filter(m -> m.getRole() == DependencyRole.INTERMEDIATE)
//                .count());
//        stats.put("terminal", (int) values().stream()
//                .filter(m -> m.getRole() == DependencyRole.TERMINAL)
//                .count());
//        stats.put("isolated", (int) values().stream()
//                .filter(m -> m.getRole() == DependencyRole.ISOLATED)
//                .count());
//
//        return stats;
//    }
//
//    /**
//     * Generates a detailed report of all dependencies.
//     *
//     * @return String containing the formatted report
//     */
//    @Override
//    public String generateReport() {
//        StringBuilder report = new StringBuilder();
//        report.append("=== Dependency MetaData Map Report ===\n");
//        report.append("Total Dependencies: ").append(size()).append("\n\n");
//
//        Map<String, Integer> stats = getStatistics();
//        report.append("Statistics:\n");
//        stats.forEach((key, value) ->
//                report.append("  ").append(key).append(": ").append(value).append("\n"));
//
//        report.append("\nDependencies by Role:\n");
//        for (DependencyRole role : DependencyRole.values()) {
//            List<IDependencyMetaData> byRole = getByRole(role);
//            if (!byRole.isEmpty()) {
//                report.append("  ").append(role).append(" (").append(byRole.size()).append("):\n");
//                byRole.forEach(meta -> {
//                    Class<?> clazz = meta.getDependencyClass();
//                    Object instance = ensureAndGetInstance(meta);
//                    report.append("    - ").append(clazz != null ? clazz.getSimpleName() : "Unknown")
//                            .append(" -> ").append(instance != null ? instance.getClass().getSimpleName() : "NULL")
//                            .append(" [depth=").append(meta.getDepth())
//                            .append(", priority=").append(meta.getPriority()).append("]\n");
//                });
//            }
//        }
//
//        return report.toString();
//    }
    @Override
    public boolean isRegistered(IDependencyClass<CLASS> dependencyClass, boolean ensureInstance) {
        if (dependencyClass == null) {
            Log.warn("[DependencyMap] " + LogColor.YELLOW + "isRegistered" + LogColor.RESET + " called with " + LogColor.RED + "null" + LogColor.RESET + " dependency class");
            return false;
        }
        String className = dependencyClass.getSimpleName();

        Log.info("[DependencyMap] Checking registration for " + LogColor.CYAN + className + LogColor.RESET + " (ensureInstance=" + ensureInstance + ")");

        // Check direct registration from the extended ConcurrentMap
        if (containsKey(dependencyClass)) {
            Log.success("[DependencyMap] Already registered in DependencyMap -> " + LogColor.CYAN + className);
            return true;
        }

        Log.info("[DependencyMap] No direct registration found, checking instance assignments...");
        return false;
    }

        // Check instances based on ensureInstance flag
        //TODO: Instance ensuring disabled until we can finish ensureAndGetInstance method
//        boolean found = values().stream()
//                .anyMatch(metaData -> {
//                    if (metaData == null) {
//                        return false;
//                    }
//
//
//                    if (ensureInstance) {
//                        // Use the existing method that tries factory if instance is null
//                        Log.info("[DependencyMap] " + LogColor.YELLOW + "Attempting" + LogColor.RESET + " factory creation for: " + clazz.getSimpleName());
//                        dependencyInstance = ensureAndGetInstance(metaData);
//                        if (dependencyInstance != null) {
//                            Log.info("[DependencyMap] " + LogColor.GREEN + "Created" + LogColor.RESET + " instance via factory: " + instance.getClass().getSimpleName());
//                        }
//                    } else {
//                        // Only get existing instance, don't create via factory
//                        Log.info("[DependencyMap] Checking " + LogColor.BOLD + "existing instance only" + LogColor.RESET + " (no factory)");
//                        dependencyInstance = metaData.getDependencyInstance(metaData.getDependencyClass());
//                    }
//
//                    boolean matches = dependencyInstance != null && dependencyClass.isInstance(dependencyInstance);
//                    if (matches) {
//                        Log.success("[DependencyMap] " + LogColor.GREEN + "MATCH" + LogColor.RESET + " found: " + clazz.getSimpleName()
//                                + " -> " + dependencyInstance);
//                    }
//                    return matches;
//                });
        
//        if (found) {
//            Log.success("[DependencyMap] " + LogColor.GREEN + "✓" + LogColor.RESET + " Registration confirmed for: " + LogColor.CYAN + clazz.getSimpleName() + LogColor.RESET);
//        } else {
//            Log.warn("[DependencyMap] " + LogColor.RED + "✗" + LogColor.RESET + " No registration found for: " + LogColor.YELLOW + clazz.getSimpleName() + LogColor.RESET);
//        }
        
//        return found;
//    }

    /**
     * Checks if a dependency is registered (has metadata).
     * Defaults to attempting factory creation if no instance exists.
     *
     * @return true if registered, false otherwise
     */
    @Override
    public boolean isRegistered(IDependencyClass<CLASS> dependencyClass) {
        String className = dependencyClass.getSimpleName();
        Log.info("[DependencyMap] isRegistered called for " + LogColor.CYAN + className + LogColor.RESET
                + " (defaulting to " + LogColor.BOLD + "ensureInstance=false" + LogColor.RESET + ")");
        return isRegistered(dependencyClass, false);
    }
    //TODO: Create new getALlInstances method
//    @Override
//    public List<INSTANCE> getAllInstances() {
//        return values().stream()
//                .map(INSTANCE)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }
    /**
     * Gets the total number of registered dependencies in this map.
     *
     * @return The number of dependencies currently registered
     */
    @Override
    public int getDependencyMapSize() {
        return size();
    }

    /**
     * Clears all dependencies and logs the action.
     */
    @Override
    public void clear() {
        int count = size();
        super.clear();
        Log.info("[DependencyMap] Cleared " + count + " dependencies");
    }


}