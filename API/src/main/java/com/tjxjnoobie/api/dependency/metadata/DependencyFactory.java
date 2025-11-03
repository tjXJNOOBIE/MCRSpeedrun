/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata;

import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyFactory;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyInstance;

import java.util.function.Supplier;

/**
 * DependencyFactory – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 11/2/2025
 */
public class DependencyFactory<INSTANCE extends IDependencyInstance<?>>
        implements IDependencyFactory<INSTANCE> {


    IDependencyFactory<Supplier<INSTANCE>> dependencyFactory;
    @Override
    public IDependencyFactory<Supplier<INSTANCE>> getDependencyFactory() {
        return dependencyFactory;
    }
    @Override
    public void setDependencyFactory(IDependencyFactory<Supplier<INSTANCE>> dependencyFactory) {
        this.dependencyFactory = dependencyFactory;
    }

}
