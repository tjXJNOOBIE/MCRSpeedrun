package com.tjxjnoobie.website;

import com.tjxjnoobie.store.domain.model.CreateCheckoutSessionCommand;
import com.tjxjnoobie.store.domain.model.FulfillmentStatus;
import com.tjxjnoobie.store.domain.model.CheckoutItemCommand;
import com.tjxjnoobie.store.persistence.entity.EntitlementEntity;
import com.tjxjnoobie.store.persistence.entity.FulfillmentJobEntity;
import com.tjxjnoobie.store.persistence.repository.EntitlementRepository;
import com.tjxjnoobie.store.persistence.repository.FulfillmentJobRepository;
import com.tjxjnoobie.website.payments.StoreCheckoutDraft;
import com.tjxjnoobie.website.payments.StoreCheckoutService;
import com.tjxjnoobie.website.payments.StripeWebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminApiControllerIntegrationTest extends WebsiteIntegrationTestSupport {

    @Autowired
    private StoreCheckoutService storeCheckoutService;

    @Autowired
    private StripeWebhookService stripeWebhookService;

    @Autowired
    private FulfillmentJobRepository fulfillmentJobRepository;

    @Autowired
    private EntitlementRepository entitlementRepository;

    @Test
    void retryRequiresCsrfAndQueuesJob() throws Exception {
        FulfillmentJobEntity job = createSettledJob("RetryBot01");
        job.setStatus(FulfillmentStatus.FAILED);
        job.setNextAttemptAt(Instant.now().plusSeconds(300));
        fulfillmentJobRepository.saveAndFlush(job);

        mockMvc.perform(post("/api/v1/admin/fulfillment/{jobId}/retry", job.getId())
                        .with(adminPrincipal()))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/admin/fulfillment/{jobId}/retry", job.getId())
                        .with(adminPrincipal())
                        .with(csrf()))
                .andExpect(status().isOk());

        FulfillmentJobEntity updated = fulfillmentJobRepository.findById(job.getId()).orElseThrow();
        assertEquals(FulfillmentStatus.RETRY_READY, updated.getStatus());
        assertTrue(!updated.getNextAttemptAt().isAfter(Instant.now().plusSeconds(1)));
    }

    @Test
    void revokeIsIdempotentAndOnlyCreatesOneRevokeJob() throws Exception {
        FulfillmentJobEntity job = createSettledJob("RevokeBot01");
        EntitlementEntity entitlement = job.getEntitlement();
        long beforeCount = fulfillmentJobRepository.findByEntitlement_IdOrderByCreatedAtDesc(entitlement.getId()).size();

        mockMvc.perform(post("/api/v1/admin/entitlements/{id}/revoke", entitlement.getId())
                        .with(adminPrincipal())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reason":"ADMIN_TEST"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/admin/entitlements/{id}/revoke", entitlement.getId())
                        .with(adminPrincipal())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reason":"ADMIN_TEST"}
                                """))
                .andExpect(status().isOk());

        EntitlementEntity updated = entitlementRepository.findById(entitlement.getId()).orElseThrow();
        List<FulfillmentJobEntity> jobs = fulfillmentJobRepository.findByEntitlement_IdOrderByCreatedAtDesc(entitlement.getId());

        assertEquals(com.tjxjnoobie.store.domain.model.EntitlementState.REVOKED, updated.getState());
        assertEquals(beforeCount + 1, jobs.size());
        assertEquals(FulfillmentStatus.PENDING, jobs.getFirst().getStatus());
    }

    private FulfillmentJobEntity createSettledJob(String username) {
        StoreCheckoutDraft draft = storeCheckoutService.createDraft(new CreateCheckoutSessionCommand(
                username,
                username,
                null,
                List.of(new CheckoutItemCommand("god-rank-lifetime", 1))
        ));
        stripeWebhookService.markTestOrderPaid(draft.orderNumber(), "test-" + username);
        return fulfillmentJobRepository.findAll().stream()
                .filter(candidate -> candidate.getEntitlement().getPlayerAccount().getCurrentUsername().equals(username.toLowerCase()))
                .findFirst()
                .orElseThrow();
    }
}
