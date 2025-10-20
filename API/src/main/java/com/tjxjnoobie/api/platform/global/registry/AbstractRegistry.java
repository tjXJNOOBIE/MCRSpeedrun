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
import com.tjxjnoobie.api.platform.global.registry.enums.RegistryType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AbstractRegistry – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 10/11/2025
 */
public abstract class AbstractRegistry<T> implements IAbstractRegistry<T> {
    public final ConcurrentHashMap<RegistryType, ConcurrentHashMap<IRegistryInstance<T>, IRegistrySettings<T>>> registries = new ConcurrentHashMap<>();




    @Override
    public IAbstractRegistry<T> createRegistry(RegistryType type) {
        registries.putIfAbsent(type, new ConcurrentHashMap<>());
        return this;
    }

    @Override
    public IAbstractRegistry<T> createRegistry(RegistryType... types) {
        for (RegistryType type : types) {
            createRegistry(type);
        }
        return this;
    }

    @Override
    public IAbstractRegistry<T> createRegistry(Map<RegistryType, Map<IRegistryInstance<T>, IRegistrySettings<T>>> prefill) {
        for (@NotNull Map.Entry<RegistryType, Map<IRegistryInstance<T>, IRegistrySettings<T>>> entry : prefill.entrySet()) {
            registries.putIfAbsent(entry.getKey(), new ConcurrentHashMap<>());
            registries.get(entry.getKey()).putAll(entry.getValue());
        }
        return this;
    }
    @Override
    public IAbstractRegistry<T> registerInstance(RegistryType type, IRegistryInstance<T> instance) {
        registries.computeIfAbsent(type, t -> new ConcurrentHashMap<>())
                .putIfAbsent(instance, null);
        return this;
    }
    @Override
    public IAbstractRegistry<T> registerInstance(RegistryType type, IRegistryInstance<T> instance, IRegistrySettings<T> settings) {
        registries.computeIfAbsent(type, t -> new ConcurrentHashMap<>())
                .put(instance, settings);
        return this;
    }

    public IAbstractRegistry<T> registerInstances(RegistryType type, Map<IRegistryInstance<T>, IRegistrySettings<T>> instances) {
        registries.computeIfAbsent(type, t -> new ConcurrentHashMap<>())
                .putAll(instances);
        return this;
    }

    // Create registries from a Map<RegistryType, Map<Instance, Settings>> prefill
    public  void createRegistryWithSettings(Map<RegistryType, Map<IRegistryInstance<T>, IRegistrySettings<T>>> registry) {
        for (Map.Entry<RegistryType, Map<IRegistryInstance<T>, IRegistrySettings<T>>> entry : registry.entrySet()) {
            registries.putIfAbsent(entry.getKey(), new ConcurrentHashMap<>());
            registries.get(entry.getKey()).putAll(entry.getValue());
        }
    }
    public Map<IRegistryInstance<T>, IRegistrySettings<T>> getRegistries(RegistryType type) {
        return registries.getOrDefault(type, new ConcurrentHashMap<>());
    }
    public IRegistryInstance<T> getRegistry(RegistryType type) {
        Map<IRegistryInstance<T>, IRegistrySettings<T>> map = registries.get(type);
        if (map == null || map.isEmpty()) return null;
        return map.keySet().iterator().next();
    }
    // Get settings from instance
    @SuppressWarnings("unchecked")
    public IRegistrySettings<T> getSettings(RegistryType type, IRegistryInstance<T> instance) {
        Map<IRegistryInstance<T>, IRegistrySettings<T>> registry = registries.get(type);
        if (registry == null) {
            Log.critical("Registry not found for type: " + type);
            return null;
        }
        return (IRegistrySettings<T>) registry.get(instance);
    }

    // Get instance from settings
    @SuppressWarnings("unchecked")
    public IRegistryInstance<T> getInstance(RegistryType type, IRegistrySettings<T> settings) {
        Map<IRegistryInstance<T>, IRegistrySettings<T>> registry = registries.get(type);
        if (registry == null){
            Log.critical("Registry not found for type: " + type);
            return null;
    }
        return (IRegistryInstance<T>) registry.entrySet().stream()
                .filter(e -> e.getValue().equals(settings))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

    }
    @Override
    public IRegistryInstance<T> getInstanceBySettings(IRegistrySettings<T> settings, RegistryType type) {
        Map<IRegistryInstance<T>, IRegistrySettings<T>> map = registries.get(type);
        if (map == null) return null;

        return map.entrySet().stream()
                .filter(e -> e.getValue().equals(settings))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
    @Override
    public Map<IRegistryInstance<T>, IRegistrySettings<T>> getRegistryByType(RegistryType type) {
        return registries.getOrDefault(type, new ConcurrentHashMap<>());
    }

    @Override
    public Map<RegistryType, ConcurrentHashMap<IRegistryInstance<T>, IRegistrySettings<T>>> getRegistries() {
        return registries;
    }
    @Override
    public boolean hasRegistry(RegistryType type) {
        return registries.containsKey(type);
    }
    @Override
    public boolean hasInstance(RegistryType type, IRegistryInstance<T> instance) {
        return registries.containsKey(type) && registries.get(type).containsKey(instance);
    }


}

