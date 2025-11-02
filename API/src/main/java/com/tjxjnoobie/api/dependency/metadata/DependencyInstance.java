/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata;

import java.util.function.Supplier;

/**
 * DependencyInstance – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 11/2/2025
 */
public class DependencyInstance<T> {

    private Supplier<? extends T> dependencyFactory;
    private T rawDependencyInstance;

    public Supplier<? extends T> getDependencyInstance() {
        return dependencyFactory;
    }

    public void setDependencyInstance(Supplier<? extends T> dependencyInstance) {
        this.dependencyFactory = dependencyInstance;
    }
    public T getRawDependencyInstance() {
        return rawDependencyInstance;
    }

}
