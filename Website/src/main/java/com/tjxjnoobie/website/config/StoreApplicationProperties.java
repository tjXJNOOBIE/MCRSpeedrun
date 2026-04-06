package com.tjxjnoobie.website.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.store")
public class StoreApplicationProperties {

    private String baseUrl;
    private String accountSessionCookie;
    private final Integration integration = new Integration();
    private final Stripe stripe = new Stripe();
    private final LocalAdmin localAdmin = new LocalAdmin();
    private final OAuth oauth = new OAuth();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAccountSessionCookie() {
        return accountSessionCookie;
    }

    public void setAccountSessionCookie(String accountSessionCookie) {
        this.accountSessionCookie = accountSessionCookie;
    }

    public Integration getIntegration() {
        return integration;
    }

    public Stripe getStripe() {
        return stripe;
    }

    public LocalAdmin getLocalAdmin() {
        return localAdmin;
    }

    public OAuth getOauth() {
        return oauth;
    }

    public static class Integration {
        private String bootstrapSecret;
        private String tokenSecret;
        private long tokenTtlSeconds = 900L;
        private boolean testModeEnabled;

        public String getBootstrapSecret() {
            return bootstrapSecret;
        }

        public void setBootstrapSecret(String bootstrapSecret) {
            this.bootstrapSecret = bootstrapSecret;
        }

        public String getTokenSecret() {
            return tokenSecret;
        }

        public void setTokenSecret(String tokenSecret) {
            this.tokenSecret = tokenSecret;
        }

        public long getTokenTtlSeconds() {
            return tokenTtlSeconds;
        }

        public void setTokenTtlSeconds(long tokenTtlSeconds) {
            this.tokenTtlSeconds = tokenTtlSeconds;
        }

        public boolean isTestModeEnabled() {
            return testModeEnabled;
        }

        public void setTestModeEnabled(boolean testModeEnabled) {
            this.testModeEnabled = testModeEnabled;
        }
    }

    public static class Stripe {
        private String secretKey;
        private String publishableKey;
        private String webhookSecret;
        private String successPath;
        private String cancelPath;

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getPublishableKey() {
            return publishableKey;
        }

        public void setPublishableKey(String publishableKey) {
            this.publishableKey = publishableKey;
        }

        public String getWebhookSecret() {
            return webhookSecret;
        }

        public void setWebhookSecret(String webhookSecret) {
            this.webhookSecret = webhookSecret;
        }

        public String getSuccessPath() {
            return successPath;
        }

        public void setSuccessPath(String successPath) {
            this.successPath = successPath;
        }

        public String getCancelPath() {
            return cancelPath;
        }

        public void setCancelPath(String cancelPath) {
            this.cancelPath = cancelPath;
        }
    }

    public static class LocalAdmin {
        private boolean enabled;
        private String defaultRole;
        private String seedUsername;
        private String seedEmail;
        private String seedPassword;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getDefaultRole() {
            return defaultRole;
        }

        public void setDefaultRole(String defaultRole) {
            this.defaultRole = defaultRole;
        }

        public String getSeedUsername() {
            return seedUsername;
        }

        public void setSeedUsername(String seedUsername) {
            this.seedUsername = seedUsername;
        }

        public String getSeedEmail() {
            return seedEmail;
        }

        public void setSeedEmail(String seedEmail) {
            this.seedEmail = seedEmail;
        }

        public String getSeedPassword() {
            return seedPassword;
        }

        public void setSeedPassword(String seedPassword) {
            this.seedPassword = seedPassword;
        }
    }

    public static class OAuth {
        private final Client google = new Client();
        private final Client discord = new Client();

        public Client getGoogle() {
            return google;
        }

        public Client getDiscord() {
            return discord;
        }
    }

    public static class Client {
        private String clientId;
        private String clientSecret;
        private String redirectUri;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }
    }
}
