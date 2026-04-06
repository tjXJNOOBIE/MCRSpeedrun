package com.tjxjnoobie.proxy.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.tjxjnoobie.store.integration.dto.FulfillmentCompleteRequest;
import com.tjxjnoobie.store.integration.dto.FulfillmentFailRequest;
import com.tjxjnoobie.store.integration.dto.IntegrationAuthRequest;
import com.tjxjnoobie.store.integration.dto.IntegrationAuthResponse;
import com.tjxjnoobie.store.integration.dto.OwnershipVerificationRequest;
import com.tjxjnoobie.store.integration.dto.OwnershipVerificationResult;
import com.tjxjnoobie.store.integration.dto.PendingFulfillmentJobView;
import com.tjxjnoobie.store.integration.dto.PlayerEntitlementView;
import com.tjxjnoobie.store.integration.dto.PlayerOrderView;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class StoreBackendClient {

    private final StoreIntegrationConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private volatile String accessToken;
    private volatile Instant accessTokenExpiry = Instant.EPOCH;

    public StoreBackendClient(StoreIntegrationConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(config.getRequestTimeout())
                .build();
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public boolean isEnabled() {
        return config.isEnabled();
    }

    public List<PendingFulfillmentJobView> fetchPendingJobs() {
        CollectionType type = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, PendingFulfillmentJobView.class);
        return getAuthorizedList("/api/v1/integration/fulfillment/pending?limit=" + config.getPendingJobLimit(), type);
    }

    public void completeJob(long jobId, String correlationId, String appliedValue, String message) {
        sendAuthorized(
                "POST",
                "/api/v1/integration/fulfillment/" + jobId + "/complete",
                new FulfillmentCompleteRequest(correlationId, appliedValue, message)
        );
    }

    public void failJob(long jobId, String correlationId, String error, boolean retryable) {
        sendAuthorized(
                "POST",
                "/api/v1/integration/fulfillment/" + jobId + "/fail",
                new FulfillmentFailRequest(correlationId, error, retryable)
        );
    }

    public OwnershipVerificationResult verifyOwnership(String code, UUID playerUuid, String playerUsername) {
        return sendAuthorized(
                "POST",
                "/api/v1/integration/ownership/verify",
                new OwnershipVerificationRequest(code, playerUuid.toString(), playerUsername),
                OwnershipVerificationResult.class
        );
    }

    public List<PlayerOrderView> getOrders(UUID playerUuid) {
        CollectionType type = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, PlayerOrderView.class);
        return getAuthorizedList("/api/v1/integration/players/" + playerUuid + "/orders", type);
    }

    public List<PlayerEntitlementView> getEntitlements(UUID playerUuid) {
        CollectionType type = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, PlayerEntitlementView.class);
        return getAuthorizedList("/api/v1/integration/players/" + playerUuid + "/entitlements", type);
    }

    private synchronized String getAccessToken() {
        if (!config.isEnabled()) {
            throw new StoreBackendException("Store integration is not configured on this proxy.", false);
        }

        Instant now = Instant.now();
        if (accessToken != null && accessTokenExpiry.isAfter(now.plusSeconds(30))) {
            return accessToken;
        }

        IntegrationAuthResponse response = sendAnonymous(
                "POST",
                "/api/v1/integration/auth/token",
                new IntegrationAuthRequest(config.getNodeId(), config.getBootstrapSecret()),
                IntegrationAuthResponse.class
        );
        this.accessToken = response.accessToken();
        this.accessTokenExpiry = Instant.ofEpochSecond(response.expiresAtEpochSecond());
        return accessToken;
    }

    private <T> List<T> getAuthorizedList(String path, CollectionType type) {
        String body = sendAuthorizedForBody("GET", path, null);
        try {
            return objectMapper.readValue(body, type);
        } catch (IOException exception) {
            throw new StoreBackendException("Unable to decode backend list response.", true, exception);
        }
    }

    private <T> T sendAuthorized(String method, String path, Object payload, Class<T> responseType) {
        String body = sendAuthorizedForBody(method, path, payload);
        if (responseType == null || responseType == Void.class || body == null || body.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(body, responseType);
        } catch (IOException exception) {
            throw new StoreBackendException("Unable to decode backend response.", true, exception);
        }
    }

    private void sendAuthorized(String method, String path, Object payload) {
        sendAuthorized(method, path, payload, Void.class);
    }

    private String sendAuthorizedForBody(String method, String path, Object payload) {
        String token = getAccessToken();
        try {
            return send(method, path, payload, token);
        } catch (StoreBackendException exception) {
            if (exception.isRetryable()) {
                throw exception;
            }
            if (exception.getMessage().contains("401")) {
                synchronized (this) {
                    accessToken = null;
                    accessTokenExpiry = Instant.EPOCH;
                }
                return send(method, path, payload, getAccessToken());
            }
            throw exception;
        }
    }

    private <T> T sendAnonymous(String method, String path, Object payload, Class<T> responseType) {
        String body = send(method, path, payload, null);
        if (responseType == null || responseType == Void.class || body == null || body.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(body, responseType);
        } catch (IOException exception) {
            throw new StoreBackendException("Unable to decode backend response.", true, exception);
        }
    }

    private String send(String method, String path, Object payload, String bearerToken) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(config.getBaseUrl() + path))
                .timeout(config.getRequestTimeout())
                .header("Accept", "application/json");

        if (bearerToken != null && !bearerToken.isBlank()) {
            builder.header("Authorization", "Bearer " + bearerToken);
        }

        if (payload != null) {
            builder.header("Content-Type", "application/json");
            builder.method(method, HttpRequest.BodyPublishers.ofString(writeJson(payload)));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        try {
            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status >= 200 && status < 300) {
                return response.body();
            }

            boolean retryable = status >= 500 || status == 408 || status == 429;
            throw new StoreBackendException(
                    "Backend request failed with HTTP " + status + ": " + response.body(),
                    retryable
            );
        } catch (IOException exception) {
            throw new StoreBackendException("Store backend request failed: " + exception.getMessage(), true, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new StoreBackendException("Store backend request interrupted.", true, exception);
        }
    }

    private String writeJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new StoreBackendException("Unable to serialize backend request.", false, exception);
        }
    }
}
