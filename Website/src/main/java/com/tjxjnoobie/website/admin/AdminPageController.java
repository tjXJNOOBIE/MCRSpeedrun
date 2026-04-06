package com.tjxjnoobie.website.admin;

import com.tjxjnoobie.website.config.StoreApplicationProperties;
import com.tjxjnoobie.website.security.StoreAdminPrincipal;
import com.tjxjnoobie.website.security.StoreClientRegistrationRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    private final AdminStoreService adminStoreService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final StoreApplicationProperties properties;

    public AdminPageController(AdminStoreService adminStoreService,
                               ClientRegistrationRepository clientRegistrationRepository,
                               StoreApplicationProperties properties) {
        this.adminStoreService = adminStoreService;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.properties = properties;
    }

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal StoreAdminPrincipal principal, Model model) {
        if (principal != null) {
            return "redirect:/admin";
        }
        if (clientRegistrationRepository instanceof StoreClientRegistrationRepository registrations) {
            model.addAttribute("oauthRegistrations", registrations.getRegistrations());
        }
        model.addAttribute("localAdminEnabled", properties.getLocalAdmin().isEnabled());
        return "admin/login";
    }

    @GetMapping({"", "/", "/overview"})
    public String overview(@AuthenticationPrincipal StoreAdminPrincipal principal, Model model) {
        populateCommon(model, principal, "overview");
        model.addAttribute("overviewMetrics", adminStoreService.getOverviewMetrics());
        model.addAttribute("recentOrders", adminStoreService.getRecentOrders().stream().limit(10).toList());
        model.addAttribute("topPlayers", adminStoreService.getTopPlayers().stream().limit(10).toList());
        model.addAttribute("fulfillmentJobs", adminStoreService.getRecentFulfillmentJobs().stream().limit(10).toList());
        return "admin/overview";
    }

    @GetMapping("/customers")
    public String customers(@AuthenticationPrincipal StoreAdminPrincipal principal, Model model) {
        populateCommon(model, principal, "customers");
        model.addAttribute("players", adminStoreService.getTopPlayers());
        return "admin/customers";
    }

    @GetMapping("/products")
    public String products(@AuthenticationPrincipal StoreAdminPrincipal principal, Model model) {
        populateCommon(model, principal, "products");
        model.addAttribute("categories", adminStoreService.getCategories());
        model.addAttribute("packages", adminStoreService.getPackages());
        model.addAttribute("benefits", adminStoreService.getBenefits());
        model.addAttribute("benefitsByPackage", adminStoreService.getBenefits().stream()
                .collect(java.util.stream.Collectors.groupingBy(benefit -> benefit.getStorePackage().getId())));
        return "admin/products";
    }

    @GetMapping("/sales")
    public String sales(@AuthenticationPrincipal StoreAdminPrincipal principal, Model model) {
        populateCommon(model, principal, "sales");
        model.addAttribute("promotions", adminStoreService.getPromotions());
        return "admin/sales";
    }

    @GetMapping("/promos")
    public String promos(@AuthenticationPrincipal StoreAdminPrincipal principal, Model model) {
        populateCommon(model, principal, "promos");
        model.addAttribute("promotions", adminStoreService.getPromotions());
        model.addAttribute("coupons", adminStoreService.getCoupons());
        return "admin/promos";
    }

    @GetMapping("/support")
    public String support(@AuthenticationPrincipal StoreAdminPrincipal principal, Model model) {
        populateCommon(model, principal, "support");
        model.addAttribute("auditEntries", adminStoreService.getRecentAuditEntries(25));
        model.addAttribute("fulfillmentJobs", adminStoreService.getRecentFulfillmentJobs().stream().limit(25).toList());
        return "admin/support";
    }

    @GetMapping("/settings")
    public String settings(@AuthenticationPrincipal StoreAdminPrincipal principal, Model model) {
        populateCommon(model, principal, "settings");
        model.addAttribute("roles", adminStoreService.getAdminRoles());
        model.addAttribute("stripeConfigured", properties.getStripe().getPublishableKey() != null && !properties.getStripe().getPublishableKey().isBlank());
        model.addAttribute("googleConfigured", properties.getOauth().getGoogle().getClientId() != null && !properties.getOauth().getGoogle().getClientId().isBlank());
        model.addAttribute("discordConfigured", properties.getOauth().getDiscord().getClientId() != null && !properties.getOauth().getDiscord().getClientId().isBlank());
        model.addAttribute("integrationTtlSeconds", properties.getIntegration().getTokenTtlSeconds());
        return "admin/settings";
    }

    private void populateCommon(Model model, StoreAdminPrincipal principal, String activePage) {
        model.addAttribute("activePage", activePage);
        model.addAttribute("adminPrincipal", principal);
    }
}
