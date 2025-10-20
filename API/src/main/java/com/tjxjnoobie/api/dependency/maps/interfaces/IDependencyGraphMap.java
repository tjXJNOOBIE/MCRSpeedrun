/*
 * TJVD License (TJ Valentine's Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps.interfaces;

import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;

import java.util.List;
import java.util.Set;

/**
 * IDependencyGraphMap – Interface for dependency graph map operations.
 * Defines contract for graph-based dependency management operations.
 * Provides empty default methods that concrete classes override with actual logic.
 *
 * @author TJ
 * @since 10/14/2025
 */
public interface IDependencyGraphMap extends IDependencyMap {


    /**
     * Register a new dependency class into the graph.
     * Empty default method - implementation provided by concrete class.
     */
    default void registerDependencyToGraph(Class<?> clazz) {
        // Empty - concrete class provides implementation
    }

    /**
     * Build graph in one sweep for multiple classes.
     * Empty default method - implementation provided by concrete class.
     */
    default void buildGraph(Set<Class<?>> classes) {
        // Empty - concrete class provides implementation
    }

    /**
     * Traverse by wave depth - returns all metadata at a specific depth level.
     * Empty default method - implementation provided by concrete class.
     */
    default List<IDependencyMetaData> getWave(int depth) {
        // Empty - concrete class provides implementation
        return null;
    }

    /**
     * Print graph summary with dependency information.
     * Empty default method - implementation provided by concrete class.
     */
    default void printSummary() {
        // Empty - concrete class provides implementation
    }
}
