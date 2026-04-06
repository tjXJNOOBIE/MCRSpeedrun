package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.PlayerAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerAccountRepository extends JpaRepository<PlayerAccountEntity, Long> {
    Optional<PlayerAccountEntity> findByMinecraftUuid(String minecraftUuid);

    Optional<PlayerAccountEntity> findByCurrentUsernameIgnoreCase(String username);
}
