/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.injection.helpers.interfaces;

import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInterface;
import com.tjxjnoobie.api.internal.InjectionConfig;

/**
 * Interface for dependency injection helper functionality.
 * Provides default implementations for fluent priority registration and initialization.
 * All injection and bind methods are centralized here with default no-op implementations.
 * Concrete implementations should override these methods to provide actual functionality.
 * 
 * @author TJ
 * @since 10/12/2025
 */
public interface IDependencyInjectorHelper<INTERFACE, INSTANCE>
        extends InjectionConfig {

    default void setupDISystem(IDependencyInterface<?> entryPoint) throws Throwable {

    }





}