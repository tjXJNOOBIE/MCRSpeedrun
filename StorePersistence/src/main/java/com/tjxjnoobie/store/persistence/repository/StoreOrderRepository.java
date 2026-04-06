package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.StoreOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreOrderRepository extends JpaRepository<StoreOrderEntity, Long> {
    List<StoreOrderEntity> findTop25ByOrderByCreatedAtDesc();

    List<StoreOrderEntity> findByRecipientPlayerAccount_MinecraftUuidOrderByCreatedAtDesc(String minecraftUuid);

    Optional<StoreOrderEntity> findByOrderNumber(String orderNumber);

    Optional<StoreOrderEntity> findByStripeCheckoutSessionId(String checkoutSessionId);
}
