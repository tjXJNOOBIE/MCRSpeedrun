package com.tjxjnoobie.website.security;

import com.tjxjnoobie.store.persistence.entity.AdminUserEntity;
import com.tjxjnoobie.store.persistence.repository.AdminUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StoreAdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    public StoreAdminUserDetailsService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUserEntity user = adminUserRepository.findByUsernameIgnoreCase(username)
                .or(() -> adminUserRepository.findByEmailIgnoreCase(username))
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found"));
        return StoreAdminPrincipal.from(user);
    }
}
