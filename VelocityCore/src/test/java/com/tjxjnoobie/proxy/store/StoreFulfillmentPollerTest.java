package com.tjxjnoobie.proxy.store;

import com.tjxjnoobie.store.integration.dto.PendingFulfillmentJobView;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StoreFulfillmentPollerTest {

    @Test
    void completesPendingJobs() {
        StoreBackendClient backendClient = mock(StoreBackendClient.class);
        PurchasedRankApplier applier = mock(PurchasedRankApplier.class);
        Logger logger = mock(Logger.class);
        PendingFulfillmentJobView job = job();
        when(backendClient.isEnabled()).thenReturn(true);
        when(backendClient.fetchPendingJobs()).thenReturn(List.of(job));
        when(applier.apply(job)).thenReturn(new FulfillmentOutcome("God", "granted"));

        new StoreFulfillmentPoller(backendClient, applier, logger).pollOnce();

        verify(backendClient).completeJob(job.jobId(), job.correlationId(), "God", "granted");
        verify(backendClient, never()).failJob(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyBoolean());
    }

    @Test
    void reportsRetryableFailureWhenApplierThrows() {
        StoreBackendClient backendClient = mock(StoreBackendClient.class);
        PurchasedRankApplier applier = mock(PurchasedRankApplier.class);
        Logger logger = mock(Logger.class);
        PendingFulfillmentJobView job = job();
        when(backendClient.isEnabled()).thenReturn(true);
        when(backendClient.fetchPendingJobs()).thenReturn(List.of(job));
        when(applier.apply(job)).thenThrow(new StoreBackendException("temporary", true));

        new StoreFulfillmentPoller(backendClient, applier, logger).pollOnce();

        verify(backendClient).failJob(job.jobId(), job.correlationId(), "temporary", true);
    }

    @Test
    void skipsPollWhenDisabled() {
        StoreBackendClient backendClient = mock(StoreBackendClient.class);
        PurchasedRankApplier applier = mock(PurchasedRankApplier.class);
        Logger logger = mock(Logger.class);
        when(backendClient.isEnabled()).thenReturn(false);

        new StoreFulfillmentPoller(backendClient, applier, logger).pollOnce();

        verify(backendClient, never()).fetchPendingJobs();
    }

    private PendingFulfillmentJobView job() {
        return new PendingFulfillmentJobView(
                42L,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "GRANT",
                UUID.randomUUID().toString(),
                "PollBot01",
                "RANK",
                "VELOCITY",
                "rank",
                "God"
        );
    }
}
