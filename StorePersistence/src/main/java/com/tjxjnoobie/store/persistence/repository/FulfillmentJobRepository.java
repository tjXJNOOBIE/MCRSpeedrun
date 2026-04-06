package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.domain.model.FulfillmentStatus;
import com.tjxjnoobie.store.persistence.entity.FulfillmentJobEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FulfillmentJobRepository extends JpaRepository<FulfillmentJobEntity, Long> {
    List<FulfillmentJobEntity> findTop50ByStatusInAndNextAttemptAtBeforeOrderByCreatedAtAsc(
            Collection<FulfillmentStatus> statuses,
            Instant nextAttemptAt
    );

    List<FulfillmentJobEntity> findByEntitlement_IdOrderByCreatedAtDesc(Long entitlementId);

    Optional<FulfillmentJobEntity> findFirstByEntitlement_IdOrderByCreatedAtDesc(Long entitlementId);

    @Query("""
            select job
            from FulfillmentJobEntity job
            join fetch job.entitlement entitlement
            join fetch entitlement.playerAccount
            order by job.createdAt desc
            """)
    List<FulfillmentJobEntity> findRecentForAdmin(Pageable pageable);
}
