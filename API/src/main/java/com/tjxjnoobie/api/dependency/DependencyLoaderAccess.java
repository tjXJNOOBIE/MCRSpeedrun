/*
 * TJVD License (TJ Valentineâ€™s Discretionary License) â€” Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency;

import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Provides static access to the active dependency loader.
 */
public final class DependencyLoaderAccess {

    private DependencyLoaderAccess() {
    }

    private static DependencyLoader loader() {
        return DependencyLoader.getDependencyLoader();
    }

    /**
     * Resolves an instance for the supplied interface token.
     *
     * @param dependencyType the interface token to resolve
     * @param <T> the dependency token type
     * @return the resolved instance, or {@code null} when unavailable
     */
    public static <T> T findInstance(Class<T> dependencyType) {
        return loader().findInstance(dependencyType);
    }

    /**
     * Resolves metadata for the supplied interface token.
     *
     * @param dependencyType the interface token to inspect
     * @param <T> the dependency token type
     * @return the stored metadata, or {@code null} when unavailable
     */
    public static <T> IDependencyMetaData<?, ?> findMetaData(Class<T> dependencyType) {
        return loader().findMetaData(dependencyType);
    }

    /**
     * Resolves an instance and fails fast with a default message when unavailable.
     *
     * @param dependencyType the interface token to resolve
     * @param <T> the dependency token type
     * @return the resolved instance
     */
    public static <T> T requireInstance(Class<T> dependencyType) {
        return requireInstance(dependencyType, "No dependency registered for " + dependencyType.getName());
    }

    /**
     * Resolves an instance and fails fast with the supplied message when unavailable.
     *
     * @param dependencyType the interface token to resolve
     * @param message the failure message to use when the instance is missing
     * @param <T> the dependency token type
     * @return the resolved instance
     */
    public static <T> T requireInstance(Class<T> dependencyType, String message) {
        T instance = findInstance(dependencyType);
        return Objects.requireNonNull(instance, message);
    }

    /**
     * Checks whether an interface token currently resolves to an instance.
     *
     * @param dependencyType the interface token to inspect
     * @return {@code true} when an instance is registered
     */
    public static boolean isInstanceRegistered(Class<?> dependencyType) {
        return loader().isInstanceRegistered(dependencyType);
    }

    /**
     * Replaces an already-registered instance.
     *
     * @param dependencyType the interface token to replace
     * @param supplier the supplier that creates the replacement instance
     * @param <T> the dependency token type
     * @return the replacement instance
     */
    public static <T> T replaceInstance(Class<T> dependencyType, Supplier<? extends T> supplier) {
        return loader().replaceInstance(dependencyType, supplier);
    }
}
