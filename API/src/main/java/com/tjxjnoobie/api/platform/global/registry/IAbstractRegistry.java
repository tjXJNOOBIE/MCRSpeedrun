package com.tjxjnoobie.api.platform.global.registry;

import com.tjxjnoobie.api.platform.global.registry.enums.RegistryType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base interface for registry containers that map RegistryType to instances/settings.
 * @param <T> the element type held by the registry instances
 */
public interface IAbstractRegistry<T> {

    /**
     * Retrieve all registry mappings.
     *
     * @return a map from RegistryType to a concurrent map of registry instances and their settings;
     *         may be {@code null} if no registries have been created
     */
    default Map<IRegistryInstance<T>, IRegistrySettings<T>> getRegistries(RegistryType type) {
        return null;
    }

    /**
     * Create an empty registry for the given type.
     *
     * @param type the registry category to initialize
     */
    default IAbstractRegistry<T> createRegistry(RegistryType type) {
        return this;
    }

    /**
     * Create empty registries for multiple types.
     *
     * @param types one or more registry categories to initialize
     */
    default IAbstractRegistry<T> createRegistry(RegistryType... types) {
        return this;
    }

    /**
     * Initialize registries with a prefill map.
     *
     * @param prefill a map where keys are registry types and values are
     *                mappings from instances to their settings
     */
    default IAbstractRegistry<T> createRegistry(Map<RegistryType, Map<IRegistryInstance<T>, IRegistrySettings<T>>> prefill) {
        return this;
    }

    /**
     * Add a new instance and its settings to the specified registry.
     *
     * @param type      the target registry category
     * @param instance  the registry instance to add
     * @param settings  the settings associated with that instance
     */
    default void add(RegistryType type, IRegistryInstance<T> instance, IRegistrySettings<T> settings) {
    }

    /**
     * Register an instance in a registry.
     *
     * @param type      the registry category
     * @param instance  the registry instance to register
     * @return this registry instance for method chaining
     */
    default IAbstractRegistry<T> registerInstance(RegistryType type, IRegistryInstance<T> instance) {
        return this;
    }

    /**
     * Register an instance with settings in a registry.
     *
     * @param type      the registry category
     * @param instance  the registry instance to register
     * @param settings  the settings associated with that instance
     * @return this registry instance for method chaining
     */
    default IAbstractRegistry<T> registerInstance(RegistryType type, IRegistryInstance<T> instance, IRegistrySettings<T> settings) {
        return this;
    }

    /**
     * Register multiple instances with settings in a registry.
     *
     * @param type      the registry category
     * @param instances a map of instances to their settings
     * @return this registry instance for method chaining
     */
    default IAbstractRegistry<T> registerInstances(RegistryType type, Map<IRegistryInstance<T>, IRegistrySettings<T>> instances) {
        return this;
    }
    /**
     * Look up the settings for a given instance in a registry.
     *
     * @param type      the registry category
     * @param instance  the registry instance whose settings are requested
     * @return the settings for the instance, or {@code null} if none exist
     */
    default IRegistrySettings<T> getSettings(RegistryType type, IRegistryInstance<T> instance) {
        return null;
    }

    /**
     * Find the registry instance that corresponds to the given settings.
     *
     * @param type      the registry category
     * @param settings  the settings key to search by
     * @return the matching instance, or {@code null} if no match is found
     */
    default IRegistryInstance<T> getInstance(RegistryType type, IRegistrySettings<T> settings) {
        return null;
    }

    /**
     * Retrieve a registry instance by type.
     *
     * @param type  the registry category
     * @return the default instance for that type, or {@code null} if not set
     */
    default IRegistryInstance<T> getRegistry(RegistryType type) {
        return null;
    }

    /**
     * Get instance by settings.
     *
     * @param settings  the settings key to search by
     * @param type      the registry category
     * @return the matching instance, or {@code null} if no match is found
     */
    default IRegistryInstance<T> getInstanceBySettings(IRegistrySettings<T> settings, RegistryType type) {
        return null;
    }

    /**
     * Retrieve all registry instances grouped by type.
     *
     * @return a map from RegistryType to a concurrent map of instances and settings
     */
    default Map<RegistryType, ConcurrentHashMap<IRegistryInstance<T>, IRegistrySettings<T>>> getRegistries() {
        return null;
    }

    /**
     * Check if a registry exists for the given type.
     *
     * @param type the registry category
     * @return true if a registry exists, false otherwise
     */
    default boolean hasRegistry(RegistryType type) {
        return false;
    }

    /**
     * Check if an instance exists in a registry.
     *
     * @param type      the registry category
     * @param instance  the instance to check for
     * @return true if the instance exists, false otherwise
     */
    default boolean hasInstance(RegistryType type, IRegistryInstance<T> instance) {
        return false;
    }

    /**
     * Retrieve a registry instance by type.
     *
     * @param type the registry category
     * @return a map of instances and settings for that type
     */
    default Map<IRegistryInstance<T>, IRegistrySettings<T>> getRegistryByType(RegistryType type) {
        return null;
    }
}