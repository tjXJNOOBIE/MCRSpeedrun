package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
}
