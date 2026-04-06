package com.tjxjnoobie.website.security;

import com.tjxjnoobie.store.persistence.entity.AdminRoleEntity;
import com.tjxjnoobie.store.persistence.entity.AdminUserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class StoreAdminPrincipal implements UserDetails, OAuth2User, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long adminUserId;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public StoreAdminPrincipal(Long adminUserId,
                               String username,
                               String email,
                               String passwordHash,
                               boolean enabled,
                               Set<GrantedAuthority> authorities,
                               Map<String, Object> attributes) {
        this.adminUserId = adminUserId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    public static StoreAdminPrincipal from(AdminUserEntity entity) {
        return new StoreAdminPrincipal(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.isEnabled(),
                toAuthorities(entity.getRoles()),
                Map.of()
        );
    }

    public static StoreAdminPrincipal from(AdminUserEntity entity, Map<String, Object> attributes) {
        return new StoreAdminPrincipal(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.isEnabled(),
                toAuthorities(entity.getRoles()),
                attributes == null ? Map.of() : Map.copyOf(attributes)
        );
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return email != null && !email.isBlank() ? email : username;
    }

    private static Set<GrantedAuthority> toAuthorities(Set<AdminRoleEntity> roles) {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        for (AdminRoleEntity role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        return authorities;
    }
}
