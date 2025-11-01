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
import com.tjxjnoobie.api.dependency.injection.helpers.interfaces.IDependencyInjectorHelper;
import com.tjxjnoobie.api.dependency.maps.interfaces.IDependencyMap;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.interfaces.IContext;
import com.tjxjnoobie.api.platform.global.console.Log;
import com.tjxjnoobie.api.platform.global.enums.DependencyRole;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class DependencyMetaData implements IDependencyMetaData, IDependencyInjectorHelper, IDependencyMap {

    private final Class<?> dependencyClass;
    private Set<Class<?>> dependencies = new HashSet<>();
    private int priority;
    private int depth;
    private DependencyRole role;
    private final Map<LifecycleType, Method> lifecycleMethods = new EnumMap<>(LifecycleType.class);
    private final Map<LifecycleType, Boolean> lifecycleSuccess = new EnumMap<>(LifecycleType.class);
    private int retryCount;
    private Object instance; // optional actual bound object
    private IContext<?> sourceContext; // which context owns it
    private Supplier<?> factory; // factory for creating instances

    public DependencyMetaData(Class<?> dependencyClass) {
        this.dependencyClass = dependencyClass;
    }

    /**
     * Populates the metadata for this component by scanning fields and methods for annotations.
     * This method identifies dependencies and pre-construction methods based on @Inject and @PreConstruct annotations.
     *
     * @param clazz the class to scan for dependencies and pre-construction methods
     */
    public void populateMetaData(Class<?> clazz) {
        Set<Class<?>> dependencies = new HashSet<>();
//        for (Field field : clazz.getDeclaredFields()) {
//            if (field.isAnnotationPresent(Inject.class)) {
//                dependencies.add(field.getType());
//            }
//        }
        if (!isClassLoadable(clazz)) Log.error("Class not loadable: " + clazz.getName());
        // Track inheritance
        //TODO: We can probably populate this in recursivelyLinkExtendedInterfaces
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            dependencies.add(clazz.getSuperclass());
        }
        dependencies.addAll(Arrays.asList(clazz.getInterfaces()));

        this.setDependencies(dependencies);
        this.depth = calculateDepth(clazz, dependencies);
        this.role = determineRole(dependencies);
        // Detect lifecycle methods via enum
//        for (LifecycleType type : LifecycleType.values()) {
//            type.findIn(clazz).ifPresent(m -> lifecycleMethods.put(type, m));
//        }

    }

    //TODO: Move to injection helper if working
    private boolean isClassLoadable(Class<?> clazz) {
        try {
            clazz.getDeclaredMethods(); // will throw NoClassDefFoundError if deps missing
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    // --- Getters / setters ---

    /**
     * Gets the metadata for a given class.
     *
     * @param clazz The class type
     * @return The metadata, or null if not found
     */
    @Override
    public IDependencyMetaData getMetaData(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return dependencyClass.equals(clazz) ? this : null;
    }

    /**
     * Returns the class of the dependency that this metadata represents.
     * This is the primary type being managed by the dependency graph.
     *
     * @return the class of the dependency
     */
    @Override
    public Class<?> getDependencyClass() {
        return dependencyClass;
    }

    /**
     * Returns the set of classes that this component directly depends on.
     * These are the types that must be resolved before this component can be initialized.
     *
     * @return a read-only set of dependency classes
     */
    @Override
    public Set<Class<?>> getDependencies() {
        return dependencies;
    }

    @Override
    public Object ensureAndGetInstance(IDependencyMetaData metaData) {
        if (metaData == null) {
            return null;
        }

        Object instance = metaData.getDependencyInstance(metaData.getDependencyClass());
        if (instance == null && metaData.getFactory() != null) {
            instance = metaData.getFactory().get();
            if (instance != null) {
                metaData.setInstance(instance);
            }
        }
        return instance;
    }

    /**
     * Sets the direct dependencies of this component.
     * This allows configuration of which types must be resolved prior to this component's initialization.
     *
     * @param deps the set of classes this component depends on
     */
    @Override
    public void setDependencies(Set<Class<?>> deps) {
        this.dependencies = deps;
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
     * Returns a list of all registered dependency instances that have been created or are bound via factory.
     *
     * <p>This method retrieves all actual instance objects from the dependency registry by iterating over each
     * registered {@link IDependencyMetaData}. For each metadata entry, it first attempts to return a pre-bound instance,
     * and if not available, uses the associated factory to create one. Only non-null instances are included in the result.</p>
     *
     * <p>The returned list contains real objects that have been injected into the system (either directly or via factory),
     * and does not include placeholders or null references.</p>
     *
     * @return A list of all currently registered and instantiated dependency objects (never null)
     * The list may be empty if no instances have been created.
     */
    @Override
    public List<Object> getAllInstances() {
        return dependencyMap.getDependencyMapValues().stream()
                .map(this::ensureAndGetInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
    public DependencyRole getRole() {
        return role;
    }

    /**
     * Sets the role of this component in the dependency graph.
     * This determines how the component behaves in the resolution and lifecycle management.
     *
     * @param role the role assigned to this component
     */
    @Override
    public void setRole(DependencyRole role) {
        this.role = role;
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
     * Returns the bound instance of this component, if any.
     * This is the actual object created and managed by the dependency graph.
     *
     * @return the bound instance, or null if not yet bound
     */
    @Override
    public Object getDependencyInstance(Class<?> requestedType) {
        if (instance != null) {
            if (requestedType == null || requestedType.isInstance(instance)) {
                return instance;
            }
            if (dependencyClass != null && requestedType != null && dependencyClass.isAssignableFrom(requestedType)) {
                return instance;
            }
        }

        if (instance == null && factory != null) {
            Object created = factory.get();
            if (created != null && (requestedType == null || requestedType.isInstance(created))) {
                instance = created;
                return instance;
            }
        }
        return null;
    }

    /**
     * Sets the bound instance of this component.
     * This is used to store the actual object instance after successful construction.
     *
     * @param instance the instance to bind
     */
    public void setInstance(Object instance) {
        this.instance = instance;
    }

    /**
     * Returns the source context that owns this dependency metadata.
     * This identifies the context in which this component is being managed.
     *
     * @return the source context, or null if not assigned
     */
    public IContext<?> getSourceContext() {
        return sourceContext;
    }

    /**
     * Assigns the source context that owns this dependency metadata.
     * This is used to track which context is responsible for managing this component.
     *
     * @param ctx the context that owns this metadata
     */
    public void setSourceContext(IContext<?> ctx) {
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

    /**
     * Returns the factory supplier for creating instances of this dependency.
     * The factory is used to create new instances on demand rather than using a singleton.
     *
     * @return the factory supplier, or null if not set
     */
    public Supplier<?> getFactory() {
        return factory;
    }

    /**
     * Sets the factory supplier for creating instances of this dependency.
     * This allows dynamic instance creation rather than singleton behavior.
     *
     * @param factory the supplier that creates new instances
     */
    public void setFactory(Supplier<?> factory) {
        this.factory = factory;
    }


    /**
     * Calculates the depth level of a component based on its dependency nesting.
     * Depth is determined by examining the declaring class hierarchy of dependencies.
     *
     * @param clazz the class being analyzed
     * @param deps  the set of dependencies for this class
     * @return the calculated depth level (0 for no dependencies, higher for nested dependencies)
     */
    public int calculateDepth(Class<?> clazz, Set<Class<?>> deps) {
        if (deps.isEmpty()) return 0;
        int maxDepth = 0;
        for (Class<?> dep : deps) {
            if (dep != null && dep.getDeclaringClass() != null) {
                maxDepth = Math.max(maxDepth, dep.getDeclaringClass().getDeclaringClass() != null ? 1 : 0);
            }
        }
        return maxDepth;
    }

    /**
     * Determines the role of a component within the dependency graph based on its dependency count.
     * Roles help categorize components by their position and responsibility in the dependency hierarchy.
     *
     * @param deps the set of dependencies for this component
     * @return BASE if no dependencies, INTERMEDIATE if one dependency, ISOLATED otherwise
     */
    public DependencyRole determineRole(Set<Class<?>> deps) {
        if (deps.isEmpty()) return DependencyRole.BASE;
        if (deps.size() == 1) return DependencyRole.INTERMEDIATE;
        return DependencyRole.ISOLATED;
    }

    /**
     * Checks if a dependency has an instance bound.
     *
     * @param clazz The class type to check
     * @return true if an instance exists, false otherwise
     */
    @Override
    public boolean hasInstance(Class<?> clazz) {
        if (instance == null) {
            return false;
        }
        return clazz == null || clazz.isInstance(instance);
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
        if (dependencyClass == null) {
            return "DependencyMetaData Summary: [NULL DEPENDENCY CLASS]";
        }

        StringBuilder summary = new StringBuilder();
        String className = dependencyClass.getSimpleName();
        String fullClassName = dependencyClass.getName();

        // Header
        summary.append("=== Dependency Metadata Summary: ").append(className).append(" ===\n");

        // Basic Information
        summary.append("Class Information:\n");
        summary.append("  • Class Name: ").append(className).append("\n");
        summary.append("  • Full Name: ").append(fullClassName).append("\n");
        summary.append("  • Is Interface: ").append(dependencyClass.isInterface()).append("\n");
        summary.append("  • Is Abstract: ").append(java.lang.reflect.Modifier.isAbstract(dependencyClass.getModifiers())).append("\n");
        summary.append("  • Is Enum: ").append(dependencyClass.isEnum()).append("\n");
        summary.append("  • Loadable: ").append(isClassLoadable(dependencyClass) ? "✓" : "✗").append("\n");

        // Dependency Graph Information
        summary.append("\nDependency Graph:\n");
        summary.append("  • Role: ").append(role != null ? role : "UNASSIGNED").append("\n");
        summary.append("  • Depth: ").append(depth).append("\n");
        summary.append("  • Priority: ").append(priority).append("\n");
        summary.append("  • Retry Count: ").append(retryCount).append("\n");

        // Dependencies
        summary.append("\nDirect Dependencies (").append(dependencies.size()).append("):\n");
        if (dependencies.isEmpty()) {
            summary.append("  • None (BASE dependency)\n");
        } else {
            dependencies.stream()
                    .sorted(Comparator.comparing(Class::getSimpleName))
                    .forEach(dep -> summary.append("  • ").append(dep.getSimpleName()).append("\n"));
        }

        // Instance Information
        summary.append("\nInstance Management:\n");
        if (instance != null) {
            summary.append("  • Instance: ✓ (").append(instance.getClass().getSimpleName()).append(")\n");
            summary.append("  • Instance Type: ").append(instance.getClass().getName()).append("\n");
            summary.append("  • Is Proxy: ").append(instance.getClass().getName().contains("$Proxy") ? "✓" : "✗").append("\n");
        } else {
            summary.append("  • Instance: ✗ (Not instantiated)\n");
        }
        summary.append("  • Has Factory: ").append(factory != null ? "✓" : "✗").append("\n");

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

        // Factory Information
        if (factory != null) {
            summary.append("\nFactory Information:\n");
            summary.append("  • Factory Type: ").append(factory.getClass().getSimpleName()).append("\n");
            summary.append("  • Can Create Instance: ✓\n");

            // Test factory if no instance exists
            if (instance == null) {
                try {
                    Object testInstance = factory.get();
                    summary.append("  • Factory Test: ").append(testInstance != null ? "✓ (Success)" : "✗ (Returns null)").append("\n");
                } catch (Exception e) {
                    summary.append("  • Factory Test: ✗ (Exception: ").append(e.getClass().getSimpleName()).append(")\n");
                }
            }
        }

        // Inheritance Hierarchy (if available)
        summary.append("\nClass Hierarchy:\n");
        Class<?> superClass = dependencyClass.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            summary.append("  • Parent Class: ").append(superClass.getSimpleName()).append("\n");
        }

        Class<?>[] interfaces = dependencyClass.getInterfaces();
        if (interfaces.length > 0) {
            summary.append("  • Implements (").append(interfaces.length).append("):\n");
            Arrays.stream(interfaces)
                    .sorted(Comparator.comparing(Class::getSimpleName))
                    .forEach(iface -> summary.append("    - ").append(iface.getSimpleName()).append("\n"));
        }

        // Statistics
        summary.append("\nStatistics:\n");
        summary.append("  • All Instances Count: ").append(getAllInstances().size()).append("\n");
        summary.append("  • Dependency Map Size: ").append(getDependencyMapSize()).append("\n");

        // Status Summary
        summary.append("\nStatus Summary:\n");
        summary.append("  • Ready for Injection: ").append(isReadyForInjection() ? "✓" : "✗").append("\n");
        summary.append("  • Has Dependencies: ").append(dependencies.isEmpty() ? "✗" : "✓").append("\n");
        summary.append("  • Instance Available: ").append(instance != null ? "✓" : "✗").append("\n");
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
        return instance != null || factory != null;
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


