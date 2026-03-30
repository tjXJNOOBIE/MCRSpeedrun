/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps;

import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.platform.global.console.Log;

import java.util.concurrent.ConcurrentHashMap;

public class DependencyMap extends ConcurrentHashMap<Class<?>, IDependencyMetaData<?, ?>> implements IDependencyMap {
    private static final DependencyMap DEPENDENCY_MAP = new DependencyMap();

    public static DependencyMap getDependencyMap() {
        return DEPENDENCY_MAP;
    }

    @Override
    public void registerDependency(Class<?> rawDependencyInterface, IDependencyMetaData<?, ?> dependencyMetaData) {
        if (rawDependencyInterface == null || dependencyMetaData == null) {
            Log.critical("[DependencyMap] Dependency registration failed because interface key or metadata was null");
            return;
        }
        put(rawDependencyInterface, dependencyMetaData);
    }

    @Override
    public <T> IDependencyMetaData<?, ?> getMetaData(Class<T> dependencyInterface) {
        if (dependencyInterface == null) {
            return null;
        }
        return get(dependencyInterface);
    }

    @Override
    public <T> T getInstance(Class<T> dependencyInterface) {
        if (dependencyInterface == null) {
            return null;
        }

        IDependencyMetaData<?, ?> metaData = getMetaData(dependencyInterface);
        if (metaData == null) {
            return null;
        }

        Object dependencyInstance = metaData.getDependencyInstance();
        if (dependencyInterface.isInstance(dependencyInstance)) {
            return dependencyInterface.cast(dependencyInstance);
        }

        Object dependencyInterfaceView = metaData.getDependencyInterface();
        if (dependencyInterface.isInstance(dependencyInterfaceView)) {
            return dependencyInterface.cast(dependencyInterfaceView);
        }

        return null;
    }

    @Override
    public void removeDependency(Class<?> dependencyInterface) {
        if (dependencyInterface == null) {
            return;
        }
        remove(dependencyInterface);
    }

    @Override
    public boolean isRegistered(Class<?> dependencyInterface) {
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
