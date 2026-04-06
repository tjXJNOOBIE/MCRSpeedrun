package com.tjxjnoobie.store.domain.service;

import com.tjxjnoobie.store.domain.model.FulfillmentDispatch;

import java.util.List;

public interface FulfillmentOrchestrator {
    List<FulfillmentDispatch> getPendingJobs(int limit);

    void markCompleted(Long jobId, String correlationId, String appliedValue, String message);

    void markFailed(Long jobId, String correlationId, String error, boolean retryable);

    void retry(Long jobId, String actorId);
}
