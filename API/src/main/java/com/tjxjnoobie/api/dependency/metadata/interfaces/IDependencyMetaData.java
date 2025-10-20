/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata.interfaces;

import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Contract interface for dependency metadata representing a component's role, depth, and relationships
 * within the dependency graph. This interface defines the standard operations for managing
 * dependency resolution, lifecycle, and context ownership.
 *
 */
public interface IDependencyMetaData  {


    default IDependencyMetaData getMetaData(Class<?> clazz){
        return null;
    }

    /**
     * Returns the class of the dependency that this metadata represents.
     * This is the primary type being managed by the dependency graph.
     *
     * @return the class of the dependency
     */
    default Class<?> getDependencyClass() {
        return null;
    }


    /**
     * Returns the set of classes that this component directly depends on.
     * These are the types that must be resolved before this component can be initialized.
     *
     * @return a read-only set of dependency classes
     */
    default Set<Class<?>> getDependencies() {
        return new HashSet<>();
    }

    /**
     * Sets the direct dependencies of this component.
     * This allows configuration of which types must be resolved prior to this component's initialization.
     *
     * @param deps the set of classes this component depends on
     */
    default void setDependencies(Set<Class<?>> deps) {
        // No-op: implementation may be overridden by concrete class
    }


    /**
     * Returns the depth level of this component within the dependency resolution graph.
     * Depth is used to determine the order of component initialization and resolution.
     *
     * @return the depth level (lower values mean earlier in the resolution order)
     */
    default int getDepth() {
        return 0;
    }

    /**
     * Sets the depth level of this component within the dependency resolution graph.
     * This influences the order in which components are resolved and initialized.
     *
     * @param depth the depth level (lower values mean earlier in the resolution order)
     */
    default void setDepth(int depth) {
        // No-op: implementation may be overridden by concrete class
    }

    /**
     * Returns the role assigned to this component in the dependency graph.
     * Roles define responsibilities such as whether a component is a provider or consumer.
     *
     * @return the role of this component
     */
    default DependencyRole getRole() {
        return DependencyRole.ISOLATED;
    }

    /**
     * Sets the role of this component in the dependency graph.
     * This determines how the component behaves in the resolution and lifecycle management.
     *
     * @param role the role assigned to this component
     */
    default void setRole(DependencyRole role) {
        // No-op: implementation may be overridden by concrete class
    }

    /**
     * Returns the pre-construction method associated with this component.
     * This method is invoked before the instance is fully constructed and is used for setup or validation.
     *
     * @return the pre-construction method, or null if not set
     */
    default Method getPreConstruct() {
        return null;
    }

    default Method getPostConstruct(){
        return null;
    }

    /**
     * Sets the pre-construction method for this component.
     * This method is called before the instance is initialized and is used for setup or validation.
     *
     * @param preConstruct the method to invoke before construction
     */
    default void setPreConstruct(Method preConstruct) {
        // No-op: implementation may be overridden by concrete class
    }

    default void setPostConstruct(Method preConstruct) {
        // No-op: implementation may be overridden by concrete class
    }

    /**
     * Checks whether the pre-construction method executed successfully.
     * This flag indicates whether the setup phase completed without errors.
     *
     * @return true if the pre-construction succeeded, false otherwise
     */
    default boolean isPreConstructSuccess() {
        return false;
    }

    /**
     * Updates the success status of the pre-construction phase.
     * This is used to track whether setup operations completed successfully.
     *
     * @param success true if the pre-construction succeeded, false otherwise
     */
    default void setPreConstructSuccess(boolean success) {
        // No-op: implementation may be overridden by concrete class
    }

    /**
     * Returns the number of retry attempts made for this component during initialization.
     * Retries are used when dependency resolution fails and must be attempted again.
     *
     * @return the retry count
     */
    default int getRetryCount() {
        return 0;
    }

    /**
     * Increments the retry count for this component.
     * This is used to track how many times initialization has failed and been retried.
     */
    default void incrementRetryCount() {
        // No-op: implementation may be overridden by concrete class
    }

    /**
     * Returns the bound instance of this component, if any.
     * This is the actual object created and managed by the dependency graph.
     *
     * @return the bound instance, or null if not yet bound
     */
    default Object getDependencyInstance(Class<?> aClass) {
        return null;
    }

    /**
     * Sets the bound instance of this component.
     * This is used to store the actual object instance after successful construction.
     *
     * @param instance the instance to bind
     */
    default void setInstance(Object instance) {
        // No-op: implementation may be overridden by concrete class
    }

    /**
     * Returns the source context that owns this dependency metadata.
     * This identifies the context in which this component is being managed.
     *
     * @return the source context, or null if not assigned
     */
    default IContext<?> getSourceContext() {
        return null;
    }

    /**
     * Assigns the source context that owns this dependency metadata.
     * This is used to track which context is responsible for managing this component.
     *
     * @param ctx the context that owns this metadata
     */
    default void setSourceContext(IContext<?> ctx) {
        // No-op: implementation may be overridden by concrete class
    }

    /**
     * Returns the priority of this component within the dependency graph.
     * Priority influences resolution and initialization precedence relative to other components.
     *
     * @return the priority value
     */
    default int getPriority() {
        return 0;
    }

    /**
     * Sets the priority of this component within the dependency graph.
     * Priority influences resolution and initialization precedence relative to other components.
     *
     * @param priority the priority value to assign
     */
    default void setPriority(int priority) {

    }

    default void setDependencyClass(Class<?> clazz){
        // Implementation overridden in concrete class
    }

    default void setRetryCount(int i){
        // Implementation overridden in concrete class

    }

    /**
     * Returns the factory supplier for creating instances of this dependency.
     * The factory is used to create new instances on demand rather than using a singleton.
     *
     * @return the factory supplier, or null if not set
     */
    default Supplier<?> getFactory() {

        return null;
    }

    /**
     * Sets the factory supplier for creating instances of this dependency.
     * This allows dynamic instance creation rather than singleton behavior.
     *
     * @param factory the supplier that creates new instances
     */
    default void setFactory(Supplier<?> factory) {
        // No-op: implementation may be overridden by concrete class
    }
    default boolean hasInstance(Class<?> clazz){
        return false;
    }

}
