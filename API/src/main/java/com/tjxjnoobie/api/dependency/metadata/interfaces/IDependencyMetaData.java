/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata.interfaces;

import com.tjxjnoobie.api.dependency.injection.enums.LifecycleType;
import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInterface;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

public interface IDependencyMetaData<INTERFACE, INSTANCE> {

    default void populateMetaData(
            Class<? extends INTERFACE> rawDependencyInterface,
            Class<? extends INSTANCE> rawDependencyConcrete,
            IDependencyInterface<INTERFACE> wrappedInterface,
            IDependencyInstance<INSTANCE> wrappedInstance) {
    }

    default void createDependencyInstance(Class<? extends INSTANCE> dependencyInstance) {
    }

    default void setWrappedInterface(IDependencyInterface<INTERFACE> wrappedInterface) {
    }

    default void setWrappedInstance(IDependencyInstance<INSTANCE> wrappedInstance) {
    }

    default IDependencyInterface<INTERFACE> getWrappedInterface() {
        return null;
    }

    default IDependencyInstance<INSTANCE> getWrappedInstance() {
        return null;
    }

    default Class<? extends INTERFACE> getPrimaryInterfaceType() {
        return null;
    }

    default Class<? extends INSTANCE> getConcreteType() {
        return null;
    }

    default INTERFACE getDependencyInterface() {
        return null;
    }

    default INSTANCE getDependencyInstance() {
        return null;
    }

    default <T> T getDependency(Class<T> dependencyType) {
        return null;
    }

    default <T> T requireDependency(Class<T> dependencyType) {
        return null;
    }

    default EnumMap<LifecycleType, Method> detectLifecycleForClass(INTERFACE dependencyClass) {
        return null;
    }

    default Set<INTERFACE> getSubDependencies() {
        return new HashSet<>();
    }

    default void setSubDependencies(Set<INTERFACE> dependencyClassSet) {
    }

    default int getDepth() {
        return 0;
    }

    default void setDepth(int depth) {
    }

    default DependencyRole getDependencyRole() {
        return DependencyRole.ISOLATED;
    }

    default void setDependencyRole(DependencyRole dependencyRole) {
    }

    default Method getPreConstruct() {
        return null;
    }

    default Method getPostConstruct() {
        return null;
    }

    default void setPreConstruct(Method preConstruct) {
    }

    default void setPostConstruct(Method preConstruct) {
    }

    default boolean isPreConstructSuccess() {
        return false;
    }

    default void setPreConstructSuccess(boolean success) {
    }

    default int getRetryCount() {
        return 0;
    }

    default void incrementRetryCount() {
    }

    default IContext<INTERFACE> getSourceContext() {
        return null;
    }

    default void setSourceContext(IContext<INTERFACE> ctx) {
    }

    default int getPriority() {
        return 0;
    }

    default void setPriority(int priority) {
    }
}
