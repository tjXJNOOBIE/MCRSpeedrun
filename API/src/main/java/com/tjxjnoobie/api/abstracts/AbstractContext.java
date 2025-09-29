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

    /**
     * Unified retrieval for dependencies:
     * - First checks the static dependency map.
     * - Then falls back to the factory map if no static instance exists.
     *
     * @param clazz The class of the dependency
     * @param <U>   Type of the dependency
     * @return The dependency instance
     * @throws RuntimeException if neither a static instance nor a factory is bound
     */
    @Override
    public <U> U get(Class<U> clazz) {
        // Check static dependencies
        Object dependency = dependencyMap.get(clazz);
        if (dependency != null) {
            return clazz.cast(dependency);
        }

        // Check factory-backed dependencies
        Supplier<?> supplier = factoryMap.get(clazz);
        if (supplier != null) {
            Object instance = supplier.get();
            if (instance == null) {
                throw new IllegalStateException("Factory returned null for " + clazz.getName());
            }
            return clazz.cast(instance);
        }

        // Not found
        throw new RuntimeException("No dependency or factory found for " + clazz.getName());
    }

    /**
     * Explicit retrieval via factory only.
     *
     * @param clazz The class of the dependency
     * @param <U>   Type of the dependency
     * @return The dependency instance from the factory
     * @throws RuntimeException if no factory is bound for the type
     */
    public <U> U getWithFactory(Class<U> clazz) {
        Supplier<?> supplier = factoryMap.get(clazz);
        if (supplier == null) {
            throw new RuntimeException("No factory binding found for " + clazz.getName());
        }
        Object instance = supplier.get();
        if (instance == null) {
            throw new IllegalStateException("Factory returned null for " + clazz.getName());
        }
        return clazz.cast(instance);
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
    public <U> void register(Class<U> clazz, U instance, Supplier<U> factory) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        if (instance == null) {
            throw new IllegalArgumentException("Instance cannot be null");
        }
        if(factory == null){
            throw new IllegalArgumentException("Factory cannot be null");
        }
        dependencyMap.put(clazz, instance);
        factoryMap.put(clazz,factory);

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
    public <U> void rebind(Class<U> clazz, U instance) {
        dependencyMap.put(clazz, instance); // Replace static dependency
    }
    public <U> void rebindFactory(Class<U> clazz, Supplier<U> factory) {
        factoryMap.put(clazz, factory); // Replace dynamic supplier
    }
    public <U> void unbind(Class<U> clazz) {
        dependencyMap.remove(clazz);
        factoryMap.remove(clazz);
    }
    public <U> void reload(Class<U> clazz, Supplier<U> factory) {
        unbind(clazz);              // remove old instance and factory
        rebindFactory(clazz, factory);
        U instance = factory.get(); // create new concrete instance
        rebind(clazz, instance);    // put it in dependencyMap for get()
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

    /**
     * Template method for subclasses to initialize their specific dependencies
     */
    protected abstract void initializeDependencies();
}