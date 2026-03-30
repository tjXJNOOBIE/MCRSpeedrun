/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata.interfaces;

import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInterface;

public interface IDependencyMetaDataHelper<INTERFACE, INSTANCE> {


    default void populateMetaData(IDependencyMetaData<INTERFACE, INSTANCE> dependencyMetaData,
                          IDependencyInterface<INTERFACE> wrappedInterface,
                          IDependencyInstance<INSTANCE> wrappedInstance){

    }
}