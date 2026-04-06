package com.tjxjnoobie.proxy.store;

import java.time.Duration;

public class StoreIntegrationConfig {

    private final String baseUrl;
    private final String bootstrapSecret;
    private final String nodeId;
    private final Duration pollInterval;
    private final Duration requestTimeout;
    private final int pendingJobLimit;
    private final String fallbackRank;

    public StoreIntegrationConfig(String baseUrl,
                                  String bootstrapSecret,
                                  String nodeId,
                                  Duration pollInterval,
                                  Duration requestTimeout,
                                  int pendingJobLimit,
                                  String fallbackRank) {
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.bootstrapSecret = bootstrapSecret;
        this.nodeId = nodeId;
        this.pollInterval = pollInterval;
        this.requestTimeout = requestTimeout;
        this.pendingJobLimit = pendingJobLimit;
        this.fallbackRank = fallbackRank == null || fallbackRank.isBlank() ? "Member" : fallbackRank.trim();
    }

    public static StoreIntegrationConfig fromEnvironment() {
        String baseUrl = resolve("NOVUS_STORE_BASE_URL", "novus.store.base-url", "");
        String bootstrapSecret = resolve("NOVUS_STORE_BOOTSTRAP_SECRET", "novus.store.bootstrap-secret", "");
        String nodeId = resolve("NOVUS_STORE_NODE_ID", "novus.store.node-id", "velocity-proxy");
        long pollIntervalSeconds = parseLong(resolve("NOVUS_STORE_POLL_INTERVAL_SECONDS",
                "novus.store.poll-interval-seconds", "15"), 15L);
        long requestTimeoutSeconds = parseLong(resolve("NOVUS_STORE_REQUEST_TIMEOUT_SECONDS",
                "novus.store.request-timeout-seconds", "10"), 10L);
        int pendingJobLimit = (int) parseLong(resolve("NOVUS_STORE_PENDING_JOB_LIMIT",
                "novus.store.pending-job-limit", "25"), 25L);
        String fallbackRank = resolve("NOVUS_STORE_FALLBACK_RANK", "novus.store.fallback-rank", "Member");
        return new StoreIntegrationConfig(
                baseUrl,
                bootstrapSecret,
                nodeId,
                Duration.ofSeconds(Math.max(5L, pollIntervalSeconds)),
                Duration.ofSeconds(Math.max(5L, requestTimeoutSeconds)),
                Math.max(1, pendingJobLimit),
                fallbackRank
        );
    }

    public boolean isEnabled() {
        return !baseUrl.isBlank() && !bootstrapSecret.isBlank();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getBootstrapSecret() {
        return bootstrapSecret;
    }

    public String getNodeId() {
        return nodeId;
    }

    public Duration getPollInterval() {
        return pollInterval;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public int getPendingJobLimit() {
        return pendingJobLimit;
    }

    public String getFallbackRank() {
        return fallbackRank;
    }

    private static String resolve(String envKey, String propertyKey, String defaultValue) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }
        String propertyValue = System.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }
        return defaultValue;
    }

    private static long parseLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value.trim());
        } catch (RuntimeException ignored) {
            return defaultValue;
        }
    }

    private static String trimTrailingSlash(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
