package com.tjxjnoobie.api.internal;

import com.tjxjnoobie.api.platform.global.console.Log;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration interface for dependency injection behavior.
 * Controls which types are eligible for injection and which should be excluded.
 * Implement this interface to customize injection rules per context.
 */
public interface InjectionConfig {

    // Internal storage for default method-backed state (no inner class)
    Map<InjectionConfig, Set<String>> __ALLOWED =
        Collections.synchronizedMap(new WeakHashMap<>());
    Map<InjectionConfig, Set<String>> __EXCLUDED =
        Collections.synchronizedMap(new WeakHashMap<>());
    Map<InjectionConfig, Map<Class<?>, Boolean>> __ELIGIBILITY =
        Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Gets the set of allowed package prefixes
     */
    default Set<String> getAllowedPackages() {
        return __ALLOWED.computeIfAbsent(this, k -> ConcurrentHashMap.newKeySet());
    }

    /**
     * Gets the set of excluded package prefixes
     */
    default Set<String> getExcludedPackages() {
        return __EXCLUDED.computeIfAbsent(this, k -> ConcurrentHashMap.newKeySet());
    }

    /**
     * Gets the eligibility cache
     */
    default Map<Class<?>, Boolean> getEligibilityCache() {
        return __ELIGIBILITY.computeIfAbsent(this, k -> new ConcurrentHashMap<>());
    }
    
    /**
     * Whether @Injectable annotation is required on types
     * Default is TRUE - only types with @Injectable will be injected
     */
    default boolean isRequireInjectableAnnotation() {
        return true;
    }
    
    /**
     * Whether concrete classes can be injected (vs only interfaces)
     */
    default boolean isAllowConcreteClasses() {
        return true;
    }
    
    /**
     * Adds a package prefix to the allowed list
     */
    default InjectionConfig allowPackage(String packagePrefix) {
        getAllowedPackages().add(packagePrefix);
        getEligibilityCache().clear();
        Log.info("[DI-Config] Added allowed package: " + packagePrefix);
        return this;
    }
    
    /**
     * Adds a package prefix to the excluded list
     */
    default InjectionConfig excludePackage(String packagePrefix) {
        getExcludedPackages().add(packagePrefix);
        getEligibilityCache().clear();
        Log.info("[DI-Config] Added excluded package: " + packagePrefix);
        return this;
    }
    
    /**
     * Removes a package from the excluded list
     */
    default InjectionConfig removeExcludedPackage(String packagePrefix) {
        getExcludedPackages().remove(packagePrefix);
        getEligibilityCache().clear();
        Log.info("[DI-Config] Removed excluded package: " + packagePrefix);
        return this;
    }
    
    /**
     * Checks if a type is eligible for dependency injection.
     * Uses caching for performance.
     */
    default boolean isEligibleForInjection(Class<?> type) {
        if (type == null) {
            Log.warn("[DI-Config] Type is null");
            return false;
        }
        // Check cache first
        Boolean cached = getEligibilityCache().get(type);
        if (cached != null) {
            return cached;
        }
        
        boolean eligible = computeInjectionEligibility(type);
        getEligibilityCache().put(type, eligible);
        return eligible;
    }
    
    /**
     * Computes eligibility for a type (not cached)
     * Primary mechanism: @Injectable annotation (whitelist approach)
     * Fallback: package-based filtering (for backward compatibility)
     */
    default boolean computeInjectionEligibility(Class<?> type) {
        String typeName = type.getSimpleName();
        String fullName = type.getName();
        
        // 1. Check if it's a primitive or array - never inject these
        if (type.isPrimitive() || type.isArray()) {
            Log.warn("[DI-Eligibility] Rejected " + typeName + " (primitive/array)");
            return false;
        }
        
        // 2. Check if concrete classes are allowed
        if (!isAllowConcreteClasses() && !type.isInterface()) {
            Log.warn("[DI-Eligibility] Rejected " + typeName + " (concrete class not allowed)");
            return false;
        }
        
        // 3. Get package name for filtering
        String packageName = type.getPackage() != null ? type.getPackage().getName() : "";
        
        // 4. ALWAYS exclude explicitly blacklisted packages (safety net)
        for (String excluded : getExcludedPackages()) {
            if (packageName.startsWith(excluded) || fullName.startsWith(excluded)) {
                Log.warn("[DI-Eligibility] Rejected " + typeName + " (excluded package: " + excluded + ")");
                return false;
            }
        }
        
        // 5. PRIMARY CHECK: @Injectable annotation (whitelist approach)
        // TODO: Wire all DI classes with @Injectable annotation before re-enabling this check
        // Currently commented out to allow injection without @Injectable requirement
        // if (isRequireInjectableAnnotation()) {
        //     // Only types with @Injectable are eligible
        //     boolean hasAnnotation = type.isAnnotationPresent(Injectable.class);
        //     
        //     if (!hasAnnotation) {
        //         // Not annotated - reject with detailed logging
        //         Log.warn("[DI-Eligibility] MISSING @Injectable on " + typeName
        //                 + " | package=" + packageName 
        //                 + " | Add: @Injectable(\"description\")");
        //         return false;
        //     }
        //     
        //     // Has @Injectable - accept (already passed exclusion check)
        //     Injectable annotation = type.getAnnotation(Injectable.class);
        //     String description = annotation.value();
        //     if (description != null && !description.isEmpty()) {
        //         Log.success("[DI-Eligibility] Accepted " + typeName
        //                 + " | @Injectable(\"" + description + "\")");
        //     } else {
        //         Log.success("[DI-Eligibility] ✅ Accepted " + typeName 
        //                 + " | @Injectable (no description)");
        //     }
        //     return true;
        // }
        
        // 6. FALLBACK: Package-based filtering (when @Injectable not required)
        // This is for backward compatibility or when you want package-level control
        if (!getAllowedPackages().isEmpty()) {
            for (String allowed : getAllowedPackages()) {
                if (packageName.startsWith(allowed) || fullName.startsWith(allowed)) {
                    Log.info("[DI-Eligibility] Accepted " + typeName
                            + " (allowed package: " + allowed + ")");
                    return true;
                }
            }
            // Not in allowed packages
            Log.info("[DI-Eligibility] Rejected " + typeName
                    + " (not in allowed packages)");
            return false;
        }
        
        // 7. Default: if no restrictions, allow it
        Log.info("[DI-Eligibility] Accepted " + typeName + " (no restrictions)");
        return true;
    }
    
    /**
     * Clears the eligibility cache.
     * Call this if you modify configuration at runtime.
     */
    default void clearInjectionCache() {
        getEligibilityCache().clear();
        Log.info("[DI-Config] Cleared eligibility cache");
    }
    
    /**
     * Gets statistics about the configuration
     */
    default String getInjectionConfigStats() {
        return String.format(
            "[DI-Config] Stats: %d allowed packages, %d excluded packages, %d cached types, requireAnnotation=%s, allowConcrete=%s",
            getAllowedPackages().size(),
            getExcludedPackages().size(),
            getEligibilityCache().size(),
            isRequireInjectableAnnotation(),
            isAllowConcreteClasses()
        );
    }
    
    /**
     * Prints current configuration
     */
    default void printInjectionConfig() {
        Log.info("[DI-Config] ========== INJECTION CONFIGURATION ==========");
        Log.info("[DI-Config] Require @Injectable: " + isRequireInjectableAnnotation());
        Log.info("[DI-Config] Allow concrete classes: " + isAllowConcreteClasses());
        Log.info("[DI-Config]");
        Log.info("[DI-Config] Allowed packages (" + getAllowedPackages().size() + "):");
        for (String pkg : getAllowedPackages()) {
            Log.info("[DI-Config]   • " + pkg);
        }
        Log.info("[DI-Config]");
        Log.info("[DI-Config] Excluded packages (" + getExcludedPackages().size() + "):");
        for (String pkg : getExcludedPackages()) {
            Log.info("[DI-Config]   • " + pkg);
        }
        Log.info("[DI-Config] ================================================");
    }
    
    /**
     * Generates a report of all types checked for injection eligibility
     * Shows which have @Injectable and which are missing it
     * TODO: Re-enable after all classes are properly annotated with @Injectable
     * Currently disabled since @Injectable requirement is temporarily disabled
     */
    default void generateInjectableReport() {
        Log.info("[DI-Injectable-Report] ========== @Injectable ANNOTATION REPORT ==========");
        Log.info("[DI-Injectable-Report] Require @Injectable: " + isRequireInjectableAnnotation());
        Log.info("[DI-Injectable-Report]");
        
        Map<Class<?>, Boolean> cache = getEligibilityCache();
        if (cache.isEmpty()) {
            Log.warn("[DI-Injectable-Report] No types have been checked yet. Run injection first.");
            return;
        }
        
        int totalChecked = cache.size();
        int accepted = 0;
        int rejected = 0;
        int missingAnnotation = 0;
        
        Log.info("[DI-Injectable-Report] === ACCEPTED TYPES (✅) ===");
        for (Map.Entry<Class<?>, Boolean> entry : cache.entrySet()) {
            if (entry.getValue()) {
                Class<?> type = entry.getKey();
                // TODO: Re-enable @Injectable annotation checking after all classes are annotated
                // boolean hasAnnotation = type.isAnnotationPresent(Injectable.class);
                String typeName = type.getSimpleName();
                String packageName = type.getPackage() != null ? type.getPackage().getName() : "";
                
                // TODO: Re-enable annotation-based reporting
                // if (hasAnnotation) {
                //     Injectable annotation = type.getAnnotation(Injectable.class);
                //     String description = annotation.value();
                //     if (description != null && !description.isEmpty()) {
                //         Log.success("[DI-Injectable-Report]   ✅ " + typeName + " | @Injectable(\"" + description + "\")");
                //     } else {
                //         Log.success("[DI-Injectable-Report]   ✅ " + typeName + " | @Injectable");
                //     }
                // } else {
                //     Log.info("[DI-Injectable-Report]   ✅ " + typeName + " | package=" + packageName);
                // }
                
                // Temporary: Show all accepted types without annotation details
                Log.info("[DI-Injectable-Report]   ✅ " + typeName + " | package=" + packageName);
                accepted++;
            }
        }
        
        Log.info("[DI-Injectable-Report]");
        Log.info("[DI-Injectable-Report] === REJECTED TYPES (❌) ===");
        for (Map.Entry<Class<?>, Boolean> entry : cache.entrySet()) {
            if (!entry.getValue()) {
                Class<?> type = entry.getKey();
                String typeName = type.getSimpleName();
                String packageName = type.getPackage() != null ? type.getPackage().getName() : "";
                // TODO: Re-enable @Injectable annotation checking
                // boolean hasAnnotation = type.isAnnotationPresent(Injectable.class);
                
                // Check if it's missing @Injectable (and not excluded for other reasons)
                boolean isExcluded = false;
                for (String excluded : getExcludedPackages()) {
                    if (packageName.startsWith(excluded)) {
                        isExcluded = true;
                        break;
                    }
                }
                
                // TODO: Re-enable annotation-based rejection reporting
                // if (!hasAnnotation && !isExcluded && isRequireInjectableAnnotation()) {
                //     Log.warn("[DI-Injectable-Report]   ❌ " + typeName + " | MISSING @Injectable | package=" + packageName);
                //     missingAnnotation++;
                // } else {
                //     Log.info("[DI-Injectable-Report]   ❌ " + typeName + " | " + 
                //             (isExcluded ? "excluded package" : "other reason"));
                // }
                
                // Temporary: Show all rejected types without annotation details
                Log.info("[DI-Injectable-Report]   ❌ " + typeName + " | " + 
                        (isExcluded ? "excluded package" : "other reason") + " | package=" + packageName);
                rejected++;
            }
        }
        
        Log.info("[DI-Injectable-Report]");
        Log.info("[DI-Injectable-Report] === SUMMARY ===");
        Log.info("[DI-Injectable-Report] Total types checked: " + totalChecked);
        Log.info("[DI-Injectable-Report] Accepted: " + accepted);
        Log.info("[DI-Injectable-Report] Rejected: " + rejected);
        // TODO: Re-enable after annotation checking is restored
        // Log.info("[DI-Injectable-Report] Missing @Injectable: " + missingAnnotation);
        
        // TODO: Re-enable warning messages
        // if (missingAnnotation > 0) {
        //     Log.warn("[DI-Injectable-Report]");
        //     Log.warn("[DI-Injectable-Report] ⚠️  " + missingAnnotation + " types are missing @Injectable annotation!");
        //     Log.warn("[DI-Injectable-Report] Add @Injectable(\"description\") to these interfaces to enable injection.");
        // } else {
        //     Log.success("[DI-Injectable-Report]");
        //     Log.success("[DI-Injectable-Report] ✅ All checked types have proper @Injectable annotations!");
        // }
        
        Log.info("[DI-Injectable-Report]");
        Log.info("[DI-Injectable-Report] NOTE: @Injectable annotation checking is currently disabled");
        Log.info("[DI-Injectable-Report] ================================================================");
    }
    
    /**
     * Initializes default configuration
     */
    default void initializeDefaults() {
        // Exclude common third-party and standard library packages
        excludePackage("java.");
        excludePackage("javax.");
        excludePackage("sun.");
        excludePackage("jdk.");
        excludePackage("com.sun.");
        
        // Common third-party libraries
        excludePackage("org.bukkit.");
        excludePackage("org.spigotmc.");
        excludePackage("net.md_5.");
        excludePackage("io.papermc.");
        excludePackage("com.google.");
        excludePackage("org.apache.");
        excludePackage("org.slf4j.");
        excludePackage("ch.qos.logback.");
        excludePackage("org.hibernate.");
        excludePackage("org.springframework.");
        excludePackage("com.fasterxml.");
        excludePackage("org.json.");
        excludePackage("com.mysql.");
        excludePackage("redis.clients.");
        
        // Allow project packages by default
        allowPackage("com.tjxjnoobie.");
        
        Log.info("[DI-Config] Initialized with " + getExcludedPackages().size() + " excluded packages");
    }
}
