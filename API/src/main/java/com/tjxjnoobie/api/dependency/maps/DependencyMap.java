/*
 * TJVD License (TJ Valentine's Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps;

import com.tjxjnoobie.api.dependency.annotations.DelegatesToInterface;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyClass;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.console.style.LogColor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom map for storing dependency metadata with Class as key and IDependencyMetaData as value.
 * This map provides specialized operations for dependency injection and metadata management.
 * Delegates injection and bind operations to IDependencyInjectorHelper.
 *
 * @author TJ
 * @since 2025
 */
@DelegatesToInterface(getClassForDelegation = IDependencyMap.class)
// Set up DI scanning for conrete/interface relationship
public class DependencyMap<CLASS, INSTANCE >
        extends ConcurrentHashMap<CLASS, INSTANCE>
        implements IDependencyMap<CLASS, INSTANCE>, IDependencyMetaData<CLASS,INSTANCE> {
    private final PriorityQueue<IDependencyClass<CLASS>> registerQueue =
            new PriorityQueue<>(Comparator.comparingInt(dep -> getPriority()));
    IDependencyClass<CLASS> dependencyClass;

    //========= Main Map Registration Methods =========\\



//    @Override
//    public IDependencyMap<CLASS, INSTANCE> registerDependencyClass(CLASS dependencyClass) {
//        put(dependencyClass, (INSTANCE) DUMMY_INSTANCE);
//
//        return this;
//
//    }
//
//    @Override
//    public IDependencyMap<CLASS, INSTANCE> registerDependencyInstance(INSTANCE dependencyInstance) {
//        put(getDependencyClass(), dependencyInstance);
//        return this;
//    }


    @Override
    public void registerDependency(Class<?> dependencyClass, Class<?> dependencyInstance) {
        if(dependencyClass != null || dependencyInstance != null) {
            Log.warn("[DependencyMap] Registering dependency: " + dependencyClass.getSimpleName());
            setDependencyClass(dependencyClass);
            setDependencyInstance(dependencyInstance);
            setDependencySupplier(dependencyInstance);
            put((CLASS) dependencyClass, (INSTANCE) dependencyInstance);
            Log.success("[DependencyMap] Registered Dependency Class: " + dependencyClass.getSimpleName() + " -> " + dependencyInstance.getSimpleName());
            return;
        }
        Log.error("[DependencyMap] dependencyClass or dependencyInstance is null ");
    }

    @Override
    public void removeDependency(CLASS dependencyClass) {
        if (dependencyClass != null) {
            remove(dependencyClass);
            Log.success("[DependencyMap] Removed: " + dependencyClass.getClass().getSimpleName());
        }
    }

    @Override
    public boolean isRegistered(CLASS dependencyClass) {
        if (dependencyClass == null) {
            Log.warn("[DependencyMap] " + LogColor.YELLOW + "isRegistered called with " + LogColor.RED + "null" + LogColor.RESET + " dependency class");
            return false;
        }
        String className = dependencyClass.getClass().getSimpleName();

        Log.info("[DependencyMap] Checking registration for " + LogColor.CYAN + className);

        // Check direct registration from the extended ConcurrentMap
        if (containsKey(dependencyClass)) {
            Log.success("[DependencyMap] Already registered in DependencyMap -> " + LogColor.CYAN + className);
            return true;
        }

        Log.info("[DependencyMap] No direct registration found, checking instance assignments...");
        return false;
    }
    //========= Boot Order Priority Methods =========\\





    //========= Class/Instace Getter Methods | All Views =========\\
    @Override
    public DependencyMap<CLASS, INSTANCE> getDependencyMap() {
        return this;
    }

    @Override
    public Set<CLASS> getClassesAsSet() {
        return new HashSet<>(keySet());
    }
    @Override
    public List<CLASS> getClassesAsList() {
        return new ArrayList<>(keySet());
    }
    @Override
    public Collection<CLASS> getClassesAsCollection() {
        return keySet();
    }
    @SuppressWarnings("unchecked")
    @Override
    public CLASS[] getClassesAsArray() {
        // Create a runtime array of IDependencyClass<?> and cast to CLASS[]
        return keySet().toArray((CLASS[]) new IDependencyClass<?>[keySet().size()]);
    }
    @Override
    public Set<INSTANCE> getInstancesAsSet() {
        return new HashSet<>(values());
    }

    @Override
    public List<INSTANCE> getInstancesAsList() {
        return new ArrayList<>(values());
    }

    @Override
    public Collection<INSTANCE> getInstancesAsCollection() {
        return values();
    }
    @SuppressWarnings("unchecked")
    @Override
    public INSTANCE[] getInstancesAsArray() {
        return values().toArray((INSTANCE[]) new IDependencyInstance<?>[values().size()]);
    }


    /**
     * Gets the total number of registered dependencies in this map.
     *
     * @return The number of dependencies currently registered
     */
    @Override
    public int getDependencyMapSize() {
        return size();
    }

    //========= Map Utility Methods =========\\
    /**
     * Clears all dependencies and logs the action.
     */
    @Override
    public void clear() {
        super.clear();
        Log.info("[DependencyMap] Cleared " + getDependencyMapSize() + " dependencies");
    }


}