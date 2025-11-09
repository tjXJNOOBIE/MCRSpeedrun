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

public interface IDependencyInstance<INSTANCE> {


    default Supplier<?> getDependencyInstance() {
        return null;
    }


    default Supplier<?> getDependencySupplier(){
        return  null;
    }

    default void setDependencyInstance(Class<?> instance){

    }

//    default void setDependencySupplier(Supplier<INSTANCE> dependencyFactory){
//
//    }

//    default void setDependencySupplier(INSTANCE dependencyFactory){
//
//    }

    default void setDependencySupplier(Class<?> dependencyFactory){

    }


    default INSTANCE refreshDependencyInstance(){
        return null;
    }

    default void rebindFactory(Supplier<INSTANCE> newFactory){

    }
}
