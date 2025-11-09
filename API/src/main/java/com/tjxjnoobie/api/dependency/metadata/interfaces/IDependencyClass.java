/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.dependency.metadata.interfaces;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;

public interface IDependencyClass<CLASS> {
    //TODO: Extend Class "class" object wrapping to another class so DependencyInstance
    // or any other class can use the wrappings so we don't have to delegate .getClass()
    // to get to our typed class







    default UUID getDependencyId(){
        return null;
    }

    default CLASS getDependencyClass(){
        return null;
    }

    default Class<?> getDependencyRawInterface(){
        return null;
    }

    default Date getCreationTime() {
        return null;
    }




    default void setDependencyClass(Class<?> dependencyClass){

    }

    default void setDependencyId(UUID dependencyId) {
        // Implementation overridden in concrete class
    }

    default void setCreationTime(Date creationTime) {
        // Implementation overridden in concrete class
    }

    // =====================================================
    // Wrapped java.lang.Class methods
    // =====================================================

    default String getName() {
        return "";
    }

    default String getSimpleName(){
        return "";
    }

    default String getPackageName() {
        return "";
    }

    default boolean isInterface() {
        return false;
    }

    default boolean isEnum() {
        return false;
    }

    default boolean isAnnotation() {
        return false;
    }

    default boolean isPrimitive() {
        return false;
    }

    default boolean isArray() {
        return false;
    }

    default boolean isAssignableFrom(Class<?> cls) {
        return false;
    }

    default boolean isInstance(Object obj) {
        return false;
    }

    default Class<?> getSuperclass() {
        return null;
    }

    default Class<?>[] getInterfaces() {
        return new Class<?>[0];
    }

    default Type[] getGenericInterfaces() {
        return new Type[0];
    }

    default Type getGenericSuperclass() {
        return null;
    }

    default Constructor<?>[] getConstructors() {
        return new Constructor<?>[0];
    }

    default Constructor<?> getConstructor(Class<?>... parameterTypes)
            throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    default Constructor<?>[] getDeclaredConstructors() {
        return new Constructor<?>[0];
    }

    default Constructor<?> getDeclaredConstructor(Class<?>... parameterTypes)
            throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    default Field[] getFields() {
        return new Field[0];
    }

    default Field getField(String name) throws NoSuchFieldException {
        throw new NoSuchFieldException();
    }

    default Field[] getDeclaredFields() {
        return new Field[0];
    }

    default Field getDeclaredField(String name) throws NoSuchFieldException {
        throw new NoSuchFieldException();
    }

    default Method[] getMethods() {
        return new Method[0];
    }

    default Method getMethod(String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    default Method[] getDeclaredMethods() {
        return new Method[0];
    }

    default Method getDeclaredMethod(String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    default Annotation[] getAnnotations() {
        return new Annotation[0];
    }

    default <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return null;
    }

    default Annotation[] getDeclaredAnnotations() {
        return new Annotation[0];
    }

    default <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        return null;
    }

    default <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass) {
        return null;
    }

}
