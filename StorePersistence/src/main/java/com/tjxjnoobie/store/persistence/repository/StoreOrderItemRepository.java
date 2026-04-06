package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.StoreOrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreOrderItemRepository extends JpaRepository<StoreOrderItemEntity, Long> {
    List<StoreOrderItemEntity> findByOrder_Id(Long orderId);
}
