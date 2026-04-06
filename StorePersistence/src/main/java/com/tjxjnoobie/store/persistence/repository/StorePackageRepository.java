package com.tjxjnoobie.store.persistence.repository;

import com.tjxjnoobie.store.persistence.entity.StorePackageEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StorePackageRepository extends JpaRepository<StorePackageEntity, Long> {
    @EntityGraph(attributePaths = "category")
    List<StorePackageEntity> findByVisibleTrueOrderByFeaturedDescNameAsc();

    @EntityGraph(attributePaths = "category")
    List<StorePackageEntity> findByVisibleTrueAndCategory_SlugOrderByFeaturedDescNameAsc(String categorySlug);

    @EntityGraph(attributePaths = "category")
    List<StorePackageEntity> findByVisibleTrueAndFeaturedTrueOrderByNameAsc();

    @EntityGraph(attributePaths = "category")
    Optional<StorePackageEntity> findBySlug(String slug);
}
