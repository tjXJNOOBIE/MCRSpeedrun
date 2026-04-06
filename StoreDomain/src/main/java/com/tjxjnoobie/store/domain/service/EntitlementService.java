package com.tjxjnoobie.store.domain.service;

import com.tjxjnoobie.store.domain.model.EntitlementSummary;

import java.util.List;
import java.util.UUID;

public interface EntitlementService {
    List<EntitlementSummary> getActiveEntitlements(UUID playerUuid);
}
