package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.AdminUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUserEntity, Long> {
    Optional<AdminUserEntity> findByUsernameIgnoreCase(String username);

    Optional<AdminUserEntity> findByEmailIgnoreCase(String email);
}
