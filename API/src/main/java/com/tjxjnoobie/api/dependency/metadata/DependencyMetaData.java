/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata;

import com.tjxjnoobie.api.dependency.injection.enums.LifecycleType;
import com.tjxjnoobie.api.dependency.maps.DependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.wrappers.interfaces.IDependencyInterface;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class DependencyMetaData<INTERFACE, INSTANCE> implements IDependencyMetaData<INTERFACE, INSTANCE> {
    private Set<INTERFACE> subDependencies = new HashSet<>();
    private int priority;
    private int depth;
    private DependencyRole dependencyRole = DependencyRole.ISOLATED;
    private final Map<LifecycleType, Method> lifecycleMethods = new EnumMap<>(LifecycleType.class);
    private final Map<LifecycleType, Boolean> lifecycleSuccess = new EnumMap<>(LifecycleType.class);
    private int retryCount;
    private IContext<INTERFACE> sourceContext;
    private Supplier<INSTANCE> dependencySupplier;
    private IDependencyInterface<INTERFACE> wrappedInterface;
    private IDependencyInstance<INSTANCE> wrappedInstance;
    private Class<? extends INTERFACE> primaryInterfaceType;
    private Class<? extends INSTANCE> concreteType;
    private INSTANCE dependencyInstance;

    public DependencyMetaData() {
        resolveTypesFromSubclass();
    }

    private void resolveTypesFromSubclass() {
        Type superType = getClass().getGenericSuperclass();
        if (!(superType instanceof ParameterizedType parameterizedType)) {
            return;
        }

        Class<? extends INTERFACE> resolvedInterface = resolveClass(parameterizedType.getActualTypeArguments()[0]);
        Class<? extends INSTANCE> resolvedConcrete = resolveClass(parameterizedType.getActualTypeArguments()[1]);

        if (resolvedInterface != null) {
            this.primaryInterfaceType = resolvedInterface;
        }
        if (resolvedConcrete != null) {
            this.concreteType = resolvedConcrete;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> resolveClass(Type type) {
        if (type instanceof Class<?> rawClass) {
            return (Class<? extends T>) rawClass;
        }
        if (type instanceof ParameterizedType parameterizedType && parameterizedType.getRawType() instanceof Class<?> rawType) {
            return (Class<? extends T>) rawType;
        }
        return null;
    }

    @Override
    public void populateMetaData(
            Class<? extends INTERFACE> rawDependencyInterface,
            Class<? extends INSTANCE> rawDependencyConcrete,
            IDependencyInterface<INTERFACE> wrappedInterface,
            IDependencyInstance<INSTANCE> wrappedInstance) {

        if (rawDependencyInterface == null || rawDependencyConcrete == null) {
            Log.error("[DependencyMetaData] dependency interface or concrete type is null on metadata population");
            return;
        }

        this.primaryInterfaceType = rawDependencyInterface;
        this.concreteType = rawDependencyConcrete;
        setWrappedInterface(wrappedInterface);
        setWrappedInstance(wrappedInstance);

        if (this.wrappedInterface != null) {
            this.wrappedInterface.setDependencyInterfaceWrapperRawClass(rawDependencyInterface);
        }
        if (this.wrappedInstance != null) {
            this.wrappedInstance.setWrappedRawInstanceClass(rawDependencyConcrete);
        }

        createDependencyInstance(rawDependencyConcrete);
    }

    @Override
    public void createDependencyInstance(Class<? extends INSTANCE> dependencyInstanceClass) {
        if (dependencyInstanceClass == null) {
            Log.error("[DependencyMetaData] dependencyInstanceClass is null during createDependencyInstance()");
            return;
        }

        this.concreteType = dependencyInstanceClass;
        setDependencySupplier(() -> instantiate(dependencyInstanceClass));
        this.dependencyInstance = dependencySupplier.get();

        if (wrappedInstance != null) {
            wrappedInstance.setWrappedRawInstanceClass(dependencyInstanceClass);
            wrappedInstance.setWrappedDependencyInstance(this.dependencyInstance);
        }

        if (wrappedInterface != null) {
            wrappedInterface.setDependencyInterface(getDependencyInterface());
        }
    }

    private INSTANCE instantiate(Class<? extends INSTANCE> dependencyInstanceClass) {
        try {
            var constructor = dependencyInstanceClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate dependency " + dependencyInstanceClass.getName(), e);
        }
    }

    public void setDependencySupplier(Supplier<INSTANCE> supplier) {
        this.dependencySupplier = supplier;
    }

    public void refreshDependencyInstance(Supplier<INSTANCE> dependencySupplier) {
        setDependencySupplier(dependencySupplier);
        this.dependencyInstance = dependencySupplier == null ? null : dependencySupplier.get();
    }

    @Override
    public void setWrappedInterface(IDependencyInterface<INTERFACE> wrappedInterface) {
        this.wrappedInterface = wrappedInterface;
    }

    @Override
    public void setWrappedInstance(IDependencyInstance<INSTANCE> wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    @Override
    public IDependencyInterface<INTERFACE> getWrappedInterface() {
        return wrappedInterface;
    }

    @Override
    public IDependencyInstance<INSTANCE> getWrappedInstance() {
        return wrappedInstance;
    }

    @Override
    public Class<? extends INTERFACE> getPrimaryInterfaceType() {
        return primaryInterfaceType;
    }

    @Override
    public Class<? extends INSTANCE> getConcreteType() {
        return concreteType;
    }

    @Override
    public INTERFACE getDependencyInterface() {
        if (primaryInterfaceType != null && primaryInterfaceType.isInstance(dependencyInstance)) {
            return primaryInterfaceType.cast(dependencyInstance);
        }

        if (wrappedInterface != null && wrappedInterface.getInterface() != null) {
            return wrappedInterface.getInterface();
        }

        if (primaryInterfaceType != null) {
            return getDependency(primaryInterfaceType);
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public INSTANCE getDependencyInstance() {
        if (dependencyInstance != null) {
            return dependencyInstance;
        }

        if (primaryInterfaceType == null) {
            return null;
        }

        Object resolved = DependencyMap.getDependencyMap().getInstance(primaryInterfaceType);
        if (resolved == null) {
            return null;
        }

        if (concreteType == null || concreteType.isInstance(resolved)) {
            return (INSTANCE) resolved;
        }

        return null;
    }

    @Override
    public <T> T getDependency(Class<T> dependencyType) {
        if (dependencyType == null) {
            return null;
        }

        if (dependencyType.isInstance(dependencyInstance)) {
            return dependencyType.cast(dependencyInstance);
        }

        return DependencyMap.getDependencyMap().getInstance(dependencyType);
    }

    @Override
    public <T> T requireDependency(Class<T> dependencyType) {
        T dependency = getDependency(dependencyType);
        if (dependency != null) {
            return dependency;
        }
        throw new IllegalStateException("No dependency registered for " + dependencyType.getName());
    }

    @Override
    public EnumMap<LifecycleType, Method> detectLifecycleForClass(INTERFACE dependencyClass) {
        EnumMap<LifecycleType, Method> map = new EnumMap<>(LifecycleType.class);
        if (dependencyClass == null) {
            return map;
        }

        for (LifecycleType lifecycle : LifecycleType.values()) {
            lifecycle.findIn(dependencyClass.getClass()).ifPresent(method -> map.put(lifecycle, method));
        }

        return map;
    }

    @Override
    public Set<INTERFACE> getSubDependencies() {
        return subDependencies;
    }

    @Override
    public void setSubDependencies(Set<INTERFACE> dependencyClassSet) {
        this.subDependencies = dependencyClassSet == null ? new HashSet<>() : dependencyClassSet;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public DependencyRole getDependencyRole() {
        return dependencyRole;
    }

    @Override
    public void setDependencyRole(DependencyRole dependencyRole) {
        this.dependencyRole = dependencyRole;
    }

    @Override
    public Method getPreConstruct() {
        return lifecycleMethods.get(LifecycleType.PRE_CONSTRUCT);
    }

    @Override
    public Method getPostConstruct() {
        return lifecycleMethods.get(LifecycleType.POST_CONSTRUCT);
    }

    @Override
    public void setPreConstruct(Method preConstruct) {
        lifecycleMethods.put(LifecycleType.PRE_CONSTRUCT, preConstruct);
    }

    @Override
    public void setPostConstruct(Method postConstruct) {
        lifecycleMethods.put(LifecycleType.POST_CONSTRUCT, postConstruct);
    }

    @Override
    public boolean isPreConstructSuccess() {
        return lifecycleSuccess.getOrDefault(LifecycleType.PRE_CONSTRUCT, false);
    }

    @Override
    public void setPreConstructSuccess(boolean success) {
        lifecycleSuccess.put(LifecycleType.PRE_CONSTRUCT, success);
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public void incrementRetryCount() {
        retryCount++;
    }

    @Override
    public IContext<INTERFACE> getSourceContext() {
        return sourceContext;
    }

    @Override
    public void setSourceContext(IContext<INTERFACE> ctx) {
        this.sourceContext = ctx;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
