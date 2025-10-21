/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.injection.helpers;

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
 * Extracted from AbstractContext to remove injection/binding responsibilities
 * from the base context class. All operations delegate to an owning
 * IDependencyInjectorHelper for resolution and injection primitives.
 *
 * Responsibilities:
 * - Inject fields into a target from one or more contexts
 * - Wave-based injection across multiple contexts
 * - Static field injection using current DI registry
 * - Utilities for detection of injectable surfaces
 *
 * Note: This helper does not own stateful DI registries; it operates on the
 * registries exposed by the provided owner helper instance.
 *
 * @author TJ
 * @since 10/17/2025
 */
public class ContextInjectionHelper implements IDependencyMap, IDependencyGraphMap {




    /**
     * Injects all dependencies from all registered contexts with full initialization.
     * This method performs:
     * 1. Context building (if contexts support it)
     * 2. Wave-based injection (leaf dependencies first, then intermediate)
     * 3. Top-level injection into provided target object
     *
     * @param target Optional target object to inject after context initialization (e.g., Main plugin instance)
     */
    @Override
    public void injectAllContextsGlobally(Object target) throws IllegalAccessException {
        Log.info("[DI] ===== Global context injection started =====");
        Log.info("[DI] Total contexts registered: " + contextRegistry.size());

        // Step 1: Skip explicit build step; contexts should register their dependencies directly
        Log.info("[DI] --- Step 1: Skipping explicit build step ---");

        // Step 2: Perform wave-based injection
        Log.info("[DI] --- Step 2: Wave-based injection ---");
        performWaveInjection(new ArrayList<>(contextRegistry));

        // Step 3: Inject into target object if provided
        if (target != null) {
            Log.info("[DI] --- Step 3: Injecting into target ---");
            Log.info("[DI] Target: " + target.getClass().getSimpleName());

            // Inject from all contexts
            for (IContext<?> context : contextRegistry) {
                if (context != null) { //TODO: Verify method implementation
                    injectAndRecordMetaData(target);
                }
            }
        }

        Log.info("[DI] ===== Global context injection complete =====");

        // Step 4: Inject static fields for critical classes
        //TODO: Automate this process
        Log.info("[DI] --- Step 4: Static field injection ---");
        injectStaticFields(InterfaceManager.class); //TODO: Add

        generateInjectableReport();
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
                    injectAndRecordMetaData(inst);
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
                    injectAndRecordMetaData(inst);
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
