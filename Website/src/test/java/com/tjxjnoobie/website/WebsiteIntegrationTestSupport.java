package com.tjxjnoobie.website;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjxjnoobie.store.integration.auth.IntegrationScope;
import com.tjxjnoobie.store.integration.auth.IntegrationTokenClaims;
import com.tjxjnoobie.store.integration.auth.IntegrationTokenCodec;
import com.tjxjnoobie.store.persistence.entity.PlayerAccountEntity;
import com.tjxjnoobie.store.persistence.repository.PlayerAccountRepository;
import com.tjxjnoobie.website.security.StoreAdminPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@SpringBootTest(classes = WebsiteApplication.class)
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
abstract class WebsiteIntegrationTestSupport {

    protected static final String INTEGRATION_BOOTSTRAP_SECRET = "integration-bootstrap-secret";
    protected static final String INTEGRATION_TOKEN_SECRET = "integration-token-secret";

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.flyway.table", () -> "store_flyway_schema_history");
        registry.add("app.store.base-url", () -> "http://localhost");
        registry.add("app.store.integration.bootstrap-secret", () -> INTEGRATION_BOOTSTRAP_SECRET);
        registry.add("app.store.integration.token-secret", () -> INTEGRATION_TOKEN_SECRET);
        registry.add("app.store.integration.test-mode-enabled", () -> "true");
        registry.add("app.store.local-admin.enabled", () -> "true");
        registry.add("app.store.local-admin.default-role", () -> "ADMIN");
        registry.add("app.store.local-admin.seed-username", () -> "storeadmin");
        registry.add("app.store.local-admin.seed-email", () -> "storeadmin@example.test");
        registry.add("app.store.local-admin.seed-password", () -> "StoreAdmin!123");
        registry.add("app.store.stripe.webhook-secret", () -> "whsec_test");
        registry.add("app.store.account-session-cookie", () -> "NOVUS_STORE_TEST");
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PlayerAccountRepository playerAccountRepository;

    protected RequestPostProcessor adminPrincipal() {
        StoreAdminPrincipal principal = new StoreAdminPrincipal(
                1L,
                "storeadmin",
                "storeadmin@example.test",
                "unused",
                true,
                Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
                Map.of()
        );
        return authentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    protected String integrationToken(IntegrationScope... scopes) {
        Instant now = Instant.now();
        return new IntegrationTokenCodec(INTEGRATION_TOKEN_SECRET).issue(
                new IntegrationTokenClaims("test-node", now.minusSeconds(5).getEpochSecond(), now.plusSeconds(600).getEpochSecond(), List.copyOf(Arrays.asList(scopes)))
        );
    }

    protected PlayerAccountEntity findPlayerByUsername(String username) {
        return playerAccountRepository.findByCurrentUsernameIgnoreCase(username).orElseThrow();
    }
}
