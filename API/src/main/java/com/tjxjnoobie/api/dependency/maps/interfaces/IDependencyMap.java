/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps.interfaces;

import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;

public interface IDependencyMap {

    default boolean isRegistered(Class<?> dependencyInterface) {
        return false;
    }

    default void registerDependency(Class<?> rawDependencyInterface, IDependencyMetaData<?, ?> dependencyMetaData) {
    }

    default <T> IDependencyMetaData<?, ?> getMetaData(Class<T> dependencyInterface) {
        return null;
    }

    default <T> T getInstance(Class<T> dependencyInterface) {
        return null;
    }

    default void removeDependency(Class<?> dependencyInterface) {
    }

    default int getDependencyMapSize() {
        return 0;
    }

    default boolean isDependencyMapEmpty() {
        return false;
    }
}
