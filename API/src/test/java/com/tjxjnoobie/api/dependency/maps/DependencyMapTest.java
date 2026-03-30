package com.tjxjnoobie.api.dependency.maps;

import com.tjxjnoobie.api.dependency.injection.helpers.fixtures.DelegatingUtilsService;
import com.tjxjnoobie.api.dependency.metadata.DependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.wrappers.DependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.wrappers.DependencyInterface;
import com.tjxjnoobie.api.interfaces.IUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DependencyMapTest {

    @AfterEach
    void clearDependencyMap() {
        DependencyMap.getDependencyMap().clear();
    }

    @Test
    void resolvesRegisteredMetadataByRealProjectInterfaceToken() {
        DependencyMetaData<IUtils, DelegatingUtilsService> metaData = new DependencyMetaData<>();
        metaData.populateMetaData(
                IUtils.class,
                DelegatingUtilsService.class,
                new DependencyInterface<>(IUtils.class),
                new DependencyInstance<>(DelegatingUtilsService.class));

        DependencyMap.getDependencyMap().registerDependency(IUtils.class, metaData);

        IUtils utils = DependencyMap.getDependencyMap().getInstance(IUtils.class);
        utils.createServerID();

        assertSame(metaData, DependencyMap.getDependencyMap().getMetaData(IUtils.class));
        assertSame(metaData.getDependencyInstance(), utils);
        assertEquals("server-generated", utils.getServerID());
    }
}
