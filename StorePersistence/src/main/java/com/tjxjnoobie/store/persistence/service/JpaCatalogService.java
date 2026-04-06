package com.tjxjnoobie.store.persistence.service;

import com.tjxjnoobie.store.domain.model.StoreCategoryView;
import com.tjxjnoobie.store.domain.model.StorePackageView;
import com.tjxjnoobie.store.domain.service.CatalogService;
import com.tjxjnoobie.store.persistence.entity.PackageCategoryEntity;
import com.tjxjnoobie.store.persistence.entity.StorePackageEntity;
import com.tjxjnoobie.store.persistence.repository.PackageCategoryRepository;
import com.tjxjnoobie.store.persistence.repository.StorePackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JpaCatalogService implements CatalogService {

    private final PackageCategoryRepository categoryRepository;
    private final StorePackageRepository storePackageRepository;

    public JpaCatalogService(PackageCategoryRepository categoryRepository,
                             StorePackageRepository storePackageRepository) {
        this.categoryRepository = categoryRepository;
        this.storePackageRepository = storePackageRepository;
    }

    @Override
    public List<StoreCategoryView> getCategories() {
        return categoryRepository.findByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::mapCategory)
                .toList();
    }

    @Override
    public List<StorePackageView> getVisiblePackages(String categorySlug) {
        List<StorePackageEntity> packages = (categorySlug == null || categorySlug.isBlank() || "all".equalsIgnoreCase(categorySlug))
                ? storePackageRepository.findByVisibleTrueOrderByFeaturedDescNameAsc()
                : storePackageRepository.findByVisibleTrueAndCategory_SlugOrderByFeaturedDescNameAsc(categorySlug);
        return packages.stream().map(this::mapPackage).toList();
    }

    @Override
    public List<StorePackageView> getFeaturedPackages() {
        return storePackageRepository.findByVisibleTrueAndFeaturedTrueOrderByNameAsc().stream()
                .map(this::mapPackage)
                .toList();
    }

    @Override
    public StorePackageView getPackageBySlug(String slug) {
        return storePackageRepository.findBySlug(slug)
                .map(this::mapPackage)
                .orElseThrow(() -> new IllegalArgumentException("Unknown package slug: " + slug));
    }

    private StoreCategoryView mapCategory(PackageCategoryEntity entity) {
        return new StoreCategoryView(
                entity.getId(),
                entity.getSlug(),
                entity.getName(),
                entity.getDescription(),
                entity.getHeroTitle(),
                entity.getHeroSubtitle(),
                entity.getIconKey()
        );
    }

    private StorePackageView mapPackage(StorePackageEntity entity) {
        return new StorePackageView(
                entity.getId(),
                entity.getSlug(),
                entity.getCategory().getSlug(),
                entity.getName(),
                entity.getShortDescription(),
                entity.getDescriptionHtml(),
                entity.getPrice(),
                entity.getCurrency(),
                entity.isVisible(),
                entity.isFeatured(),
                entity.isGiftable(),
                entity.getPackageType(),
                entity.getBillingInterval(),
                entity.getAccentKey()
        );
    }
}
