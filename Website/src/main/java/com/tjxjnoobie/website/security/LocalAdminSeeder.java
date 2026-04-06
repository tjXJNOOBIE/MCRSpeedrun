package com.tjxjnoobie.website.security;

import com.tjxjnoobie.store.persistence.entity.AdminRoleEntity;
import com.tjxjnoobie.store.persistence.entity.AdminUserEntity;
import com.tjxjnoobie.store.persistence.repository.AdminRoleRepository;
import com.tjxjnoobie.store.persistence.repository.AdminUserRepository;
import com.tjxjnoobie.website.config.StoreApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;

@Component
public class LocalAdminSeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalAdminSeeder.class);

    private final StoreApplicationProperties properties;
    private final AdminUserRepository adminUserRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public LocalAdminSeeder(StoreApplicationProperties properties,
                            AdminUserRepository adminUserRepository,
                            AdminRoleRepository adminRoleRepository,
                            PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.adminUserRepository = adminUserRepository;
        this.adminRoleRepository = adminRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void ensureLocalAdmin() {
        if (!properties.getLocalAdmin().isEnabled()) {
            return;
        }

        AdminRoleEntity role = adminRoleRepository.findByName(properties.getLocalAdmin().getDefaultRole())
                .orElseGet(() -> {
                    AdminRoleEntity created = new AdminRoleEntity();
                    created.setName(properties.getLocalAdmin().getDefaultRole());
                    return adminRoleRepository.save(created);
                });

        AdminUserEntity user = adminUserRepository.findByUsernameIgnoreCase(properties.getLocalAdmin().getSeedUsername())
                .orElseGet(() -> adminUserRepository.findByEmailIgnoreCase(properties.getLocalAdmin().getSeedEmail()).orElse(null));
        if (user == null) {
            user = new AdminUserEntity();
            user.setUsername(properties.getLocalAdmin().getSeedUsername());
            user.setEmail(properties.getLocalAdmin().getSeedEmail());
            user.setBreakGlassEnabled(true);
            user.setEnabled(true);
            user.setPasswordHash(passwordEncoder.encode(properties.getLocalAdmin().getSeedPassword()));
            user.setRoles(new LinkedHashSet<>());
            user.getRoles().add(role);
            adminUserRepository.save(user);
            LOGGER.info("Seeded local break-glass admin user '{}'", user.getUsername());
            return;
        }

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
        }
        user.setBreakGlassEnabled(true);
        user.setEnabled(true);
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(properties.getLocalAdmin().getSeedPassword()));
        }
        adminUserRepository.save(user);
    }
}
