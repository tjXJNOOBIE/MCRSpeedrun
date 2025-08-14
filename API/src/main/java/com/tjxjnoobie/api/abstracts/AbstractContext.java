package com.tjxjnoobie.api.abstracts;

import com.tjxjnoobie.api.annotations.AutoInjectAll;
import com.tjxjnoobie.api.annotations.Inject;
import com.tjxjnoobie.api.annotations.PostConstruct;
import com.tjxjnoobie.api.console.Log;
import com.tjxjnoobie.api.interfaces.IContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Abstract base class for context implementations providing common dependency injection functionality
 * @param <T> The concrete context type
 */
public abstract class AbstractContext<T extends AbstractContext<?>> implements IContext<T> {

    protected final HashMap<Class<?>, Object> dependencyMap = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> factoryMap = new HashMap<>();
    private T context;



    public T getContext() {
        return context;
    }

    @Override
    public <U> U get(Class<U> clazz) {
        // First, try a direct lookup
        Object dependency = dependencyMap.get(clazz);
        if (dependency != null) {
            return clazz.cast(dependency);
        }
        
        // Try to find an instance that is assignable
        for (Object obj : dependencyMap.values()) {
            if (clazz.isInstance(obj)) {
                return clazz.cast(obj);
            }
        }
        
        throw new IllegalArgumentException("No dependency found for " + clazz.getName());
    }
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    @Override
    public HashMap<Class<?>, Object> getDependencyMap() {
        return dependencyMap;
    }

    @Override
    public <U> void register(Class<U> clazz, U instance) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        if (instance == null) {
            throw new IllegalArgumentException("Instance cannot be null");
        }
        dependencyMap.put(clazz, instance);


    }
    public <U> void registerFactory(Class<U> clazz, Supplier<U> factory) {
        if (clazz == null || factory == null) {
            throw new IllegalArgumentException("Factory registration cannot have null arguments.");
        }
        factoryMap.put(clazz, () -> {
            U instance = factory.get();
            injectFields(instance);
            return instance;
        });
    }
    public <U> U createFromFactory(Class<U> clazz) {
        Supplier<?> supplier = factoryMap.get(clazz);
        if (supplier == null) {
            throw new IllegalArgumentException("No factory registered for " + clazz.getName());
        }
        return clazz.cast(supplier.get());
    }
    public <U> U getOrCreate(Class<U> clazz) {
        U instance = getOrNull(clazz);
        if (instance != null) return instance;

        U created = createFromFactory(clazz);
        register(clazz, created);
        return created;
    }
    public void injectFields(Object target) {
        if (target == null) return;
        Class<?> clazz = target.getClass();
        boolean auto = clazz.isAnnotationPresent(AutoInjectAll.class);
        while (clazz != null && clazz != Object.class) {

            for (Field field : clazz.getDeclaredFields()) {
                boolean shouldInject = auto || field.isAnnotationPresent(Inject.class);
                if (!shouldInject) continue;
                Inject inject = field.getAnnotation(Inject.class);

                field.setAccessible(true);
                Class<?> dependencyClass = field.getType();
                Object value = getOrNull(dependencyClass);

                if (value == null && !inject.optional()) {
                    Log.error("[DI] Missing dependency: " + dependencyClass.getName() +
                            " for field '" + field.getName() + "' in " + clazz.getSimpleName());
                    continue;
                }

                try {
                    field.set(target, value);
                    injectFields(value);
                    Log.success("[DI] Injected " + dependencyClass.getSimpleName() +
                            " → " + clazz.getSimpleName() + "." + field.getName());
                } catch (IllegalAccessException e) {
                    Log.error("[DI] Failed to inject " + dependencyClass.getName() +
                            " into " + clazz.getSimpleName() + "." + field.getName());
                }
            }
            clazz = clazz.getSuperclass();



        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.setAccessible(true);
                if (method.getParameterCount() != 0) {
                    Log.error("[DI] PostConstruct method must have no arguments: "
                            + method.getName() + " in " + clazz.getSimpleName());
                    continue;
                }
                try {
                    method.invoke(target);
                    Log.success("[DI] Ran @PostConstruct: " + method.getName()
                            + " in " + clazz.getSimpleName());
                } catch (Exception e) {
                    Log.error("[DI] Failed @PostConstruct: " + method.getName()
                            + " in " + clazz.getSimpleName());
                }
            }
        }
        }
    }
    public boolean isRegistered(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        
        // Check direct registration
        if (dependencyMap.containsKey(clazz)) {
            return true;
        }
        
        // Check if any registered instance is assignable to the class
        for (Object obj : dependencyMap.values()) {
            if (clazz.isInstance(obj)) {
                return true;
            }
        }
        
        return false;
    }



    public <U> U getOrNull(Class<U> clazz) {
        try {
            return get(clazz);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    public void setContext(T context) {
        this.context = context;
    }

    /**
     * Registers multiple dependencies at once
     * @param dependencies Map of class to instance mappings
     */
    protected void registerAll(HashMap<Class<?>, Object> dependencies) {
        if (dependencies != null) {
            dependencyMap.putAll(dependencies);

        }
    }
    public <U> void reload(Class<U> clazz) {
        Supplier<?> factory = factoryMap.get(clazz);
        if (factory == null) {
            throw new IllegalStateException("No factory registered for " + clazz.getName());
        }

        U newInstance = clazz.cast(factory.get());
        register(clazz, newInstance);
    }
    /**
     * Template method for subclasses to initialize their specific dependencies
     */
    protected abstract void initializeDependencies();
}