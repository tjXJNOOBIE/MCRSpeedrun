/*
 * TJVD License (TJ Valentine's Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps;

import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.DependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Inject private IDependencyInjectorHelper injectorHelper;




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
    public void registerDependency(Class<?> clazz, Object instance, Supplier<?> factory, IContext<?> sourceContext) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must be non-null");
        }
        // Allow null instance to support factory-only registration
        DependencyMetaData metaData = new DependencyMetaData(clazz);
        if (instance != null) {
            metaData.setInstance(instance);
            metaData.populateMetaData(instance.getClass());
        }
        metaData.setFactory(factory);
        metaData.setSourceContext(sourceContext);

        put(clazz, metaData);
        Log.info("[DependencyMetaDataMap] Registered: " + clazz.getSimpleName() +
                (instance != null ? " -> " + instance.getClass().getSimpleName() : " (factory only)"));
    }
    @Override
    public Object ensureInstance(IDependencyMetaData metaData) {
        if (metaData == null) {
            return null;
        }

        Object instance = metaData.getDependencyInstance(metaData.getDependencyClass());
        if (instance == null && metaData.getFactory() != null) {
            instance = metaData.getFactory().get();
            if (instance != null) {
                metaData.setInstance(instance);
            }
        }
        return instance;
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





    @Override
    public IDependencyMetaData getDependency(Class<?> clazz){
        return get(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> U getInstance(Class<U> clazz) {
        if (clazz == null) {
            return null;
        }

        IDependencyMetaData metaData = get(clazz);
        Object instance = ensureInstance(metaData);
        if (clazz.isInstance(instance)) {
            return (U) instance;
        }

        for (IDependencyMetaData other : values()) {
            if (other == metaData) {
                continue;
            }
            Object candidate = ensureInstance(other);
            if (clazz.isInstance(candidate)) {
                return (U) candidate;
            }
        }
        return null;
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
     * @param <U> The type of the dependency
     * @return The instance if found, null otherwise
     */
    @SuppressWarnings("unchecked")
    //TODO: Replace IDepende... implementation with interface methods
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
            Log.info("[DependencyMetaDataMap] Removed: " + clazz.getSimpleName());
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
                .map(this::ensureInstance)
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
                    Object instance = ensureInstance(meta);
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
                .map(this::ensureInstance)
                .anyMatch(instance -> instance != null && clazz.isInstance(instance));
    }

    @Override
    public Collection<Object> getAllInstances() {
        return values().stream()
                .map(this::ensureInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    /**
     * Clears all dependencies and logs the action.
     */
    @Override
    public void clear() {
        int count = size();
        super.clear();
        Log.info("[DependencyMetaDataMap] Cleared " + count + " dependencies");
    }


}