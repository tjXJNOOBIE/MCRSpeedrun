/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata.interfaces;

import java.util.function.Supplier;

public interface IDependencyInstance<T> {


    default Supplier<? extends T> getDependencyFactory() {
        return null;
    }

    default void setDependencyFactory(Supplier<? extends T> dependencyInstance){
        // Implementation overridden in concrete class
    }

}
