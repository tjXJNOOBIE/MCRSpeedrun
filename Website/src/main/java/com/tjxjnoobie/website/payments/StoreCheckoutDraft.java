package com.tjxjnoobie.website.payments;

import com.tjxjnoobie.store.domain.model.CheckoutQuote;
import com.tjxjnoobie.store.domain.model.UsernameResolution;

public record StoreCheckoutDraft(
        String sessionToken,
        String orderNumber,
        CheckoutQuote quote,
        UsernameResolution purchaser,
        UsernameResolution recipient
) {
}
