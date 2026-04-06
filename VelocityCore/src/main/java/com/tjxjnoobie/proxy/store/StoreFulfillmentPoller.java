package com.tjxjnoobie.proxy.store;

import com.tjxjnoobie.store.integration.dto.PendingFulfillmentJobView;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class StoreFulfillmentPoller {

    private final StoreBackendClient backendClient;
    private final PurchasedRankApplier purchasedRankApplier;
    private final Logger logger;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public StoreFulfillmentPoller(StoreBackendClient backendClient,
                                  PurchasedRankApplier purchasedRankApplier,
                                  Logger logger) {
        this.backendClient = backendClient;
        this.purchasedRankApplier = purchasedRankApplier;
        this.logger = logger;
    }

    public void pollOnce() {
        if (!backendClient.isEnabled()) {
            return;
        }
        if (!running.compareAndSet(false, true)) {
            logger.debug("Store fulfillment poll skipped because a previous poll is still running.");
            return;
        }

        try {
            List<PendingFulfillmentJobView> jobs = backendClient.fetchPendingJobs();
            for (PendingFulfillmentJobView job : jobs) {
                process(job);
            }
        } catch (StoreBackendException exception) {
            logger.warn("Store fulfillment poll failed: {}", exception.getMessage());
        } finally {
            running.set(false);
        }
    }

    private void process(PendingFulfillmentJobView job) {
        try {
            FulfillmentOutcome outcome = purchasedRankApplier.apply(job);
            backendClient.completeJob(job.jobId(), job.correlationId(), outcome.appliedValue(), outcome.message());
            logger.info("Completed store fulfillment job {} for {} ({})",
                    job.jobId(), job.playerUsername(), outcome.appliedValue());
        } catch (StoreBackendException exception) {
            handleFailure(job, exception.getMessage(), exception.isRetryable(), exception);
        } catch (RuntimeException exception) {
            handleFailure(job, exception.getMessage(), true, exception);
        }
    }

    private void handleFailure(PendingFulfillmentJobView job,
                               String message,
                               boolean retryable,
                               RuntimeException exception) {
        String error = message == null || message.isBlank() ? exception.getClass().getSimpleName() : message;
        logger.warn("Store fulfillment job {} failed: {}", job.jobId(), error, exception);
        try {
            backendClient.failJob(job.jobId(), job.correlationId(), error, retryable);
        } catch (RuntimeException failException) {
            logger.error("Unable to report fulfillment failure for job {}: {}", job.jobId(),
                    failException.getMessage(), failException);
        }
    }
}
