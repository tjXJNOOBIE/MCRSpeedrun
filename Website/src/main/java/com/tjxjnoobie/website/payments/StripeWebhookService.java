package com.tjxjnoobie.website.payments;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.tjxjnoobie.store.domain.model.ActorType;
import com.tjxjnoobie.store.domain.model.AuditRecord;
import com.tjxjnoobie.store.domain.model.EntitlementState;
import com.tjxjnoobie.store.domain.model.FulfillmentStatus;
import com.tjxjnoobie.store.domain.model.OrderState;
import com.tjxjnoobie.store.domain.model.PaymentProvider;
import com.tjxjnoobie.store.domain.model.PaymentStatus;
import com.tjxjnoobie.store.domain.model.StackingPolicy;
import com.tjxjnoobie.store.domain.service.AuditService;
import com.tjxjnoobie.store.persistence.entity.CheckoutSessionEntity;
import com.tjxjnoobie.store.persistence.entity.EntitlementEntity;
import com.tjxjnoobie.store.persistence.entity.FulfillmentJobEntity;
import com.tjxjnoobie.store.persistence.entity.PackageBenefitEntity;
import com.tjxjnoobie.store.persistence.entity.PaymentTransactionEntity;
import com.tjxjnoobie.store.persistence.entity.StoreOrderEntity;
import com.tjxjnoobie.store.persistence.entity.StoreOrderItemEntity;
import com.tjxjnoobie.store.persistence.repository.CheckoutSessionRepository;
import com.tjxjnoobie.store.persistence.repository.EntitlementRepository;
import com.tjxjnoobie.store.persistence.repository.FulfillmentJobRepository;
import com.tjxjnoobie.store.persistence.repository.PackageBenefitRepository;
import com.tjxjnoobie.store.persistence.repository.PaymentTransactionRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderItemRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderRepository;
import com.tjxjnoobie.website.config.StoreApplicationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StripeWebhookService {

    private final StoreApplicationProperties properties;
    private final CheckoutSessionRepository checkoutSessionRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final StoreOrderItemRepository storeOrderItemRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PackageBenefitRepository packageBenefitRepository;
    private final EntitlementRepository entitlementRepository;
    private final FulfillmentJobRepository fulfillmentJobRepository;
    private final AuditService auditService;

    public StripeWebhookService(StoreApplicationProperties properties,
                                CheckoutSessionRepository checkoutSessionRepository,
                                StoreOrderRepository storeOrderRepository,
                                StoreOrderItemRepository storeOrderItemRepository,
                                PaymentTransactionRepository paymentTransactionRepository,
                                PackageBenefitRepository packageBenefitRepository,
                                EntitlementRepository entitlementRepository,
                                FulfillmentJobRepository fulfillmentJobRepository,
                                AuditService auditService) {
        this.properties = properties;
        this.checkoutSessionRepository = checkoutSessionRepository;
        this.storeOrderRepository = storeOrderRepository;
        this.storeOrderItemRepository = storeOrderItemRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.packageBenefitRepository = packageBenefitRepository;
        this.entitlementRepository = entitlementRepository;
        this.fulfillmentJobRepository = fulfillmentJobRepository;
        this.auditService = auditService;
    }

    @Transactional
    public void handleWebhook(String payload, String signatureHeader, String correlationId) {
        Event event = verify(payload, signatureHeader);
        if (paymentTransactionRepository.findByProviderEventId(event.getId()).isPresent()) {
            auditService.record(new AuditRecord(
                    ActorType.INTEGRATION,
                    "stripe",
                    "PAYMENT_EVENT_DUPLICATE_IGNORED",
                    "payment_transaction",
                    event.getId(),
                    correlationId,
                    "{\"type\":\"" + event.getType() + "\"}"
            ));
            return;
        }

        switch (event.getType()) {
            case "checkout.session.completed", "checkout.session.async_payment_succeeded" ->
                    processCheckoutSuccess(event, payload, correlationId);
            case "checkout.session.expired" ->
                    processCheckoutExpired(event, payload, correlationId);
            case "checkout.session.async_payment_failed" ->
                    processCheckoutFailure(event, payload, correlationId);
            default -> recordEventOnly(event, payload, correlationId);
        }
    }

    @Transactional
    public void markTestOrderPaid(String orderNumber, String correlationId) {
        StoreOrderEntity order = storeOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        String providerReference = "test_checkout_" + orderNumber;
        String providerEventId = "test_event_" + orderNumber;

        checkoutSessionRepository.findByOrderNumber(orderNumber).ifPresent(checkoutSession -> {
            checkoutSession.setStatus(com.tjxjnoobie.store.domain.model.CheckoutSessionStatus.CONVERTED);
            checkoutSession.setStripeCheckoutSessionId(providerReference);
            checkoutSession.setStripeCustomerId("test-mode");
            checkoutSessionRepository.save(checkoutSession);
        });

        PaymentTransactionEntity payment = paymentTransactionRepository.findByProviderReference(providerReference)
                .orElseGet(PaymentTransactionEntity::new);
        payment.setOrder(order);
        payment.setProvider(PaymentProvider.STRIPE);
        payment.setProviderReference(providerReference);
        payment.setProviderEventId(providerEventId);
        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setAmount(order.getTotal());
        payment.setCurrency(order.getCurrency());
        payment.setPayloadJson("{\"testMode\":true}");
        payment.setProcessedAt(Instant.now());
        paymentTransactionRepository.save(payment);

        reconcilePaidOrder(order, providerReference, "test-mode", correlationId);

        auditService.record(new AuditRecord(
                ActorType.INTEGRATION,
                "test-harness",
                "PAYMENT_TEST_SETTLED",
                "store_order",
                order.getOrderNumber(),
                correlationId,
                "{\"providerReference\":\"" + providerReference + "\"}"
        ));
    }

    private Event verify(String payload, String signatureHeader) {
        try {
            return Webhook.constructEvent(payload, signatureHeader, properties.getStripe().getWebhookSecret());
        } catch (SignatureVerificationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stripe webhook signature verification failed", exception);
        }
    }

    private void processCheckoutSuccess(Event event, String payload, String correlationId) {
        Session session = readCheckoutSession(event);
        CheckoutSessionEntity checkoutSession = findCheckoutSession(session);
        StoreOrderEntity order = storeOrderRepository.findByOrderNumber(checkoutSession.getOrderNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for checkout session"));

        PaymentTransactionEntity payment = paymentTransactionRepository.findByProviderReference(session.getId())
                .orElseGet(PaymentTransactionEntity::new);
        payment.setOrder(order);
        payment.setProvider(PaymentProvider.STRIPE);
        payment.setProviderReference(session.getId());
        payment.setProviderEventId(event.getId());
        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setAmount(BigDecimal.valueOf(Optional.ofNullable(session.getAmountTotal()).orElse(0L), 2));
        payment.setCurrency(order.getCurrency());
        payment.setPayloadJson(payload);
        payment.setProcessedAt(Instant.now());
        paymentTransactionRepository.save(payment);

        checkoutSession.setStatus(com.tjxjnoobie.store.domain.model.CheckoutSessionStatus.CONVERTED);
        checkoutSession.setStripeCheckoutSessionId(session.getId());
        checkoutSession.setStripeCustomerId(session.getCustomer());
        checkoutSessionRepository.save(checkoutSession);

        reconcilePaidOrder(order, session.getId(), session.getCustomer(), correlationId);

        auditService.record(new AuditRecord(
                ActorType.INTEGRATION,
                "stripe",
                "PAYMENT_SUCCEEDED",
                "store_order",
                order.getOrderNumber(),
                correlationId,
                "{\"eventId\":\"" + event.getId() + "\",\"stripeSessionId\":\"" + session.getId() + "\"}"
        ));
    }

    private void processCheckoutExpired(Event event, String payload, String correlationId) {
        Session session = readCheckoutSession(event);
        CheckoutSessionEntity checkoutSession = findCheckoutSession(session);
        StoreOrderEntity order = storeOrderRepository.findByOrderNumber(checkoutSession.getOrderNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for checkout session"));

        checkoutSession.setStatus(com.tjxjnoobie.store.domain.model.CheckoutSessionStatus.EXPIRED);
        checkoutSessionRepository.save(checkoutSession);
        if (isFinalized(order)) {
            recordIgnoredPaymentStateTransition(order, event, correlationId, "EXPIRED_AFTER_PAYMENT");
            return;
        }
        order.setState(OrderState.CANCELLED);
        storeOrderRepository.save(order);

        savePaymentEvent(order, session.getId(), event.getId(), payload, PaymentStatus.FAILED);
        auditService.record(new AuditRecord(
                ActorType.INTEGRATION,
                "stripe",
                "PAYMENT_EXPIRED",
                "store_order",
                order.getOrderNumber(),
                correlationId,
                "{\"eventId\":\"" + event.getId() + "\"}"
        ));
    }

    private void processCheckoutFailure(Event event, String payload, String correlationId) {
        Session session = readCheckoutSession(event);
        CheckoutSessionEntity checkoutSession = findCheckoutSession(session);
        StoreOrderEntity order = storeOrderRepository.findByOrderNumber(checkoutSession.getOrderNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for checkout session"));

        if (isFinalized(order)) {
            recordIgnoredPaymentStateTransition(order, event, correlationId, "FAILURE_AFTER_PAYMENT");
            return;
        }
        order.setState(OrderState.FAILED);
        storeOrderRepository.save(order);
        savePaymentEvent(order, session.getId(), event.getId(), payload, PaymentStatus.FAILED);
        auditService.record(new AuditRecord(
                ActorType.INTEGRATION,
                "stripe",
                "PAYMENT_FAILED",
                "store_order",
                order.getOrderNumber(),
                correlationId,
                "{\"eventId\":\"" + event.getId() + "\"}"
        ));
    }

    private void recordEventOnly(Event event, String payload, String correlationId) {
        PaymentTransactionEntity payment = new PaymentTransactionEntity();
        payment.setProvider(PaymentProvider.STRIPE);
        payment.setProviderEventId(event.getId());
        payment.setStatus(PaymentStatus.REQUIRES_ACTION);
        payment.setAmount(BigDecimal.ZERO);
        payment.setCurrency("USD");
        payment.setPayloadJson(payload);
        payment.setProcessedAt(Instant.now());
        paymentTransactionRepository.save(payment);

        auditService.record(new AuditRecord(
                ActorType.INTEGRATION,
                "stripe",
                "PAYMENT_EVENT_CAPTURED",
                "payment_transaction",
                String.valueOf(payment.getId()),
                correlationId,
                "{\"eventId\":\"" + event.getId() + "\",\"type\":\"" + event.getType() + "\"}"
        ));
    }

    private void savePaymentEvent(StoreOrderEntity order,
                                  String providerReference,
                                  String providerEventId,
                                  String payload,
                                  PaymentStatus status) {
        PaymentTransactionEntity payment = paymentTransactionRepository.findByProviderReference(providerReference)
                .orElseGet(PaymentTransactionEntity::new);
        if (payment.getId() != null && payment.getStatus() == PaymentStatus.SUCCEEDED && status != PaymentStatus.SUCCEEDED) {
            return;
        }
        payment.setOrder(order);
        payment.setProvider(PaymentProvider.STRIPE);
        payment.setProviderReference(providerReference);
        payment.setProviderEventId(providerEventId);
        payment.setStatus(status);
        payment.setAmount(order.getTotal());
        payment.setCurrency(order.getCurrency());
        payment.setPayloadJson(payload);
        payment.setProcessedAt(Instant.now());
        paymentTransactionRepository.save(payment);
    }

    private void createEntitlementsForOrder(StoreOrderEntity order, String correlationId) {
        List<StoreOrderItemEntity> orderItems = storeOrderItemRepository.findByOrder_Id(order.getId());
        for (StoreOrderItemEntity orderItem : orderItems) {
            List<PackageBenefitEntity> benefits = packageBenefitRepository.findByStorePackage_Id(orderItem.getStorePackage().getId());
            for (PackageBenefitEntity benefit : benefits) {
                if (benefit.getStackingPolicy() == StackingPolicy.REPLACE) {
                    List<EntitlementEntity> existing = entitlementRepository
                            .findByPlayerAccount_MinecraftUuidAndBenefitTypeAndTargetSystemAndTargetKeyAndState(
                                    order.getRecipientPlayerAccount().getMinecraftUuid(),
                                    benefit.getBenefitType(),
                                    benefit.getTargetSystem(),
                                    benefit.getTargetKey(),
                                    EntitlementState.ACTIVE
                            );
                    for (EntitlementEntity active : existing) {
                        if (active.getOrderItem().getId().equals(orderItem.getId())) {
                            continue;
                        }
                        active.setState(EntitlementState.REVOKED);
                        active.setRevokedAt(Instant.now());
                        active.setRevocationReason("REPLACED_BY_PURCHASE");
                        entitlementRepository.save(active);
                    }
                }

                EntitlementEntity entitlement = entitlementRepository
                        .findByOrderItem_IdAndBenefitTypeAndTargetSystemAndTargetKeyAndTargetValue(
                                orderItem.getId(),
                                benefit.getBenefitType(),
                                benefit.getTargetSystem(),
                                benefit.getTargetKey(),
                                benefit.getTargetValue()
                        )
                        .orElseGet(() -> createEntitlement(order, orderItem, benefit));

                ensureFulfillmentJob(entitlement, benefit.getTargetSystem(), correlationId);
            }
        }
    }

    private Session readCheckoutSession(Event event) {
        StripeObject object = event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stripe event payload could not be deserialized"));
        if (!(object instanceof Session session)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stripe event did not contain a checkout session");
        }
        return session;
    }

    private CheckoutSessionEntity findCheckoutSession(Session session) {
        String sessionToken = session.getMetadata() == null ? null : session.getMetadata().get("sessionToken");
        if (sessionToken != null) {
            Optional<CheckoutSessionEntity> byToken = checkoutSessionRepository.findBySessionToken(sessionToken);
            if (byToken.isPresent()) {
                return byToken.get();
            }
        }
        return checkoutSessionRepository.findByStripeCheckoutSessionId(session.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Checkout session not found"));
    }

    private void reconcilePaidOrder(StoreOrderEntity order,
                                    String providerReference,
                                    String customerId,
                                    String correlationId) {
        if (!isFinalized(order)) {
            order.setState(OrderState.PAID);
            order.setPaidAt(Instant.now());
            order.setStripeCheckoutSessionId(providerReference);
            order.setStripeCustomerId(customerId);
            storeOrderRepository.save(order);
        }
        createEntitlementsForOrder(order, correlationId);
    }

    private boolean isFinalized(StoreOrderEntity order) {
        return order.getState() == OrderState.PAID
                || order.getState() == OrderState.FULFILLED
                || order.getState() == OrderState.PARTIALLY_FULFILLED;
    }

    private void recordIgnoredPaymentStateTransition(StoreOrderEntity order,
                                                     Event event,
                                                     String correlationId,
                                                     String reason) {
        auditService.record(new AuditRecord(
                ActorType.INTEGRATION,
                "stripe",
                "PAYMENT_STATE_TRANSITION_IGNORED",
                "store_order",
                order.getOrderNumber(),
                correlationId,
                "{\"eventId\":\"" + event.getId() + "\",\"type\":\"" + event.getType() + "\",\"reason\":\"" + reason + "\"}"
        ));
    }

    private EntitlementEntity createEntitlement(StoreOrderEntity order,
                                                StoreOrderItemEntity orderItem,
                                                PackageBenefitEntity benefit) {
        Instant now = Instant.now();
        EntitlementEntity entitlement = new EntitlementEntity();
        entitlement.setPlayerAccount(order.getRecipientPlayerAccount());
        entitlement.setOrderItem(orderItem);
        entitlement.setBenefitType(benefit.getBenefitType());
        entitlement.setTargetSystem(benefit.getTargetSystem());
        entitlement.setTargetKey(benefit.getTargetKey());
        entitlement.setTargetValue(benefit.getTargetValue());
        entitlement.setState(EntitlementState.ACTIVE);
        entitlement.setEffectiveAt(now);
        entitlement.setExpiresAt(benefit.getDurationDays() == null ? null : now.plusSeconds(benefit.getDurationDays() * 86400L));
        entitlement.setSourcePackageName(orderItem.getPackageNameSnapshot());
        return entitlementRepository.save(entitlement);
    }

    private void ensureFulfillmentJob(EntitlementEntity entitlement, String targetSystem, String correlationId) {
        if (fulfillmentJobRepository.findFirstByEntitlement_IdOrderByCreatedAtDesc(entitlement.getId()).isPresent()) {
            return;
        }
        FulfillmentJobEntity job = new FulfillmentJobEntity();
        job.setEntitlement(entitlement);
        job.setTargetSystem(targetSystem);
        job.setStatus(FulfillmentStatus.PENDING);
        job.setRetryCount(0);
        job.setIdempotencyKey(UUID.randomUUID().toString());
        job.setCorrelationId(correlationId);
        job.setNextAttemptAt(Instant.now());
        fulfillmentJobRepository.save(job);
    }
}
