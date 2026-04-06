package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.PaymentTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, Long> {
    Optional<PaymentTransactionEntity> findByProviderEventId(String providerEventId);

    Optional<PaymentTransactionEntity> findByProviderReference(String providerReference);

    List<PaymentTransactionEntity> findByOrder_IdOrderByProcessedAtAsc(Long orderId);
}
