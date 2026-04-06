package com.tjxjnoobie.store.persistence.service;

import com.tjxjnoobie.store.domain.model.EntitlementState;
import com.tjxjnoobie.store.domain.model.EntitlementSummary;
import com.tjxjnoobie.store.domain.service.EntitlementService;
import com.tjxjnoobie.store.persistence.entity.EntitlementEntity;
import com.tjxjnoobie.store.persistence.repository.EntitlementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class JpaEntitlementService implements EntitlementService {

    private final EntitlementRepository entitlementRepository;

    public JpaEntitlementService(EntitlementRepository entitlementRepository) {
        this.entitlementRepository = entitlementRepository;
    }

    @Override
    public List<EntitlementSummary> getActiveEntitlements(UUID playerUuid) {
        return entitlementRepository.findByPlayerAccount_MinecraftUuidAndStateOrderByEffectiveAtDesc(
                        playerUuid.toString(),
                        EntitlementState.ACTIVE
                ).stream()
                .map(this::map)
                .toList();
    }

    private EntitlementSummary map(EntitlementEntity entity) {
        return new EntitlementSummary(
                entity.getId(),
                entity.getSourcePackageName(),
                entity.getBenefitType(),
                entity.getTargetKey(),
                entity.getTargetValue(),
                entity.getState(),
                entity.getEffectiveAt(),
                entity.getExpiresAt()
        );
    }
}
