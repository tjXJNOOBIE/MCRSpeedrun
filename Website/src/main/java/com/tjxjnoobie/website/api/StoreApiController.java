package com.tjxjnoobie.website.api;

import com.tjxjnoobie.store.domain.model.CreateCheckoutSessionCommand;
import com.tjxjnoobie.store.domain.model.ActorType;
import com.tjxjnoobie.store.domain.model.AuditRecord;
import com.tjxjnoobie.store.domain.service.AuditService;
import com.tjxjnoobie.store.domain.service.CatalogService;
import com.tjxjnoobie.store.domain.service.CheckoutService;
import com.tjxjnoobie.store.domain.service.PlayerIdentityService;
import com.tjxjnoobie.website.api.dto.ApiStatusResponse;
import com.tjxjnoobie.website.api.dto.PlayerLookupResponse;
import com.tjxjnoobie.website.api.dto.SessionTokenRequest;
import com.tjxjnoobie.website.api.dto.SupportTicketRequest;
import com.tjxjnoobie.website.payments.StoreCheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
public class StoreApiController {

    private final CatalogService catalogService;
    private final CheckoutService checkoutService;
    private final PlayerIdentityService playerIdentityService;
    private final StoreCheckoutService storeCheckoutService;
    private final AuditService auditService;

    public StoreApiController(CatalogService catalogService,
                              CheckoutService checkoutService,
                              PlayerIdentityService playerIdentityService,
                              StoreCheckoutService storeCheckoutService,
                              AuditService auditService) {
        this.catalogService = catalogService;
        this.checkoutService = checkoutService;
        this.playerIdentityService = playerIdentityService;
        this.storeCheckoutService = storeCheckoutService;
        this.auditService = auditService;
    }

    @GetMapping("/store/categories")
    public Object getCategories() {
        return catalogService.getCategories();
    }

    @GetMapping("/store/packages")
    public Object getPackages(@RequestParam(name = "category", required = false) String category) {
        return catalogService.getVisiblePackages(category);
    }

    @GetMapping("/store/packages/{slug}")
    public Object getPackage(@PathVariable String slug) {
        return catalogService.getPackageBySlug(slug);
    }

    @PostMapping("/checkout/session")
    public Object createCheckoutSession(@Valid @RequestBody CreateCheckoutSessionCommand command) {
        return storeCheckoutService.createDraft(command);
    }

    @PostMapping("/checkout/apply-coupon")
    public Object applyCoupon(@Valid @RequestBody CreateCheckoutSessionCommand command) {
        validateUsername("purchaser", command.purchaserUsername());
        validateUsername("recipient", command.recipientUsername());
        return checkoutService.prepareQuote(command);
    }

    @PostMapping("/payments/create-intent")
    public Object createPaymentIntent(@Valid @RequestBody SessionTokenRequest request) {
        return storeCheckoutService.createStripeCheckoutSession(request.sessionToken());
    }

    @GetMapping("/players/lookup")
    public PlayerLookupResponse lookupPlayer(@RequestParam("username") String username) {
        var resolution = playerIdentityService.resolveUsername(username);
        return new PlayerLookupResponse(resolution.uuid().toString(), resolution.normalizedUsername());
    }

    @PostMapping("/support/tickets")
    public ApiStatusResponse createSupportTicket(@Valid @RequestBody SupportTicketRequest request) {
        auditService.record(new AuditRecord(
                ActorType.PLAYER,
                request.username() == null || request.username().isBlank() ? request.email() : request.username(),
                "SUPPORT_TICKET_CREATED",
                "support_ticket",
                request.subject(),
                null,
                "{\"fullName\":\"" + request.fullName().replace("\"", "\\\"") + "\","
                        + "\"email\":\"" + (request.email() == null ? "" : request.email().replace("\"", "\\\"")) + "\","
                        + "\"issueType\":\"" + request.issueType().replace("\"", "\\\"") + "\","
                        + "\"description\":\"" + request.description().replace("\"", "\\\"") + "\"}"
        ));
        return new ApiStatusResponse(true, "Support request submitted to the operations queue");
    }

    private void validateUsername(String field, String username) {
        if (!playerIdentityService.isValidMinecraftUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + field + " Minecraft username");
        }
    }
}
