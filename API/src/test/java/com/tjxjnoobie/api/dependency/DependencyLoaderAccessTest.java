package com.tjxjnoobie.api.dependency;

import com.tjxjnoobie.api.dependency.injection.helpers.fixtures.DelegatingUtilsService;
import com.tjxjnoobie.api.dependency.maps.DependencyMap;
import com.tjxjnoobie.api.dependency.metadata.DependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.interfaces.IDependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.wrappers.DependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.wrappers.DependencyInterface;
import com.tjxjnoobie.api.interfaces.IUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DependencyLoaderAccessTest {

    @AfterEach
    void clearDependencyMap() {
        DependencyMap.getDependencyMap().clear();
    }

    @Test
    void returnsNullWhenInstanceIsMissing() {
        assertNull(DependencyLoaderAccess.findInstance(IUtils.class));
        assertFalse(DependencyLoaderAccess.isInstanceRegistered(IUtils.class));
        assertNull(DependencyLoaderAccess.findMetaData(IUtils.class));
    }

    @Test
    void throwsWhenRequiredInstanceIsMissing() {
        assertThrows(NullPointerException.class, () -> DependencyLoaderAccess.requireInstance(IUtils.class));
    }

    @Test
    void exposesRegisteredMetadataAndInstances() {
        DependencyMetaData<IUtils, DelegatingUtilsService> metaData = new DependencyMetaData<>();
        metaData.populateMetaData(
                IUtils.class,
                DelegatingUtilsService.class,
                new DependencyInterface<>(IUtils.class),
                new DependencyInstance<>(DelegatingUtilsService.class));
        DependencyMap.getDependencyMap().registerDependency(IUtils.class, metaData);

        IDependencyMetaData<?, ?> registeredMetaData = DependencyLoaderAccess.findMetaData(IUtils.class);
        IUtils utils = DependencyLoaderAccess.requireInstance(IUtils.class);

        assertNotNull(registeredMetaData);
        assertSame(metaData, registeredMetaData);
        assertSame(metaData.getDependencyInstance(), utils);
        assertTrue(DependencyLoaderAccess.isInstanceRegistered(IUtils.class));
    }

    @Test
    void replacesRegisteredInstance() {
        DependencyMetaData<IUtils, DelegatingUtilsService> metaData = new DependencyMetaData<>();
        metaData.populateMetaData(
                IUtils.class,
                DelegatingUtilsService.class,
                new DependencyInterface<>(IUtils.class),
                new DependencyInstance<>(DelegatingUtilsService.class));
        DependencyMap.getDependencyMap().registerDependency(IUtils.class, metaData);

        IUtils original = DependencyLoaderAccess.requireInstance(IUtils.class);
        IUtils replacement = DependencyLoaderAccess.replaceInstance(IUtils.class, DelegatingUtilsService::new);

        assertNotSame(original, replacement);
        assertSame(replacement, DependencyLoaderAccess.findInstance(IUtils.class));
        assertSame(replacement, metaData.getDependencyInstance());
    }

    @Test
    void rejectsReplacementWhenTokenIsMissing() {
        assertThrows(IllegalStateException.class,
                () -> DependencyLoaderAccess.replaceInstance(IUtils.class, DelegatingUtilsService::new));
    }
}
