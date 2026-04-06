package com.tjxjnoobie.website.security;

import com.tjxjnoobie.website.config.StoreApplicationProperties;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoreClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration> {

    private final Map<String, ClientRegistration> registrations = new LinkedHashMap<>();

    public StoreClientRegistrationRepository(StoreApplicationProperties properties) {
        maybeRegisterGoogle(properties);
        maybeRegisterDiscord(properties);
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return registrations.get(registrationId);
    }

    @Override
    public Iterator<ClientRegistration> iterator() {
        return new ArrayList<>(registrations.values()).iterator();
    }

    public List<ClientRegistration> getRegistrations() {
        return List.copyOf(registrations.values());
    }

    private void maybeRegisterGoogle(StoreApplicationProperties properties) {
        StoreApplicationProperties.Client google = properties.getOauth().getGoogle();
        if (isBlank(google.getClientId()) || isBlank(google.getClientSecret())) {
            return;
        }
        registrations.put("google", ClientRegistration.withRegistrationId("google")
                .clientId(google.getClientId())
                .clientSecret(google.getClientSecret())
                .scope("openid", "email", "profile")
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(Objects.requireNonNullElse(google.getRedirectUri(), "{baseUrl}/login/oauth2/code/{registrationId}"))
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .issuerUri("https://accounts.google.com")
                .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google")
                .build());
    }

    private void maybeRegisterDiscord(StoreApplicationProperties properties) {
        StoreApplicationProperties.Client discord = properties.getOauth().getDiscord();
        if (isBlank(discord.getClientId()) || isBlank(discord.getClientSecret())) {
            return;
        }
        registrations.put("discord", ClientRegistration.withRegistrationId("discord")
                .clientId(discord.getClientId())
                .clientSecret(discord.getClientSecret())
                .scope("identify", "email")
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(Objects.requireNonNullElse(discord.getRedirectUri(), "{baseUrl}/login/oauth2/code/{registrationId}"))
                .authorizationUri("https://discord.com/api/oauth2/authorize")
                .tokenUri("https://discord.com/api/oauth2/token")
                .userInfoUri("https://discord.com/api/users/@me")
                .userNameAttributeName("id")
                .clientName("Discord")
                .build());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
