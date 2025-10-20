/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.injection.maps;

import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.dependency.maps.DependencyGraphMap;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.annotations.PreConstruct;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InjectionMap – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 10/14/2025
 */
public class InjectionMap<K> extends ConcurrentHashMap<K, IDependencyMetaData> {

    private DependencyGraphMap dependencyGraphMap = new DependencyGraphMap();

    @SuppressWarnings("unchecked")
    public void populateInjectionMap(List<Object> objects) {
        for (Object o : objects) {
            if (o == null) continue;
            Class<?> clazz = o.getClass();
            IDependencyMetaData meta = dependencyGraphMap.get(clazz);
            if (meta == null) {
                meta = new IDependencyMetaData(){};
                // Initialize with default values
            }
            put((K) clazz, meta);
        }
    }

    public List<IDependencyMetaData> getSortedDependencyMetaData() {
        return values().stream()
                .sorted(Comparator.comparingInt(IDependencyMetaData::getPriority)
                        .thenComparingInt(IDependencyMetaData::getDepth))
                .toList();
    }

    // Populate metadata by scanning fields, annotations, etc.
    public void populateMetaData(IDependencyMetaData meta) {
        Class<?> clazz = meta.getDependencyClass();
        Set<Class<?>> dependencies = new HashSet<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                dependencies.add(field.getType());
            }
        }

        // Track inheritance
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            dependencies.add(clazz.getSuperclass());
        }
        dependencies.addAll(Arrays.asList(clazz.getInterfaces()));

        meta.setDependencies(dependencies);
        meta.setDepth(calculateDepth(clazz, dependencies));
        meta.setRole(determineRole(meta));

        // Preconstruct method detection
        Method pre = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(PreConstruct.class))
                .findFirst()
                .orElse(null);
        meta.setPreConstruct(pre);
    }

    private int calculateDepth(Class<?> clazz, Set<Class<?>> deps) {
        if (deps.isEmpty()) return 0;
        int maxDepth = 0;
        for (Class<?> dep : deps) {
            IDependencyMetaData depMeta = dependencyGraphMap.get(dep);
            if (depMeta != null) {
                maxDepth = Math.max(maxDepth, depMeta.getDepth() + 1);
            }
        }
        return maxDepth;
    }

    private DependencyRole determineRole(IDependencyMetaData meta) {
        Set<Class<?>> deps = meta.getDependencies();
        if (deps.isEmpty()) return DependencyRole.BASE;
        if (deps.size() == 1) return DependencyRole.INTERMEDIATE;
        return DependencyRole.ISOLATED;
    }

    // Run @PreConstruct if present
    public void runPreConstruct(Object instance) {
        Class<?> clazz = instance.getClass();
        IDependencyMetaData meta = dependencyGraphMap.get(clazz);
        if (meta == null || meta.getPreConstruct() == null) return;

        Method pre = meta.getPreConstruct();
        try {
            pre.setAccessible(true);
            pre.invoke(instance);
            meta.setPreConstructSuccess(true);
            Log.success("[GraphMap] @PreConstruct executed for " + clazz.getSimpleName());
        } catch (Exception e) {
            meta.setPreConstructSuccess(false);
            meta.incrementRetryCount();
            Log.critical("[GraphMap] @PreConstruct failed for " + clazz.getSimpleName() + " :: " + e.getMessage());
        }
    }
}
