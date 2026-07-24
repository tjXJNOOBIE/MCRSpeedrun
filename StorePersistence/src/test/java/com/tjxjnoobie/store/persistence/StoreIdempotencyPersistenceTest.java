package com.tjxjnoobie.store.persistence;

import com.tjxjnoobie.store.domain.model.AccountState;
import com.tjxjnoobie.store.domain.model.BenefitType;
import com.tjxjnoobie.store.domain.model.EntitlementState;
import com.tjxjnoobie.store.domain.model.FulfillmentStatus;
import com.tjxjnoobie.store.domain.model.OrderState;
import com.tjxjnoobie.store.domain.model.PaymentProvider;
import com.tjxjnoobie.store.domain.model.PaymentStatus;
import com.tjxjnoobie.store.persistence.entity.EntitlementEntity;
import com.tjxjnoobie.store.persistence.entity.FulfillmentJobEntity;
import com.tjxjnoobie.store.persistence.entity.PackageBenefitEntity;
import com.tjxjnoobie.store.persistence.entity.PlayerAccountEntity;
import com.tjxjnoobie.store.persistence.entity.StoreOrderEntity;
import com.tjxjnoobie.store.persistence.entity.StoreOrderItemEntity;
import com.tjxjnoobie.store.persistence.entity.StorePackageEntity;
import com.tjxjnoobie.store.persistence.entity.PaymentTransactionEntity;
import com.tjxjnoobie.store.persistence.repository.EntitlementRepository;
import com.tjxjnoobie.store.persistence.repository.FulfillmentJobRepository;
import com.tjxjnoobie.store.persistence.repository.PackageBenefitRepository;
import com.tjxjnoobie.store.persistence.repository.PlayerAccountRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderItemRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderRepository;
import com.tjxjnoobie.store.persistence.repository.StorePackageRepository;
import com.tjxjnoobie.store.persistence.repository.PaymentTransactionRepository;
import com.tjxjnoobie.store.persistence.service.JpaFulfillmentOrchestrator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StoreIdempotencyPersistenceTest extends StorePersistenceIntegrationTestSupport {

    @Autowired
    private PlayerAccountRepository playerAccountRepository;

    @Autowired
    private StoreOrderRepository storeOrderRepository;

    @Autowired
    private StoreOrderItemRepository storeOrderItemRepository;

    @Autowired
    private EntitlementRepository entitlementRepository;

    @Autowired
    private FulfillmentJobRepository fulfillmentJobRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private StorePackageRepository storePackageRepository;

    @Autowired
    private PackageBenefitRepository packageBenefitRepository;

    @Autowired
    private JpaFulfillmentOrchestrator fulfillmentOrchestrator;

    @Test
    void enforcesUniqueOrderNumbers() {
        PlayerAccountEntity player = savePlayer("persist-order");

        StoreOrderEntity first = saveOrder("NOV-PERSIST-001", player);
        StoreOrderEntity duplicate = new StoreOrderEntity();
        duplicate.setOrderNumber(first.getOrderNumber());
        duplicate.setPurchaserPlayerAccount(player);
        duplicate.setRecipientPlayerAccount(player);
        duplicate.setPurchaserUsernameSnapshot(player.getCurrentUsername());
        duplicate.setRecipientUsernameSnapshot(player.getCurrentUsername());
        duplicate.setState(OrderState.PENDING_PAYMENT);
        duplicate.setSubtotal(new BigDecimal("10.00"));
        duplicate.setDiscountTotal(BigDecimal.ZERO);
        duplicate.setTotal(new BigDecimal("10.00"));
        duplicate.setCurrency("USD");
        duplicate.setPaymentProvider(PaymentProvider.STRIPE);

        assertThrows(DataIntegrityViolationException.class, () -> storeOrderRepository.saveAndFlush(duplicate));
    }

    @Test
    void enforcesUniquePaymentProviderReferencePerProvider() {
        PlayerAccountEntity player = savePlayer("persist-payment");
        StoreOrderEntity order = saveOrder("NOV-PERSIST-002", player);

        PaymentTransactionEntity first = new PaymentTransactionEntity();
        first.setOrder(order);
        first.setProvider(PaymentProvider.STRIPE);
        first.setStatus(PaymentStatus.PENDING);
        first.setProviderReference("pi_shared_reference");
        first.setAmount(order.getTotal());
        first.setCurrency("USD");
        first.setPayloadJson("{}");
        paymentTransactionRepository.saveAndFlush(first);

        PaymentTransactionEntity duplicate = new PaymentTransactionEntity();
        duplicate.setOrder(order);
        duplicate.setProvider(PaymentProvider.STRIPE);
        duplicate.setStatus(PaymentStatus.PENDING);
        duplicate.setProviderReference("pi_shared_reference");
        duplicate.setAmount(order.getTotal());
        duplicate.setCurrency("USD");
        duplicate.setPayloadJson("{}");

        assertThrows(DataIntegrityViolationException.class, () -> paymentTransactionRepository.saveAndFlush(duplicate));
    }

    @Test
    void enforcesUniqueEntitlementPerOrderItemBenefitCombination() {
        PlayerAccountEntity player = savePlayer("persist-entitlement");
        StoreOrderEntity order = saveOrder("NOV-PERSIST-003", player);
        StoreOrderItemEntity orderItem = saveOrderItem(order);

        EntitlementEntity first = saveEntitlement(player, orderItem, "God");
        EntitlementEntity duplicate = new EntitlementEntity();
        duplicate.setPlayerAccount(player);
        duplicate.setOrderItem(orderItem);
        duplicate.setBenefitType(first.getBenefitType());
        duplicate.setTargetSystem(first.getTargetSystem());
        duplicate.setTargetKey(first.getTargetKey());
        duplicate.setTargetValue(first.getTargetValue());
        duplicate.setState(EntitlementState.ACTIVE);
        duplicate.setEffectiveAt(Instant.now());
        duplicate.setSourcePackageName(first.getSourcePackageName());

        assertThrows(DataIntegrityViolationException.class, () -> entitlementRepository.saveAndFlush(duplicate));
    }

    @Test
    void markFailedOnlyTransitionsOncePerAttemptWindow() {
        PlayerAccountEntity player = savePlayer("persist-retry");
        StoreOrderEntity order = saveOrder("NOV-PERSIST-004", player);
        StoreOrderItemEntity orderItem = saveOrderItem(order);
        EntitlementEntity entitlement = saveEntitlement(player, orderItem, "God");
        FulfillmentJobEntity job = saveJob(entitlement);

        fulfillmentOrchestrator.markFailed(job.getId(), "corr-1", "temporary failure", true);
        fulfillmentOrchestrator.markFailed(job.getId(), "corr-1", "temporary failure", true);

        FulfillmentJobEntity updated = fulfillmentJobRepository.findById(job.getId()).orElseThrow();
        assertEquals(FulfillmentStatus.RETRY_READY, updated.getStatus());
        assertEquals(1, updated.getRetryCount());
        assertEquals("temporary failure", updated.getLastError());
        assertTrue(updated.getNextAttemptAt().isAfter(Instant.now()));
    }

    @Test
    void retryRequeuesJobAndRevokedEntitlementsMapToRevokeOperations() {
        PlayerAccountEntity player = savePlayer("persist-revoke");
        StoreOrderEntity order = saveOrder("NOV-PERSIST-005", player);
        StoreOrderItemEntity orderItem = saveOrderItem(order);
        EntitlementEntity entitlement = saveEntitlement(player, orderItem, "God");
        FulfillmentJobEntity job = saveJob(entitlement);

        fulfillmentOrchestrator.markFailed(job.getId(), "corr-2", "temporary failure", true);
        fulfillmentOrchestrator.retry(job.getId(), "tester");

        FulfillmentJobEntity retried = fulfillmentJobRepository.findById(job.getId()).orElseThrow();
        assertEquals(FulfillmentStatus.RETRY_READY, retried.getStatus());
        assertTrue(!retried.getNextAttemptAt().isAfter(Instant.now().plusSeconds(1)));

        entitlement.setState(EntitlementState.REVOKED);
        entitlement.setRevokedAt(Instant.now());
        entitlementRepository.saveAndFlush(entitlement);

        String operation = fulfillmentOrchestrator.getPendingJobs(10).stream()
                .filter(dispatch -> dispatch.jobId().equals(job.getId()))
                .findFirst()
                .orElseThrow()
                .operation();
        assertEquals("REVOKE", operation);
    }

    private PlayerAccountEntity savePlayer(String suffix) {
        PlayerAccountEntity entity = new PlayerAccountEntity();
        entity.setMinecraftUuid(UUID.randomUUID().toString());
        entity.setCurrentUsername(("player_" + suffix.substring(0, Math.min(2, suffix.length()))
                + UUID.randomUUID().toString().replace("-", "")).substring(0, 16));
        entity.setAccountState(AccountState.ACTIVE);
        entity.setLinkedIdentitiesJson("{}");
        return playerAccountRepository.saveAndFlush(entity);
    }

    private StoreOrderEntity saveOrder(String orderNumber, PlayerAccountEntity player) {
        StoreOrderEntity order = new StoreOrderEntity();
        order.setOrderNumber(orderNumber);
        order.setPurchaserPlayerAccount(player);
        order.setRecipientPlayerAccount(player);
        order.setPurchaserUsernameSnapshot(player.getCurrentUsername());
        order.setRecipientUsernameSnapshot(player.getCurrentUsername());
        order.setState(OrderState.PENDING_PAYMENT);
        order.setSubtotal(new BigDecimal("149.99"));
        order.setDiscountTotal(BigDecimal.ZERO);
        order.setTotal(new BigDecimal("149.99"));
        order.setCurrency("USD");
        order.setPaymentProvider(PaymentProvider.STRIPE);
        return storeOrderRepository.saveAndFlush(order);
    }

    private StoreOrderItemEntity saveOrderItem(StoreOrderEntity order) {
        StorePackageEntity storePackage = storePackageRepository.findBySlug("god-rank-lifetime").orElseThrow();
        PackageBenefitEntity benefit = packageBenefitRepository.findByStorePackage_Id(storePackage.getId()).stream().findFirst().orElseThrow();

        StoreOrderItemEntity orderItem = new StoreOrderItemEntity();
        orderItem.setOrder(order);
        orderItem.setStorePackage(storePackage);
        orderItem.setPackageSlugSnapshot(storePackage.getSlug());
        orderItem.setPackageNameSnapshot(storePackage.getName());
        orderItem.setQuantity(1);
        orderItem.setUnitPrice(storePackage.getPrice());
        orderItem.setTotalPrice(storePackage.getPrice());
        orderItem.setBenefitSnapshotJson("[{\"benefitType\":\"" + benefit.getBenefitType().name() + "\"}]");
        return storeOrderItemRepository.saveAndFlush(orderItem);
    }

    private EntitlementEntity saveEntitlement(PlayerAccountEntity player, StoreOrderItemEntity orderItem, String targetValue) {
        EntitlementEntity entitlement = new EntitlementEntity();
        entitlement.setPlayerAccount(player);
        entitlement.setOrderItem(orderItem);
        entitlement.setBenefitType(BenefitType.RANK);
        entitlement.setTargetSystem("VELOCITY");
        entitlement.setTargetKey("rank");
        entitlement.setTargetValue(targetValue);
        entitlement.setState(EntitlementState.ACTIVE);
        entitlement.setEffectiveAt(Instant.now());
        entitlement.setSourcePackageName(orderItem.getPackageNameSnapshot());
        return entitlementRepository.saveAndFlush(entitlement);
    }

    private FulfillmentJobEntity saveJob(EntitlementEntity entitlement) {
        FulfillmentJobEntity job = new FulfillmentJobEntity();
        job.setEntitlement(entitlement);
        job.setTargetSystem(entitlement.getTargetSystem());
        job.setStatus(FulfillmentStatus.PENDING);
        job.setRetryCount(0);
        job.setIdempotencyKey(UUID.randomUUID().toString());
        job.setCorrelationId(UUID.randomUUID().toString());
        job.setNextAttemptAt(Instant.now().minusSeconds(5));
        return fulfillmentJobRepository.saveAndFlush(job);
    }
}
