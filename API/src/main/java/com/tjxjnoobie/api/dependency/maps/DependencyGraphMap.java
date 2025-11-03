/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps;

import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyGraphMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyClass;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.platform.global.annotations.PreConstruct;
import com.tjxjnoobie.api.platform.global.console.Log;

import java.util.concurrent.ConcurrentHashMap;

/**
 * DependencyGraphMap – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 11/2/2025
 */
public class DependencyGraphMap<CLASS extends IDependencyClass<?>,
        INSTANCE extends IDependencyInstance<?>>
        extends ConcurrentHashMap<IDependencyClass<?>, IDependencyInstance<?>>
        implements IDependencyGraphMap<CLASS,INSTANCE> {




    public DependencyGraphMap() {
        super();
        initializeDependencyGraph();
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

        if (getSubDependenciesForBase() != null && !getSubDependenciesForBase().isEmpty()) {
            nodesWithDeps++;
        } else {
            isolatedNodes++;
        }
        maxDepth = Math.max(maxDepth, getDepth());
        Log.info("[GraphMap] Nodes with dependencies: " + nodesWithDeps);
        Log.info("[GraphMap] Isolated nodes: " + isolatedNodes);
        Log.info("[GraphMap] Maximum depth level: " + maxDepth);
        Log.success("[GraphMap] Dependency Graph Map initialized successfully");

    }
    @Override
    public void registerDependencyToGraph() {
        Log.info("[GraphMap] Registering dependency for class: " + getDependencyClass().getSimpleName());
        computeIfAbsent(getDependencyClass(), c -> getDependencyInstance());
        Log.success("[GraphMap] Regsigtered " + getDependencyClass().getSimpleName()+
                " -> " + getDependencyInstance().getClass().getSimpleName()
                + " in DepeencyGraph Map registered successfully");

    }
    @Override
    public void registerDependencyToGraph(IDependencyClass<CLASS> dependencyClass) {
        Log.info("[GraphMap] Registering dependency for class: " + dependencyClass.getSimpleName());
        computeIfAbsent(dependencyClass, c -> getDependencyInstance());
        Log.success("[GraphMap] Regsigtered " + getDependencyClass().getSimpleName()+
                " -> " + getDependencyInstance().getClass().getSimpleName()
                + " in DepeencyGraph Map registered successfully");
    }

    @Override
    public IDependencyGraphMap<CLASS,INSTANCE> getDependencyGraph() {
        return this;
    }





//    @Override
//    public void printSummary() {
//        Log.info("[GraphMap] ===== Dependency Graph Summary =====");
//        for (IDependencyMetaData meta : values()) {
//            Log.info(" - " + meta.getDependencyClass().getSimpleName() +
//                    " | depth=" + meta.getDepth() +
//                    " | role=" + meta.getDependencyRole() +
//                    " | deps=" + meta.getSubDependenciesForBase().size());
//        }
//        Log.info("[GraphMap] ===================================");
//    }




    /**
     * Delegates dependency resolution to the injector helper.
     *
     * @param dependencyClass the dependency class to resolve
     * @return the resolved dependency, or null if not found
     */
    @Override
    public boolean isRegisteredinDependencyGraph(IDependencyClass<CLASS> dependencyClass) {
        //TODO: Make a resolver for the dependency graph map
        //Check direct map registartion from our extended map
        if (containsKey(dependencyClass)) {
            Log.success("[GraphMap] Dependency " + dependencyClass.getSimpleName() + " is registered in the dependency graph map");
            return true;
        }
        Log.error("[GraphMap] Dependency " + dependencyClass.getSimpleName() + " is not registered in the dependency graph map");
        return containsKey(dependencyClass);
    }

}
