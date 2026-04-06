package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.domain.model.EntitlementState;
import com.tjxjnoobie.store.domain.model.BenefitType;
import com.tjxjnoobie.store.persistence.entity.EntitlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntitlementRepository extends JpaRepository<EntitlementEntity, Long> {
    List<EntitlementEntity> findByPlayerAccount_MinecraftUuidAndStateOrderByEffectiveAtDesc(String minecraftUuid,
                                                                                             EntitlementState state);

    List<EntitlementEntity> findByPlayerAccount_MinecraftUuidAndBenefitTypeAndTargetSystemAndTargetKeyAndState(
            String minecraftUuid,
            BenefitType benefitType,
            String targetSystem,
            String targetKey,
            EntitlementState state
    );

    List<EntitlementEntity> findByOrderItem_Order_Id(Long orderId);

    Optional<EntitlementEntity> findByOrderItem_IdAndBenefitTypeAndTargetSystemAndTargetKeyAndTargetValue(
            Long orderItemId,
            BenefitType benefitType,
            String targetSystem,
            String targetKey,
            String targetValue
    );
}
