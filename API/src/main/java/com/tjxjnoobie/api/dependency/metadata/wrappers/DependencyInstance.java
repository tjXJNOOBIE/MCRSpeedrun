/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata.wrappers;

import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.platform.global.console.Log;

public class DependencyInstance<INSTANCE> implements IDependencyInstance<INSTANCE> {
    private INSTANCE dependencyInstance;
    private Class<? extends INSTANCE> dependencyInstanceRawClass;

    public DependencyInstance() {
    }

    public DependencyInstance(Class<? extends INSTANCE> rawDependencyInstanceClass) {
        setWrappedRawInstanceClass(rawDependencyInstanceClass);
    }

    @Override
    public INSTANCE getWrappedDependencyInstance() {
        return dependencyInstance;
    }

    @Override
    public Class<? extends INSTANCE> getDependencyInstanceClass() {
        return dependencyInstanceRawClass;
    }

    @Override
    public void setWrappedDependencyInstance(INSTANCE dependencyInstance) {
        this.dependencyInstance = dependencyInstance;
    }

    @Override
    public void setWrappedRawInstanceClass(Class<? extends INSTANCE> rawDependencyInstanceClass) {
        if (rawDependencyInstanceClass == null) {
            Log.error("[DependencyInstance] rawDependencyInstanceClass is null during setWrappedRawInstanceClass()");
            return;
        }
        this.dependencyInstanceRawClass = rawDependencyInstanceClass;
    }
}
