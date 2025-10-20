package com.tjxjnoobie.api.platform.global.metadata.interfaces;

import com.tjxjnoobie.api.platform.global.metadata.enums.ClassSetting;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Comprehensive interface for class metadata operations.
 * Provides all metadata functionality through proper DI without static methods or singletons.
 *
 * Combined with IClassMetaData read-only API so consumers only depend on a single contract.
 * All methods are default and delegate to the underlying AbstractClassMetaData instance when available,
 * so implementors are not forced to implement anything.
 *
 * @param <T> The class type that implements this interface (should be the implementing class)
 */
public interface IAbstractClassMetaData<T>    {
    // ===== Helper resolution =====

    /**
     * Locate the active metadata provider to delegate operations to.
     */
    default IAbstractClassMetaData<?> meta() {
        Object inst = getAbstractClassMetaDataInstance();
        if (inst instanceof IAbstractClassMetaData<?>) {
            return (IAbstractClassMetaData<?>) inst;
        }
        throw new IllegalStateException("No AbstractClassMetaData provider available");
    }
    

    /**
     * Convenience to get per-class view for this type.
     */
    default IAbstractClassMetaData<?> md() {
        Class<?> type = getMetadataTargetType();
        return getOrCreateMetadata(type != null ? type : Object.class);
    }

    // ========== Core Class Metadata ==========

    /**
     * Get the class type this metadata represents.
     * @return The class type
     */
    default Class<T> getMetadataTargetType() { return null; }

    /**
     * Get metadata for any class.
     *
     * @param type The class to get metadata for
     * @return The class metadata
     */
    default IAbstractClassMetaData<?> getOrCreateMetadata(Class<?> type) { return meta().getOrCreateMetadata(type); }

    /**
     * Get the AbstractClassMetaData instance for direct access.
     * Implementations can override to return their composed instance.
     * @return The AbstractClassMetaData instance or null if not directly available
     */
    default Object getAbstractClassMetaDataInstance() { return null; }

    // ========== IClassMetaData Methods (Combined from IClassMetaData interface) ==========

    // Identity
    default Class<?> getType() { return getMetadataTargetType(); }

    // Structure
    default int getClassConstructorCount() { return md().getConstructorCount(); }
    default int getConstructorCount() { return md().getConstructorCount(); }
    default boolean isClassInterface() { return md().isInterface(); }
    default boolean isClassAbstract() { return md().isAbstract(); }
    default boolean isClassEnum() { return md().isEnum(); }
    default boolean isInterface() { return md().isInterface(); }
    default boolean isAbstract() { return md().isAbstract(); }
    default boolean isEnum() { return md().isEnum(); }

    // Members
    default Set<String> getClassPublicMethods() { return md().getPublicMethods(); }
    default Set<String> getClassProtectedMethods() { return md().getProtectedMethods(); }
    default Set<String> getClassPrivateMethods() { return md().getPrivateMethods(); }
    default Set<String> getPublicMethods() { return md().getPublicMethods(); }
    default Set<String> getProtectedMethods() { return md().getProtectedMethods(); }
    default Set<String> getPrivateMethods() { return md().getPrivateMethods(); }

    default Set<String> getClassPublicFields() { return md().getPublicFields(); }
    default Set<String> getClassProtectedFields() { return md().getProtectedFields(); }
    default Set<String> getClassPrivateFields() { return md().getPrivateFields(); }
    default Set<String> getPublicFields() { return md().getPublicFields(); }
    default Set<String> getProtectedFields() { return md().getProtectedFields(); }
    default Set<String> getPrivateFields() { return md().getPrivateFields(); }

    // ========== Dependency Injection Metadata ==========

    default Set<Class<?>> getProvidedAs() { return md().getProvidedAs(); }
    default Set<Class<?>> getAttachedDependencies() { return md().getAttachedDependencies(); }
    default Set<Class<?>> getInstantiatedBy() { return md().getInstantiatedBy(); }

    default int getRegistrationCount() { return md().getRegistrationCount(); }
    default int getInstantiationCount() { return md().getInstantiationCount(); }

    default void recordDiRegistration(Class<?> instanceType, Class<?> providedAs) { meta().recordDiRegistration(instanceType, providedAs); }
    default void recordDiAttachedDependency(Class<?> targetType, Class<?> dependencyType) { meta().recordDiAttachedDependency(targetType, dependencyType); }
    default void recordDiInstantiation(Class<?> instanceType, Class<?> instantiatorType) { meta().recordDiInstantiation(instanceType, instantiatorType); }

    // ========== Settings and Configuration ==========

    default Map<ClassSetting, Object> getSettings() { return md().getSettings(); }
    default <V> V getSetting(ClassSetting setting) { return md().getSetting(setting); }

    default void setClassSetting(Class<?> type, ClassSetting setting, Object value) { meta().setClassSetting(type, setting, value); }
    default <V> V getClassSetting(Class<?> type, ClassSetting setting) { return meta().getClassSetting(type, setting); }
    default boolean isClassSettingEnabled(Class<?> type, ClassSetting setting) { return meta().isClassSettingEnabled(type, setting); }

    default boolean isExceptionWrappingEnabled() { return md().isExceptionWrappingEnabled(); }
    default boolean isLogEligibleInjection() { return md().isLogEligibleInjection(); }
    default String[] getTags() { return md().getTags(); }
    default int getLogVerbosityLevel() { return md().getLogVerbosityLevel(); }
    default boolean isCacheInstances() { return md().isCacheInstances(); }
    default boolean isLazyInitialization() { return md().isLazyInitialization(); }
    default boolean isSingletonScope() { return md().isSingletonScope(); }
    default boolean isProxyCreationEnabled() { return md().isProxyCreationEnabled(); }
    default boolean isDependencyValidationStrict() { return md().isDependencyValidationStrict(); }
    default boolean isPerformanceMonitoring() { return md().isPerformanceMonitoring(); }

    // ========== Registry Operations ==========

    default void clearMetadataRegistry() { meta().clearMetadataRegistry(); }
    default int getMetadataRegistrySize() { return meta().getMetadataRegistrySize(); }
    default int countJvmLoadedClasses() { return meta().countJvmLoadedClasses(); }

    // ========== Class Loading Statistics ==========

    default int getTotalLoadedClasses() { return countJvmLoadedClasses(); }
    default int getTrackedClasses() { return getMetadataRegistrySize(); }

    // Defaults for percentages delegate to meta() for registered/instantiated counts
    default int getRegisteredClasses() { return meta().getRegisteredClasses(); }
    default int getInstantiatedClasses() { return meta().getInstantiatedClasses(); }

    default double getTrackingPercentage() {
        int total = getTotalLoadedClasses();
        int tracked = getTrackedClasses();
        return total > 0 ? (double) tracked / total * 100.0 : 0.0;
    }

    default double getRegistrationPercentage() {
        int tracked = getTrackedClasses();
        int registered = getRegisteredClasses();
        return tracked > 0 ? (double) registered / tracked * 100.0 : 0.0;
    }

    default double getInstantiationPercentage() {
        int tracked = getTrackedClasses();
        int instantiated = getInstantiatedClasses();
        return tracked > 0 ? (double) instantiated / tracked * 100.0 : 0.0;
    }

    default String getFormattedStatsSummary() {
        return String.format("ClassLoadingStats{total=%d, tracked=%d (%.1f%%), registered=%d (%.1f%%), instantiated=%d (%.1f%%)}",
            getTotalLoadedClasses(), getTrackedClasses(), getTrackingPercentage(),
            getRegisteredClasses(), getRegistrationPercentage(),
            getInstantiatedClasses(), getInstantiationPercentage());
    }

    default String getMetadataClassLoadingStatsSummary() { return getFormattedStatsSummary(); }

    // ========== Class Loading Analysis ==========

    default List<Class<?>> getApplicationLoadedClasses(String packagePrefix) { return meta().getApplicationLoadedClasses(packagePrefix); }
    default List<Class<?>> getApplicationLoadedClasses(String packagePrefix, ClassLoader classLoader) { return meta().getApplicationLoadedClasses(packagePrefix, classLoader); }
    default List<String> getApplicationUnloadedClasses(String packagePrefix) { return meta().getApplicationUnloadedClasses(packagePrefix); }

    // ========== Application Class Loading Info Methods ==========

    default String getAppPackagePrefix(String packagePrefix) { return packagePrefix; }
    default int getAppLoadedCount(String packagePrefix) { return getApplicationLoadedClasses(packagePrefix).size(); }
    default int getAppUnloadedCount(String packagePrefix) { return getApplicationUnloadedClasses(packagePrefix).size(); }
    default int getAppTrackedCount(String packagePrefix) { return meta().getAppTrackedCount(packagePrefix); }
    default int getAppRegisteredCount(String packagePrefix) { return meta().getAppRegisteredCount(packagePrefix); }
    default int getAppInstantiatedCount(String packagePrefix) { return meta().getAppInstantiatedCount(packagePrefix); }
    default int getAppTotalAvailableCount(String packagePrefix) { return getAppLoadedCount(packagePrefix) + getAppUnloadedCount(packagePrefix); }
    default double getAppLoadedPercentage(String packagePrefix) {
        int total = getAppTotalAvailableCount(packagePrefix);
        int loaded = getAppLoadedCount(packagePrefix);
        return total > 0 ? (double) loaded / total * 100.0 : 0.0;
    }
    default double getAppTrackedPercentage(String packagePrefix) {
        int loaded = getAppLoadedCount(packagePrefix);
        int tracked = getAppTrackedCount(packagePrefix);
        return loaded > 0 ? (double) tracked / loaded * 100.0 : 0.0;
    }
    default String getAppFormattedSummary(String packagePrefix) {
        return String.format("MyClassLoadingInfo{package='%s', loaded=%d/%d (%.1f%%), tracked=%d (%.1f%%), registered=%d, instantiated=%d}",
            packagePrefix, getAppLoadedCount(packagePrefix), getAppTotalAvailableCount(packagePrefix),
            getAppLoadedPercentage(packagePrefix), getAppTrackedCount(packagePrefix),
            getAppTrackedPercentage(packagePrefix), getAppRegisteredCount(packagePrefix),
            getAppInstantiatedCount(packagePrefix));
    }

    // ========== ClassLoader Introspection ==========

    default List<Class<?>> getClassLoaderLoadedClasses(ClassLoader classLoader) { return meta().getClassLoaderLoadedClasses(classLoader); }
    default int countClassLoaderLoadedClasses(ClassLoader classLoader) { return meta().countClassLoaderLoadedClasses(classLoader); }
    default int estimateJvmLoadedClasses() { return meta().estimateJvmLoadedClasses(); }

    // ========== Scanning ==========

    default Set<String> scanUrlForClasses(URL url, String packagePrefix) { return meta().scanUrlForClasses(url, packagePrefix); }
    default Set<String> scanDirectoryForClasses(File directory, String packagePrefix, String currentPackage) { return meta().scanDirectoryForClasses(directory, packagePrefix, currentPackage); }
    default Set<String> scanJarFileForClasses(File jarFile, String packagePrefix) { return meta().scanJarFileForClasses(jarFile, packagePrefix); }
    default List<String> findApplicationUnloadedClasses(String packagePrefix, List<Class<?>> loadedClasses) { return meta().findApplicationUnloadedClasses(packagePrefix, loadedClasses); }

    // ========== Distributed System Support ==========

    default String getNodeIdentifier() {
        try {
            String hostname = java.net.InetAddress.getLocalHost().getHostName();
            String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            return hostname + "-" + pid;
        } catch (Exception e) {
            return "node-" + System.nanoTime();
        }
    }

    default Map<String, Map<String, Object>> collectDistributedMetadataSnapshot() {
        Map<String, Map<String, Object>> snapshot = new HashMap<>();
        // Delegation could be added here when a distributed registry is implemented
        return snapshot;
    }

    default void mergeDistributedMetadata(String nodeId, Map<String, Map<String, Object>> remoteMetadata) {
        // No-op by default
    }

    default long getLastMetadataUpdateTimestamp() { return System.currentTimeMillis(); }
}