package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.CheckoutSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckoutSessionRepository extends JpaRepository<CheckoutSessionEntity, Long> {
    Optional<CheckoutSessionEntity> findBySessionToken(String sessionToken);

    Optional<CheckoutSessionEntity> findByOrderNumber(String orderNumber);

    Optional<CheckoutSessionEntity> findByStripeCheckoutSessionId(String stripeCheckoutSessionId);
}
