/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps;

import com.tjxjnoobie.api.dependency.IDependencyInjectableInterface;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.platform.global.console.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Global registry that maps interface tokens to dependency metadata.
 */
public class DependencyMap extends ConcurrentHashMap<Class<?>, IDependencyMetaData<?, ?>> implements IDependencyMap {
    private static final DependencyMap DEPENDENCY_MAP = new DependencyMap();

    /**
     * Returns the singleton dependency map used by the API module.
     *
     * @return the shared dependency map
     */
    public static DependencyMap getDependencyMap() {
        return DEPENDENCY_MAP;
    }

    @Override
    public void registerDependency(
            Class<? extends IDependencyInjectableInterface> rawDependencyInterface,
            IDependencyMetaData<?, ?> dependencyMetaData) {
        if (rawDependencyInterface == null || dependencyMetaData == null) {
            throw new IllegalArgumentException("[DependencyMap] interface key and metadata are required");
        }
        if (!rawDependencyInterface.isInterface()) {
            throw new IllegalArgumentException("[DependencyMap] dependency key must be an interface: "
                    + rawDependencyInterface.getName());
        }

        put(rawDependencyInterface, dependencyMetaData);
    }

    @Override
    public <T> IDependencyMetaData<?, ?> findMetaData(Class<T> dependencyInterface) {
        if (dependencyInterface == null) {
            return null;
        }
        return get(dependencyInterface);
    }

    @Override
    public <T> T findInstance(Class<T> dependencyInterface) {
        if (dependencyInterface == null) {
            return null;
        }

        IDependencyMetaData<?, ?> metaData = findMetaData(dependencyInterface);
        if (metaData == null) {
            return null;
        }

        Object resolvedDependency = metaData.resolveLocalInstance(dependencyInterface);
        if (dependencyInterface.isInstance(resolvedDependency)) {
            return dependencyInterface.cast(resolvedDependency);
        }

        return null;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> T replaceInstance(Class<T> dependencyInterface, Supplier<? extends T> supplier) {
        if (dependencyInterface == null) {
            throw new IllegalArgumentException("[DependencyMap] dependency key is required");
        }
        if (supplier == null) {
            throw new IllegalArgumentException("[DependencyMap] replacement supplier is required");
        }

        IDependencyMetaData<?, ?> metaData = findMetaData(dependencyInterface);
        if (metaData == null) {
            throw new IllegalStateException("replaceInstance: instance not registered: " + dependencyInterface.getName());
        }

        Object existing = metaData.resolveLocalInstance(dependencyInterface);
        if (!dependencyInterface.isInstance(existing)) {
            throw new IllegalStateException("replaceInstance: instance not registered: " + dependencyInterface.getName());
        }

        ((IDependencyMetaData) metaData).replaceDependencyInstance((Supplier) supplier);
        Object replacement = metaData.resolveLocalInstance(dependencyInterface);
        if (!dependencyInterface.isInstance(replacement)) {
            throw new IllegalStateException("replaceInstance: replacement type mismatch for " + dependencyInterface.getName());
        }

        return dependencyInterface.cast(replacement);
    }

    @Override
    public void removeDependency(Class<?> dependencyInterface) {
        if (dependencyInterface == null) {
            return;
        }
        remove(dependencyInterface);
    }

    @Override
    public boolean isInstanceRegistered(Class<?> dependencyInterface) {
        return dependencyInterface != null && containsKey(dependencyInterface);
    }

    @Override
    public int getDependencyMapSize() {
        return size();
    }

    @Override
    public boolean isDependencyMapEmpty() {
        return isEmpty();
    }

    @Override
    public void clear() {
        super.clear();
        Log.info("[DependencyMap] Cleared dependency registrations");
    }
}
