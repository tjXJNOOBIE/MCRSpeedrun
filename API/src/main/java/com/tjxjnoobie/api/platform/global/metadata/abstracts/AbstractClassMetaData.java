package com.tjxjnoobie.api.platform.global.metadata.abstracts;

import com.tjxjnoobie.api.platform.global.annotations.ClassOptions;
import com.tjxjnoobie.api.platform.global.annotations.Injectable;
import com.tjxjnoobie.api.platform.global.metadata.enums.ClassSetting;
import com.tjxjnoobie.api.platform.global.metadata.interfaces.IAbstractClassMetaData;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Abstract base class providing comprehensive class metadata functionality.
 * This is the central hub for all metadata operations, following proper DI principles.
 * No static methods, singletons, or inner classes - everything is instance-based and injectable.
 * 
 * The type parameter T represents the implementing class, and all metadata operations
 * automatically work with this type. Any class that extends this automatically gets
 * tracked and counted without any extra wiring.
 * 
 * @param <T> The class type that this metadata represents (should be the implementing class)
 */
@Injectable("Abstract Class Metadata Manager")
public abstract class AbstractClassMetaData<T> implements IAbstractClassMetaData<T> {
    
    // Registry for all class metadata - maps class to its metadata fields
    private final ConcurrentHashMap<Class<?>, AtomicInteger> constructorCountRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Boolean> isInterfaceRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Boolean> isAbstractRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Boolean> isEnumRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Set<String>> publicMethodsRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Set<String>> protectedMethodsRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Set<String>> privateMethodsRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Set<String>> publicFieldsRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Set<String>> protectedFieldsRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Set<String>> privateFieldsRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Set<Class<?>>> providedAsRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Set<Class<?>>> attachedDependenciesRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Set<Class<?>>> instantiatedByRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, AtomicInteger> registrationCountRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, AtomicInteger> instantiationCountRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Map<ClassSetting, Object>> settingsRegistry = new ConcurrentHashMap<>();
    //TODO: Collapse all maps into one map, replace all registers with abstract registry
    // Make ENUMS for most maps
    /**
     * Constructor that automatically registers this instance's type.
     */
    protected AbstractClassMetaData() {
        // Automatically register and track this class
        Class<T> actualType = getActualType();
        initializeMetadataForClass(actualType);
        // Increment instantiation count for this type
        recordDiInstantiation(actualType, null);
    }
    
    /**
     * Get the actual class type for this instance.
     * This uses reflection to determine the implementing class.
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getActualType() {
        return (Class<T>) this.getClass();
    }
    
    /**
     * Initialize metadata for a class if not already initialized.
     */
    private void initializeMetadataForClass(Class<?> type) {
        if (type == null) return;
        
        // Initialize structure metadata if not present
        constructorCountRegistry.computeIfAbsent(type, t -> new AtomicInteger(t.getDeclaredConstructors().length));
        isInterfaceRegistry.computeIfAbsent(type, Class::isInterface);
        isAbstractRegistry.computeIfAbsent(type, t -> Modifier.isAbstract(t.getModifiers()));
        isEnumRegistry.computeIfAbsent(type, Class::isEnum);
        
        // Initialize method metadata
        publicMethodsRegistry.computeIfAbsent(type, t -> computeMethods(t, Modifier.PUBLIC));
        protectedMethodsRegistry.computeIfAbsent(type, t -> computeMethods(t, Modifier.PROTECTED));
        privateMethodsRegistry.computeIfAbsent(type, t -> computeMethods(t, Modifier.PRIVATE));
        
        // Initialize field metadata
        publicFieldsRegistry.computeIfAbsent(type, t -> computeFields(t, Modifier.PUBLIC));
        protectedFieldsRegistry.computeIfAbsent(type, t -> computeFields(t, Modifier.PROTECTED));
        privateFieldsRegistry.computeIfAbsent(type, t -> computeFields(t, Modifier.PRIVATE));
        
        // Initialize DI metadata
        providedAsRegistry.computeIfAbsent(type, t -> ConcurrentHashMap.newKeySet());
        attachedDependenciesRegistry.computeIfAbsent(type, t -> ConcurrentHashMap.newKeySet());
        instantiatedByRegistry.computeIfAbsent(type, t -> ConcurrentHashMap.newKeySet());
        registrationCountRegistry.computeIfAbsent(type, t -> new AtomicInteger(0));
        instantiationCountRegistry.computeIfAbsent(type, t -> new AtomicInteger(0));
        
        // Initialize settings from annotations
        settingsRegistry.computeIfAbsent(type, t -> {
            Map<ClassSetting, Object> settings = new ConcurrentHashMap<>();
            ClassOptions options = t.getAnnotation(ClassOptions.class);
            if (options != null) {
                settings.put(ClassSetting.EXCEPTION_WRAPPING_ENABLED, options.exceptionWrapping());
                settings.put(ClassSetting.LOG_ELIGIBLE_INJECTION, options.logEligibleInjection());
                settings.put(ClassSetting.TAGS, options.tags());
                settings.put(ClassSetting.LOG_VERBOSITY_LEVEL, options.logVerbosityLevel());
                settings.put(ClassSetting.CACHE_INSTANCES, options.cacheInstances());
                settings.put(ClassSetting.LAZY_INITIALIZATION, options.lazyInitialization());
                settings.put(ClassSetting.SINGLETON_SCOPE, options.singletonScope());
                settings.put(ClassSetting.PROXY_CREATION_ENABLED, options.proxyCreationEnabled());
                settings.put(ClassSetting.DEPENDENCY_VALIDATION_STRICT, options.dependencyValidationStrict());
                settings.put(ClassSetting.PERFORMANCE_MONITORING, options.performanceMonitoring());
            }
            return settings;
        });
    }
    
    /**
     * Compute methods for a class with specific visibility.
     */
    private Set<String> computeMethods(Class<?> clazz, int visibility) {
        Set<String> methods = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if ((method.getModifiers() & visibility) != 0) {
                methods.add(formatMethodSignature(method));
            }
        }
        return Collections.unmodifiableSet(methods);
    }
    
    /**
     * Compute fields for a class with specific visibility.
     */
    private Set<String> computeFields(Class<?> clazz, int visibility) {
        Set<String> fields = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if ((field.getModifiers() & visibility) != 0) {
                fields.add(field.getName());
            }
        }
        return Collections.unmodifiableSet(fields);
    }
    
    /**
     * Format method signature for display.
     */
    private String formatMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName()).append("(");
        Class<?>[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(params[i].getSimpleName());
        }
        sb.append(")");
        return sb.toString();
    }
    
    // ========== IClassMetaData Implementation (for type T) ==========
    
    public Class<?> getType() {
        return getActualType();
    }
    
    
    public int getConstructorCount() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return constructorCountRegistry.get(type).get();
    }
    
    
    public boolean isInterface() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return isInterfaceRegistry.get(type);
    }
    
    
    public boolean isAbstract() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return isAbstractRegistry.get(type);
    }
    
    
    public boolean isEnum() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return isEnumRegistry.get(type);
    }
    
    
    public Set<String> getPublicMethods() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return publicMethodsRegistry.get(type);
    }
    
    
    public Set<String> getProtectedMethods() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return protectedMethodsRegistry.get(type);
    }
    
    
    public Set<String> getPrivateMethods() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return privateMethodsRegistry.get(type);
    }
    
    
    public Set<String> getPublicFields() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return publicFieldsRegistry.get(type);
    }
    
    
    public Set<String> getProtectedFields() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return protectedFieldsRegistry.get(type);
    }
    
    
    public Set<String> getPrivateFields() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return privateFieldsRegistry.get(type);
    }
    
    
    public Set<Class<?>> getProvidedAs() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return Collections.unmodifiableSet(new HashSet<>(providedAsRegistry.get(type)));
    }
    
    
    public Set<Class<?>> getAttachedDependencies() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return Collections.unmodifiableSet(new HashSet<>(attachedDependenciesRegistry.get(type)));
    }
    
    
    public Set<Class<?>> getInstantiatedBy() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return Collections.unmodifiableSet(new HashSet<>(instantiatedByRegistry.get(type)));
    }
    
    
    public int getRegistrationCount() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return registrationCountRegistry.get(type).get();
    }
    
    
    public int getInstantiationCount() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return instantiationCountRegistry.get(type).get();
    }
    
    
    public Map<ClassSetting, Object> getSettings() {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        return Collections.unmodifiableMap(new HashMap<>(settingsRegistry.get(type)));
    }
    
    
    @SuppressWarnings("unchecked")
    public <V> V getSetting(ClassSetting setting) {
        Class<T> type = getActualType();
        initializeMetadataForClass(type);
        Object value = settingsRegistry.get(type).get(setting);
        return setting.castValue(value);
    }
    
    
    public boolean isExceptionWrappingEnabled() {
        return getSetting(ClassSetting.EXCEPTION_WRAPPING_ENABLED);
    }
    
    
    public boolean isLogEligibleInjection() {
        return getSetting(ClassSetting.LOG_ELIGIBLE_INJECTION);
    }
    
    
    public String[] getTags() {
        return getSetting(ClassSetting.TAGS);
    }
    
    
    public int getLogVerbosityLevel() {
        return getSetting(ClassSetting.LOG_VERBOSITY_LEVEL);
    }
    
    
    public boolean isCacheInstances() {
        return getSetting(ClassSetting.CACHE_INSTANCES);
    }
    
    
    public boolean isLazyInitialization() {
        return getSetting(ClassSetting.LAZY_INITIALIZATION);
    }
    
    
    public boolean isSingletonScope() {
        return getSetting(ClassSetting.SINGLETON_SCOPE);
    }
    
    
    public boolean isProxyCreationEnabled() {
        return getSetting(ClassSetting.PROXY_CREATION_ENABLED);
    }
    
    
    public boolean isDependencyValidationStrict() {
        return getSetting(ClassSetting.DEPENDENCY_VALIDATION_STRICT);
    }
    
    
    public boolean isPerformanceMonitoring() {
        return getSetting(ClassSetting.PERFORMANCE_MONITORING);
    }
    
    // ========== IAbstractClassMetaData Implementation ==========
    
    
    public Class<T> getMetadataTargetType() {
        return getActualType();
    }
    
    
    public IAbstractClassMetaData<?> getOrCreateMetadata(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        
        // Initialize metadata for the requested type
        initializeMetadataForClass(type);

        // Return a type-bound view over our registries
        return this;
    }
    
    
    public int getClassConstructorCount() {
        return getConstructorCount();
    }
    
    
    public boolean isClassInterface() {
        return isInterface();
    }
    
    
    public boolean isClassAbstract() {
        return isAbstract();
    }
    
    
    public boolean isClassEnum() {
        return isEnum();
    }
    
    
    public Set<String> getClassPublicMethods() {
        return getPublicMethods();
    }
    
    
    public Set<String> getClassProtectedMethods() {
        return getProtectedMethods();
    }
    
    
    public Set<String> getClassPrivateMethods() {
        return getPrivateMethods();
    }
    
    
    public Set<String> getClassPublicFields() {
        return getPublicFields();
    }
    
    
    public Set<String> getClassProtectedFields() {
        return getProtectedFields();
    }
    
    
    public Set<String> getClassPrivateFields() {
        return getPrivateFields();
    }
    
    
    public void recordDiRegistration(Class<?> instanceType, Class<?> providedAsType) {
        if (instanceType == null || providedAsType == null) return;
        
        initializeMetadataForClass(instanceType);
        providedAsRegistry.get(instanceType).add(providedAsType);
        registrationCountRegistry.get(instanceType).incrementAndGet();
    }
    
    
    public void recordDiAttachedDependency(Class<?> targetType, Class<?> dependencyType) {
        if (targetType == null || dependencyType == null) return;
        
        initializeMetadataForClass(targetType);
        attachedDependenciesRegistry.get(targetType).add(dependencyType);
    }
    
    
    public void recordDiInstantiation(Class<?> instanceType, Class<?> instantiatorType) {
        if (instanceType == null) return;
        
        initializeMetadataForClass(instanceType);
        instantiationCountRegistry.get(instanceType).incrementAndGet();
        
        if (instantiatorType != null) {
            instantiatedByRegistry.get(instanceType).add(instantiatorType);
        }
    }
    
    
    public void setClassSetting(Class<?> type, ClassSetting setting, Object value) {
        if (type == null || setting == null) return;
        
        if (!setting.isValidValue(value)) {
            throw new IllegalArgumentException("Invalid value " + value + " for setting " + setting + 
                                             " (expected " + setting.getType().getSimpleName() + ")");
        }
        
        initializeMetadataForClass(type);
        settingsRegistry.get(type).put(setting, value);
    }
    
    
    @SuppressWarnings("unchecked")
    public <V> V getClassSetting(Class<?> type, ClassSetting setting) {
        if (type == null || setting == null) {
            return setting != null ? (V) setting.getDefaultValueAs(Object.class) : null;
        }
        
        initializeMetadataForClass(type);
        Object value = settingsRegistry.get(type).get(setting);
        return setting.castValue(value);
    }
    
    
    public boolean isClassSettingEnabled(Class<?> type, ClassSetting setting) {
        if (setting.getType() != Boolean.class) {
            throw new IllegalArgumentException("Setting " + setting + " is not a boolean setting");
        }
        return getClassSetting(type, setting);
    }
    
    // ========== Registry Operations ==========
    
    
    public void clearMetadataRegistry() {
        // Clear all registries
        constructorCountRegistry.clear();
        isInterfaceRegistry.clear();
        isAbstractRegistry.clear();
        isEnumRegistry.clear();
        publicMethodsRegistry.clear();
        protectedMethodsRegistry.clear();
        privateMethodsRegistry.clear();
        publicFieldsRegistry.clear();
        protectedFieldsRegistry.clear();
        privateFieldsRegistry.clear();
        providedAsRegistry.clear();
        attachedDependenciesRegistry.clear();
        instantiatedByRegistry.clear();
        registrationCountRegistry.clear();
        instantiationCountRegistry.clear();
        settingsRegistry.clear();
        
        // Re-register self after clearing
        initializeMetadataForClass(getActualType());
    }
    
    
    public int getMetadataRegistrySize() {
        // Return the size based on the constructor count registry (all types should be there)
        return constructorCountRegistry.size();
    }
    
    
    public int countJvmLoadedClasses() {
        try {
            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            return countClassLoaderLoadedClasses(systemClassLoader);
        } catch (Exception e) {
            return estimateJvmLoadedClasses();
        }
    }
    
    // ========== Class Loading Statistics Methods (from IClassLoadingStats) ==========
    
    /**
     * Get total loaded classes in the JVM.
     * @return Total number of loaded classes
     */
    public int getTotalLoadedClasses() {
        return countJvmLoadedClasses();
    }
    
    /**
     * Get number of tracked classes in metadata registry.
     * @return Number of tracked classes
     */
    public int getTrackedClasses() {
        return getMetadataRegistrySize();
    }
    
    /**
     * Get number of registered classes (classes registered in DI).
     * @return Number of registered classes
     */
    public int getRegisteredClasses() {
        final AtomicInteger registered = new AtomicInteger(0);
        synchronized (registrationCountRegistry) {
            for (AtomicInteger count : registrationCountRegistry.values()) {
                if (count.get() > 0) registered.incrementAndGet();
            }
        }
        return registered.get();
    }
    
    /**
     * Get number of instantiated classes.
     * @return Number of instantiated classes
     */
    public int getInstantiatedClasses() {
        final AtomicInteger instantiated = new AtomicInteger(0);
        synchronized (instantiationCountRegistry) {
            for (AtomicInteger count : instantiationCountRegistry.values()) {
                if (count.get() > 0) instantiated.incrementAndGet();
            }
        }
        return instantiated.get();
    }
    
    /**
     * Get tracking percentage (tracked/total loaded).
     * @return Percentage of tracked classes
     */
    public double getTrackingPercentage() {
        int total = getTotalLoadedClasses();
        int tracked = getTrackedClasses();
        return total > 0 ? (double) tracked / total * 100.0 : 0.0;
    }
    
    /**
     * Get registration percentage (registered/tracked).
     * @return Percentage of registered classes
     */
    public double getRegistrationPercentage() {
        int tracked = getTrackedClasses();
        int registered = getRegisteredClasses();
        return tracked > 0 ? (double) registered / tracked * 100.0 : 0.0;
    }
    
    /**
     * Get instantiation percentage (instantiated/tracked).
     * @return Percentage of instantiated classes
     */
    public double getInstantiationPercentage() {
        int tracked = getTrackedClasses();
        int instantiated = getInstantiatedClasses();
        return tracked > 0 ? (double) instantiated / tracked * 100.0 : 0.0;
    }
    
    /**
     * Get formatted summary of class loading statistics.
     * @return Formatted string with statistics
     */
    public String getFormattedStatsSummary() {
        return String.format("ClassLoadingStats{total=%d, tracked=%d (%.1f%%), registered=%d (%.1f%%), instantiated=%d (%.1f%%)}",
            getTotalLoadedClasses(), getTrackedClasses(), getTrackingPercentage(),
            getRegisteredClasses(), getRegistrationPercentage(),
            getInstantiatedClasses(), getInstantiationPercentage());
    }
    
    /**
     * Get metadata class loading statistics summary string.
     * Uses this class's direct stat methods to build the summary.
     */
    public String getMetadataClassLoadingStatsSummary() {
        return getFormattedStatsSummary();
    }

    
    // ========== Class Loading Analysis ==========
    
    
    public List<Class<?>> getApplicationLoadedClasses(String packagePrefix) {
        return getApplicationLoadedClasses(packagePrefix, Thread.currentThread().getContextClassLoader());
    }
    
    
    public List<Class<?>> getApplicationLoadedClasses(String packagePrefix, ClassLoader classLoader) {
        List<Class<?>> allClasses = getClassLoaderLoadedClasses(classLoader);
        List<Class<?>> myClasses = new ArrayList<>();
        
        for (Class<?> clazz : allClasses) {
            if (clazz.getName().startsWith(packagePrefix)) {
                myClasses.add(clazz);
            }
        }
        
        if (myClasses.isEmpty()) {
            for (Class<?> clazz : constructorCountRegistry.keySet()) {
                if (clazz.getName().startsWith(packagePrefix)) {
                    myClasses.add(clazz);
                }
            }
        }
        
        return myClasses;
    }
    
    
    public List<String> getApplicationUnloadedClasses(String packagePrefix) {
        List<Class<?>> loadedClasses = getApplicationLoadedClasses(packagePrefix);
        return findApplicationUnloadedClasses(packagePrefix, loadedClasses);
    }
    
    // ========== Application Class Loading Info Methods (from IMyClassLoadingInfo) ==========
    
    /**
     * Get package prefix for application class loading info.
     * @param packagePrefix The package prefix to analyze
     * @return The package prefix
     */
    public String getAppPackagePrefix(String packagePrefix) {
        return packagePrefix;
    }
    
    /**
     * Get count of loaded classes for a package prefix.
     * @param packagePrefix The package prefix to analyze
     * @return Number of loaded classes
     */
    public int getAppLoadedCount(String packagePrefix) {
        return getApplicationLoadedClasses(packagePrefix).size();
    }
    
    /**
     * Get count of unloaded classes for a package prefix.
     * @param packagePrefix The package prefix to analyze
     * @return Number of unloaded classes
     */
    public int getAppUnloadedCount(String packagePrefix) {
        return getApplicationUnloadedClasses(packagePrefix).size();
    }
    
    /**
     * Get count of tracked classes for a package prefix.
     * @param packagePrefix The package prefix to analyze
     * @return Number of tracked classes
     */
    public int getAppTrackedCount(String packagePrefix) {
        List<Class<?>> loadedClasses = getApplicationLoadedClasses(packagePrefix);
        int trackedCount = 0;
        for (Class<?> clazz : loadedClasses) {
            if (constructorCountRegistry.containsKey(clazz)) {
                trackedCount++;
            }
        }
        return trackedCount;
    }
    
    /**
     * Get count of registered classes for a package prefix.
     * @param packagePrefix The package prefix to analyze
     * @return Number of registered classes
     */
    public int getAppRegisteredCount(String packagePrefix) {
        List<Class<?>> loadedClasses = getApplicationLoadedClasses(packagePrefix);
        int registeredCount = 0;
        for (Class<?> clazz : loadedClasses) {
            AtomicInteger regCount = registrationCountRegistry.get(clazz);
            if (regCount != null && regCount.get() > 0) {
                registeredCount++;
            }
        }
        return registeredCount;
    }
    
    /**
     * Get count of instantiated classes for a package prefix.
     * @param packagePrefix The package prefix to analyze
     * @return Number of instantiated classes
     */
    public int getAppInstantiatedCount(String packagePrefix) {
        List<Class<?>> loadedClasses = getApplicationLoadedClasses(packagePrefix);
        int instantiatedCount = 0;
        for (Class<?> clazz : loadedClasses) {
            AtomicInteger instCount = instantiationCountRegistry.get(clazz);
            if (instCount != null && instCount.get() > 0) {
                instantiatedCount++;
            }
        }
        return instantiatedCount;
    }
    
    /**
     * Get total available count (loaded + unloaded) for a package prefix.
     * @param packagePrefix The package prefix to analyze
     * @return Total available classes
     */
    public int getAppTotalAvailableCount(String packagePrefix) {
        return getAppLoadedCount(packagePrefix) + getAppUnloadedCount(packagePrefix);
    }
    
    /**
     * Get loaded percentage for a package prefix.
     * @param packagePrefix The package prefix to analyze
     * @return Percentage of loaded classes
     */
    public double getAppLoadedPercentage(String packagePrefix) {
        int total = getAppTotalAvailableCount(packagePrefix);
        int loaded = getAppLoadedCount(packagePrefix);
        return total > 0 ? (double) loaded / total * 100.0 : 0.0;
    }
    
    /**
     * Get tracked percentage for a package prefix.
     * @param packagePrefix The package prefix to analyze
     * @return Percentage of tracked classes
     */
    public double getAppTrackedPercentage(String packagePrefix) {
        int loaded = getAppLoadedCount(packagePrefix);
        int tracked = getAppTrackedCount(packagePrefix);
        return loaded > 0 ? (double) tracked / loaded * 100.0 : 0.0;
    }
    
    /**
     * Get formatted summary for application class loading info.
     * @param packagePrefix The package prefix to analyze
     * @return Formatted summary string
     */
    public String getAppFormattedSummary(String packagePrefix) {
        return String.format("MyClassLoadingInfo{package='%s', loaded=%d/%d (%.1f%%), tracked=%d (%.1f%%), registered=%d, instantiated=%d}",
            packagePrefix, getAppLoadedCount(packagePrefix), getAppTotalAvailableCount(packagePrefix), 
            getAppLoadedPercentage(packagePrefix), getAppTrackedCount(packagePrefix), 
            getAppTrackedPercentage(packagePrefix), getAppRegisteredCount(packagePrefix), 
            getAppInstantiatedCount(packagePrefix));
    }
    

    
    
    public List<Class<?>> getClassLoaderLoadedClasses(ClassLoader classLoader) {
        List<Class<?>> classes = new ArrayList<>();
        
        try {
            java.lang.reflect.Field classesField = ClassLoader.class.getDeclaredField("classes");
            classesField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            java.util.Vector<Class<?>> loadedClasses = (java.util.Vector<Class<?>>) classesField.get(classLoader);
            
            classes.addAll(loadedClasses);
            
            ClassLoader parent = classLoader.getParent();
            if (parent != null && parent != classLoader) {
                classes.addAll(getClassLoaderLoadedClasses(parent));
            }
        } catch (Exception e) {
            // Reflection failed - return empty list
        }
        
        return classes;
    }
    
    
    public int countClassLoaderLoadedClasses(ClassLoader classLoader) {
        try {
            java.lang.reflect.Field classesField = ClassLoader.class.getDeclaredField("classes");
            classesField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            java.util.Vector<Class<?>> classes = (java.util.Vector<Class<?>>) classesField.get(classLoader);
            
            int count = classes.size();
            
            ClassLoader parent = classLoader.getParent();
            if (parent != null) {
                count += countClassLoaderLoadedClasses(parent);
            }
            
            return count;
        } catch (Exception e) {
            return 0;
        }
    }
    
    
    public int estimateJvmLoadedClasses() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        return (int) Math.max(1000, Math.min(50000, usedMemory / 1024));
    }
    
    
    public Set<String> scanUrlForClasses(URL url, String packagePrefix) {
        Set<String> classNames = new HashSet<>();
        
        try {
            File file = new File(url.toURI());
            
            if (file.isDirectory()) {
                classNames.addAll(scanDirectoryForClasses(file, packagePrefix, ""));
            } else if (file.getName().endsWith(".jar")) {
                classNames.addAll(scanJarFileForClasses(file, packagePrefix));
            }
        } catch (Exception e) {
            // Skip problematic URLs
        }
        
        return classNames;
    }
    
    
    public Set<String> scanDirectoryForClasses(File directory, String packagePrefix, String currentPackage) {
        Set<String> classNames = new HashSet<>();
        
        File[] files = directory.listFiles();
        if (files == null) return classNames;
        
        for (File file : files) {
            if (file.isDirectory()) {
                String subPackage = currentPackage.isEmpty() ? file.getName() : currentPackage + "." + file.getName();
                classNames.addAll(scanDirectoryForClasses(file, packagePrefix, subPackage));
            } else if (file.getName().endsWith(".class")) {
                String className = currentPackage.isEmpty() ? 
                    file.getName().substring(0, file.getName().length() - 6) :
                    currentPackage + "." + file.getName().substring(0, file.getName().length() - 6);
                
                if (className.startsWith(packagePrefix)) {
                    classNames.add(className);
                }
            }
        }
        
        return classNames;
    }
    
    @Deprecated(forRemoval = true, since = "10/27/25")
    //TODO: Replace method with class scanning method in DependencyInjectionHelper
    public Set<String> scanJarFileForClasses(File jarFile, String packagePrefix) {
        Set<String> classNames = new HashSet<>();
        
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                
                if (name.endsWith(".class")) {
                    String className = name.substring(0, name.length() - 6).replace('/', '.');
                    if (className.startsWith(packagePrefix)) {
                        classNames.add(className);
                    }
                }
            }
        } catch (Exception e) {
            // Skip problematic JAR files
        }
        
        return classNames;
    }
    
    
    public List<String> findApplicationUnloadedClasses(String packagePrefix, List<Class<?>> loadedClasses) {
        Set<String> allAvailableClasses = new HashSet<>();
        Set<String> loadedClassNames = new HashSet<>();
        
        for (Class<?> clazz : loadedClasses) {
            loadedClassNames.add(clazz.getName());
        }
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            for (URL url : urlClassLoader.getURLs()) {
                allAvailableClasses.addAll(scanUrlForClasses(url, packagePrefix));
            }
        } else {
            String classPath = System.getProperty("java.class.path");
            if (classPath != null) {
                String[] paths = classPath.split(System.getProperty("path.separator"));
                for (String path : paths) {
                    try {
                        File file = new File(path);
                        if (file.exists()) {
                            allAvailableClasses.addAll(scanUrlForClasses(file.toURI().toURL(), packagePrefix));
                        }
                    } catch (Exception e) {
                        // Skip invalid paths
                    }
                }
            }
        }
        
        return allAvailableClasses.stream()
                .filter(className -> !loadedClassNames.contains(className))
                .sorted()
                .collect(Collectors.toList());
    }
}
