package com.tjxjnoobie.store.domain.service;

import com.tjxjnoobie.store.domain.model.CheckoutQuote;
import com.tjxjnoobie.store.domain.model.CreateCheckoutSessionCommand;

public interface CheckoutService {
    CheckoutQuote prepareQuote(CreateCheckoutSessionCommand command);
}
