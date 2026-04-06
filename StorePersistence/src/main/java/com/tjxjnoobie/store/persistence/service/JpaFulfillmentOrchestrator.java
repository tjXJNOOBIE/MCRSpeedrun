package com.tjxjnoobie.store.persistence.service;

import com.tjxjnoobie.store.domain.model.AuditRecord;
import com.tjxjnoobie.store.domain.model.ActorType;
import com.tjxjnoobie.store.domain.model.EntitlementState;
import com.tjxjnoobie.store.domain.model.FulfillmentDispatch;
import com.tjxjnoobie.store.domain.model.FulfillmentStatus;
import com.tjxjnoobie.store.domain.service.AuditService;
import com.tjxjnoobie.store.domain.service.FulfillmentOrchestrator;
import com.tjxjnoobie.store.persistence.entity.FulfillmentJobEntity;
import com.tjxjnoobie.store.persistence.repository.FulfillmentJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class JpaFulfillmentOrchestrator implements FulfillmentOrchestrator {

    private final FulfillmentJobRepository fulfillmentJobRepository;
    private final AuditService auditService;

    public JpaFulfillmentOrchestrator(FulfillmentJobRepository fulfillmentJobRepository,
                                      AuditService auditService) {
        this.fulfillmentJobRepository = fulfillmentJobRepository;
        this.auditService = auditService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FulfillmentDispatch> getPendingJobs(int limit) {
        return fulfillmentJobRepository.findTop50ByStatusInAndNextAttemptAtBeforeOrderByCreatedAtAsc(
                        Set.of(FulfillmentStatus.PENDING, FulfillmentStatus.RETRY_READY),
                        Instant.now()
                ).stream()
                .limit(limit)
                .map(this::mapDispatch)
                .toList();
    }

    @Override
    @Transactional
    public void markCompleted(Long jobId, String correlationId, String appliedValue, String message) {
        FulfillmentJobEntity job = fulfillmentJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown fulfillment job: " + jobId));
        if (job.getStatus() == FulfillmentStatus.COMPLETED) {
            return;
        }
        job.setStatus(FulfillmentStatus.COMPLETED);
        job.setLastAttemptAt(Instant.now());
        job.setLastError(null);
        fulfillmentJobRepository.save(job);
        auditService.record(new AuditRecord(
                ActorType.INTEGRATION,
                "proxy",
                "FULFILLMENT_COMPLETED",
                "fulfillment_job",
                String.valueOf(jobId),
                correlationId,
                "{\"appliedValue\":\"" + escape(appliedValue) + "\",\"message\":\"" + escape(message) + "\"}"
        ));
    }

    @Override
    @Transactional
    public void markFailed(Long jobId, String correlationId, String error, boolean retryable) {
        FulfillmentJobEntity job = fulfillmentJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown fulfillment job: " + jobId));
        Instant now = Instant.now();
        if (job.getStatus() == FulfillmentStatus.COMPLETED) {
            return;
        }
        if ((job.getStatus() == FulfillmentStatus.RETRY_READY || job.getStatus() == FulfillmentStatus.FAILED)
                && job.getNextAttemptAt() != null
                && job.getNextAttemptAt().isAfter(now)) {
            return;
        }
        job.setRetryCount(job.getRetryCount() + 1);
        job.setLastAttemptAt(now);
        job.setLastError(error);
        job.setStatus(retryable ? FulfillmentStatus.RETRY_READY : FulfillmentStatus.FAILED);
        job.setNextAttemptAt(now.plusSeconds(retryable ? 30L * Math.max(1, job.getRetryCount()) : 3600L));
        fulfillmentJobRepository.save(job);
        auditService.record(new AuditRecord(
                ActorType.INTEGRATION,
                "proxy",
                "FULFILLMENT_FAILED",
                "fulfillment_job",
                String.valueOf(jobId),
                correlationId,
                "{\"error\":\"" + escape(error) + "\",\"retryable\":" + retryable + "}"
        ));
    }

    @Override
    @Transactional
    public void retry(Long jobId, String actorId) {
        FulfillmentJobEntity job = fulfillmentJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown fulfillment job: " + jobId));
        if (job.getStatus() == FulfillmentStatus.COMPLETED) {
            return;
        }
        job.setStatus(FulfillmentStatus.RETRY_READY);
        job.setNextAttemptAt(Instant.now());
        fulfillmentJobRepository.save(job);
        auditService.record(new AuditRecord(
                ActorType.ADMIN,
                actorId,
                "FULFILLMENT_RETRY_REQUESTED",
                "fulfillment_job",
                String.valueOf(jobId),
                job.getCorrelationId(),
                "{}"
        ));
    }

    private FulfillmentDispatch mapDispatch(FulfillmentJobEntity job) {
        return new FulfillmentDispatch(
                job.getId(),
                job.getIdempotencyKey(),
                job.getCorrelationId(),
                job.getEntitlement().getState() == EntitlementState.REVOKED ? "REVOKE" : "GRANT",
                job.getEntitlement().getPlayerAccount().getMinecraftUuid(),
                job.getEntitlement().getPlayerAccount().getCurrentUsername(),
                job.getEntitlement().getBenefitType(),
                job.getTargetSystem(),
                job.getEntitlement().getTargetKey(),
                job.getEntitlement().getTargetValue()
        );
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }
}
