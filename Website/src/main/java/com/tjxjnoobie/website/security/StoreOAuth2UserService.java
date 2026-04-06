package com.tjxjnoobie.website.security;

import com.tjxjnoobie.store.persistence.entity.AdminIdentityEntity;
import com.tjxjnoobie.store.persistence.entity.AdminUserEntity;
import com.tjxjnoobie.store.persistence.repository.AdminIdentityRepository;
import com.tjxjnoobie.store.persistence.repository.AdminUserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
public class StoreOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final AdminIdentityRepository adminIdentityRepository;
    private final AdminUserRepository adminUserRepository;

    public StoreOAuth2UserService(AdminIdentityRepository adminIdentityRepository,
                                  AdminUserRepository adminUserRepository) {
        this.adminIdentityRepository = adminIdentityRepository;
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = delegate.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId().toLowerCase(Locale.ROOT);
        String userNameAttribute = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        String providerUserId = Objects.toString(oauthUser.getAttribute(userNameAttribute), null);
        String email = normalizeEmail(Objects.toString(oauthUser.getAttribute("email"), null));
        if (providerUserId == null || email == null) {
            throw unauthorized("OAuth identity is missing required email or subject attributes");
        }

        Optional<AdminIdentityEntity> providerIdentity = adminIdentityRepository.findByProviderAndProviderUserId(provider, providerUserId);
        AdminUserEntity adminUser = providerIdentity
                .map(AdminIdentityEntity::getAdminUser)
                .or(() -> adminUserRepository.findByEmailIgnoreCase(email))
                .orElseThrow(() -> unauthorized("OAuth identity is not authorized for admin access"));

        if (!adminUser.isEnabled()) {
            throw unauthorized("Admin account is disabled");
        }

        if (providerIdentity.isEmpty()) {
            AdminIdentityEntity identity = new AdminIdentityEntity();
            identity.setAdminUser(adminUser);
            identity.setProvider(provider);
            identity.setProviderUserId(providerUserId);
            identity.setEmail(email);
            adminIdentityRepository.save(identity);
        }

        adminUser.setLastLoginAt(Instant.now());
        adminUserRepository.save(adminUser);
        return StoreAdminPrincipal.from(adminUser, oauthUser.getAttributes());
    }

    private OAuth2AuthenticationException unauthorized(String message) {
        return new OAuth2AuthenticationException(new OAuth2Error("unauthorized_admin", message, null));
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
