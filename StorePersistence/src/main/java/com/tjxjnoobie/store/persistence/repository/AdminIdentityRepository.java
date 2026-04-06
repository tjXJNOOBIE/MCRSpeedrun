package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.AdminIdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminIdentityRepository extends JpaRepository<AdminIdentityEntity, Long> {
    Optional<AdminIdentityEntity> findByProviderAndProviderUserId(String provider, String providerUserId);

    Optional<AdminIdentityEntity> findByEmailIgnoreCase(String email);
}
