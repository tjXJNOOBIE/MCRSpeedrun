package com.tjxjnoobie.website;

import com.tjxjnoobie.store.domain.model.CheckoutItemCommand;
import com.tjxjnoobie.store.domain.model.CreateCheckoutSessionCommand;
import com.tjxjnoobie.store.domain.model.EntitlementState;
import com.tjxjnoobie.store.domain.model.FulfillmentStatus;
import com.tjxjnoobie.store.persistence.repository.EntitlementRepository;
import com.tjxjnoobie.store.persistence.repository.FulfillmentJobRepository;
import com.tjxjnoobie.store.persistence.repository.PaymentTransactionRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderRepository;
import com.tjxjnoobie.website.payments.StoreCheckoutDraft;
import com.tjxjnoobie.website.payments.StoreCheckoutService;
import com.tjxjnoobie.website.payments.StripeWebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StripeWebhookServiceTest extends WebsiteIntegrationTestSupport {

    @Autowired
    private StoreCheckoutService storeCheckoutService;

    @Autowired
    private StripeWebhookService stripeWebhookService;

    @Autowired
    private EntitlementRepository entitlementRepository;

    @Autowired
    private FulfillmentJobRepository fulfillmentJobRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private StoreOrderRepository storeOrderRepository;

    @Test
    void duplicateTestSettlementDoesNotDuplicateEntitlementsOrJobs() {
        StoreCheckoutDraft draft = storeCheckoutService.createDraft(new CreateCheckoutSessionCommand(
                "SettleBot01",
                "SettleBot01",
                null,
                List.of(new CheckoutItemCommand("god-rank-lifetime", 1))
        ));

        stripeWebhookService.markTestOrderPaid(draft.orderNumber(), "corr-settle");
        stripeWebhookService.markTestOrderPaid(draft.orderNumber(), "corr-settle");

        Long orderId = storeOrderRepository.findByOrderNumber(draft.orderNumber()).orElseThrow().getId();

        assertEquals(1, entitlementRepository.findByOrderItem_Order_Id(orderId).size());
        assertEquals(1, fulfillmentJobRepository.findAll().stream()
                .filter(job -> job.getEntitlement().getOrderItem().getOrder().getId().equals(orderId))
                .count());
        assertEquals(1, paymentTransactionRepository.findAll().stream()
                .filter(payment -> payment.getOrder() != null && payment.getOrder().getId().equals(orderId))
                .count());
        assertEquals(EntitlementState.ACTIVE, entitlementRepository.findByOrderItem_Order_Id(orderId).getFirst().getState());
        assertEquals(FulfillmentStatus.PENDING, fulfillmentJobRepository.findAll().stream()
                .filter(job -> job.getEntitlement().getOrderItem().getOrder().getId().equals(orderId))
                .findFirst()
                .orElseThrow()
                .getStatus());
        assertNotNull(entitlementRepository.findByOrderItem_Order_Id(orderId).getFirst().getEffectiveAt());
    }
}
