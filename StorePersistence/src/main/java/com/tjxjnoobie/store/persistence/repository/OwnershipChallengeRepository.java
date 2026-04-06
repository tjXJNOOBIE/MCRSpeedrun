package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.domain.model.OwnershipChallengeStatus;
import com.tjxjnoobie.store.persistence.entity.OwnershipChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface OwnershipChallengeRepository extends JpaRepository<OwnershipChallengeEntity, Long> {
    Optional<OwnershipChallengeEntity> findByCodeAndStatusAndExpiresAtAfter(String code,
                                                                            OwnershipChallengeStatus status,
                                                                            Instant expiresAt);
}
