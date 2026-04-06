package com.tjxjnoobie.website.payments;

public record StripeCheckoutLaunch(
        String orderNumber,
        String sessionToken,
        String stripeCheckoutSessionId,
        String redirectUrl
) {
}
