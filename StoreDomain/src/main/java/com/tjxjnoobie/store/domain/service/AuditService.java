package com.tjxjnoobie.store.domain.service;

import com.tjxjnoobie.store.domain.model.AuditRecord;

public interface AuditService {
    void record(AuditRecord record);
}
