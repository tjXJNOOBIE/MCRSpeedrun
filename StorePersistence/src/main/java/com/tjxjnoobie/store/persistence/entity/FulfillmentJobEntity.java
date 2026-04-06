package com.tjxjnoobie.store.persistence.entity;

import com.tjxjnoobie.store.domain.model.FulfillmentStatus;
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
@Table(name = "fulfillment_job")
public class FulfillmentJobEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entitlement_id")
    private EntitlementEntity entitlement;

    @Column(nullable = false)
    private String targetSystem;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FulfillmentStatus status = FulfillmentStatus.PENDING;

    @Column(nullable = false)
    private int retryCount;

    @Column(columnDefinition = "text")
    private String lastError;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private Instant nextAttemptAt;

    private Instant lastAttemptAt;

    private String processedByNode;

    public Long getId() {
        return id;
    }

    public EntitlementEntity getEntitlement() {
        return entitlement;
    }

    public void setEntitlement(EntitlementEntity entitlement) {
        this.entitlement = entitlement;
    }

    public String getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(String targetSystem) {
        this.targetSystem = targetSystem;
    }

    public FulfillmentStatus getStatus() {
        return status;
    }

    public void setStatus(FulfillmentStatus status) {
        this.status = status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public void setNextAttemptAt(Instant nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
    }

    public Instant getLastAttemptAt() {
        return lastAttemptAt;
    }

    public void setLastAttemptAt(Instant lastAttemptAt) {
        this.lastAttemptAt = lastAttemptAt;
    }

    public String getProcessedByNode() {
        return processedByNode;
    }

    public void setProcessedByNode(String processedByNode) {
        this.processedByNode = processedByNode;
    }
}
