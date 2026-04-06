package com.tjxjnoobie.store.domain.service;

import com.tjxjnoobie.store.domain.model.AppliedCoupon;
import com.tjxjnoobie.store.domain.model.CheckoutQuote;
import com.tjxjnoobie.store.domain.model.CreateCheckoutSessionCommand;
import com.tjxjnoobie.store.domain.model.StorePackageView;

import java.util.List;

public interface PricingService {
    CheckoutQuote calculateQuote(CreateCheckoutSessionCommand command,
                                 List<StorePackageView> packages,
                                 AppliedCoupon appliedCoupon);
}
