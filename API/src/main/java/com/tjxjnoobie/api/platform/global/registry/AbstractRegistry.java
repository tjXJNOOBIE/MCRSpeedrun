/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.registry;

import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.console.style.LogColor;
import com.tjxjnoobie.api.platform.global.registry.enums.RegistryType;
import com.tjxjnoobie.api.platform.global.registry.metadata.interfaces.IRegistryData;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AbstractRegistry – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 10/11/2025
 */
public abstract class AbstractRegistry<REG_TYPE extends RegistryType<REG_TYPE,REG_DATA>, REG_DATA extends IRegistryData<REG_TYPE,REG_DATA>>
        extends ConcurrentHashMap<REG_TYPE, REG_DATA> implements IAbstractRegistry<REG_TYPE, REG_DATA> {


    //========= Main Registry Registration Methods =========\\

    @Override
    public IAbstractRegistry<REG_TYPE, REG_DATA> createRegistry(REG_TYPE registryType, REG_DATA registryData) {
        computeIfAbsent(registryType, (k) -> {
            Log.success("[Registry] Registering Registry Type:"
                    +registryType.getClass().getSimpleName() +
                    " Bound RegistryData to -> " + registryType);
            return registryData;
        });

        return this;
    }

    //========= Registry Getter Methods | Multi-View =========\\


    @Override
    public IAbstractRegistry<REG_TYPE,REG_DATA> getAbstractRegistry() {
        return this;
    }
    //TODO: Check method function, we should be retruning the type, which is a key not a value
    @Override
    public RegistryType<REG_TYPE,REG_DATA> getRegistryTypeByData(REG_DATA registryData) {
        if (registryData == null ) {
            Log.error("[AbstractRegistry] Registry type not found: " + registryData);
            return null;
        }
        Log.success("[AbstractRegistry] Found -> " + LogColor.BLUE + registryData.getClass().getSimpleName());
        return keySet().stream().filter(k -> k.equals(registryData)).findFirst().orElse(null);
    }

    @Override
    public REG_DATA getRegistryData(REG_TYPE registryType){
        return get(registryType);
    }
    @Override
    public Set<REG_TYPE> getRegistryTypesAsSet() {
        return new HashSet<>(keySet());
    }
    @Override
    public List<REG_TYPE> getRegistryTypesAsList() {
        return new ArrayList<>(keySet());
    }
    @Override
    public Collection<REG_TYPE> getRegistryTypesAsCollection() {
        return keySet();
    }
    @SuppressWarnings("unchecked")
    @Override
    public REG_TYPE[] getRegistryTypesAsArray() {
        REG_TYPE[] registryTypeArray  = (REG_TYPE[]) Array.newInstance(
                getRegistryType().getClass(), keySet().size()
        );
        return keySet().toArray(registryTypeArray);
    }

    @Override
    public Set<REG_DATA> getRegistryDataBySet() {
        return new HashSet<>(values());
    }
    @Override
    public List<REG_DATA> getRegistryDatasByList() {
        return new ArrayList<>(values());
    }
    @Override
    public Collection<REG_DATA> getRegistryDataByCollection() {
        return values();
    }
    @SuppressWarnings("unchecked")
    @Override
    public REG_DATA[] getRegistryDataByArray() {
        return values().toArray((REG_DATA[]) new IRegistryData<?>[keySet().size()]);
    }


    //========= Registry Checker/Boolean Methods =========\\


    @Override
    public boolean hasRegistryType(REG_TYPE registryType) {
        if(registryType == null){
            Log.error("[AbstractRegistry] Registry type is null: " + registryType);
            return false;
        }
        Log.success("[AbstractRegistry] Found Registry Type -> " + LogColor.BLUE + registryType.getClass().getSimpleName());
        return containsKey(registryType);
    }
    @Override
    public boolean hasRegistryData(REG_DATA registryData) {
        if(registryData == null){
            Log.error("[AbstractRegistry] Registry data is null: " + registryData);
            return false;
        }
        Log.success("[AbstractRegistry] Found Registry Data -> " + LogColor.BLUE + registryData.getClass().getSimpleName());
        return containsValue(registryData);
    }




}

