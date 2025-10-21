/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps;

import com.tjxjnoobie.api.dependency.metadata.DependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyGraphMap;
import com.tjxjnoobie.api.platform.global.annotations.PreConstruct;
import com.tjxjnoobie.api.platform.global.console.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DependencyGraphMap – Custom map for managing dependency metadata with graph operations.
 * Extends ConcurrentHashMap to provide thread-safe dependency graph management.
 * Delegates injection and bind operations to IDependencyInjectorHelper.
 *
 * @author TJ
 * @since 10/14/2025
 */
public class DependencyGraphMap extends ConcurrentHashMap<Class<?>, IDependencyMetaData> implements IDependencyGraphMap  {







    public DependencyGraphMap() {
        super();
        Log.info("[GraphMap] Initialized DependencyGraphMap");
    }

    /**
     * PreConstruct initialization method with priority 5.
     * Initializes the dependency graph after DI system is ready.
     * This runs after DependencyInjectorHelper (priority 0) but before DependencyMap (priority 10).
     */
    @PreConstruct(priority = 5)
    public void initializeDependencyGraph() {

        Log.info("[GraphMap] ===== Initializing Dependency Graph Map =====");
        Log.info("[GraphMap] Current graph size: " + size() + " nodes");
        
        // Validate graph structure
        int nodesWithDeps = 0;
        int isolatedNodes = 0;
        int maxDepth = 0;
        
        for (IDependencyMetaData meta : values()) {
            if (meta.getDependencies() != null && !meta.getDependencies().isEmpty()) {
                nodesWithDeps++;
            } else {
                isolatedNodes++;
            }
            maxDepth = Math.max(maxDepth, meta.getDepth());
        }
        
        Log.info("[GraphMap] Nodes with dependencies: " + nodesWithDeps);
        Log.info("[GraphMap] Isolated nodes: " + isolatedNodes);
        Log.info("[GraphMap] Maximum depth level: " + maxDepth);
        

        
        // Print initial summary if graph is not empty
        if (!isEmpty()) {
            printSummary();
        }
        
        Log.success("[GraphMap] Dependency Graph Map initialized successfully");
    }

    @Override
    public void registerDependencyToGraph(Class<?> clazz) {
        computeIfAbsent(clazz, c -> {
            IDependencyMetaData meta = new DependencyMetaData(c);
            Log.info("[GraphMap] Registered dependency: " + c.getSimpleName());
            return meta;
        });
    }

    @Override
    public void buildGraph(Set<Class<?>> classes) {
        Log.info("[GraphMap] Building dependency graph for " + classes.size() + " classes");
        for (Class<?> clazz : classes) registerDependencyToGraph(clazz);
        Log.info("[GraphMap] Build complete (" + size() + " nodes)");
    }

  

    @Override
    public void printSummary() {
        Log.info("[GraphMap] ===== Dependency Graph Summary =====");
        for (IDependencyMetaData meta : values()) {
            Log.info(" - " + meta.getDependencyClass().getSimpleName() +
                    " | depth=" + meta.getDepth() +
                    " | role=" + meta.getRole() +
                    " | deps=" + meta.getDependencies().size());
        }
        Log.info("[GraphMap] ===================================");
    }




    /**
     * Delegates building of dependency graph to the injector helper.
     */
    public void buildDependencyGraph() {
        buildDependencyGraph();

    }
    /**
     * Delegates dependency resolution to the injector helper.
     * 
     * @param depClass the dependency class to resolve
     * @return the resolved dependency, or null if not found
     */
    @Override
    public Object resolveDependencyFromGraph(Class<?> depClass) {
        //TODO: Make a resolver for the dependency graph map
        return null;
    }

}

