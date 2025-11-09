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
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyClass;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.lang.reflect.Method;
import java.util.*;


public class DependencyMetaData<CLASS extends IDependencyClass<?>,
        INSTANCE extends IDependencyInstance<?>>
        implements IDependencyMetaData<CLASS, INSTANCE> {

    //TODO: Move DependencySet to a dedicated class and fill it on regisration
    private Set<CLASS> subDependencies;
    private int priority;
    private int depth;
    private DependencyRole dependencyRole;
    private final Map<LifecycleType, Method> lifecycleMethods = new EnumMap<>(LifecycleType.class);
    private final Map<LifecycleType, Boolean> lifecycleSuccess = new EnumMap<>(LifecycleType.class);
    private int retryCount;
    private IContext<CLASS> sourceContext; // which context owns it



    @Override
    public void populateMetaData(CLASS dependencyClass, INSTANCE dependencyInstance) {
        if (dependencyClass != null) {
            Log.warn("[DependencyMetaData] Populating metadata for dependency: " + dependencyClass.getName());
            this.depth = calculateDepth(dependencyClass, subDependencies);
            this.dependencyRole = determineRole(subDependencies);
            setSubDependencies(subDependencies);
            detectLifecycleForClass(dependencyClass);
            Log.success("[DependencyMetaData] Metadata populated successfully for: " + dependencyClass.getName());
        }
        Log.error("[DependencyMetaData] Dependency class not found");
    }

    @Override
    public IDependencyMetaData<CLASS, INSTANCE> getDependencyMetaData() {
        return this;
    }

    @Override
    public IDependencyMetaData<CLASS, INSTANCE> getDependencyMetaData(CLASS dependencyClass) {
        return this;
    }




    @Override
    public EnumMap<LifecycleType, Method> detectLifecycleForClass(CLASS dependencyClass) {
        EnumMap<LifecycleType, Method> map = new EnumMap<>(LifecycleType.class);
        for (LifecycleType lifecycle : LifecycleType.values()) {
            lifecycle.findIn(dependencyClass.getClass()).ifPresent(m -> map.put(lifecycle, m));
        }
        return map;
    }
    // --- Getters / setters ---


    /**
     * Returns the set of classes that this component directly depends on.
     * These are the types that must be resolved before this component can be initialized.
     *
     * @return a read-only set of dependency classes
     */
    @Override
    public Set<CLASS> getSubDependencies() {
        return subDependencies;
    }


    /**
     * Sets the direct dependencies of this component.
     * This allows configuration of which types must be resolved prior to this component's initialization.
     *
     * @param dependencyClassSet the set of classes this component depends on
     */
    @Override
    public void setSubDependencies(Set<CLASS> dependencyClassSet) {
        this.subDependencies = dependencyClassSet;
    }

    /**
     * Returns the depth level of this component within the dependency resolution graph.
     * Depth is used to determine the order of component initialization and resolution.
     *
     * @return the depth level (lower values mean earlier in the resolution order)
     */
    @Override
    public int getDepth() {
        return depth;
    }


    /**
     * Sets the depth level of this component within the dependency resolution graph.
     * This influences the order in which components are resolved and initialized.
     *
     * @param depth the depth level (lower values mean earlier in the resolution order)
     */
    @Override
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Returns the role assigned to this component in the dependency graph.
     * Roles define responsibilities such as whether a component is a provider or consumer.
     *
     * @return the role of this component
     */
    @Override
    public DependencyRole getDependencyRole() {
        return dependencyRole;
    }

    /**
     * Sets the role of this component in the dependency graph.
     * This determines how the component behaves in the resolution and lifecycle management.
     *
     * @param dependencyRole the role assigned to this component
     */
    @Override
    public void setDependencyRole(DependencyRole dependencyRole) {
        this.dependencyRole = dependencyRole;
    }

    /**
     * Returns the pre-construction method associated with this component.
     * This method is invoked before the instance is fully constructed and is used for setup or validation.
     *
     * @return the pre-construction method, or null if not set
     */
    @Override
    public Method getPreConstruct() {
        return lifecycleMethods.get(LifecycleType.PRE_CONSTRUCT);
    }

    /**
     * Sets the pre-construction method for this component.
     * This method is called before the instance is initialized and is used for setup or validation.
     *
     * @param preConstruct the method to invoke before construction
     */
    @Override
    public void setPreConstruct(Method preConstruct) {
        if (preConstruct == null) lifecycleMethods.remove(LifecycleType.PRE_CONSTRUCT);
        else lifecycleMethods.put(LifecycleType.PRE_CONSTRUCT, preConstruct);
    }

    /**
     * Returns the post-construction method associated with this component.
     * This method is invoked after the instance is fully constructed and dependencies are injected.
     *
     * @return the post-construction method, or null if not set
     */
    @Override
    public Method getPostConstruct() {
        return lifecycleMethods.get(LifecycleType.POST_CONSTRUCT);
    }

    /**
     * Sets the post-construction method for this component.
     * This method is called after the instance is initialized and dependencies are injected.
     *
     * @param postConstruct the method to invoke after construction
     */
    @Override
    public void setPostConstruct(Method postConstruct) {
        if (postConstruct == null) lifecycleMethods.remove(LifecycleType.POST_CONSTRUCT);
        else lifecycleMethods.put(LifecycleType.POST_CONSTRUCT, postConstruct);
    }

    /**
     * Checks whether the pre-construction method executed successfully.
     * This flag indicates whether the setup phase completed without errors.
     *
     * @return true if the pre-construction succeeded, false otherwise
     */
    public boolean isPreConstructSuccess() {
        return lifecycleSuccess.getOrDefault(LifecycleType.PRE_CONSTRUCT, false);
    }

    /**
     * Updates the success status of the pre-construction phase.
     * This is used to track whether setup operations completed successfully.
     *
     * @param success true if the pre-construction succeeded, false otherwise
     */
    public void setPreConstructSuccess(boolean success) {
        lifecycleSuccess.put(LifecycleType.PRE_CONSTRUCT, success);
    }

    /**
     * Returns the number of retry attempts made for this component during initialization.
     * Retries are used when dependency resolution fails and must be attempted again.
     *
     * @return the retry count
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * Increments the retry count for this component.
     * This is used to track how many times initialization has failed and been retried.
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }

    /**
     * Returns the source context that owns this dependency metadata.
     * This identifies the context in which this component is being managed.
     *
     * @return the source context, or null if not assigned
     */
    public IContext<CLASS> getSourceContext() {
        return sourceContext;
    }

    /**
     * Assigns the source context that owns this dependency metadata.
     * This is used to track which context is responsible for managing this component.
     *
     * @param ctx the context that owns this metadata
     */
    public void setSourceContext(IContext<CLASS> ctx) {
        this.sourceContext = ctx;
    }

    /**
     * Returns the priority of this component within the dependency graph.
     * Priority influences resolution and initialization precedence relative to other components.
     *
     * @return the priority value
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this component within the dependency graph.
     * Priority influences resolution and initialization precedence relative to other components.
     *
     * @param priority the priority value to assign
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }


    @Override
    public int calculateDepth(CLASS dependencyClass, Set<CLASS> dependencyClassSet) {
        if (dependencyClassSet.isEmpty()) return 0;
        int maxDepth = 0;
        //TODO: ADd getDeclaringClass as a wrapped method in IDependencyClass/DependencyClass so
        // we dont need a extra .getClass() delegation
        for (CLASS dep : dependencyClassSet) {
            if (dep != null && dependencyClass.getClass().getDeclaringClass() != null) {
                maxDepth = Math.max(maxDepth, dependencyClass.getClass().getDeclaringClass().getDeclaringClass() != null ? 1 : 0);
            }
        }
        return maxDepth;
    }

    @Override
    public DependencyRole determineRole(Set<CLASS> dependencyClassSet) {
        if (dependencyClassSet.isEmpty()) return DependencyRole.BASE;
        if (dependencyClassSet.size() == 1) return DependencyRole.INTERMEDIATE;
        return DependencyRole.ISOLATED;
    }


    /**
     * Generates a comprehensive summary of this dependency's metadata.
     * Includes all relevant information about the dependency's state, relationships,
     * lifecycle, and configuration in a human-readable format.
     *
     * @return A formatted string containing the complete metadata summary
     */
    @Override
    public String getDependencyMetaDataSummary() {
        if (getDependencyClass() == null) {
            return "DependencyMetaData Summary: [NULL DEPENDENCY CLASS]";
        }

        StringBuilder summary = new StringBuilder();
        String className = getDependencyClass().getSimpleName();
        String fullClassName = getDependencyClass().getName();

        // Header
        summary.append("=== Dependency Metadata Summary: ").append(className).append(" ===\n");

        // Basic Information
        summary.append("Class Information:\n");
        summary.append("  • Class Name: ").append(className).append("\n");
        summary.append("  • Full Name: ").append(fullClassName).append("\n");
        summary.append("  • Is Interface: ").append(getDependencyClass().isInterface()).append("\n");
        summary.append("  • Is Abstract: ").append(java.lang.reflect.Modifier.isAbstract(getDependencyClass().getClass().getModifiers())).append("\n");
        summary.append("  • Is Enum: ").append(getDependencyClass().isEnum()).append("\n");
        summary.append("  • Loadable: ").append(isClassLoadable(getDependencyClass().getClass()) ? "✓" : "✗").append("\n");

        // Dependency Graph Information
        summary.append("\nDependency Graph:\n");
        summary.append("  • Role: ").append(dependencyRole != null ? dependencyRole : "UNASSIGNED").append("\n");
        summary.append("  • Depth: ").append(depth).append("\n");
        summary.append("  • Priority: ").append(priority).append("\n");
        summary.append("  • Retry Count: ").append(retryCount).append("\n");

        // Dependencies
        summary.append("\nDirect Dependencies (").append(subDependencies.size()).append("):\n");
        if (subDependencies.isEmpty()) {
            summary.append("  • None (BASE dependency)\n");
        } else {
            subDependencies.stream()
                    .sorted(Comparator.comparing(IDependencyClass::getSimpleName))
                    .forEach(dep -> summary.append("  • ").append(dep.getSimpleName()).append("\n"));
        }

        // Instance Information
        summary.append("\nInstance Management:\n");
        if (getDependencyInstance() != null) {
            summary.append("  • Instance: ✓ (").append(getDependencyInstance().getClass().getSimpleName()).append(")\n");
            summary.append("  • Instance Type: ").append(getDependencyInstance().getClass().getName()).append("\n");
            summary.append("  • Is Proxy: ").append(getDependencyInstance().getClass().getName().contains("$Proxy") ? "✓" : "✗").append("\n");
        } else {
            summary.append("  • Instance: ✗ (Not instantiated)\n");
        }
        // Lifecycle Methods
        summary.append("\nLifecycle Methods:\n");
        Method preConstruct = lifecycleMethods.get(LifecycleType.PRE_CONSTRUCT);
        Method postConstruct = lifecycleMethods.get(LifecycleType.POST_CONSTRUCT);

        if (preConstruct != null) {
            summary.append("  • PreConstruct: ✓ ").append(preConstruct.getName()).append("()\n");
            Boolean preSuccess = lifecycleSuccess.get(LifecycleType.PRE_CONSTRUCT);
            summary.append("    - Executed Successfully: ").append(preSuccess != null && preSuccess ? "✓" : "✗").append("\n");
        } else {
            summary.append("  • PreConstruct: ✗ (Not defined)\n");
        }

        if (postConstruct != null) {
            summary.append("  • PostConstruct: ✓ ").append(postConstruct.getName()).append("()\n");
            Boolean postSuccess = lifecycleSuccess.get(LifecycleType.POST_CONSTRUCT);
            summary.append("    - Executed Successfully: ").append(postSuccess != null && postSuccess ? "✓" : "✗").append("\n");
        } else {
            summary.append("  • PostConstruct: ✗ (Not defined)\n");
        }

        // Context Information
        summary.append("\nContext Management:\n");
        if (sourceContext != null) {
            summary.append("  • Source Context: ✓ (").append(sourceContext.getClass().getSimpleName()).append(")\n");
            summary.append("  • Context Type: ").append(sourceContext.getClass().getName()).append("\n");
        } else {
            summary.append("  • Source Context: ✗ (Not assigned)\n");
        }


        // Inheritance Hierarchy (if available)
        summary.append("\nClass Hierarchy:\n");
        Class<?> superClass = getDependencyClass().getSuperclass();
        if (superClass != null && superClass != Object.class) {
            summary.append("  • Parent Class: ").append(superClass.getSimpleName()).append("\n");
        }

        Class<?>[] interfaces = getDependencyClass().getInterfaces();
        if (interfaces.length > 0) {
            summary.append("  • Implements (").append(interfaces.length).append("):\n");
            Arrays.stream(interfaces)
                    .sorted(Comparator.comparing(Class::getSimpleName))
                    .forEach(iface -> summary.append("    - ").append(iface.getSimpleName()).append("\n"));
        }

        // Statistics
        summary.append("\nStatistics:\n");
        summary.append("  • All Instances Count: ").append(getDependencyMap().size()).append("\n");
        summary.append("  • Dependency Map Size: ").append(getDependencyMapSize()).append("\n");

        // Status Summary
        summary.append("\nStatus Summary:\n");
        summary.append("  • Ready for Injection: ").append(isReadyForInjection() ? "✓" : "✗").append("\n");
        summary.append("  • Has Dependencies: ").append(subDependencies.isEmpty() ? "✗" : "✓").append("\n");
        summary.append("  • Instance Available: ").append(getDependencyInstance() != null ? "✓" : "✗").append("\n");
        summary.append("  • Lifecycle Complete: ").append(isLifecycleComplete() ? "✓" : "✗").append("\n");

        summary.append("=====================================");

        return summary.toString();
    }

    /**
     * Helper method to determine if this dependency is ready for injection.
     *
     * @return true if the dependency can be injected into other components
     */
    private boolean isReadyForInjection() {
        return getDependencyClass() != null || getDependencySupplier() != null;
    }

    /**
     * Helper method to determine if lifecycle methods have been executed successfully.
     *
     * @return true if all defined lifecycle methods have executed successfully
     */
    private boolean isLifecycleComplete() {
        // Check PreConstruct
        if (lifecycleMethods.containsKey(LifecycleType.PRE_CONSTRUCT)) {
            Boolean preSuccess = lifecycleSuccess.get(LifecycleType.PRE_CONSTRUCT);
            if (preSuccess == null || !preSuccess) {
                return false;
            }
        }

        // Check PostConstruct
        if (lifecycleMethods.containsKey(LifecycleType.POST_CONSTRUCT)) {
            Boolean postSuccess = lifecycleSuccess.get(LifecycleType.POST_CONSTRUCT);
            if (postSuccess == null || !postSuccess) {
                return false;
            }
        }

        return true;
    }
}



