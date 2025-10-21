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
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.dependency.maps.DependencyMap;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyGraphMap;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.interfaces.InterfaceManager;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.console.Log;

import java.lang.reflect.Field;
import java.util.*;

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
public class ContextInjectionHelper implements IContextInjectionHelper, IDependencyMap, IDependencyGraphMap {




    /**
     * Injects dependencies into a target using a single source context's dependency map.
     *
     * @param target  object to inject into
     * @param context source context providing dependencies
     */
    @SuppressWarnings("unchecked")
    public void injectFieldsFromContext(Object target, IContext<?> context) {
        if (target == null || context == null) {
            Log.warn("[DI] Cannot inject from null target or context");
            return;
        }

        Log.info("[DI] Injecting into " + target.getClass().getSimpleName()
                + " from context " + context.getClass().getSimpleName());

        // Build merged dependency map from the single context
        HashMap<Class<?>, Object> merged = new HashMap<>();
        DependencyMap ctxMap = context.getDependencyMap();
        for (Map.Entry<Class<?>, IDependencyMetaData> entry : ctxMap.entrySet()) {
            Class<?> type = entry.getKey();
            IDependencyMetaData meta = entry.getValue();
            if (meta == null) continue;
            Object inst = safeInstance(target);
            if (inst != null) merged.put(type, inst);
        }

        // Inject using merged dependencies
        injectWithMergedDependencies(target, merged);
    }

    /**
     * Injects dependencies into a target object from multiple contexts.
     * Merges all contexts' dependency maps and performs a single injection pass.
     *
     * @param target   object to inject into
     * @param contexts list of contexts to merge from
     */
    public void injectFieldsFromContexts(Object target, List<IContext<?>> contexts) {
        if (target == null || contexts == null || contexts.isEmpty()) {
            Log.warn("[DI] Cannot inject from null target or empty contexts");
            return;
        }

        Log.info("[DI] Injecting into " + target.getClass().getSimpleName()
                + " from " + contexts.size() + " contexts");

        // Merge all dependencies
        HashMap<Class<?>, Object> merged = new HashMap<>();
        for (IContext<?> ctx : contexts) {
            if (ctx == null) continue;
            DependencyMap ctxMap = ctx.getDependencyMap();
            for (Map.Entry<Class<?>, IDependencyMetaData> entry : ctxMap.entrySet()) {
                Class<?> type = entry.getKey();
                IDependencyMetaData meta = entry.getValue();
                if (meta == null) continue;
                Object inst = safeInstance(target);
                if (inst != null) merged.putIfAbsent(type, inst);
            }
        }

        // Inject using merged dependencies
        injectWithMergedDependencies(target, merged);
    }

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
     * Performs wave-based injection across provided contexts.
     * Wave 1: inject leaf dependencies (no @Inject fields)
     * Wave 2: inject remaining dependencies (intermediate/others)
     *
     * @param contexts list of contexts
     * @return set of objects injected in wave 1
     */
    public Set<Object> performWaveInjection(List<IContext<?>> contexts) {
        if (contexts == null || contexts.isEmpty()) {
            Log.warn("[DI] No contexts provided for wave injection");
            return new HashSet<>();
        }

        Log.info("[DI] ===== Starting wave-based injection =====");
        Set<Object> wave1 = new HashSet<>();

        // Create merged dependency map from all contexts
        HashMap<Class<?>, Object> mergedDependencies = new HashMap<>();
        for (IContext<?> context : contexts) {
            if (context != null) {
                DependencyMap ctxMap = context.getDependencyMap();
                for (Map.Entry<Class<?>, IDependencyMetaData> entry : ctxMap.entrySet()) {
                    IDependencyMetaData meta = entry.getValue();
                    if (meta == null) continue;
                    Object inst = safeInstance(null);
                    if (inst != null) mergedDependencies.putIfAbsent(entry.getKey(), inst);
                }
            } else {
                Log.critical("[DI] Cannot inject from null context");
            }
        }
        Log.info("[DI] Merged " + mergedDependencies.size() + " dependencies from " + contexts.size() + " contexts");

        // ===== WAVE 1: Inject leaf dependencies (no @Inject fields) =====
        Log.info("[DI] --- Wave 1: Injecting leaf dependencies ---");
        for (IContext<?> context : contexts) {
            if (context == null) {
                Log.critical("[DI] Cannot inject from null context");
                continue;
            }

            // Snapshot avoid CME
            List<Object> dependencies = new ArrayList<>();
            for (IDependencyMetaData meta : context.getDependencyMap().values()) {
                Object inst = safeInstance(null);
                if (inst != null) dependencies.add(inst);
            }

            for (Object dep : dependencies) {
                if (dep == null) continue;
                if (hasNoInjectFields(dep) && !wave1.contains(dep)) {
                    injectWithMergedDependencies(dep, mergedDependencies);
                    wave1.add(dep);
                    Log.info("[DI] Wave 1 injected: " + dep.getClass().getSimpleName());
                }
            }
        }
        Log.info("[DI] Wave 1 complete: " + wave1.size() + " leaf dependencies injected");

        // ===== WAVE 2: Inject intermediate dependencies =====
        Log.info("[DI] --- Wave 2: Injecting intermediate dependencies ---");
        int wave2Count = 0;
        for (IContext<?> context : contexts) {
            if (context == null) continue;

            // Snapshot
            List<Object> dependencies = new ArrayList<>();
            for (IDependencyMetaData meta : context.getDependencyMap().values()) {
                Object inst = safeInstance(null);
                if (inst != null) dependencies.add(inst);
            }

            for (Object dep : dependencies) {
                if (dep == null) continue;
                if (!wave1.contains(dep)) {
                    injectWithMergedDependencies(dep, mergedDependencies);
                    wave2Count++;
                    Log.info("[DI] Wave 2 injected: " + dep.getClass().getSimpleName());
                }
            }
        }
        Log.info("[DI] Wave 2 complete: " + wave2Count + " intermediate dependencies injected");
        Log.info("[DI] ===== Wave-based injection complete =====");

        return wave1;
    }

    /**
     * Injects fields into a target object using a merged dependency map.
     * All merged entries are temporarily registered into the owner's dependency map
     * before running a single injection+record pass.
     */
    private void injectWithMergedDependencies(Object target, HashMap<Class<?>, Object> mergedDependencies) {
        if (target == null) {
            Log.warn("[DI] Cannot inject: target is null");
            return;
        }
        if (mergedDependencies == null) {
            Log.warn("[DI] Cannot inject: merged dependencies map is null");
            return;
        }
        if (mergedDependencies.isEmpty()) {
            Log.warn("[DI] Cannot inject: merged dependencies map is empty");
            return;
        }

        for (Map.Entry<Class<?>, Object> entry : mergedDependencies.entrySet()) {
            registerDependency(entry.getKey(), entry.getValue(), entry::getValue, null);
        }

        injectAndRecordMetaData(target);
    }



    /**
     * Checks if an object has no @Inject annotated fields (leaf dependency).
     */
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

    /**
     * Safely retrieves an instance from metadata, tolerating null target class.
     */
    private Object safeInstance(Object target) {
        try {
            Class<?> targetClass = (target != null ? target.getClass() : Object.class);
            return getDependencyInstance(targetClass);
        } catch (Throwable t) {
            return null;
        }
    }
}
