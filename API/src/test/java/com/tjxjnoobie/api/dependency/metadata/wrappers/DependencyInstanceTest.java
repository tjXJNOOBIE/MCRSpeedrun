package com.tjxjnoobie.api.dependency.metadata.wrappers;

import com.tjxjnoobie.api.dependency.injection.helpers.fixtures.DelegatingRedisService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class DependencyInstanceTest {

    @Test
    void storesConcreteClassAndRuntimeInstanceSeparately() {
        DependencyInstance<DelegatingRedisService> dependencyInstance =
                new DependencyInstance<>(DelegatingRedisService.class);
        DelegatingRedisService redisService = new DelegatingRedisService();

        dependencyInstance.setWrappedDependencyInstance(redisService);

        assertSame(DelegatingRedisService.class, dependencyInstance.getDependencyInstanceClass());
        assertSame(redisService, dependencyInstance.getWrappedDependencyInstance());
    }
}
