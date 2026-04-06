package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.PackageBenefitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageBenefitRepository extends JpaRepository<PackageBenefitEntity, Long> {
    List<PackageBenefitEntity> findByStorePackage_Id(Long storePackageId);
}
