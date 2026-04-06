/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.injection.helpers.interfaces;

import com.tjxjnoobie.api.dependency.IDependencyInjectableConcrete;
import com.tjxjnoobie.api.dependency.IDependencyInjectableInterface;
import com.tjxjnoobie.api.internal.InjectionConfig;

/**
 * Interface for dependency injection helper functionality.
 * Provides the entrypoint used to scan and register DI bindings.
 *
 * @param <INTERFACE> the injectable interface token type
 * @param <INSTANCE> the injectable concrete instance type
 */
public interface IDependencyInjectorHelper<
        INTERFACE extends IDependencyInjectableInterface,
        INSTANCE extends IDependencyInjectableConcrete> extends InjectionConfig {

    /**
     * Scans and registers dependencies for the supplied DI entrypoint.
     *
     * @param entryPoint the object whose class loader should be used for scanning
     * @throws Throwable when startup registration fails
     */
    void setupDISystem(Object entryPoint) throws Throwable;
}
