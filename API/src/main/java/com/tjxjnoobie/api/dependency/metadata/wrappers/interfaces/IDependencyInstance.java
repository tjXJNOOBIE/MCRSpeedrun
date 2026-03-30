/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces;

public interface IDependencyInstance<INSTANCE>  {


    default INSTANCE getWrappedDependencyInstance(){
        return null;
    }

    default Class<? extends INSTANCE> getDependencyInstanceClass(){
        return null;
    }


    default void setWrappedDependencyInstance(INSTANCE dependencyInstance){

    }


    default void setWrappedRawInstanceClass(Class<? extends INSTANCE> rawDependencyInstanceClass){

    }

//    default Supplier<INSTANCE> getDependencySupplier(){
//        return  null;
//    }



//    default void refreshDependencyInstance(){
//
//    }




}