/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata;

import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.platform.global.console.Log;

import java.util.function.Supplier;

/**
 * DependencyInstance – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 11/2/2025
 */
public class DependencyInstance<INSTANCE extends IDependencyInstance<?>>
        implements IDependencyInstance<INSTANCE> {

    Supplier<INSTANCE> dependencyFactory;
    INSTANCE instance;

    @Override
    public INSTANCE getDependencyInstance() {
        if (dependencyFactory == null) {
            Log.error("[DependencyInstance] dependencyFactory is null!" +
                    " Don't forget to setDependencyFactory()! ");
            return null;
        }
        return dependencyFactory.get();
    }


    @Override
    public Supplier<INSTANCE> getDependencySupplier() {
        return dependencyFactory;
    }

    @Override
    public void setDependencyInstance(INSTANCE instance) {
        this.instance = instance;
    }

    @Override
    public void setDependencyFactoryAndInstance(INSTANCE dependencyInstance) {
        this.dependencyFactory = () -> dependencyInstance;
        setDependencyInstance(dependencyInstance);
    }

    @Override
    public void setDependencySupplier(INSTANCE dependencyFactory) {
        this.dependencyFactory = () -> dependencyFactory;
    }

    @Override
    public INSTANCE getOrCreateDependencyInstance() {
        if (instance == null) instance = dependencyFactory.get();
        return instance;
    }

    @Override
    public INSTANCE refreshDependencyInstance() {
        instance = dependencyFactory.get();
        return instance;
    }

    @Override
    public void rebindFactory(Supplier<INSTANCE> newFactory) {
        this.dependencyFactory = newFactory;
        refreshDependencyInstance();
    }


}
