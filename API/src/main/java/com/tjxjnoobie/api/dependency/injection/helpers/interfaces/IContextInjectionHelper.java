/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.injection.helpers.interfaces;

import com.tjxjnoobie.api.interfaces.IContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * IContextInjectionHelper – contract for context-based injection orchestration.
 * Provides default no-op implementations so callers can safely depend on the API
 * while concrete helpers override behavior.
 */
public interface IContextInjectionHelper {

    /**
     * Injects context dependencies globally into the specified target object using all available contexts.
     * This method performs a full, global injection of dependencies across all registered contexts,
     * applying them to the provided target instance. It may involve field injection and other context-specific logic.
     *
     * @param target the object into which context-dependent fields should be injected
     * @throws IllegalAccessException if an illegal access exception occurs during field injection operations
     */
    default void injectAllContextsGlobally(Object target) throws IllegalAccessException{

    }

    /**
     * Performs wave-based injection across provided contexts.
     * Returns the set of objects injected during the first wave.
     */
    default Set<Object> performWaveInjection(List<IContext<?>> contexts) {
        return new HashSet<>();
    }

    /**
     * Injects static fields for the specified class.
     */
    default void injectStaticFields(Class<?> clazz) {
        // Default no-op
    }

    /**
     * Determines whether the given object has no @Inject fields (leaf dependency).
     */
    default boolean hasNoInjectFields(Object obj) {
        return true;
    }
}
