/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.injection.helpers;

import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IContextInjectionHelper;
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyGraphMap;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.interfaces.InterfaceManager;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ContextInjectorHelper – Context-based injection orchestration utilities.
 * Concrete implementation of IContextInjectionHelper that handles:
 * - AutoBind phase to register context dependencies
 * - Wave-based injection across multiple contexts
 * - Static field injection using current DI registry
 * - Detection of injectable surfaces
 *
 * This helper operates on the static registries exposed by IDependencyMap and IDependencyGraphMap.
 *
 * @author TJ
 * @since 10/17/2025
 */
public class ContextInjectionHelper implements IContextInjectionHelper, IDependencyMap, IDependencyGraphMap {

    //TODO: Remove concrete delegation
    IDependencyInjectorHelper dependencyInjectorHelper = new DependencyInjectorHelper();

    /**
     * Injects all dependencies from all registered contexts with full initialization.
     * This method performs sequential phases with wait gates:
     * 1. AutoBind phase - scan all contexts and register their dependencies (WAIT GATE)
     * 2. Role calculation - analyze dependencies and assign roles (WAIT GATE)
     * 3. Graph registration - register in DependencyGraphMap with roles (WAIT GATE)
     * 4. Wave-based injection - inject dependencies in order
     * 5. Target injection - inject into main target object
     *
     * @param target Optional target object to inject after context initialization (e.g., Main plugin instance)
     */
    @Override
    public void injectAllContextsGlobally(Object target) throws IllegalAccessException {
        Log.info("[DI] ===== Context Based Injection Started =====");
        Log.info("[DI] Total contexts registered: " + contextRegistry.size());

        // ===== PHASE 1: AutoBind - Register all dependencies (WAIT GATE) =====
        Log.info("[DI] --- Phase 1: AutoBind - Scanning and registering dependencies ---");
        Log.info("[DI] DependencyMap size: " + getDependencyMap().getDependencies().size());
        for (Class<?> allClasses : getDependencyMap().getDependencies()) {
            if (allClasses != null) {
                Log.info("[AUTO-BIND] AutoBinding from DependencyMap: " + allClasses.getSimpleName());
                dependencyInjectorHelper.autoBind(allClasses);
                // WAIT: autoBind must complete for this context before moving to next
            } else{
                Log.error("[AUTO-BIND] No classes available for binding! Skipping...");
            }
        }
        Log.info("[DI] ✓ AutoBind phase complete. Total registered: " + dependencyMap.getDependencyMapSize());

        // ===== PHASE 2: Role Calculation (WAIT GATE) =====
        Log.info("[DI] --- Phase 2: Calculating dependency roles ---");
        calculateAndAssignRoles();
        Log.info("[DI] ✓ Role calculation complete");

        // ===== PHASE 3: Graph Registration (WAIT GATE) =====
        Log.info("[DI] --- Phase 3: Registering dependencies in graph ---");
        registerDependenciesInGraph();
        Log.info("[DI] ✓ Graph registration complete");

        // ===== PHASE 4: Wave-based injection =====
        Log.info("[DI] --- Phase 4: Wave-based injection ---");
        performWaveInjection(new ArrayList<>(contextRegistry));

        // ===== PHASE 5: Target injection =====
        if (target != null) {
            Log.info("[DI] --- Phase 5: Injecting into target ---");
            Log.info("[DI] Target: " + target.getClass().getSimpleName());

            // Inject from all contexts
            for (Class<?> context : getDependencyMap().getDependencies()) {
                if (context != null) {
                    dependencyInjectorHelper.injectAndRecordMetaData(target);
                }
            }
        }

        Log.info("[DI] ===== Global context injection complete =====");

        // ===== PHASE 6: Static field injection =====
        Log.info("[DI] --- Phase 6: Static field injection ---");
        //TODO: Automate finding classes with static fields
        dependencyInjectorHelper.injectStaticFields(InterfaceManager.class);

        generateInjectableReport();
    }

    /**
     * Phase 2: Calculate and assign roles to all registered dependencies.
     * This must complete before graph registration.
     */
    private void calculateAndAssignRoles() {
        for (IDependencyMetaData meta : dependencyMap.getDependencyMapValues()) {
            if (meta != null && meta.getDependencyClass() != null) {
                // Role is already calculated during autoBind, but we verify here
                if (meta.getRole() == null) {
                    Set<Class<?>> deps = meta.getDependencies();
                    DependencyRole role = determineRole(deps != null ? deps : new HashSet<>());
                    meta.setRole(role);
                    Log.info("[DI-ROLE] Assigned role " + role + " to " + meta.getDependencyClass().getSimpleName());
                }
            }
        }
    }

    /**
     * Phase 3: Register all dependencies in the dependency graph with their roles.
     * This must complete before injection begins.
     */
    private void registerDependenciesInGraph() {
        for (IDependencyMetaData meta : dependencyMap.getDependencyMapValues()) {
            if (meta != null && meta.getDependencyClass() != null) {
                Class<?> clazz = meta.getDependencyClass();
                if (!dependencyGraph.containsKey(clazz)) {
                    dependencyGraph.registerDependencyToGraph(clazz);
                    // Copy metadata to graph
                    IDependencyMetaData graphMeta = dependencyGraph.get(clazz);
                    if (graphMeta != null) {
                        graphMeta.setRole(meta.getRole());
                        graphMeta.setDependencies(meta.getDependencies());
                        graphMeta.setDepth(meta.getDepth());
                    }
                }
            }
        }
    }

    /**
     * Helper method to determine role based on dependency count.
     */
    public DependencyRole determineRole(Set<Class<?>> dependencies) {
        if (dependencies.isEmpty()) return DependencyRole.BASE;
        if (dependencies.size() <= 2) return DependencyRole.INTERMEDIATE;
        return DependencyRole.ISOLATED;
    }

    /**
     * Performs wave-based injection across provided contexts using metadata roles.
     * Wave 1: inject BASE role dependencies (no dependencies)
     * Wave 2: inject INTERMEDIATE and other role dependencies
     *
     * @param contexts list of contexts
     * @return set of objects injected in wave 1
     */
    @Override
    public Set<Object> performWaveInjection(List<IContext<?>> contexts) {
        if (contexts == null || contexts.isEmpty()) {
            Log.warn("[DI-WAVE] No contexts provided for wave injection");
            return new HashSet<>();
        }

        Log.info("[DI-WAVE] ===== Starting wave-based injection =====");
        Set<Object> wave1 = new HashSet<>();

        // Collect all metadata from all contexts
        List<IDependencyMetaData> allMetaData = new ArrayList<>();
        for (IContext<?> context : contexts) {
            if (context != null) {
                IDependencyMap ctxMap = context.getDependencyMap();
                allMetaData.addAll(ctxMap.getDependencyMapValues());
            } else {
                Log.critical("[DI-WAVE] Cannot inject from null context");
            }
        }
        Log.info("[DI-WAVE] Collected " + allMetaData.size() + " dependencies from " + contexts.size() + " contexts");

        // ===== WAVE 1: Inject BASE role dependencies (no dependencies) =====
        Log.info("[DI-WAVE] --- Wave 1: Injecting BASE role dependencies ---");
        for (IDependencyMetaData meta : allMetaData) {
            if (meta != null && meta.getRole() == DependencyRole.BASE) {
                Object inst = meta.ensureAndGetInstance(meta);
                if (inst != null) {
                    dependencyInjectorHelper.injectAndRecordMetaData(inst);
                    wave1.add(inst);
                    Log.info("[DI-WAVE] Wave 1 injected: " + inst.getClass().getSimpleName() + " (BASE)");
                }
            }
        }
        Log.info("[DI-WAVE] Wave 1 complete: " + wave1.size() + " BASE dependencies injected");

        // ===== WAVE 2: Inject INTERMEDIATE and other role dependencies =====
        Log.info("[DI-WAVE] --- Wave 2: Injecting INTERMEDIATE and other dependencies ---");
        int wave2Count = 0;
        for (IDependencyMetaData meta : allMetaData) {
            if (meta != null && meta.getRole() != DependencyRole.BASE) {
                Object inst = meta.ensureAndGetInstance(meta);
                if (inst != null && !wave1.contains(inst)) {
                    dependencyInjectorHelper.injectAndRecordMetaData(inst);
                    wave2Count++;
                    Log.info("[DI-WAVE] Wave 2 injected: " + inst.getClass().getSimpleName() + " (" + meta.getRole() + ")");
                }
            }
        }
        Log.info("[DI-WAVE] Wave 2 complete: " + wave2Count + " INTERMEDIATE/other dependencies injected");
        Log.info("[DI-WAVE] ===== Wave-based injection complete =====");

        return wave1;
    }

    /**
     * Injects static fields for the specified class.
     */
    @Override
    public void injectStaticFields(Class<?> clazz) {
        // Default no-op - can be overridden in subclasses
    }

    /**
     * Checks if an object has no @Inject annotated fields (leaf dependency).
     */
    @Override
    public boolean hasNoInjectFields(Object obj) {
        if (obj == null) return true;
        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    return false;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return true;
    }
}
