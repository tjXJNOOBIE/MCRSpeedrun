package com.tjxjnoobie.store.domain.service;

import com.tjxjnoobie.store.domain.model.StoreCategoryView;
import com.tjxjnoobie.store.domain.model.StorePackageView;

import java.util.List;

public interface CatalogService {
    List<StoreCategoryView> getCategories();

    List<StorePackageView> getVisiblePackages(String categorySlug);

    List<StorePackageView> getFeaturedPackages();

    StorePackageView getPackageBySlug(String slug);
}
