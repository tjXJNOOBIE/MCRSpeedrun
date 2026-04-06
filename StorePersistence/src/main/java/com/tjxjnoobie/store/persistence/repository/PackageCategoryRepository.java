package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.PackageCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PackageCategoryRepository extends JpaRepository<PackageCategoryEntity, Long> {
    List<PackageCategoryEntity> findByActiveTrueOrderByDisplayOrderAsc();

    Optional<PackageCategoryEntity> findBySlug(String slug);
}
