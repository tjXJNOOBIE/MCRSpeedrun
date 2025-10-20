package com.tjxjnoobie.api.platform.global.metadata;

/**
 * Abstract base class that provides metadata capabilities to logging classes.
 * Now works with the new GlobalMetadataManager system, similar to how AbstractContext works.
 * 
 * Classes that extend this automatically get all metadata functionality without
 * having to implement any methods.
 */

import com.tjxjnoobie.api.platform.global.metadata.abstracts.AbstractClassMetaData;
public abstract class AbstractLogMetaData<T> extends AbstractClassMetaData<AbstractLogMetaData>   {
    
    protected AbstractLogMetaData() {
        // No need for manual initialization - GlobalMetadataManager handles everything
        // The type parameter is automatically resolved to the concrete implementing class
    }
}