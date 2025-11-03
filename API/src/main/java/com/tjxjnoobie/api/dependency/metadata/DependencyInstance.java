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

/**
 * DependencyInstance – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 11/2/2025
 */
public class DependencyInstance<INSTANCE extends IDependencyInstance<?>>
        implements IDependencyInstance<INSTANCE>  {

    //TODO: Make a full factory with hot reload and rebinding functionality
    IDependencyFactory<INSTANCE> dependencyFactory;
    IDependencyInstance<INSTANCE> dependencyInstance;


    @Override
    public IDependencyInstance<INSTANCE> getDependencyInstance() {
        return dependencyInstance;
    }

    @Override
    public void setDependencyInstance(IDependencyInstance<INSTANCE> dependencyInstance) {
        this.dependencyInstance = dependencyInstance;

    }



}
