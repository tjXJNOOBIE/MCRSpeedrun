package com.tjxjnoobie.store.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CheckoutItemCommand(
        @NotBlank String packageSlug,
        @Min(1) int quantity
) {
}
