package com.tjxjnoobie.website.account;

import com.tjxjnoobie.store.domain.service.EntitlementService;
import com.tjxjnoobie.store.domain.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    private final AccountSessionService accountSessionService;
    private final OrderService orderService;
    private final EntitlementService entitlementService;

    public AccountController(AccountSessionService accountSessionService,
                             OrderService orderService,
                             EntitlementService entitlementService) {
        this.accountSessionService = accountSessionService;
        this.orderService = orderService;
        this.entitlementService = entitlementService;
    }

    @GetMapping("/account")
    public String account(Model model, HttpSession session) {
        PlayerAccountSession playerSession = accountSessionService.current(session).orElse(null);
        model.addAttribute("playerSession", playerSession);
        if (playerSession != null) {
            model.addAttribute("orders", orderService.getOrdersForPlayer(playerSession.playerUuid()));
            model.addAttribute("entitlements", entitlementService.getActiveEntitlements(playerSession.playerUuid()));
        }
        return "account/index";
    }
}
