package com.tjxjnoobie.store.domain.model;

public record StoreCategoryView(
        Long id,
        String slug,
        String name,
        String description,
        String heroTitle,
        String heroSubtitle,
        String iconKey
) {
}
