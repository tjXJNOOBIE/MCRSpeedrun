/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.injection.helpers.interfaces;

import com.tjxjnoobie.api.dependency.maps.DependencyMap;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.internal.InjectionConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * Interface for dependency injection helper functionality.
 * Provides default implementations for fluent priority registration and initialization.
 * All injection and bind methods are centralized here with default no-op implementations.
 * Concrete implementations should override these methods to provide actual functionality.
 * 
 * @author TJ
 * @since 10/12/2025
 */
public interface IDependencyInjectorHelper extends InjectionConfig {

    /**
     * Registers a class with high priority in the dependency injection system.
     * Higher priority classes are initialized earlier in the dependency resolution process.
     * 
     * @param clazz the class to register with priority
     * @param priority the priority level (higher values = higher priority)
     * @return this helper instance for method chaining
     */
    default IDependencyInjectorHelper registerImportant(Class<?> clazz, int priority) {
        // Default no-op implementation - override in concrete class
        return this;
    }


    /**
     * Initializes the dependency injection system.
     * This method builds the dependency graph, computes depth levels,
     * performs injection, and processes any retry queues.
     * 
     * @throws Exception if initialization fails
     */
    default void initialize() throws Exception {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Automatically binds dependencies for the given target.
     * Handles both type-level binding (for Class<?>) and instance-level binding.
     * 
     * @param target the target object or class to bind dependencies for
     */
    default void autoBind(Object target) {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Determines if a field should be injected based on annotations.
     * 
     * @param field the field to check
     * @param autoInject whether auto-injection is enabled
     * @return true if the field should be injected
     */
    default boolean shouldInjectField(java.lang.reflect.Field field, boolean autoInject) {
        // Default no-op implementation - override in concrete class
        return false;
    }

    /**
     * Determines if a method should be injected based on annotations.
     * 
     * @param method the method to check
     * @param autoInject whether auto-injection is enabled
     * @return true if the method should be injected
     */
    default boolean shouldInjectMethod(java.lang.reflect.Method method, boolean autoInject) {
        // Default no-op implementation - override in concrete class
        return false;
    }

    /**
     * Injects a value into a field with error handling.
     * 
     * @param target the target object
     * @param field the field to inject into
     * @param value the value to inject
     * @param optional whether the dependency is optional
     * @param isStatic whether the field is static
     * @param depClass the dependency class
     * @param clazz the declaring class
     */
    default void injectFieldValue(Object target, java.lang.reflect.Field field, Object value, 
                                   boolean optional, boolean isStatic, Class<?> depClass, Class<?> clazz) {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Injects a value via method invocation with error handling.
     * 
     * @param target the target object
     * @param method the method to invoke
     * @param value the value to inject
     * @param optional whether the dependency is optional
     * @param depClass the dependency class
     * @param clazz the declaring class
     */
    default void injectMethodValue(Object target, java.lang.reflect.Method method, Object value, 
                                    boolean optional, Class<?> depClass, Class<?> clazz) {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Executes a lifecycle method (PreConstruct or PostConstruct) with error handling.
     * 
     * @param method the lifecycle method to execute
     * @param target the target object
     * @param clazz the class containing the method
     * @param lifecycleType the type of lifecycle method (e.g., "PreConstruct", "PostConstruct")
     */
    default void executeLifecycleMethod(java.lang.reflect.Method method, Object target, 
                                        Class<?> clazz, String lifecycleType) {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Finds a field in a target class that matches the dependency class.
     * 
     * @param target the target class to search
     * @param depClass the dependency class to find
     * @return the matching field, or null if not found
     */
    default java.lang.reflect.Field findField(Class<?> target, Class<?> depClass) {
        // Default no-op implementation - override in concrete class
        return null;
    }

    /**
     * Calculates the depth of a class based on its dependencies.
     * 
     * @param clazz the class to calculate depth for
     * @param dependencies the set of dependencies
     * @return the calculated depth level
     */
    default int calculateDepth(Class<?> clazz, java.util.Set<Class<?>> dependencies) {
        // Default no-op implementation - override in concrete class
        return 0;
    }

    /**
     * Determines the role of a component based on its dependencies.
     * 
     * @param dependencies the set of dependencies
     * @return the determined role
     */
    default Object determineRole(java.util.Set<Class<?>> dependencies) {
        // Default no-op implementation - override in concrete class
        return null;
    }

    /**
     * Builds the dependency graph for all injectable classes.
     */
    default void buildDependencyGraph() {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Computes depth levels for all classes in the dependency graph.
     */
    default void computeDepthLevels() {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Computes the depth for a specific class recursively.
     * 
     * @param clazz the class to compute depth for
     * @param visited set of already visited classes
     * @param stack current recursion stack for cycle detection
     * @return the computed depth
     */
    default int computeDepthFor(Class<?> clazz, java.util.Set<Class<?>> visited, 
                                java.util.Set<Class<?>> stack) {
        // Default no-op implementation - override in concrete class
        return 0;
    }

    /**
     * Injects all dependencies for all injectable classes.
     * 
     * @throws IllegalAccessException if field access fails
     */
    default void injectAll() throws IllegalAccessException {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Unified injection and metadata population for any target.
     * 
     * @param target the target object to inject and record
     */
    default void injectAndRecordMetaData(Object target) {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Resolves a dependency by class type.
     * 
     * @param depClass the dependency class to resolve
     * @return the resolved dependency instance, or null if not found
     */
    default Object resolveDependency(Class<?> depClass) {
        // Default no-op implementation - override in concrete class
        return null;
    }

    /**
     * Processes the PreConstruct retry queue for failed initializations.
     * 
     * @throws InterruptedException if the thread is interrupted during retry
     */
    default void processPreConstructRetryQueue() throws InterruptedException {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Binds a type-level dependency (for Class<?> objects).
     * 
     * @param type the type to bind
     * @throws Exception if binding fails
     */
    default void bindType(Class<?> type) throws Exception {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Binds a field-level dependency for an instance.
     * 
     * @param target the target object
     * @param field the field to bind
     */
    default void bindField(Object target, java.lang.reflect.Field field) {
        // Default no-op implementation - override in concrete class
    }

    /**
     * Creates a self-proxy for an interface with default methods.
     * 
     * @param iface the interface to create a proxy for
     * @return the created proxy instance
     * @throws Exception if proxy creation fails
     */
    default Object createSelfProxy(Class<?> iface) throws Exception {
        // Default no-op implementation - override in concrete class
        return null;
    }

    /**
     * Creates a placeholder proxy for an unimplemented interface.
     * 
     * @param iface the interface to create a placeholder for
     * @return the created placeholder proxy
     */
    default Object createPlaceholderProxy(Class<?> iface) {
        // Default no-op implementation - override in concrete class
        return null;
    }


    /**
     * Finds implementations of an interface in the specified package.
     * 
     * @param interfaceType the interface to find implementations for
     * @param basePackage the base package to search in
     * @return set of implementation classes
     */
    default Set<Class<?>> findImplementations(Class<?> interfaceType, String basePackage) {
        // Default no-op implementation - override in concrete class
        return new HashSet<>();
    }


    /**
     * Gets the dependency map for direct access.
     * 
     * @return map of registered dependencies
     */
    default IDependencyMap getDependencyMap() {
        // Default no-op implementation - override in concrete class
        return new DependencyMap();
    }
    default java.util.Collection<Object> getAllInstances(){
        return java.util.Collections.emptyList();
    }
//    /**
//     * Gets a dependency by its class type.
//     *
//     * @param clazz the class type to retrieve
//     * @param <U> the type parameter
//     * @return the dependency instance, or null if not found
//     */
//    default <U> U get(Class<U> clazz) {
//        // Default no-op implementation - override in concrete class
//        return null;
//    }
}
