package com.tjxjnoobie.website.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminCategoryUpsertRequest(
        @NotBlank String slug,
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String heroTitle,
        @NotBlank String heroSubtitle,
        @NotBlank String iconKey,
        int displayOrder,
        boolean active
) {
}
