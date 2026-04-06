package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.AdminRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRoleRepository extends JpaRepository<AdminRoleEntity, Long> {
    Optional<AdminRoleEntity> findByName(String name);
}
