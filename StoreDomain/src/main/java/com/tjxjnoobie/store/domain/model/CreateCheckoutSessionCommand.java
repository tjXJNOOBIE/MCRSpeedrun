package com.tjxjnoobie.store.domain.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateCheckoutSessionCommand(
        @NotBlank String purchaserUsername,
        @NotBlank String recipientUsername,
        String couponCode,
        @Valid @NotEmpty List<CheckoutItemCommand> items
) {
}
