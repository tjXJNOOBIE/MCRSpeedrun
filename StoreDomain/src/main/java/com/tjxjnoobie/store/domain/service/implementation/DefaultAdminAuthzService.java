package com.tjxjnoobie.store.domain.service.implementation;

import com.tjxjnoobie.store.domain.model.AdminRoleName;
import com.tjxjnoobie.store.domain.service.AdminAuthzService;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class DefaultAdminAuthzService implements AdminAuthzService {

    @Override
    public boolean hasRole(Collection<String> authorities, AdminRoleName requiredRole) {
        return authorities.stream().anyMatch(authority ->
                authority.equals(requiredRole.name()) || authority.equals("ROLE_" + requiredRole.name()));
    }
}
