package com.tjxjnoobie.store.persistence.entity;

import com.tjxjnoobie.store.domain.model.CheckoutSessionStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "checkout_session")
public class CheckoutSessionEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionToken;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String purchaserUsername;

    @Column(nullable = false)
    private String recipientUsername;

    @Column(nullable = false, length = 36)
    private String purchaserUuid;

    @Column(nullable = false, length = 36)
    private String recipientUuid;

    private String couponCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CheckoutSessionStatus status = CheckoutSessionStatus.DRAFT;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountTotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String lineItemsJson;

    private String stripeCheckoutSessionId;

    private String stripeCustomerId;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private Instant expiresAt;

    public Long getId() {
        return id;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPurchaserUsername() {
        return purchaserUsername;
    }

    public void setPurchaserUsername(String purchaserUsername) {
        this.purchaserUsername = purchaserUsername;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public String getPurchaserUuid() {
        return purchaserUuid;
    }

    public void setPurchaserUuid(String purchaserUuid) {
        this.purchaserUuid = purchaserUuid;
    }

    public String getRecipientUuid() {
        return recipientUuid;
    }

    public void setRecipientUuid(String recipientUuid) {
        this.recipientUuid = recipientUuid;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public CheckoutSessionStatus getStatus() {
        return status;
    }

    public void setStatus(CheckoutSessionStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(BigDecimal discountTotal) {
        this.discountTotal = discountTotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getLineItemsJson() {
        return lineItemsJson;
    }

    public void setLineItemsJson(String lineItemsJson) {
        this.lineItemsJson = lineItemsJson;
    }

    public String getStripeCheckoutSessionId() {
        return stripeCheckoutSessionId;
    }

    public void setStripeCheckoutSessionId(String stripeCheckoutSessionId) {
        this.stripeCheckoutSessionId = stripeCheckoutSessionId;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
