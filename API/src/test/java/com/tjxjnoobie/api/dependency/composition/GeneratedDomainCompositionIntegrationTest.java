package com.tjxjnoobie.api.dependency.composition;

import com.tjxjnoobie.api.dependency.composition.domains.IInfrastructureDomain;
import com.tjxjnoobie.api.dependency.metadata.DependencyMetaData;
import com.tjxjnoobie.api.dependency.metadata.fixtures.RealProjectMultiInterfaceService;
import com.tjxjnoobie.api.dependency.metadata.wrappers.DependencyInstance;
import com.tjxjnoobie.api.dependency.metadata.wrappers.DependencyInterface;
import com.tjxjnoobie.api.interfaces.IRedis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneratedDomainCompositionIntegrationTest {

    @BeforeEach
    void resetFixture() {
        RealProjectMultiInterfaceService.reset();
    }

    @Test
    void generatedDomainBridgeExposesBareDependencyMethods() {
        InfrastructureMetaData metaData = new InfrastructureMetaData();
        metaData.populateMetaData(
                IRedis.class,
                RealProjectMultiInterfaceService.class,
                new DependencyInterface<>(IRedis.class),
                new DependencyInstance<>(RealProjectMultiInterfaceService.class));

        metaData.connectToRedis();
        metaData.createServerID();
        metaData.createGameID();
        metaData.setConfigValue("surface", "generated-domain");

        assertEquals(1, RealProjectMultiInterfaceService.getRedisConnectCalls());
        assertEquals("multi-server", metaData.getServerID());
        assertEquals("multi-game", metaData.getGameID());
        assertEquals("generated-domain", metaData.getConfigValues().get("surface"));
    }

    private static class InfrastructureMetaData
            extends DependencyMetaData<IRedis, RealProjectMultiInterfaceService>
            implements IInfrastructureDomain {
    }
}
