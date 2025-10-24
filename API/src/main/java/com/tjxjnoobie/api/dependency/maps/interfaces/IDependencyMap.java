/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.maps.interfaces;

import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.maps.DependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.util.*;
import java.util.function.Supplier;

/**
 * Interface defining the contract for dependency management operations.
 * All methods are abstract and provide no default implementation. Concrete
 * implementations must define the actual behavior of registering, retrieving,
 * checking, and managing dependencies.
 */
public interface IDependencyMap extends IDependencyInjectorHelper, IDependencyMetaData {
    IDependencyMap dependencyMap = new DependencyMap(); //TODO: Replace usages with getDependencyMap()

    List<IContext<?>> contextRegistry = new ArrayList<>(); //TODO: Move into a interface within the registry system (undone)

    /**
     * Registers a dependency instance with its class type and optional factory method.
     * The factory supplier is used to create instances when needed, while direct
     * registration allows for pre-constructed objects. This operation is typically
     * performed during application startup or configuration phase.
     *
     * @param clazz the class type of the dependency being registered
     * @param instance the instance object to register (or a factory that creates it)
     * @param factory an optional supplier function that produces new instances of the dependency
     * @param sourceContext context information about where this registration originated
     */
    default void registerDependency(Class<?> clazz, Object instance, Supplier<?> factory, IContext<?> sourceContext) {
    }


    /**
     * Registers a dependency instance with its class type.
     * This method stores an instance directly without requiring a factory.
     *
     * @param clazz the class type of the dependency being registered
     * @param instance the instance object to register
     */


    default void registerDependency(Class<?> clazz, Object instance){

    }

    default void registerDependency(Class<?> clazz, Object instance, Supplier<?> factory){

    }

    /**
     * Updates the existing instance of a given class in the dependency registry.
     * This allows for reassigning or refreshing a previously registered object.
     *
     * @param clazz the class type whose instance should be updated
     * @param instance the new instance to replace the current one
     */
    default <U> void updateInstance(Class<U> clazz, U instance) {
        //TODO: Change method params to match object instance
    }


    /**
     * Checks whether an instance of a given class is currently registered in the registry.
     *
     * @param clazz the class type to check for registration
     * @return true if the class has been registered; false otherwise
     */
    default boolean isRegistered(Class<?> clazz) {
        return false;
    }

    /**
     * Determines if at least one instance exists for the given class in the registry.
     * This method may differ slightly from isRegistered depending on implementation,
     * but typically returns whether any instance of the specified type is present.
     *
     * @param clazz the class to check for existence
     * @return true if an instance exists; false otherwise
     */
    default boolean hasInstance(Class<?> clazz) {
        return false;
    }

    /**
     * Returns a collection containing all registered dependency instances.
     * This method may return a view or snapshot of current registrations and is useful
     * during diagnostics, testing, or debugging phases.
     *
     * @return a list of all currently registered dependencies
     */
    default List<Object> getAllInstances() {
        return Collections.emptyList();
    }


    /**
     * Retrieves a collection of {@link IDependencyMetaData} entries representing the registered dependencies in this map.
     * Each entry contains metadata about a registered dependency, such as its class type, factory configuration, role, and context.
     *
     * @return a collection of dependency metadata entries for all currently registered dependencies
     */
    default Collection<IDependencyMetaData> getDependencyMapValues(){
        return null;
    }

    default IDependencyMetaData getDependency(Class<?> clazz){
        return null;
    }

    /**
     * Retrieves a list of registered dependency metadata entries sorted by some defined criteria,
     * such as creation time, priority, or type. This method is useful for ordered dependency resolution.
     *
     * @return a list of sorted dependency metadata entries
     */
    default List<IDependencyMetaData> getSortedMetaData() {
        return Collections.emptyList();
    }

    /**
     * Returns all dependency metadata entries that match the specified role (e.g., "main", "config").
     * Roles help categorize dependencies by function or purpose, enabling targeted access.
     *
     * @param role the role filter to apply
     * @return a list of metadata entries matching the given role
     */
    default List<IDependencyMetaData> getByRole(DependencyRole role) {
        return null;
    }

    /**
     * Returns all dependency metadata entries that belong to a specific context (e.g., test, production).
     * Context-based filtering allows for environment-specific dependency access.
     *
     * @param context the context filter to apply
     * @return a list of metadata entries matching the given context
     */
    default List<IDependencyMetaData> getByContext(IContext<?> context) {
        return java.util.Collections.emptyList();
    }

    /**
     * Finds an instance that matches the specified class type, even if it's not exactly registered.
     * This method supports upcasting or interface-based resolution through reflection.
     *
     * @param clazz the class type to match against
     * @return an instance that can be cast to the specified type, or null if none found
     */
    @SuppressWarnings("unchecked")
    default IDependencyMetaData findByAssignableType(Class<?> clazz) {
        return null;
    }


    /**
     * Removes a registered instance of the given class type from the registry.
     * @param clazz the class type whose instance should be removed
     * @return the removed dependency metadata if successfully deleted; null otherwise
     */
    default IDependencyMetaData removeDependency(Class<?> clazz) {
        return null;
    }

    /**
     * Returns a map of statistics about the current state of the dependency registry,
     * including counts by role, context, or registration source.
     *
     * @return a map containing key metrics (e.g., total entries, per-role count)
     */
    default Map<String, Integer> getStatistics() {
        return new HashMap<>();
    }

    /**
     * Generates a human-readable string representation of the current state of the dependency registry.
     * This method is useful for logging, debugging, and monitoring purposes.
     *
     * @return a textual summary of all registered dependencies
     */
    default String generateReport() {
        return "";
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
