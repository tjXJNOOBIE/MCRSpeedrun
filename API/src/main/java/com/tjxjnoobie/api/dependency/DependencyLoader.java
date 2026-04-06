/*
 * TJVD License (TJ Valentineâ€™s Discretionary License) â€” Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency;

import com.tjxjnoobie.api.dependency.maps.DependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;

import java.util.function.Supplier;

/**
 * Runtime-facing loader API for resolving and replacing DI-backed instances.
 */
public final class DependencyLoader {
    private static final DependencyLoader DEPENDENCY_LOADER = new DependencyLoader();

    private DependencyLoader() {
    }

    /**
     * Returns the shared dependency loader instance.
     *
     * @return the singleton loader
     */
    public static DependencyLoader getDependencyLoader() {
        return DEPENDENCY_LOADER;
    }

    /**
     * Resolves an instance for the supplied interface token.
     *
     * @param dependencyType the interface token to resolve
     * @param <T> the dependency token type
     * @return the resolved instance, or {@code null} when unavailable
     */
    public <T> T findInstance(Class<T> dependencyType) {
        return DependencyMap.getDependencyMap().findInstance(dependencyType);
    }

    /**
     * Resolves metadata for the supplied interface token.
     *
     * @param dependencyType the interface token to inspect
     * @param <T> the dependency token type
     * @return the stored metadata, or {@code null} when missing
     */
    public <T> IDependencyMetaData<?, ?> findMetaData(Class<T> dependencyType) {
        return DependencyMap.getDependencyMap().findMetaData(dependencyType);
    }

    /**
     * Resolves an instance and fails fast when the token is missing.
     *
     * @param dependencyType the interface token to resolve
     * @param <T> the dependency token type
     * @return the resolved instance
     */
    public <T> T requireInstance(Class<T> dependencyType) {
        T instance = findInstance(dependencyType);
        if (instance != null) {
            return instance;
        }
        throw new IllegalStateException("No dependency registered for " + dependencyType.getName());
    }

    /**
     * Checks whether an interface token currently resolves to an instance.
     *
     * @param dependencyType the interface token to inspect
     * @return {@code true} when an instance is registered
     */
    public boolean isInstanceRegistered(Class<?> dependencyType) {
        return DependencyMap.getDependencyMap().isInstanceRegistered(dependencyType);
    }

    /**
     * Replaces an already-registered dependency instance.
     *
     * @param dependencyType the interface token to replace
     * @param supplier the supplier that builds the replacement instance
     * @param <T> the dependency token type
     * @return the replacement instance that was stored
     */
    public <T> T replaceInstance(Class<T> dependencyType, Supplier<? extends T> supplier) {
        return DependencyMap.getDependencyMap().replaceInstance(dependencyType, supplier);
    }
}
