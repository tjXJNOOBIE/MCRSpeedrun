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
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;

import java.util.Date;
import java.util.UUID;

/**
 * DependencyClass – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 11/1/2025
 */
public class DependencyClass<CLASS extends IDependencyClass<?>>
        implements IDependencyClass<CLASS> {

    //To add to class Doc sstring: We have to wrap Class methods here since Class object is final at runtime and not easily changeable
    //TODO: Add logging to all methods so we can see what methods are being called and their parameters
    // This can also be used to make it so we can make a registry of all classes and their methods, fields, extends, implmentations, etc.
    private CLASS dependencyClass;
    private IDependencyMetaData<CLASS, ?> metadata;
    private final Class<?> interfaceClass;
    public UUID dependencyId;
    public Date creationTime;
    public DependencyClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
    @Override
    public CLASS getDependencyClass(){
        return dependencyClass;
    }
    @Override
    public Class<?> getDependencyRawInterface() {
        return interfaceClass;
    }
    @Override
    public Date getCreationTime() {
        return creationTime;
    }

    @Override
    public UUID getDependencyId() {
        return dependencyId;
    }

    @Override
    public void setDependencyClass(CLASS dependencyClass) {
        this.dependencyClass = (CLASS) dependencyClass;
    }

    @Override

    public void setDependencyId(UUID dependencyId) {
        this.dependencyId = dependencyId;
    }

    @Override
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;


    }


}

