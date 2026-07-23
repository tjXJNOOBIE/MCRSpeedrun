/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.utils.interfaces;

import com.tjxjnoobie.api.platform.global.config.keys.interfaces.IDefaultConfigKey;
import com.tjxjnoobie.api.platform.global.config.values.interfaces.IConfigValues;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface IConfigUtils extends org.tavall.dependency.IDependencyInjectableInterface {

    Map<IDefaultConfigKey<?>, IConfigValues<?>> configMaps = new HashMap<>();
    Map<IDefaultConfigKey<?>, IConfigValues<?>> configValues = new ConcurrentHashMap<>();



    @SuppressWarnings("unchecked")
    default <T> T get(IDefaultConfigKey<T> key) {
        IConfigValues<?> values = configValues.get(key);
        if (values != null && key.getType().isInstance(values.getValue())) {
            return (T) values.getValue();
        }
        return null;
    }

    default <T> void set(IDefaultConfigKey<T> key, IConfigValues<?> value) {
        if (key != null && value != null) {
            configValues.put(key,value);
        }
    }

    default boolean contains(IDefaultConfigKey<?> key) {
        return configValues.containsKey(key);
    }

    default void clear(IDefaultConfigKey<?> key) {
        configValues.remove(key);
    }

    default Set<IDefaultConfigKey<?>> getAllKeys() {
        return configValues.keySet();
    }

    default Collection<IConfigValues<?>> getAllValues() {
        return configValues.values();
    }

    default void clearAll() {
        configValues.clear();
    }



    default <T> IConfigValues<?> of(IDefaultConfigKey<T> key, IConfigValues<?> value) {
        return value;
    }

    default <T> IConfigValues<?> getValues(IDefaultConfigKey<T> key) {
        IConfigValues<?> values = configValues.get(key);
        if (values != null && key.getType().isInstance(values.getValue())) {
            return values;
        }
        return null;
    }
}
