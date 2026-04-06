package com.tjxjnoobie.store.persistence.entity;

import com.tjxjnoobie.store.domain.model.OrderState;
import com.tjxjnoobie.store.domain.model.PaymentProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "store_order")
public class StoreOrderEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaser_player_id")
    private PlayerAccountEntity purchaserPlayerAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_player_id")
    private PlayerAccountEntity recipientPlayerAccount;

    @Column(nullable = false)
    private String purchaserUsernameSnapshot;

    @Column(nullable = false)
    private String recipientUsernameSnapshot;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderState state = OrderState.PENDING_PAYMENT;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountTotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentProvider paymentProvider = PaymentProvider.STRIPE;

    private String stripeCheckoutSessionId;

    private String stripeCustomerId;

    private Instant paidAt;

    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public PlayerAccountEntity getPurchaserPlayerAccount() {
        return purchaserPlayerAccount;
    }

    public void setPurchaserPlayerAccount(PlayerAccountEntity purchaserPlayerAccount) {
        this.purchaserPlayerAccount = purchaserPlayerAccount;
    }

    public PlayerAccountEntity getRecipientPlayerAccount() {
        return recipientPlayerAccount;
    }

    public void setRecipientPlayerAccount(PlayerAccountEntity recipientPlayerAccount) {
        this.recipientPlayerAccount = recipientPlayerAccount;
    }

    public String getPurchaserUsernameSnapshot() {
        return purchaserUsernameSnapshot;
    }

    public void setPurchaserUsernameSnapshot(String purchaserUsernameSnapshot) {
        this.purchaserUsernameSnapshot = purchaserUsernameSnapshot;
    }

    public String getRecipientUsernameSnapshot() {
        return recipientUsernameSnapshot;
    }

    public void setRecipientUsernameSnapshot(String recipientUsernameSnapshot) {
        this.recipientUsernameSnapshot = recipientUsernameSnapshot;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentProvider getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(PaymentProvider paymentProvider) {
        this.paymentProvider = paymentProvider;
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

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }
}
