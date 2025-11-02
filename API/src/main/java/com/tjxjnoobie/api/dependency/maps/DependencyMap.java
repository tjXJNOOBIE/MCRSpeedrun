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
import com.tjxjnoobie.api.dependency.metadata.DependencyClass;
import com.tjxjnoobie.api.dependency.metadata.DependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyClass;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.console.style.LogColor;
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
@DelegatesToInterface(getClassForDelegation = IDependencyMap.class)
public class DependencyMap<T> extends ConcurrentHashMap<IDependencyClass<T>, IDependencyMetaData<T>> implements IDependencyMap<T>, IDependencyClass<T>{

    public IDependencyClass<T> dependencyClass = new DependencyClass<>();
    public IDependencyMetaData<T> classMetaData = getDependencyMetadata();
    public Supplier<? extends T> dependencyFactory = getDependencyInstanceWithFactory();

    /**
     * Registers a dependency with its dependencyInstance, dependencyFactory, and source context.
     * Creates and populates metadata automatically.
     *
     * @param clazz The class type of the dependency
     * @param dependencyInstance The actual dependencyInstance of the dependency
     * @param dependencyFactory The dependencyFactory supplier for creating new instances
     * @param dependencyContext The context that owns this dependency
     */
    @Override
    public void registerDependency(IDependencyClass<T> dependencyClass) {

        if (clazzMeta == null || clazzMeta.getDependencyClass() == null) {
            Log.error("[DependencyMap] Registration failed: IDependencyMetaData or its dependencyClass is null");
            throw new IllegalArgumentException("IDependencyMetaData and its dependencyClass must be non-null");
        }

        if (clazz == null) {
            Log.error("[DependencyMap] Registration failed: Class parameter is null");
            throw new IllegalArgumentException("Class must be non-null");
        }
        //TODO: Replace these interface calls with our global fields in the class since they're type safe via T parameter
        IDependencyClass depClass = clazzMeta.getDependencyClass();
        IDependencyMetaData storedMeta = computeIfAbsent(depClass, c -> clazzMeta);
        if (storedMeta != clazzMeta) {
            // Merge essential fields from the provided metadata if caller passed a fresh one
            if (getFactory() != null) storedMeta.setFactory(getFactory());
            //TODO: Make getSourceContext have methods parameters so we can search for the source context if we have one,
            // if we don't have one, fail gracefully and log.
            if (getSourceContext() != null) storedMeta.setSourceContext(getSourceContext());
            if (getPriority() != 0) storedMeta.setPriority(getPriority());
        }

        try {
            // Allow null dependencyInstance to support dependencyFactory-only registration
            Log.info("[DependencyMap] Creating metadata for: " + getSimpleName());
            IDependencyMetaData metaData = new DependencyMetaData(storedMeta);
            // Attach dependencyInstance if provided
            if (dependencyClass != null) {
                storedMeta.setInstance(dependencyInstance);

                // Populate metadata using the most specific available class:
                // - Prefer dependencyInstance class (captures concrete type, including proxies/impls)
                // - Fallback to declared dependency class
                Class<?> toPopulateFrom = dependencyInstance.getClass() != null ? dependencyInstance.getClass() : depClass;
                try {
                    storedMeta.populateMetaData(toPopulateFrom);
                    Log.success("[DependencyMap] Populated metadata for " + depClass.getSimpleName() +
                            " from " + toPopulateFrom.getSimpleName());
                } catch (Throwable t) {
                    Log.error("[DependencyMap] Failed to populate metadata for " + depClass.getSimpleName() +
                            " from " + toPopulateFrom.getSimpleName() + ": " + t.getMessage());
                }
            } else {
                // No dependencyInstance yet — still populate from the declared dependency class to establish basic graph info
                try {
                    storedMeta.populateMetaData(depClass);
                    Log.success("[DependencyMap] Populated metadata (no dependencyInstance) for " + depClass.getSimpleName());
                } catch (Throwable t) {
                    Log.error("[DependencyMap] Failed to populate metadata for " + depClass.getSimpleName() +
                            " (no dependencyInstance): " + t.getMessage());
                }
            }
            
            if (dependencyFactory != null) {
                Log.info("[DependencyMap] Setting dependencyFactory supplier for: " + getSimpleName());
                metaData.setFactory(dependencyFactory);
            } else {
                Log.info("[DependencyMap] No dependencyFactory supplier provided for: " + getSimpleName());
            }
            
            if (dependencyContext != null) {
                Log.info("[DependencyMap] Setting source context for: " + getSimpleName() +
                        " from context: " + dependencyContext.getClass().getSimpleName());
                metaData.setSourceContext(dependencyContext);
            } else {
                Log.info("[DependencyMap] No source context provided for: " + getSimpleName());
            }

            computeIfAbsent(dependencyClass, c ->metaData);

            Log.success("[DependencyMap] Successfully registered: " + getSimpleName() +
                    (dependencyInstance != null ? " -> " + dependencyInstance.getClass().getSimpleName() : " (dependencyFactory only)") +
                    (dependencyFactory != null ? " [with dependencyFactory]" : "") +
                    (dependencyContext != null ? " [from " + dependencyContext.getClass().getSimpleName() + "]" : ""));
        } catch (Exception e) {
            Log.error("[DependencyMap] Failed to register dependency: " + getSimpleName() +
                    " - Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw e;
        }
    }
    @Override
    public DependencyMap<T> getDependencyMap() {
        return this;
    }

    /**
     * Registers a dependency with minimal information.
     * Factory and source context can be set later.
     *
     * @param clazz The class type of the dependency
     * @param instance The actual instance of the dependency
     */
    @Override
    public void registerDependency(IDependencyMetaData clazz, Object instance) {
        getDependencyMap().comp
        registerDependency(clazz, instance, null, null);
    }

    /**
     * Registers a dependency with optional factory but without a source context.
     */
    @Override
    public void registerDependency(IDependencyMetaData clazz, Object instance, Supplier<?> factory) {
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
     * Retrieves all registered dependency metadata entries from this map.
     *
     * @return A collection containing the metadata for all registered dependencies
     */
    @Override
    public Collection<IDependencyMetaData> getDependencyMapValues(){
       return values();
    }


    /**
     * Retrieves the metadata for a dependency of the specified class type.
     *
     * @param clazz The class type of the dependency to retrieve metadata for
     * @return The metadata associated with the given class, or null if no such dependency is registered
     */
    @Override
    public IDependencyMetaData getDependency(IDependencyClass<?> clazz){
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

    @Override
    public IDependencyMetaData ensureAndGetInstance(IDependencyMetaData classToEnsure) {
        if (classToEnsure == null) {
            return null;
        }

        IDependencyMetaData instance = getDependencyMap().getDependencyInstanceWithFactory(getDependencyClass());
        if (instance != null) {
            return instance;
        }

        Supplier<?> factory = getDependencyMap().getDependency(classToEnsure.getClass()).getFactory();
        if (factory != null) {
            IDependencyMetaData created = (IDependencyMetaData) factory.get();
            if (created != null) {
                IDependencyMetaData ensuredInstance = new DependencyMetaData(classToEnsure.getClass());
                ensuredInstance.setInstance(created);
                return created;
            }
        }

        return null;
    }

    /**
     * Finds a dependency by assignable type.
     * Searches for a registered class that is assignable from the given class.
     *
     * @param dependencyToFind The dependency class type to search for
     * @return The instance if found, null otherwise
     */
    @Override
    public Class<?> findByAssignableType(IDependencyMetaData<T> dependencyToFind) {
        if (dependencyToFind == null) {
            return null;
        }

        Class<?> direct = getRawDependencyClass();
        if (direct != null) {
            return direct;
        }

        for (IDependencyMetaData dependency : getMetaData(dependencyToFind)) {
            Class<?> registeredType = getDependency(dependency.getDependencyClass()).getDependencyClass();
            Class<?> meta = getDependencyMap().getMetaData(getDe();
            if (registeredType != null && dependencyToFind.isAssignableFrom(registeredType)) {
                return meta;
            }

            Object instance = getDependencyMap().getDependencyInstanceWithFactory(meta);
            if (dependencyToFind.isInstance(instance)) {
                return meta;
            }
        }

        return null;
    }

//    @Override
//    public IDependencyMetaData getDependencyInstance(Class<?> clazz) {
//        if (clazz == null) {
//            return null;
//        }
//
//        Class<?> compatible = findByAssignableType(clazz);
//        return compatible != null ? ensureAndGetInstance(compatible) : null;
//    }


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
     * @param ensureInstance If true, attempts to create instances via factories; if false, only checks existing instances
     * @return true if registered, false otherwise
     */
    @Override
    public boolean isRegistered(Class<?> clazz, boolean ensureInstance) {
        if (clazz == null) {
            Log.warn("[DependencyMap] " + LogColor.YELLOW + "isRegistered" + LogColor.RESET + " called with " + LogColor.RED + "null" + LogColor.RESET + " class");
            return false;
        }
        
        Log.info("[DependencyMap] Checking registration for " + LogColor.CYAN + clazz.getSimpleName() + LogColor.RESET + " (ensureInstance=" + ensureInstance + ")");
        
        // Check direct registration
        if (containsKey(clazz)) {
            Log.success("[DependencyMap] Already registered in DependencyMap -> " + LogColor.CYAN + clazz.getSimpleName() + LogColor.RESET);
            return true;
        }
        
        Log.info("[DependencyMap] No direct registration found, checking instance assignments...");
        
        // Check instances based on ensureInstance flag
        boolean found = values().stream()
                .anyMatch(metaData -> {
                    if (metaData == null) {
                        return false;
                    }
                    
                    Object instance;
                    if (ensureInstance) {
                        // Use the existing method that tries factory if instance is null
                        Log.info("[DependencyMap] " + LogColor.YELLOW + "Attempting" + LogColor.RESET + " factory creation for: " + clazz.getSimpleName());
                        instance = ensureAndGetInstance(metaData);
                        if (instance != null) {
                            Log.info("[DependencyMap] " + LogColor.GREEN + "Created" + LogColor.RESET + " instance via factory: " + instance.getClass().getSimpleName());
                        }
                    } else {
                        // Only get existing instance, don't create via factory
                        Log.info("[DependencyMap] Checking " + LogColor.BOLD + "existing instance only" + LogColor.RESET + " (no factory)");
                        instance = metaData.getDependencyInstance(metaData.getDependencyClass());
                    }
                    
                    boolean matches = instance != null && clazz.isInstance(instance);
                    if (matches) {
                        Log.success("[DependencyMap] " + LogColor.GREEN + "MATCH" + LogColor.RESET + " found: " + clazz.getSimpleName() 
                                + " -> " + instance.getClass().getSimpleName());
                    }
                    return matches;
                });
        
        if (found) {
            Log.success("[DependencyMap] " + LogColor.GREEN + "✓" + LogColor.RESET + " Registration confirmed for: " + LogColor.CYAN + clazz.getSimpleName() + LogColor.RESET);
        } else {
            Log.warn("[DependencyMap] " + LogColor.RED + "✗" + LogColor.RESET + " No registration found for: " + LogColor.YELLOW + clazz.getSimpleName() + LogColor.RESET);
        }
        
        return found;
    }

    /**
     * Checks if a dependency is registered (has metadata).
     * Defaults to attempting factory creation if no instance exists.
     *
     * @param clazz The class type to check
     * @return true if registered, false otherwise
     */
    @Override
    public boolean isRegistered(Class<?> clazz) {
        Log.info("[DependencyMap] isRegistered called for " + LogColor.CYAN + clazz.getSimpleName() + LogColor.RESET 
                + " (defaulting to " + LogColor.BOLD + "ensureInstance=false" + LogColor.RESET + ")");
        return isRegistered(clazz, false);
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