package com.tjxjnoobie.website.payments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.tjxjnoobie.store.domain.model.ActorType;
import com.tjxjnoobie.store.domain.model.AuditRecord;
import com.tjxjnoobie.store.domain.model.BillingInterval;
import com.tjxjnoobie.store.domain.model.CheckoutQuote;
import com.tjxjnoobie.store.domain.model.CheckoutSessionStatus;
import com.tjxjnoobie.store.domain.model.CreateCheckoutSessionCommand;
import com.tjxjnoobie.store.domain.model.OrderState;
import com.tjxjnoobie.store.domain.model.PackageType;
import com.tjxjnoobie.store.domain.model.PaymentProvider;
import com.tjxjnoobie.store.domain.model.PaymentStatus;
import com.tjxjnoobie.store.domain.model.UsernameResolution;
import com.tjxjnoobie.store.domain.service.AuditService;
import com.tjxjnoobie.store.domain.service.CheckoutService;
import com.tjxjnoobie.store.domain.service.PlayerIdentityService;
import com.tjxjnoobie.store.persistence.entity.CheckoutSessionEntity;
import com.tjxjnoobie.store.persistence.entity.PackageBenefitEntity;
import com.tjxjnoobie.store.persistence.entity.PaymentTransactionEntity;
import com.tjxjnoobie.store.persistence.entity.PlayerAccountEntity;
import com.tjxjnoobie.store.persistence.entity.StoreOrderEntity;
import com.tjxjnoobie.store.persistence.entity.StoreOrderItemEntity;
import com.tjxjnoobie.store.persistence.entity.StorePackageEntity;
import com.tjxjnoobie.store.persistence.repository.CheckoutSessionRepository;
import com.tjxjnoobie.store.persistence.repository.PackageBenefitRepository;
import com.tjxjnoobie.store.persistence.repository.PaymentTransactionRepository;
import com.tjxjnoobie.store.persistence.repository.PlayerAccountRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderItemRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderRepository;
import com.tjxjnoobie.store.persistence.repository.StorePackageRepository;
import com.tjxjnoobie.website.config.StoreApplicationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class StoreCheckoutService {

    private static final DateTimeFormatter ORDER_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final CheckoutService checkoutService;
    private final PlayerIdentityService playerIdentityService;
    private final PlayerAccountRepository playerAccountRepository;
    private final CheckoutSessionRepository checkoutSessionRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final StoreOrderItemRepository storeOrderItemRepository;
    private final StorePackageRepository storePackageRepository;
    private final PackageBenefitRepository packageBenefitRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final AuditService auditService;
    private final StoreApplicationProperties properties;
    private final ObjectMapper objectMapper;

    public StoreCheckoutService(CheckoutService checkoutService,
                                PlayerIdentityService playerIdentityService,
                                PlayerAccountRepository playerAccountRepository,
                                CheckoutSessionRepository checkoutSessionRepository,
                                StoreOrderRepository storeOrderRepository,
                                StoreOrderItemRepository storeOrderItemRepository,
                                StorePackageRepository storePackageRepository,
                                PackageBenefitRepository packageBenefitRepository,
                                PaymentTransactionRepository paymentTransactionRepository,
                                AuditService auditService,
                                StoreApplicationProperties properties,
                                ObjectMapper objectMapper) {
        this.checkoutService = checkoutService;
        this.playerIdentityService = playerIdentityService;
        this.playerAccountRepository = playerAccountRepository;
        this.checkoutSessionRepository = checkoutSessionRepository;
        this.storeOrderRepository = storeOrderRepository;
        this.storeOrderItemRepository = storeOrderItemRepository;
        this.storePackageRepository = storePackageRepository;
        this.packageBenefitRepository = packageBenefitRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.auditService = auditService;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public StoreCheckoutDraft createDraft(CreateCheckoutSessionCommand command) {
        validateUsername("purchaser", command.purchaserUsername());
        validateUsername("recipient", command.recipientUsername());
        CheckoutQuote quote;
        try {
            quote = checkoutService.prepareQuote(command);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
        UsernameResolution purchaser = playerIdentityService.resolveUsername(command.purchaserUsername());
        UsernameResolution recipient = playerIdentityService.resolveUsername(command.recipientUsername());

        PlayerAccountEntity purchaserAccount = getOrCreatePlayer(purchaser);
        PlayerAccountEntity recipientAccount = getOrCreatePlayer(recipient);

        String orderNumber = generateOrderNumber();
        String sessionToken = UUID.randomUUID().toString().replace("-", "");
        String correlationId = UUID.randomUUID().toString();

        StoreOrderEntity order = new StoreOrderEntity();
        order.setOrderNumber(orderNumber);
        order.setPurchaserPlayerAccount(purchaserAccount);
        order.setRecipientPlayerAccount(recipientAccount);
        order.setPurchaserUsernameSnapshot(purchaser.normalizedUsername());
        order.setRecipientUsernameSnapshot(recipient.normalizedUsername());
        order.setState(OrderState.PENDING_PAYMENT);
        order.setSubtotal(quote.subtotal());
        order.setDiscountTotal(quote.discountTotal());
        order.setTotal(quote.total());
        order.setCurrency("USD");
        order.setPaymentProvider(PaymentProvider.STRIPE);
        storeOrderRepository.save(order);

        Map<String, BigDecimal> lineTotalsBySlug = new LinkedHashMap<>();
        quote.lines().forEach(line -> lineTotalsBySlug.put(line.packageSlug(), line.lineTotal()));

        for (var item : command.items()) {
            StorePackageEntity storePackage = storePackageRepository.findBySlug(item.packageSlug())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown package slug: " + item.packageSlug()));

            StoreOrderItemEntity orderItem = new StoreOrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setStorePackage(storePackage);
            orderItem.setPackageSlugSnapshot(storePackage.getSlug());
            orderItem.setPackageNameSnapshot(storePackage.getName());
            orderItem.setQuantity(item.quantity());
            orderItem.setUnitPrice(storePackage.getPrice());
            orderItem.setTotalPrice(lineTotalsBySlug.getOrDefault(item.packageSlug(), storePackage.getPrice()));
            orderItem.setBenefitSnapshotJson(serializeBenefitSnapshot(storePackage));
            storeOrderItemRepository.save(orderItem);
        }

        CheckoutSessionEntity session = new CheckoutSessionEntity();
        session.setSessionToken(sessionToken);
        session.setOrderNumber(orderNumber);
        session.setPurchaserUsername(purchaser.normalizedUsername());
        session.setRecipientUsername(recipient.normalizedUsername());
        session.setPurchaserUuid(purchaser.uuid().toString());
        session.setRecipientUuid(recipient.uuid().toString());
        session.setCouponCode(command.couponCode());
        session.setStatus(CheckoutSessionStatus.PENDING_PAYMENT);
        session.setSubtotal(quote.subtotal());
        session.setDiscountTotal(quote.discountTotal());
        session.setTotal(quote.total());
        session.setLineItemsJson(writeJson(quote.lines()));
        session.setIdempotencyKey(UUID.randomUUID().toString());
        session.setExpiresAt(Instant.now().plusSeconds(1800));
        checkoutSessionRepository.save(session);

        auditService.record(new AuditRecord(
                ActorType.PLAYER,
                purchaser.uuid().toString(),
                "CHECKOUT_SESSION_CREATED",
                "store_order",
                orderNumber,
                correlationId,
                "{\"sessionToken\":\"" + sessionToken + "\",\"recipientUuid\":\"" + recipient.uuid() + "\"}"
        ));

        return new StoreCheckoutDraft(sessionToken, orderNumber, quote, purchaser, recipient);
    }

    @Transactional(readOnly = true)
    public StoreCheckoutDraft requireDraft(String sessionToken) {
        CheckoutSessionEntity session = checkoutSessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Checkout session not found"));
        return new StoreCheckoutDraft(
                session.getSessionToken(),
                session.getOrderNumber(),
                new CheckoutQuote(List.of(), session.getSubtotal(), session.getDiscountTotal(), session.getTotal(), null),
                new UsernameResolution(UUID.fromString(session.getPurchaserUuid()), session.getPurchaserUsername()),
                new UsernameResolution(UUID.fromString(session.getRecipientUuid()), session.getRecipientUsername())
        );
    }

    @Transactional
    public StripeCheckoutLaunch createStripeCheckoutSession(String sessionToken) {
        if (properties.getStripe().getSecretKey() == null || properties.getStripe().getSecretKey().isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Stripe is not configured");
        }

        CheckoutSessionEntity checkoutSession = checkoutSessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Checkout session not found"));
        if (checkoutSession.getExpiresAt().isBefore(Instant.now())) {
            checkoutSession.setStatus(CheckoutSessionStatus.EXPIRED);
            checkoutSessionRepository.save(checkoutSession);
            throw new ResponseStatusException(HttpStatus.GONE, "Checkout session expired");
        }

        StoreOrderEntity order = storeOrderRepository.findByOrderNumber(checkoutSession.getOrderNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        List<StoreOrderItemEntity> orderItems = storeOrderItemRepository.findByOrder_Id(order.getId());
        boolean containsSubscription = orderItems.stream()
                .anyMatch(item -> item.getStorePackage().getPackageType() == PackageType.SUBSCRIPTION);
        if (containsSubscription && orderItems.size() > 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subscription packages must be purchased separately");
        }

        Stripe.apiKey = properties.getStripe().getSecretKey();
        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(containsSubscription ? SessionCreateParams.Mode.SUBSCRIPTION : SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(properties.getBaseUrl() + properties.getStripe().getSuccessPath() + "?order=" + order.getOrderNumber() + "&session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(properties.getBaseUrl() + properties.getStripe().getCancelPath() + "?order=" + order.getOrderNumber())
                .putMetadata("orderNumber", order.getOrderNumber())
                .putMetadata("sessionToken", checkoutSession.getSessionToken())
                .putMetadata("recipientUuid", checkoutSession.getRecipientUuid())
                .putMetadata("recipientUsername", checkoutSession.getRecipientUsername());

        for (StoreOrderItemEntity item : orderItems) {
            builder.addLineItem(buildLineItem(item));
        }

        try {
            Session stripeSession = Session.create(
                    builder.build(),
                    RequestOptions.builder()
                            .setApiKey(properties.getStripe().getSecretKey())
                            .setIdempotencyKey(checkoutSession.getIdempotencyKey())
                            .build()
            );

            checkoutSession.setStripeCheckoutSessionId(stripeSession.getId());
            checkoutSession.setStripeCustomerId(stripeSession.getCustomer());
            checkoutSessionRepository.save(checkoutSession);

            order.setStripeCheckoutSessionId(stripeSession.getId());
            order.setStripeCustomerId(stripeSession.getCustomer());
            storeOrderRepository.save(order);

            PaymentTransactionEntity paymentTransaction = paymentTransactionRepository.findByProviderReference(stripeSession.getId())
                    .orElseGet(PaymentTransactionEntity::new);
            paymentTransaction.setOrder(order);
            paymentTransaction.setProvider(PaymentProvider.STRIPE);
            paymentTransaction.setStatus(PaymentStatus.PENDING);
            paymentTransaction.setProviderReference(stripeSession.getId());
            paymentTransaction.setAmount(order.getTotal());
            paymentTransaction.setCurrency(order.getCurrency());
            paymentTransaction.setPayloadJson("{\"sessionId\":\"" + stripeSession.getId() + "\"}");
            paymentTransactionRepository.save(paymentTransaction);

            return new StripeCheckoutLaunch(
                    order.getOrderNumber(),
                    checkoutSession.getSessionToken(),
                    stripeSession.getId(),
                    stripeSession.getUrl()
            );
        } catch (StripeException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to create Stripe checkout session", exception);
        }
    }

    private PlayerAccountEntity getOrCreatePlayer(UsernameResolution resolution) {
        return playerAccountRepository.findByMinecraftUuid(resolution.uuid().toString())
                .map(existing -> {
                    existing.setCurrentUsername(resolution.normalizedUsername());
                    return playerAccountRepository.save(existing);
                })
                .or(() -> playerAccountRepository.findByCurrentUsernameIgnoreCase(resolution.normalizedUsername())
                        .map(existing -> {
                            existing.setMinecraftUuid(resolution.uuid().toString());
                            existing.setCurrentUsername(resolution.normalizedUsername());
                            return playerAccountRepository.save(existing);
                        }))
                .orElseGet(() -> {
                    PlayerAccountEntity entity = new PlayerAccountEntity();
                    entity.setMinecraftUuid(resolution.uuid().toString());
                    entity.setCurrentUsername(resolution.normalizedUsername());
                    entity.setLinkedIdentitiesJson("{}");
                    return playerAccountRepository.save(entity);
                });
    }

    private String serializeBenefitSnapshot(StorePackageEntity storePackage) {
        List<Map<String, Object>> benefits = packageBenefitRepository.findByStorePackage_Id(storePackage.getId())
                .stream()
                .map(this::toBenefitMap)
                .toList();
        return writeJson(benefits);
    }

    private Map<String, Object> toBenefitMap(PackageBenefitEntity benefit) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("benefitType", benefit.getBenefitType().name());
        map.put("targetSystem", benefit.getTargetSystem());
        map.put("targetKey", benefit.getTargetKey());
        map.put("targetValue", benefit.getTargetValue());
        map.put("durationDays", benefit.getDurationDays());
        map.put("stackingPolicy", benefit.getStackingPolicy().name());
        return map;
    }

    private SessionCreateParams.LineItem buildLineItem(StoreOrderItemEntity item) {
        StorePackageEntity storePackage = item.getStorePackage();
        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getPackageNameSnapshot())
                .setDescription(storePackage.getShortDescription())
                .build();

        SessionCreateParams.LineItem.PriceData.Builder priceDataBuilder = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(storePackage.getCurrency().toLowerCase(Locale.ROOT))
                .setUnitAmount(toMinorUnits(item.getUnitPrice()))
                .setProductData(productData);

        if (storePackage.getPackageType() == PackageType.SUBSCRIPTION) {
            priceDataBuilder.setRecurring(
                    SessionCreateParams.LineItem.PriceData.Recurring.builder()
                            .setInterval(resolveRecurringInterval(storePackage.getBillingInterval()))
                            .build()
            );
        }

        return SessionCreateParams.LineItem.builder()
                .setQuantity((long) item.getQuantity())
                .setPriceData(priceDataBuilder.build())
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.Recurring.Interval resolveRecurringInterval(BillingInterval interval) {
        return switch (interval) {
            case YEARLY -> SessionCreateParams.LineItem.PriceData.Recurring.Interval.YEAR;
            case MONTHLY, NONE -> SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH;
        };
    }

    private Long toMinorUnits(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize checkout data", exception);
        }
    }

    private String generateOrderNumber() {
        return "NOV-" + ORDER_NUMBER_FORMAT.format(LocalDateTime.now(ZoneOffset.UTC)) + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private void validateUsername(String field, String username) {
        if (!playerIdentityService.isValidMinecraftUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + field + " Minecraft username");
        }
    }
}
