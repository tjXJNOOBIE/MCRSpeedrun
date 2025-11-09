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
import com.tjxjnoobie.api.platform.global.console.style.LogColor;

import java.util.function.Supplier;

/**
 * DependencyInstance – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 11/2/2025
 */
public class DependencyInstance<INSTANCE>
        implements IDependencyInstance<INSTANCE> {

    INSTANCE instance;
    Supplier<?> dependencySupplier;
    Class<?> dependencyConcrete;
    IDependencyInstance<INSTANCE> dependencyInstance;

    public DependencyInstance() {
        Log.info(LogColor.BOLD + "[DependencyInstance] " + LogColor.CYAN + "constructed with no concrete class");
    }

    /** Constructor for immediate binding (pre-instantiated or factory-provided) */
    public DependencyInstance(Supplier<INSTANCE> supplier, Class<?> dependencyConcrete) {
        this.dependencySupplier = supplier;
        this.instance = (INSTANCE) dependencyConcrete;
        this.dependencyConcrete = instance != null ? (Class<? extends INSTANCE>) instance.getClass() : null;

        Log.info(LogColor.BOLD + "[DependencyInstance] Bound to concrete: " +
                LogColor.GREEN + (instance != null ? instance.getClass().getSimpleName() : "null"));
    }
    public DependencyInstance(Class<?> dependencyConcrete) {
        if(dependencyConcrete != null) {
        Log.info("[DependencyInstance] Trying to constucut DependencyInstance Constructor for -> " + dependencyConcrete.getClass().getSimpleName());
        createDefaultSupplier(dependencyConcrete);
        this.dependencyConcrete = dependencyConcrete;
        this.instance = (INSTANCE) this.dependencySupplier.get();
        Log.success(LogColor.BOLD + "[DependencyInstance] " +
                 "bound to concrete: " + LogColor.GREEN +
                 (dependencyConcrete != null ? dependencySupplier.get().getClass().getName() : "null"));
    }
        Log.error(LogColor.RED + "[DependencyInstance] dependencyConcrete is null during constructor creation");

    }
    @Override
    public Supplier<?> getDependencyInstance() {
        if (dependencySupplier == null) {
            Log.error(LogColor.BOLD + "[DependencyInstance] " + LogColor.RED + "dependencyFactory is null" +
                      " — call " + LogColor.YELLOW + "setDependencySupplier(...)" + " first.");
            return null;
        }
        Log.info(LogColor.BOLD + "[DependencyInstance] " + "fetching " + LogColor.BLUE + "instance");
        return dependencySupplier;
    }


    @Override
    public Supplier<?> getDependencySupplier() {
        return dependencySupplier;
    }

    @Override
    public void setDependencyInstance(Class<?> instance) {
        if(instance != null){
        Log.warn("[DependencyInstance] Trying to set " + LogColor.YELLOW + "instance" + LogColor.GREEN + "instance: " + LogColor.CYAN + instance.getClass().getSimpleName());
        this.instance = (INSTANCE) instance;
        this.instance = (INSTANCE) dependencyInstance;
        Log.success(LogColor.BOLD + "[DependencyInstance] " + "instance set to " + LogColor.GREEN + instance.getClass().getSimpleName());
    }
        }

    @Override
    public void setDependencySupplier(Class<?> dependencyClass) {
        if (dependencyClass == null) {
            Log.error("[DependencyInstance] Null dependencyClass in setDependencySupplier()");
            return;
        }
        this.dependencySupplier = () -> (INSTANCE) dependencyConcrete;
        Log.warn(LogColor.BOLD + "[DependencyInstance] " + "supplier set from " + LogColor.YELLOW + "Class<?>" +
                ": " + LogColor.CYAN + (dependencyClass != null ? dependencyClass.getSimpleName() : "null"));
    }

//    @Override
//    public void setDependencySupplier(INSTANCE dependencyFactory) {
//        this.dependencyFactory = () -> dependencyFactory;
//        Log.info(LogColor.BOLD + "[DependencyInstance] " + "supplier set from " + LogColor.GREEN + "INSTANCE" +
//                 ": " + LogColor.CYAN + (dependencyFactory != null ? dependencyFactory.getClass().getName() : "null"));
//    }


    @Override
    public INSTANCE refreshDependencyInstance() {
        if (dependencySupplier == null) {
            Log.error(LogColor.BOLD + "[DependencyInstance] " + LogColor.RED + "cannot refresh instance" +
                      " — " + LogColor.YELLOW + "dependencyFactory" + " is null");
            return null;
        }
        Log.warn(LogColor.BOLD + "[DependencyInstance] " + "refreshing " + LogColor.BLUE + "instance");
        instance = (INSTANCE) dependencySupplier;
        return instance;
    }

    @Override
    public void rebindFactory(Supplier<INSTANCE> newFactory) {
        this.dependencySupplier = newFactory;
        Log.info(LogColor.BOLD + "[DependencyInstance] " + "rebound " + LogColor.YELLOW + "factory" +
                 " and " + LogColor.BLUE + "refreshing instance");
        refreshDependencyInstance();
    }
    private void createDefaultSupplier(Class<?> type) {
        dependencySupplier = () -> type ;
        }
}



