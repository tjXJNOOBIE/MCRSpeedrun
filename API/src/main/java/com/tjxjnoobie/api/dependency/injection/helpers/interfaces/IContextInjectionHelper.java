/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.injection.helpers.interfaces;

import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyGraphMap;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyClass;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;

import java.lang.reflect.InvocationTargetException;

/**
 * IContextInjectionHelper – contract for context-based injection orchestration.
 * Provides default no-op implementations so callers can safely depend on the API
 * while concrete helpers override behavior.
 */
public interface IContextInjectionHelper<CLASS extends IDependencyClass<?>,
        INSTANCE extends IDependencyInstance<?>>
        extends IDependencyMetaData<CLASS,INSTANCE>, IDependencyMap<CLASS,INSTANCE>,
    IDependencyGraphMap<CLASS, INSTANCE>, IDependencyInjectorHelper<CLASS, INSTANCE> {

    /**
     * Injects context dependencies globally into the specified target object using all available contexts.
     * This method performs a full, global injection of dependencies across all registered contexts,
     * applying them to the provided target instance. It may involve field injection and other context-specific logic.
     *
     * @param target the object into which context-dependent fields should be injected
     * @throws IllegalAccessException if an illegal access exception occurs during field injection operations
     */
    default void injectAllContextsGlobally(Object target) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

    }


}
