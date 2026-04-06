package com.tjxjnoobie.website.integration;

import com.tjxjnoobie.store.integration.auth.IntegrationScope;
import com.tjxjnoobie.store.integration.auth.IntegrationTokenClaims;
import com.tjxjnoobie.store.integration.auth.IntegrationTokenCodec;
import com.tjxjnoobie.store.integration.dto.IntegrationAuthRequest;
import com.tjxjnoobie.store.integration.dto.IntegrationAuthResponse;
import com.tjxjnoobie.website.config.StoreApplicationProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class StoreIntegrationAuthService {

    private final StoreApplicationProperties properties;
    private final IntegrationTokenCodec tokenCodec;

    public StoreIntegrationAuthService(StoreApplicationProperties properties) {
        this.properties = properties;
        this.tokenCodec = new IntegrationTokenCodec(properties.getIntegration().getTokenSecret());
    }

    public IntegrationAuthResponse issueToken(IntegrationAuthRequest request) {
        if (!constantTimeEquals(properties.getIntegration().getBootstrapSecret(), request.bootstrapSecret())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid bootstrap secret");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.getIntegration().getTokenTtlSeconds());
        Set<IntegrationScope> scopes = EnumSet.allOf(IntegrationScope.class);
        IntegrationTokenClaims claims = new IntegrationTokenClaims(
                request.nodeId() == null || request.nodeId().isBlank() ? "proxy" : request.nodeId(),
                now.getEpochSecond(),
                expiresAt.getEpochSecond(),
                List.copyOf(scopes)
        );
        return new IntegrationAuthResponse(tokenCodec.issue(claims), expiresAt.getEpochSecond());
    }

    public IntegrationTokenClaims requireClaims(HttpServletRequest request, IntegrationScope requiredScope) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
        }
        String token = authorization.substring("Bearer ".length()).trim();
        try {
            IntegrationTokenClaims claims = tokenCodec.verify(token);
            if (!claims.scopes().contains(requiredScope)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing integration scope");
            }
            return claims;
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage(), exception);
        }
    }

    private boolean constantTimeEquals(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8)
        );
    }
}
