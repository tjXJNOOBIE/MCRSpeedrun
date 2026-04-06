package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    Optional<CouponEntity> findByCodeIgnoreCase(String code);
}
