/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.config.keys.interfaces;

import com.tjxjnoobie.api.platform.global.config.enums.ConfigDomain;
import com.tjxjnoobie.api.platform.global.config.enums.ConfigPathType;
import com.tjxjnoobie.api.platform.global.config.enums.ConfigType;

import java.nio.file.Path;

public interface IDefaultConfigKey<T> {

    IDefaultConfigKey<T> getConfigKeyInstance();

    T getKey();
    default Path getPath() { return getConfigKeyInstance().getPath(); }
    default Class<T> getType() { return getConfigKeyInstance().getType(); }
    default ConfigDomain getDomain(){
        return getConfigKeyInstance().getDomain();
    }
    default ConfigPathType getPathType() { return getConfigKeyInstance().getPathType(); }

    default ConfigType getConfigType(){
        return getConfigKeyInstance().getConfigType();
    }


    default boolean isCustom() { return getConfigKeyInstance().isCustom(); }

    // Optional convenience method:
    default String fullPath() {
        return getDomain().name().toLowerCase() + "." + getPath();
    }

    default boolean isValid(Object value){
        return getConfigKeyInstance().isValid(value);
    }


}
