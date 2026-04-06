package com.tjxjnoobie.store.domain.service;

import com.tjxjnoobie.store.domain.model.AdminRoleName;

import java.util.Collection;

public interface AdminAuthzService {
    boolean hasRole(Collection<String> authorities, AdminRoleName requiredRole);
}
