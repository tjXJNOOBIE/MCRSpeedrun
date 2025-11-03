/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps.interfaces;

import com.tjxjnoobie.api.dependency.maps.DependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyClass;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyFactory;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Interface defining the contract for dependency management operations.
 * All methods are abstract and provide no default implementation. Concrete
 * implementations must define the actual behavior of registering, retrieving,
 * checking, and managing dependencies.
 */
public interface IDependencyMap<CLASS extends IDependencyClass<?>,
        INSTANCE extends IDependencyInstance<?>>
        extends IDependencyMetaData<CLASS,INSTANCE>,
        IDependencyFactory<INSTANCE> {




    default void registerDependency(){

    }



    default List<Object> getAllInstances() {
        return Collections.emptyList();
    }




    default DependencyMap<CLASS, INSTANCE> getDependencyMap(){
        return null;
    }

    default Collection<INSTANCE> getRawDependencyMapValues(){
        return  null;
    }

    /**
     * Returns all dependency metadata entries that match the specified role (e.g., "main", "config").
     * Roles help categorize dependencies by function or purpose, enabling targeted access.
     *
     * @param role the role filter to apply
     * @return a list of metadata entries matching the given role
     */
    default List<INSTANCE> getByRole(DependencyRole role) {
        return null;
    }

    /**
     * Returns all dependency metadata entries that belong to a specific context (e.g., test, production).
     * Context-based filtering allows for environment-specific dependency access.
     *
     * @param context the context filter to apply
     * @return a list of metadata entries matching the given context
     */
    default List<INSTANCE> getByContext(IContext<?> context) {
        return java.util.Collections.emptyList();
    }


    default void removeDependency(CLASS dependencyClass) {

    }



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
    default boolean isRegistered(IDependencyClass<CLASS> dependencyClass, boolean ensureInstance){
        return false;
    }

    default boolean isRegistered(IDependencyClass<CLASS> dependencyClass){
        return false;
    }

    /**
     * Gets the total number of registered dependencies in this map.
     *
     * @return The number of dependencies currently registered
     */
    default int getDependencyMapSize() {
        return 0;
    }
}
