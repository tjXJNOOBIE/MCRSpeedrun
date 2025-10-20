package com.tjxjnoobie.api.platform.global.metadata;

import com.tjxjnoobie.api.platform.global.metadata.enums.ClassSetting;

import java.util.Map;
import java.util.Set;

/**
 * Read-only metadata about a class, gathered via reflection and DI events.
 */
public interface IClassMetaData {
    // Identity
    Class<?> getType();

    // Structure
    int getConstructorCount();
    boolean isInterface();
    boolean isAbstract();
    boolean isEnum();

    // Members (simple string signatures: name or name(params))
    Set<String> getPublicMethods();
    Set<String> getProtectedMethods();
    Set<String> getPrivateMethods();

    Set<String> getPublicFields();
    Set<String> getProtectedFields();
    Set<String> getPrivateFields();

    // DI relations
    // Types this class was registered under (keys in DI map)
    Set<Class<?>> getProvidedAs();
    // Types that were attached/injected into this class (field/interface types)
    Set<Class<?>> getAttachedDependencies();
    // Classes that created/instantiated this class through DI
    Set<Class<?>> getInstantiatedBy();

    // Counters
    int getRegistrationCount();
    int getInstantiationCount();

    // Per-class settings (enum-based)
    Map<ClassSetting, Object> getSettings();

    // Convenience typed options (no default implementations to avoid diamond inheritance)
    boolean isExceptionWrappingEnabled();
    boolean isLogEligibleInjection();
    String[] getTags();
    int getLogVerbosityLevel();
    boolean isCacheInstances();
    boolean isLazyInitialization();
    boolean isSingletonScope();
    boolean isProxyCreationEnabled();
    boolean isDependencyValidationStrict();
    boolean isPerformanceMonitoring();

    // Generic setting getter with type safety
    <T> T getSetting(ClassSetting setting);
}