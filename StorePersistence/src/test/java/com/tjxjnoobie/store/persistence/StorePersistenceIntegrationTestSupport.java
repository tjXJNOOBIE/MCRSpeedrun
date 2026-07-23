package com.tjxjnoobie.store.persistence;

import com.tjxjnoobie.store.domain.service.implementation.DefaultAdminAuthzService;
import com.tjxjnoobie.store.domain.service.implementation.DefaultCouponService;
import com.tjxjnoobie.store.domain.service.implementation.DefaultPlayerIdentityService;
import com.tjxjnoobie.store.domain.service.implementation.DefaultPricingService;
import com.tjxjnoobie.store.persistence.entity.PlayerAccountEntity;
import com.tjxjnoobie.store.persistence.service.JpaAuditService;
import com.tjxjnoobie.store.persistence.service.JpaCatalogService;
import com.tjxjnoobie.store.persistence.service.JpaCheckoutService;
import com.tjxjnoobie.store.persistence.service.JpaEntitlementService;
import com.tjxjnoobie.store.persistence.service.JpaFulfillmentOrchestrator;
import com.tjxjnoobie.store.persistence.service.JpaOrderService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(classes = StorePersistenceIntegrationTestSupport.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
abstract class StorePersistenceIntegrationTestSupport {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16-alpine");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.flyway.table", () -> "store_flyway_schema_history");
    }

    @EnableAutoConfiguration
    @ConfigurationPropertiesScan(basePackageClasses = PlayerAccountEntity.class)
    @EntityScan(basePackages = "com.tjxjnoobie.store.persistence.entity")
    @EnableJpaRepositories(basePackages = "com.tjxjnoobie.store.persistence.repository")
    @ComponentScan(basePackageClasses = {
            JpaAuditService.class,
            JpaCatalogService.class,
            JpaCheckoutService.class,
            JpaEntitlementService.class,
            JpaFulfillmentOrchestrator.class,
            JpaOrderService.class,
            DefaultPricingService.class,
            DefaultCouponService.class,
            DefaultAdminAuthzService.class,
            DefaultPlayerIdentityService.class
    })
    static class TestApplication {
    }
}
