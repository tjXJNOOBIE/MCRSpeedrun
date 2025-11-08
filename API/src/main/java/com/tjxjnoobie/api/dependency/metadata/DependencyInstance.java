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
    Supplier<INSTANCE> dependencyFactory;
    Class<?> dependencyConcrete;

    public DependencyInstance() {
        Log.info(LogColor.BOLD + "[DependencyInstance] " + LogColor.CYAN + "constructed with no concrete class");
    }
    public DependencyInstance(Class<?> dependencyConcrete) {
        this.dependencyConcrete = dependencyConcrete;
        Log.info(LogColor.BOLD + "[DependencyInstance] " +
                 "bound to concrete: " + LogColor.GREEN +
                 (dependencyConcrete != null ? dependencyConcrete.getName() : "null"));
    }
    @Override
    public INSTANCE getDependencyInstance() {
        if (dependencyFactory == null) {
            Log.error(LogColor.BOLD + "[DependencyInstance] " + LogColor.RED + "dependencyFactory is null" +
                      " — call " + LogColor.YELLOW + "setDependencySupplier(...)" + " first.");
            return null;
        }
        Log.info(LogColor.BOLD + "[DependencyInstance] " + "fetching " + LogColor.BLUE + "instance");
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
    public void setDependencySupplier(Class<?> dependencyInstance) {
        this.dependencyFactory = () -> (INSTANCE) dependencyConcrete;

    }

    @Override
    public void setDependencySupplier(INSTANCE dependencyFactory) {
        this.dependencyFactory = () -> dependencyFactory;
        Log.info(LogColor.BOLD + "[DependencyInstance] " + "supplier set from " + LogColor.GREEN + "INSTANCE" +
                 ": " + LogColor.CYAN + (dependencyFactory != null ? dependencyFactory.getClass().getName() : "null"));
    }

    @Override
    public INSTANCE getOrCreateDependencyInstance() {
        if (dependencyFactory == null) {
            Log.error(LogColor.BOLD + "[DependencyInstance] " + LogColor.RED + "cannot create instance" +
                      " — " + LogColor.YELLOW + "dependencyFactory" + " is null");
            return null;
        }
        if (instance == null) {
            Log.info(LogColor.BOLD + "[DependencyInstance] " + "creating " + LogColor.BLUE + "new instance");
            instance = dependencyFactory.get();
        } else {
            Log.info(LogColor.BOLD + "[DependencyInstance] " + "returning " + LogColor.GREEN + "cached instance");
        }
        return instance;
    }

    @Override
    public INSTANCE refreshDependencyInstance() {
        if (dependencyFactory == null) {
            Log.error(LogColor.BOLD + "[DependencyInstance] " + LogColor.RED + "cannot refresh instance" +
                      " — " + LogColor.YELLOW + "dependencyFactory" + " is null");
            return null;
        }
        Log.warn(LogColor.BOLD + "[DependencyInstance] " + "refreshing " + LogColor.BLUE + "instance");
        instance = dependencyFactory.get();
        return instance;
    }

    @Override
    public void rebindFactory(Supplier<INSTANCE> newFactory) {
        this.dependencyFactory = newFactory;
        Log.info(LogColor.BOLD + "[DependencyInstance] " + "rebound " + LogColor.YELLOW + "factory" +
                 " and " + LogColor.BLUE + "refreshing instance");
        refreshDependencyInstance();
    }


}
