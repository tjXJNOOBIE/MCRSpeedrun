package com.tjxjnoobie.website.integration;

import com.tjxjnoobie.store.domain.service.EntitlementService;
import com.tjxjnoobie.store.domain.service.FulfillmentOrchestrator;
import com.tjxjnoobie.store.domain.service.OrderService;
import com.tjxjnoobie.store.integration.auth.IntegrationScope;
import com.tjxjnoobie.store.persistence.entity.EntitlementEntity;
import com.tjxjnoobie.store.persistence.entity.FulfillmentJobEntity;
import com.tjxjnoobie.store.persistence.entity.StoreOrderEntity;
import com.tjxjnoobie.store.persistence.repository.EntitlementRepository;
import com.tjxjnoobie.store.persistence.repository.FulfillmentJobRepository;
import com.tjxjnoobie.store.persistence.repository.PaymentTransactionRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderRepository;
import com.tjxjnoobie.store.integration.dto.FulfillmentCompleteRequest;
import com.tjxjnoobie.store.integration.dto.FulfillmentFailRequest;
import com.tjxjnoobie.store.integration.dto.IntegrationAuthRequest;
import com.tjxjnoobie.store.integration.dto.PendingFulfillmentJobView;
import com.tjxjnoobie.store.integration.dto.PlayerEntitlementView;
import com.tjxjnoobie.store.integration.dto.PlayerOrderView;
import com.tjxjnoobie.store.integration.dto.OwnershipVerificationRequest;
import com.tjxjnoobie.website.account.OwnershipChallengeService;
import com.tjxjnoobie.website.admin.AdminStoreService;
import com.tjxjnoobie.website.config.StoreApplicationProperties;
import com.tjxjnoobie.website.payments.StripeWebhookService;
import com.tjxjnoobie.website.api.dto.RevokeEntitlementRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/integration")
public class IntegrationApiController {

    private final StoreIntegrationAuthService integrationAuthService;
    private final FulfillmentOrchestrator fulfillmentOrchestrator;
    private final EntitlementService entitlementService;
    private final OrderService orderService;
    private final OwnershipChallengeService ownershipChallengeService;
    private final StripeWebhookService stripeWebhookService;
    private final StoreApplicationProperties properties;
    private final AdminStoreService adminStoreService;
    private final StoreOrderRepository storeOrderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final EntitlementRepository entitlementRepository;
    private final FulfillmentJobRepository fulfillmentJobRepository;

    public IntegrationApiController(StoreIntegrationAuthService integrationAuthService,
                                    FulfillmentOrchestrator fulfillmentOrchestrator,
                                    EntitlementService entitlementService,
                                    OrderService orderService,
                                    OwnershipChallengeService ownershipChallengeService,
                                    StripeWebhookService stripeWebhookService,
                                    StoreApplicationProperties properties,
                                    AdminStoreService adminStoreService,
                                    StoreOrderRepository storeOrderRepository,
                                    PaymentTransactionRepository paymentTransactionRepository,
                                    EntitlementRepository entitlementRepository,
                                    FulfillmentJobRepository fulfillmentJobRepository) {
        this.integrationAuthService = integrationAuthService;
        this.fulfillmentOrchestrator = fulfillmentOrchestrator;
        this.entitlementService = entitlementService;
        this.orderService = orderService;
        this.ownershipChallengeService = ownershipChallengeService;
        this.stripeWebhookService = stripeWebhookService;
        this.properties = properties;
        this.adminStoreService = adminStoreService;
        this.storeOrderRepository = storeOrderRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.entitlementRepository = entitlementRepository;
        this.fulfillmentJobRepository = fulfillmentJobRepository;
    }

    @PostMapping("/auth/token")
    public Object issueToken(@Valid @RequestBody IntegrationAuthRequest request) {
        return integrationAuthService.issueToken(request);
    }

    @GetMapping("/fulfillment/pending")
    public Object getPendingFulfillmentJobs(@RequestParam(name = "limit", defaultValue = "25") int limit,
                                            HttpServletRequest request) {
        integrationAuthService.requireClaims(request, IntegrationScope.FULFILLMENT_READ);
        return fulfillmentOrchestrator.getPendingJobs(limit).stream()
                .map(dispatch -> new PendingFulfillmentJobView(
                        dispatch.jobId(),
                        dispatch.idempotencyKey(),
                        dispatch.correlationId(),
                        dispatch.operation(),
                        dispatch.playerUuid(),
                        dispatch.playerUsername(),
                        dispatch.benefitType().name(),
                        dispatch.targetSystem(),
                        dispatch.targetKey(),
                        dispatch.targetValue()
                ))
                .toList();
    }

    @PostMapping("/fulfillment/{jobId}/complete")
    public void markFulfillmentComplete(@PathVariable Long jobId,
                                        @Valid @RequestBody FulfillmentCompleteRequest request,
                                        HttpServletRequest servletRequest) {
        integrationAuthService.requireClaims(servletRequest, IntegrationScope.FULFILLMENT_WRITE);
        fulfillmentOrchestrator.markCompleted(jobId, request.correlationId(), request.appliedValue(), request.message());
    }

    @PostMapping("/fulfillment/{jobId}/fail")
    public void markFulfillmentFailed(@PathVariable Long jobId,
                                      @Valid @RequestBody FulfillmentFailRequest request,
                                      HttpServletRequest servletRequest) {
        integrationAuthService.requireClaims(servletRequest, IntegrationScope.FULFILLMENT_WRITE);
        fulfillmentOrchestrator.markFailed(jobId, request.correlationId(), request.error(), request.retryable());
    }

    @GetMapping("/players/{uuid}/entitlements")
    public Object getPlayerEntitlements(@PathVariable UUID uuid, HttpServletRequest request) {
        integrationAuthService.requireClaims(request, IntegrationScope.PLAYER_READ);
        return entitlementService.getActiveEntitlements(uuid).stream()
                .map(entitlement -> new PlayerEntitlementView(
                        entitlement.entitlementId(),
                        uuid.toString(),
                        entitlement.packageName(),
                        entitlement.benefitType().name(),
                        entitlement.targetKey(),
                        entitlement.targetValue(),
                        entitlement.state().name(),
                        entitlement.effectiveAt().toString(),
                        entitlement.expiresAt() == null ? null : entitlement.expiresAt().toString()
                ))
                .toList();
    }

    @GetMapping("/players/{uuid}/orders")
    public Object getPlayerOrders(@PathVariable UUID uuid, HttpServletRequest request) {
        integrationAuthService.requireClaims(request, IntegrationScope.PLAYER_READ);
        return orderService.getOrdersForPlayer(uuid).stream()
                .map(order -> new PlayerOrderView(
                        order.orderId(),
                        order.orderNumber(),
                        order.packageName(),
                        order.orderState().name(),
                        order.total(),
                        order.purchasedAt().toString()
                ))
                .toList();
    }

    @PostMapping("/ownership/verify")
    public Object verifyOwnership(@Valid @RequestBody OwnershipVerificationRequest request,
                                  HttpServletRequest servletRequest) {
        integrationAuthService.requireClaims(servletRequest, IntegrationScope.OWNERSHIP_VERIFY);
        return ownershipChallengeService.verifyFromGame(request);
    }

    @PostMapping("/test/orders/{orderNumber}/settle")
    public void settleTestOrder(@PathVariable String orderNumber, HttpServletRequest servletRequest) {
        integrationAuthService.requireClaims(servletRequest, IntegrationScope.FULFILLMENT_WRITE);
        if (!properties.getIntegration().isTestModeEnabled()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Integration test mode is disabled");
        }
        stripeWebhookService.markTestOrderPaid(orderNumber, "test-" + orderNumber);
    }

    @PostMapping("/test/fulfillment/{jobId}/retry")
    public void retryTestFulfillment(@PathVariable Long jobId, HttpServletRequest servletRequest) {
        integrationAuthService.requireClaims(servletRequest, IntegrationScope.FULFILLMENT_WRITE);
        if (!properties.getIntegration().isTestModeEnabled()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Integration test mode is disabled");
        }
        adminStoreService.retryFulfillment(jobId, "integration-test");
    }

    @PostMapping("/test/entitlements/{id}/revoke")
    public void revokeTestEntitlement(@PathVariable Long id,
                                      @RequestBody(required = false) RevokeEntitlementRequest request,
                                      HttpServletRequest servletRequest) {
        integrationAuthService.requireClaims(servletRequest, IntegrationScope.FULFILLMENT_WRITE);
        if (!properties.getIntegration().isTestModeEnabled()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Integration test mode is disabled");
        }
        adminStoreService.revokeEntitlement(
                id,
                request == null ? "integration-test-revoke" : request.reason(),
                "integration-test"
        );
    }

    @GetMapping("/test/orders/{orderNumber}")
    public Object getTestOrderDiagnostics(@PathVariable String orderNumber, HttpServletRequest servletRequest) {
        integrationAuthService.requireClaims(servletRequest, IntegrationScope.FULFILLMENT_READ);
        if (!properties.getIntegration().isTestModeEnabled()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Integration test mode is disabled");
        }

        StoreOrderEntity order = storeOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("orderNumber", order.getOrderNumber());
        response.put("state", order.getState().name());
        response.put("purchaser", order.getPurchaserUsernameSnapshot());
        response.put("recipient", order.getRecipientUsernameSnapshot());
        response.put("paidAt", order.getPaidAt());
        response.put("payments", paymentTransactionRepository.findByOrder_IdOrderByProcessedAtAsc(order.getId()).stream()
                .map(payment -> {
                    Map<String, Object> paymentMap = new LinkedHashMap<>();
                    paymentMap.put("id", payment.getId());
                    paymentMap.put("provider", payment.getProvider().name());
                    paymentMap.put("status", payment.getStatus().name());
                    paymentMap.put("providerReference", payment.getProviderReference());
                    paymentMap.put("providerEventId", payment.getProviderEventId());
                    paymentMap.put("processedAt", payment.getProcessedAt());
                    return paymentMap;
                })
                .toList());
        response.put("entitlements", entitlementRepository.findByOrderItem_Order_Id(order.getId()).stream()
                .map(this::mapEntitlementDiagnostics)
                .toList());
        return response;
    }

    private Map<String, Object> mapEntitlementDiagnostics(EntitlementEntity entitlement) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", entitlement.getId());
        map.put("benefitType", entitlement.getBenefitType().name());
        map.put("targetSystem", entitlement.getTargetSystem());
        map.put("targetKey", entitlement.getTargetKey());
        map.put("targetValue", entitlement.getTargetValue());
        map.put("state", entitlement.getState().name());
        map.put("effectiveAt", entitlement.getEffectiveAt());
        map.put("revokedAt", entitlement.getRevokedAt());
        map.put("revocationReason", entitlement.getRevocationReason());
        map.put("jobs", fulfillmentJobRepository.findByEntitlement_IdOrderByCreatedAtDesc(entitlement.getId()).stream()
                .map(this::mapJobDiagnostics)
                .toList());
        return map;
    }

    private Map<String, Object> mapJobDiagnostics(FulfillmentJobEntity job) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", job.getId());
        map.put("status", job.getStatus().name());
        map.put("retryCount", job.getRetryCount());
        map.put("lastError", job.getLastError());
        map.put("correlationId", job.getCorrelationId());
        map.put("idempotencyKey", job.getIdempotencyKey());
        map.put("nextAttemptAt", job.getNextAttemptAt());
        map.put("lastAttemptAt", job.getLastAttemptAt());
        return map;
    }
}
