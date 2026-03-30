/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces;

import java.util.Date;
import java.util.UUID;

public interface IDependencyInterface<INTERFACE> {

    default UUID getDependencyId() {
        return null;
    }

    default INTERFACE getInterface() {
        return null;
    }

    default Class<? extends INTERFACE> getRawDependencyInterface() {
        return null;
    }

    default Date getCreationTime() {
        return null;
    }

    default void setDependencyInterfaceWrapperRawClass(Class<? extends INTERFACE> rawDependencyInterfaceClass) {
    }

    default void setDependencyInterface(INTERFACE dependencyInterface) {
    }

    default void setDependencyId(UUID dependencyId) {
    }

    default void setCreationTime(Date creationTime) {
    }
}
