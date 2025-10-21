/*
 * TJVD License (TJ Valentine's Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps;

import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.DependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Custom map for storing dependency metadata with Class as key and IDependencyMetaData as value.
 * This map provides specialized operations for dependency injection and metadata management.
 * Delegates injection and bind operations to IDependencyInjectorHelper.
 *
 * @author TJ
 * @since 2025
 */
public class DependencyMap extends ConcurrentHashMap<Class<?>, IDependencyMetaData> implements IDependencyMap {






//    /**
//     * PreConstruct initialization method with priority 10.
//     * Initializes the dependency map after DI system is ready.
//     * This runs after DependencyInjectorHelper (priority 0).
//     */
//    @PreConstruct(priority = 10)
//    public void initializeDependencyMap() {
//        Log.info("[DependencyMap] ===== Initializing Dependency Map =====");
//        Log.info("[DependencyMap] Current size: " + size());
//
//        // Validate existing entries
//        int validEntries = 0;
//        int invalidEntries = 0;
//
//        for (Map.Entry<Class<?>, IDependencyMetaData> entry : entrySet()) {
//            if (entry.getValue() != null && entry.getValue().getInstance(entry.) != null) {
//                validEntries++;
//            } else {
//                invalidEntries++;
//            }
//        }
//
//        Log.info("[DependencyMap] Valid entries: " + validEntries);
//        if (invalidEntries > 0) {
//            Log.warn("[DependencyMap] Invalid entries (no instance): " + invalidEntries);
//        }
//
//        // Set up injector helper if available
//        if (injectorHelper != null) {
//            Log.info("[DependencyMap] Injector helper is configured");
//        } else {
//            Log.warn("[DependencyMap] No injector helper configured - delegation disabled");
//        }
//
//        Log.success("[DependencyMap] Dependency Map initialized successfully");
//    }

    /**
     * Registers a dependency with its instance, factory, and source context.
     * Creates and populates metadata automatically.
     *
     * @param clazz The class type of the dependency
     * @param instance The actual instance of the dependency
     * @param factory The factory supplier for creating new instances
     * @param sourceContext The context that owns this dependency
     */
    @Override
    public void registerDependency(Class<?> clazz, Object instance, Supplier<?> factory, IContext<?> sourceContext) {
        Log.info("[DependencyMap] Attempting to register dependency: clazz=" + (clazz != null ? clazz.getName() : "null") +
                ", hasInstance=" + (instance != null) + ", hasFactory=" + (factory != null) + 
                ", hasContext=" + (sourceContext != null));
        
        if (clazz == null) {
            Log.error("[DependencyMap] Registration failed: Class parameter is null");
            throw new IllegalArgumentException("Class must be non-null");
        }

        try {
            // Allow null instance to support factory-only registration
            Log.info("[DependencyMap] Creating metadata for: " + clazz.getSimpleName());
            DependencyMetaData metaData = new DependencyMetaData(clazz);
            
            if (instance != null) {
                Log.info("[DependencyMap] Setting instance for " + clazz.getSimpleName() + 
                        " -> " + instance.getClass().getSimpleName());
                metaData.setInstance(instance);
                
                Log.info("[DependencyMap] Populating metadata from instance class: " + instance.getClass().getName());
                metaData.populateMetaData(instance.getClass());
                Log.info("[DependencyMap] Metadata population completed for: " + clazz.getSimpleName());
            } else {
                Log.info("[DependencyMap] No instance provided - factory-only registration for: " + clazz.getSimpleName());
            }
            
            if (factory != null) {
                Log.info("[DependencyMap] Setting factory supplier for: " + clazz.getSimpleName());
                metaData.setFactory(factory);
            } else {
                Log.info("[DependencyMap] No factory supplier provided for: " + clazz.getSimpleName());
            }
            
            if (sourceContext != null) {
                Log.info("[DependencyMap] Setting source context for: " + clazz.getSimpleName() + 
                        " from context: " + sourceContext.getClass().getSimpleName());
                metaData.setSourceContext(sourceContext);
            } else {
                Log.info("[DependencyMap] No source context provided for: " + clazz.getSimpleName());
            }

            put(clazz, metaData);

            Log.success("[DependencyMap] Successfully registered: " + clazz.getSimpleName() +
                    (instance != null ? " -> " + instance.getClass().getSimpleName() : " (factory only)") +
                    (factory != null ? " [with factory]" : "") +
                    (sourceContext != null ? " [from " + sourceContext.getClass().getSimpleName() + "]" : ""));
        } catch (Exception e) {
            Log.error("[DependencyMap] Failed to register dependency: " + clazz.getSimpleName() + 
                    " - Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw e;
        }
    }


    /**
     * Registers a dependency with minimal information.
     * Factory and source context can be set later.
     *
     * @param clazz The class type of the dependency
     * @param instance The actual instance of the dependency
     */
    @Override
    public void registerDependency(Class<?> clazz, Object instance) {
        registerDependency(clazz, instance, null, null);
    }

    /**
     * Registers a dependency with optional factory but without a source context.
     */
    @Override
    public void registerDependency(Class<?> clazz, Object instance, Supplier<?> factory) {
        registerDependency(clazz, instance, factory, null);
    }



    /**
     * Updates the instance for an existing dependency.
     *
     * @param clazz The class type
     * @param instance The new instance
     * @param <U> The type of the dependency
     */
    @Override
    public <U> void updateInstance(Class<U> clazz, U instance) {
        IDependencyMetaData metaData = get(clazz);
        if (metaData != null) {
            metaData.setInstance(instance);
            Log.info("[DependencyMetaDataMap] Updated instance for: " + clazz.getSimpleName());
        } else {
            Log.warn("[DependencyMetaDataMap] Cannot update instance - no metadata found for: " + clazz.getSimpleName());
        }
    }





    /**
     * Retrieves the metadata for a dependency of the specified class type.
     *
     * @param clazz The class type of the dependency to retrieve metadata for
     * @return The metadata associated with the given class, or null if no such dependency is registered
     */
    @Override
    public IDependencyMetaData getDependency(Class<?> clazz){
        return get(clazz);
    }


    /**
     * Gets all metadata sorted by priority and depth.
     *
     * @return List of sorted metadata
     */
    @Override
    public List<IDependencyMetaData> getSortedMetaData() {
        return values().stream()
                .sorted(Comparator.comparingInt(IDependencyMetaData::getPriority)
                        .thenComparingInt(IDependencyMetaData::getDepth))
                .collect(Collectors.toList());
    }

    /**
     * Gets all dependencies with a specific role.
     *
     * @param role The dependency role to filter by
     * @return List of metadata with the specified role
     */
    @Override
    public List<IDependencyMetaData> getByRole(DependencyRole role) {
        return values().stream()
                .filter(meta -> meta.getRole() == role)
                .collect(Collectors.toList());
    }

    /**
     * Gets all dependencies from a specific context.
     *
     * @param context The source context to filter by
     * @return List of metadata from the specified context
     */
    @Override
    public List<IDependencyMetaData> getByContext(IContext<?> context) {
        return values().stream()
                .filter(meta -> meta.getSourceContext() == context)
                .collect(Collectors.toList());
    }

    /**
     * Finds a dependency by assignable type.
     * Searches for a registered class that is assignable from the given class.
     *
     * @param clazz The class type to search for
     * @return The instance if found, null otherwise
     */
    @SuppressWarnings("unchecked")
    @Override
    public IDependencyMetaData findByAssignableType(Class<?> clazz) {
        return getDependency(clazz);
    }


    /**
     * Removes a dependency and returns its metadata.
     *
     * @param clazz The class type to remove
     * @return The removed metadata, or null if not found
     */
    @Override
    public IDependencyMetaData removeDependency(Class<?> clazz) {
        IDependencyMetaData removed = remove(clazz);
        if (removed != null) {
            Log.info("[DependencyMap] Removed: " + clazz.getSimpleName());
        }
        return removed;
    }

    /**
     * Gets statistics about the dependencies in this map.
     *
     * @return Map of statistic names to values
     */
    @Override
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", size());
        stats.put("withInstances", (int) values().stream()
                .map(this::ensureAndGetInstance)
                .filter(Objects::nonNull)
                .count());
        stats.put("withFactories", (int) values().stream()
                .filter(m -> m.getFactory() != null)
                .count());
        stats.put("base", (int) values().stream()
                .filter(m -> m.getRole() == DependencyRole.BASE)
                .count());
        stats.put("intermediate", (int) values().stream()
                .filter(m -> m.getRole() == DependencyRole.INTERMEDIATE)
                .count());
        stats.put("terminal", (int) values().stream()
                .filter(m -> m.getRole() == DependencyRole.TERMINAL)
                .count());
        stats.put("isolated", (int) values().stream()
                .filter(m -> m.getRole() == DependencyRole.ISOLATED)
                .count());

        return stats;
    }

    /**
     * Generates a detailed report of all dependencies.
     *
     * @return String containing the formatted report
     */
    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== Dependency MetaData Map Report ===\n");
        report.append("Total Dependencies: ").append(size()).append("\n\n");

        Map<String, Integer> stats = getStatistics();
        report.append("Statistics:\n");
        stats.forEach((key, value) ->
                report.append("  ").append(key).append(": ").append(value).append("\n"));

        report.append("\nDependencies by Role:\n");
        for (DependencyRole role : DependencyRole.values()) {
            List<IDependencyMetaData> byRole = getByRole(role);
            if (!byRole.isEmpty()) {
                report.append("  ").append(role).append(" (").append(byRole.size()).append("):\n");
                byRole.forEach(meta -> {
                    Class<?> clazz = meta.getDependencyClass();
                    Object instance = ensureAndGetInstance(meta);
                    report.append("    - ").append(clazz != null ? clazz.getSimpleName() : "Unknown")
                            .append(" -> ").append(instance != null ? instance.getClass().getSimpleName() : "NULL")
                            .append(" [depth=").append(meta.getDepth())
                            .append(", priority=").append(meta.getPriority()).append("]\n");
                });
            }
        }

        return report.toString();
    }
    /**
     * Checks if a dependency is registered (has metadata).
     *
     * @param clazz The class type to check
     * @return true if registered, false otherwise
     */
    @Override
    public boolean isRegistered(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        // Check direct registration
        if (containsKey(clazz)) {
            return true;
        }

        return values().stream()
                .map(this::ensureAndGetInstance)
                .anyMatch(instance -> instance != null && clazz.isInstance(instance));
    }

    @Override
    public List<Object> getAllInstances() {
        return values().stream()
                .map(this::ensureAndGetInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
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