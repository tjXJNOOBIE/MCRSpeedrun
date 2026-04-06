package com.tjxjnoobie.website;

import com.tjxjnoobie.store.domain.model.AccountState;
import com.tjxjnoobie.store.domain.model.BenefitType;
import com.tjxjnoobie.store.domain.model.EntitlementState;
import com.tjxjnoobie.store.domain.model.FulfillmentStatus;
import com.tjxjnoobie.store.domain.model.OrderState;
import com.tjxjnoobie.store.domain.model.PaymentProvider;
import com.tjxjnoobie.store.integration.auth.IntegrationScope;
import com.tjxjnoobie.store.persistence.entity.EntitlementEntity;
import com.tjxjnoobie.store.persistence.entity.FulfillmentJobEntity;
import com.tjxjnoobie.store.persistence.entity.PlayerAccountEntity;
import com.tjxjnoobie.store.persistence.entity.StoreOrderEntity;
import com.tjxjnoobie.store.persistence.entity.StoreOrderItemEntity;
import com.tjxjnoobie.store.persistence.entity.StorePackageEntity;
import com.tjxjnoobie.store.persistence.repository.EntitlementRepository;
import com.tjxjnoobie.store.persistence.repository.FulfillmentJobRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderItemRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderRepository;
import com.tjxjnoobie.store.persistence.repository.StorePackageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IntegrationApiControllerIntegrationTest extends WebsiteIntegrationTestSupport {

    @Autowired
    private StoreOrderRepository storeOrderRepository;

    @Autowired
    private StoreOrderItemRepository storeOrderItemRepository;

    @Autowired
    private StorePackageRepository storePackageRepository;

    @Autowired
    private EntitlementRepository entitlementRepository;

    @Autowired
    private FulfillmentJobRepository fulfillmentJobRepository;

    @Test
    void requiresBearerTokenAndScopesForPendingFulfillment() throws Exception {
        PlayerAccountEntity player = new PlayerAccountEntity();
        player.setMinecraftUuid(UUID.randomUUID().toString());
        player.setCurrentUsername("scopeuser01");
        player.setAccountState(AccountState.ACTIVE);
        player.setLinkedIdentitiesJson("{}");
        player = playerAccountRepository.saveAndFlush(player);

        StoreOrderEntity order = new StoreOrderEntity();
        order.setOrderNumber("NOV-SCOPE-001");
        order.setPurchaserPlayerAccount(player);
        order.setRecipientPlayerAccount(player);
        order.setPurchaserUsernameSnapshot(player.getCurrentUsername());
        order.setRecipientUsernameSnapshot(player.getCurrentUsername());
        order.setState(OrderState.PAID);
        order.setSubtotal(new BigDecimal("149.99"));
        order.setDiscountTotal(BigDecimal.ZERO);
        order.setTotal(new BigDecimal("149.99"));
        order.setCurrency("USD");
        order.setPaymentProvider(PaymentProvider.STRIPE);
        order = storeOrderRepository.saveAndFlush(order);

        StorePackageEntity storePackage = storePackageRepository.findBySlug("god-rank-lifetime").orElseThrow();
        StoreOrderItemEntity item = new StoreOrderItemEntity();
        item.setOrder(order);
        item.setStorePackage(storePackage);
        item.setPackageSlugSnapshot(storePackage.getSlug());
        item.setPackageNameSnapshot(storePackage.getName());
        item.setQuantity(1);
        item.setUnitPrice(storePackage.getPrice());
        item.setTotalPrice(storePackage.getPrice());
        item.setBenefitSnapshotJson("[]");
        item = storeOrderItemRepository.saveAndFlush(item);

        EntitlementEntity entitlement = new EntitlementEntity();
        entitlement.setPlayerAccount(player);
        entitlement.setOrderItem(item);
        entitlement.setBenefitType(BenefitType.RANK);
        entitlement.setTargetSystem("VELOCITY");
        entitlement.setTargetKey("rank");
        entitlement.setTargetValue("God");
        entitlement.setState(EntitlementState.ACTIVE);
        entitlement.setEffectiveAt(Instant.now());
        entitlement.setSourcePackageName(storePackage.getName());
        entitlement = entitlementRepository.saveAndFlush(entitlement);

        FulfillmentJobEntity job = new FulfillmentJobEntity();
        job.setEntitlement(entitlement);
        job.setTargetSystem("VELOCITY");
        job.setStatus(FulfillmentStatus.PENDING);
        job.setRetryCount(0);
        job.setIdempotencyKey(UUID.randomUUID().toString());
        job.setCorrelationId(UUID.randomUUID().toString());
        job.setNextAttemptAt(Instant.now().minusSeconds(1));
        fulfillmentJobRepository.saveAndFlush(job);

        mockMvc.perform(get("/api/v1/integration/fulfillment/pending"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/integration/fulfillment/pending")
                        .header("Authorization", "Bearer " + integrationToken(IntegrationScope.OWNERSHIP_VERIFY)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/integration/fulfillment/pending")
                        .header("Authorization", "Bearer " + integrationToken(IntegrationScope.FULFILLMENT_READ)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobId").value(job.getId()))
                .andExpect(jsonPath("$[0].targetValue").value("God"));
    }
}
