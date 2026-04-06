package com.tjxjnoobie.store.persistence.service;

import com.tjxjnoobie.store.domain.model.PurchaseHistoryEntry;
import com.tjxjnoobie.store.domain.service.OrderService;
import com.tjxjnoobie.store.persistence.entity.StoreOrderEntity;
import com.tjxjnoobie.store.persistence.repository.StoreOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class JpaOrderService implements OrderService {

    private final StoreOrderRepository storeOrderRepository;

    public JpaOrderService(StoreOrderRepository storeOrderRepository) {
        this.storeOrderRepository = storeOrderRepository;
    }

    @Override
    public List<PurchaseHistoryEntry> getOrdersForPlayer(UUID playerUuid) {
        return storeOrderRepository.findByRecipientPlayerAccount_MinecraftUuidOrderByCreatedAtDesc(playerUuid.toString())
                .stream()
                .map(this::mapOrder)
                .toList();
    }

    @Override
    public List<PurchaseHistoryEntry> getRecentOrders() {
        return storeOrderRepository.findTop25ByOrderByCreatedAtDesc().stream()
                .map(this::mapOrder)
                .toList();
    }

    private PurchaseHistoryEntry mapOrder(StoreOrderEntity entity) {
        return new PurchaseHistoryEntry(
                entity.getId(),
                entity.getOrderNumber(),
                entity.getRecipientUsernameSnapshot(),
                entity.getState(),
                entity.getTotal(),
                entity.getPaidAt() != null ? entity.getPaidAt() : entity.getCreatedAt()
        );
    }
}
