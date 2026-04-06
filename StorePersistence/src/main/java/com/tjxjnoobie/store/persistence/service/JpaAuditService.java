package com.tjxjnoobie.store.persistence.service;

import com.tjxjnoobie.store.domain.model.AuditRecord;
import com.tjxjnoobie.store.domain.service.AuditService;
import com.tjxjnoobie.store.persistence.entity.AuditLogEntity;
import com.tjxjnoobie.store.persistence.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaAuditService implements AuditService {

    private final AuditLogRepository auditLogRepository;

    public JpaAuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public void record(AuditRecord record) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setActorType(record.actorType());
        entity.setActorId(record.actorId());
        entity.setAction(record.action());
        entity.setTargetType(record.targetType());
        entity.setTargetId(record.targetId());
        entity.setCorrelationId(record.correlationId());
        entity.setMetadataJson(record.metadataJson());
        auditLogRepository.save(entity);
    }
}
