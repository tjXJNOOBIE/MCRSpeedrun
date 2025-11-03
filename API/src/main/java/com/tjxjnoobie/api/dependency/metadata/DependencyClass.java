/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata;

import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * DependencyClass – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 11/1/2025
 */
public class DependencyClass<CLASS extends IDependencyClass<?>> implements IDependencyClass<CLASS> {

    //To add to class Doc sstring: We have to wrap Class methods here since Class object is final at runtime and not easily changeable
    //TODO: Add logging to all methods so we can see what methods are being called and their parameters
    // This can also be used to make it so we can make a registry of all classes and their methods, fields, extends, implmentations, etc.
    private IDependencyClass<CLASS> dependencyClass;

    private UUID dependencyId;
    private Date creationTime;


    @Override
    public IDependencyClass<CLASS> getDependencyClass() {
        return dependencyClass;
    }
    @Override
    public UUID getDependencyId() {
        return dependencyId;
    }

    @Override
    public Date getCreationTime() {
        return creationTime;
    }

    @Override
    public void setDependencyClass(IDependencyClass<CLASS> dependencyClass) {
        this.dependencyClass = dependencyClass;
    }

    @Override

    public void setDependencyId(UUID dependencyId) {
        this.dependencyId = dependencyId;
    }

    @Override
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;


    }

    // =====================================================
    // Wrapped java.lang.Class methods
    // =====================================================

    @Override
    public String getName() {
        return getDependencyClass().getName();
    }

    @Override
    public String getSimpleName() {
        return getDependencyClass().getSimpleName();
    }

    @Override
    public String getPackageName() {
        return getDependencyClass().getClass().getPackageName();
    }

    @Override
    public boolean isInterface() {
        return getDependencyClass().isInterface();
    }

    @Override
    public boolean isEnum() {
        return getDependencyClass().isEnum();
    }

    @Override
    public boolean isAnnotation() {
        return getDependencyClass().getClass().isAnnotation();
    }

    @Override
    public boolean isPrimitive() {
        return getDependencyClass().getClass().isPrimitive();
    }

    @Override
    public boolean isArray() {
        return getDependencyClass().getClass().isArray();
    }

    @Override
    public boolean isAssignableFrom(Class<?> cls) {
        return getDependencyClass().getClass().isAssignableFrom(cls);
    }

    @Override
    public boolean isInstance(Object obj) {
        return getDependencyClass().getClass().isInstance(obj);
    }

    @Override
    public Class<?> getSuperclass() {
        return getDependencyClass().getClass().getSuperclass();
    }

    @Override
    public Class<?>[] getInterfaces() {
        return getDependencyClass().getClass().getInterfaces();
    }

    @Override
    public Type[] getGenericInterfaces() {
        return getDependencyClass().getClass().getGenericInterfaces();
    }

    @Override
    public Type getGenericSuperclass() {
        return getDependencyClass().getClass().getGenericSuperclass();
    }

    @Override
    public Constructor<?>[] getConstructors() {
        return getDependencyClass().getClass().getConstructors();
    }

    @Override
    public Constructor<?> getConstructor(Class<?>... parameterTypes)
            throws NoSuchMethodException {
        return getDependencyClass().getClass().getConstructor(parameterTypes);
    }

    @Override
    public Constructor<?>[] getDeclaredConstructors() {
        return getDependencyClass().getClass().getDeclaredConstructors();
    }

    @Override
    public Constructor<?> getDeclaredConstructor(Class<?>... parameterTypes)
            throws NoSuchMethodException {
        return getDependencyClass().getClass().getDeclaredConstructor(parameterTypes);
    }

    @Override
    public Field[] getFields() {
        return getDependencyClass().getClass().getFields();
    }

    @Override
    public Field getField(String name) throws NoSuchFieldException {
        return getDependencyClass().getClass().getField(name);
    }

    @Override
    public Field[] getDeclaredFields() {
        return getDependencyClass().getClass().getDeclaredFields();
    }

    @Override
    public Field getDeclaredField(String name) throws NoSuchFieldException {
        return getDependencyClass().getClass().getDeclaredField(name);
    }

    @Override
    public Method[] getMethods() {
        return getDependencyClass().getClass().getMethods();
    }

    @Override
    public Method getMethod(String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        return getDependencyClass().getClass().getMethod(name, parameterTypes);
    }

    @Override
    public Method[] getDeclaredMethods() {
        return getDependencyClass().getClass().getDeclaredMethods();
    }

    @Override
    public Method getDeclaredMethod(String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        return getDependencyClass().getClass().getDeclaredMethod(name, parameterTypes);
    }

    @Override
    public Annotation[] getAnnotations() {
        return getDependencyClass().getClass().getAnnotations();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return getDependencyClass().getClass().getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return getDependencyClass().getClass().getDeclaredAnnotations();
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        return getDependencyClass().getClass().getAnnotationsByType(annotationClass);
    }

    @Override
    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass) {
        return getDependencyClass().getClass().getDeclaredAnnotation(annotationClass);
    }

    // =====================================================
    // Utility
    // =====================================================

    @Override
    public String toString() {
        return "DependencyClass[" +
                "dep=" + (dependencyClass != null ? dependencyClass.getName() : "null") +
                ", id=" + dependencyId +
                ", created=" + creationTime +
                ']';
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependencyClass, dependencyId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DependencyClass<?> other)) return false;
        return Objects.equals(this.dependencyClass, other.dependencyClass)
                && Objects.equals(this.dependencyId, other.dependencyId);
    }
}

