/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.config.values;

import com.tjxjnoobie.api.platform.global.config.enums.ConfigDomain;
import com.tjxjnoobie.api.platform.global.config.enums.ConfigPathType;
import com.tjxjnoobie.api.platform.global.config.values.interfaces.IConfigValues;

import java.util.*;

public class ConfigValues<T> implements IConfigValues<T> {


    private final T value;
    private final ConfigDomain primaryDomain;
    private final Set<ConfigDomain> associatedDomains;
    private final Map<ConfigDomain, Map<String, Object>> valuesMap;
    private final Set<ConfigPathType> pathTypes;

    public ConfigValues(ConfigDomain primaryDomain, Set<ConfigPathType> pathTypes, T value) {
        this(value, primaryDomain, Set.of(primaryDomain), pathTypes);
    }
    public ConfigValues(T value, ConfigDomain primaryDomain, Set<ConfigDomain> associatedDomains, Set<ConfigPathType> pathTypes) {
        this.value = value;
        this.pathTypes = pathTypes;
        if (primaryDomain == null) throw new IllegalArgumentException("Primary domain cannot be null");
        if (associatedDomains == null || associatedDomains.isEmpty())
            throw new IllegalArgumentException("Associated domains cannot be null or empty");

        this.primaryDomain = primaryDomain;
        this.associatedDomains = Collections.unmodifiableSet(associatedDomains);
        this.valuesMap = new EnumMap<>(ConfigDomain.class);

        for (ConfigDomain domain : associatedDomains) {
            valuesMap.put(domain, new HashMap<>());
        }
    }

    @Override
    public T getValue() {
        return value;
    }
    /**
     * Get a typed value by domain and key path.
     */
    @SuppressWarnings("unchecked")
    public T get(ConfigDomain domain, String key, Class<T> clazz) {
        Map<String, Object> domainMap = valuesMap.get(domain);
        if (domainMap == null) return null;

        Object val = domainMap.get(key);
        if (clazz.isInstance(val)) {
            return (T) val;
        }
        return null;
    }


    /**
     * Set a value for a domain and key path.
     */
    public void set(ConfigDomain domain, String key, T value) {
        Map<String, Object> domainMap = valuesMap.get(domain);
        if (domainMap == null) {
            throw new IllegalArgumentException("Domain not registered: " + domain);
        }
        domainMap.put(key, value);
    }

    public ConfigDomain getPrimaryDomain() {
        return primaryDomain;
    }

    public Set<ConfigDomain> getAssociatedDomains() {
        return associatedDomains;
    }

    public Map<ConfigDomain, Map<String, Object>> getValuesMap() {
        return valuesMap;
    }

    @Override
    public Object getRaw(String key) {
        return valuesMap.get(key);
    }
    @Override
    public Map<ConfigDomain, Map<String, Object>> getAll() {
        return Collections.unmodifiableMap(valuesMap);
    }
    @Override
    public boolean has(String key) {
        return valuesMap.containsKey(key);
    }
    @Override
    public void remove(String key) {
        valuesMap.remove(key);
    }
    @Override
    public void clear() {
        valuesMap.clear();
    }


}

