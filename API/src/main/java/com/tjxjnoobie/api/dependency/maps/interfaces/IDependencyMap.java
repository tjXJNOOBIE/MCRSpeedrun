/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps.interfaces;

import com.tjxjnoobie.api.dependency.maps.DependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyClass;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyInstance;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Interface defining the contract for dependency management operations.
 * All methods are abstract and provide no default implementation. Concrete
 * implementations must define the actual behavior of registering, retrieving,
 * checking, and managing dependencies.
 */
public interface IDependencyMap<CLASS, INSTANCE>
        extends IDependencyClass<CLASS>, IDependencyInstance<INSTANCE> {




//    default  IDependencyMap<CLASS, INSTANCE> registerDependencyInstance(INSTANCE dependencyInstance) {
//        return null;
//    }
//
//    default  IDependencyMap<CLASS, INSTANCE> registerDependencyClass(CLASS dependencyClass) {
//        return null;
//    }


//    default <ENTRYCLASS extends CLASS, ENTRYINSTANCE extends INSTANCE> void registerDependency(ENTRYCLASS dependencyClass, ENTRYINSTANCE dependencyInstance) {
//
//    }

    default void registerDependency(Class<?> dependencyClass, Class<?> dependencyInstance){

    }

    default void removeDependency(CLASS dependencyClass) {

    }

    default boolean isRegistered(CLASS dependencyClass) {
        return false;
    }


    default Set<CLASS> getClassesAsSet() {
        return null;
    }

    default List<CLASS> getClassesAsList() {
        return null;
    }

    default Collection<CLASS> getClassesAsCollection() {
        return null;
    }

    @SuppressWarnings("unchecked")
    default CLASS[] getClassesAsArray() {
        return null;
    }

    default Set<INSTANCE> getInstancesAsSet() {
        return null;
    }

    default List<INSTANCE> getInstancesAsList() {
        return null;
    }

    default Collection<INSTANCE> getInstancesAsCollection() {
        return null;
    }
    @SuppressWarnings("unchecked")
    default INSTANCE[] getInstancesAsArray() {
        return null;
    }


    default DependencyMap<CLASS,INSTANCE> getDependencyMap() {
        return null;
    }




    /**
     * Gets the total number of registered dependencies in this map.
     *
     * @return The number of dependencies currently registered
     */
    default int getDependencyMapSize() {
        return 0;
    }
}
