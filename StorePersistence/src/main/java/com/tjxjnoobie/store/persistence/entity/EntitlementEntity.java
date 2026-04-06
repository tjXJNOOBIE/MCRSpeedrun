package com.tjxjnoobie.store.persistence.entity;

import com.tjxjnoobie.store.domain.model.BenefitType;
import com.tjxjnoobie.store.domain.model.EntitlementState;
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

import java.time.Instant;

@Entity
@Table(name = "entitlement")
public class EntitlementEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_account_id")
    private PlayerAccountEntity playerAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id")
    private StoreOrderItemEntity orderItem;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BenefitType benefitType;

    @Column(nullable = false)
    private String targetSystem;

    @Column(nullable = false)
    private String targetKey;

    @Column(nullable = false)
    private String targetValue;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EntitlementState state = EntitlementState.PENDING;

    @Column(nullable = false)
    private Instant effectiveAt;

    private Instant expiresAt;

    private Instant revokedAt;

    private String revocationReason;

    @Column(nullable = false)
    private String sourcePackageName;

    public Long getId() {
        return id;
    }

    public PlayerAccountEntity getPlayerAccount() {
        return playerAccount;
    }

    public void setPlayerAccount(PlayerAccountEntity playerAccount) {
        this.playerAccount = playerAccount;
    }

    public StoreOrderItemEntity getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(StoreOrderItemEntity orderItem) {
        this.orderItem = orderItem;
    }

    public BenefitType getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(BenefitType benefitType) {
        this.benefitType = benefitType;
    }

    public String getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(String targetSystem) {
        this.targetSystem = targetSystem;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public EntitlementState getState() {
        return state;
    }

    public void setState(EntitlementState state) {
        this.state = state;
    }

    public Instant getEffectiveAt() {
        return effectiveAt;
    }

    public void setEffectiveAt(Instant effectiveAt) {
        this.effectiveAt = effectiveAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getRevocationReason() {
        return revocationReason;
    }

    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }

    public String getSourcePackageName() {
        return sourcePackageName;
    }

    public void setSourcePackageName(String sourcePackageName) {
        this.sourcePackageName = sourcePackageName;
    }
}
