/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata.wrappers;

import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInterface;
import com.tjxjnoobie.api.platform.global.console.Log;

import java.util.Date;
import java.util.UUID;

public class DependencyInterface<INTERFACE> implements IDependencyInterface<INTERFACE> {
    private INTERFACE dependencyInterface;
    private Class<? extends INTERFACE> rawDependencyInterfaceClass;
    private UUID dependencyId;
    private Date creationTime;

    public DependencyInterface() {
        this.dependencyId = UUID.randomUUID();
        this.creationTime = new Date();
    }

    public DependencyInterface(Class<? extends INTERFACE> dependencyInterface) {
        this();
        setDependencyInterfaceWrapperRawClass(dependencyInterface);
    }

    @Override
    public INTERFACE getInterface() {
        return dependencyInterface;
    }

    @Override
    public Class<? extends INTERFACE> getRawDependencyInterface() {
        return rawDependencyInterfaceClass;
    }

    @Override
    public Date getCreationTime() {
        return creationTime;
    }

    @Override
    public UUID getDependencyId() {
        return dependencyId;
    }

    @Override
    public void setDependencyInterfaceWrapperRawClass(Class<? extends INTERFACE> rawDependencyInterfaceClass) {
        if (rawDependencyInterfaceClass == null) {
            Log.error("[DependencyInterface] rawDependencyInterfaceClass is null on setDependencyInterfaceWrapperRawClass()");
            return;
        }
        this.rawDependencyInterfaceClass = rawDependencyInterfaceClass;
    }

    @Override
    public void setDependencyInterface(INTERFACE dependencyInterface) {
        this.dependencyInterface = dependencyInterface;
    }

    @Override
    public void setDependencyId(UUID dependencyId) {
        this.dependencyId = dependencyId;
    }

    @Override
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
