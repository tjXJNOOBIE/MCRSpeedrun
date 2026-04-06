package com.tjxjnoobie.website.payments;

import com.tjxjnoobie.website.observability.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    public StripeWebhookController(StripeWebhookService stripeWebhookService) {
        this.stripeWebhookService = stripeWebhookService;
    }

    @PostMapping("/webhook/stripe")
    public ResponseEntity<Void> handleStripeWebhook(@RequestBody String payload,
                                                    @RequestHeader("Stripe-Signature") String signatureHeader,
                                                    HttpServletRequest request) {
        String correlationId = String.valueOf(request.getAttribute(CorrelationIdFilter.CORRELATION_ID_KEY));
        stripeWebhookService.handleWebhook(payload, signatureHeader, correlationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
