package com.tjxjnoobie.website.account;

import com.tjxjnoobie.store.domain.model.UsernameResolution;
import com.tjxjnoobie.store.domain.service.EntitlementService;
import com.tjxjnoobie.store.domain.service.OrderService;
import com.tjxjnoobie.website.api.dto.ApiStatusResponse;
import com.tjxjnoobie.website.api.dto.UsernameRequest;
import com.tjxjnoobie.website.api.dto.VerificationCodeRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/account")
public class AccountApiController {

    private final OwnershipChallengeService ownershipChallengeService;
    private final AccountSessionService accountSessionService;
    private final OrderService orderService;
    private final EntitlementService entitlementService;

    public AccountApiController(OwnershipChallengeService ownershipChallengeService,
                                AccountSessionService accountSessionService,
                                OrderService orderService,
                                EntitlementService entitlementService) {
        this.ownershipChallengeService = ownershipChallengeService;
        this.accountSessionService = accountSessionService;
        this.orderService = orderService;
        this.entitlementService = entitlementService;
    }

    @PostMapping("/verification/challenges")
    @ResponseStatus(HttpStatus.CREATED)
    public Object issueChallenge(@Valid @RequestBody UsernameRequest request) {
        return ownershipChallengeService.issueChallenge(request.username());
    }

    @GetMapping("/verification/challenges/{code}")
    public OwnershipChallengeStatusView getChallengeStatus(@PathVariable String code) {
        return ownershipChallengeService.getStatus(code);
    }

    @PostMapping("/session")
    public ApiStatusResponse establishSession(@Valid @RequestBody VerificationCodeRequest request, HttpSession session) {
        UsernameResolution resolution = ownershipChallengeService.getVerifiedResolution(request.code())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Ownership verification is still pending"));
        accountSessionService.establish(session, resolution);
        return new ApiStatusResponse(true, "Player session established");
    }

    @DeleteMapping("/session")
    public ApiStatusResponse clearSession(HttpSession session) {
        accountSessionService.clear(session);
        return new ApiStatusResponse(true, "Player session cleared");
    }

    @GetMapping("/orders")
    public Object getOrders(HttpSession session) {
        PlayerAccountSession playerSession = accountSessionService.current(session)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Verify ownership first"));
        return orderService.getOrdersForPlayer(playerSession.playerUuid());
    }

    @GetMapping("/entitlements")
    public Object getEntitlements(HttpSession session) {
        PlayerAccountSession playerSession = accountSessionService.current(session)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Verify ownership first"));
        return entitlementService.getActiveEntitlements(playerSession.playerUuid());
    }
}
