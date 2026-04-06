package com.tjxjnoobie.website.storefront;

import com.tjxjnoobie.store.domain.model.StoreCategoryView;
import com.tjxjnoobie.store.domain.service.CatalogService;
import com.tjxjnoobie.store.persistence.entity.PromotionEntity;
import com.tjxjnoobie.store.persistence.repository.PromotionRepository;
import com.tjxjnoobie.website.account.AccountSessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.List;

@Controller
public class StorefrontController {

    private final CatalogService catalogService;
    private final PromotionRepository promotionRepository;
    private final AccountSessionService accountSessionService;

    public StorefrontController(CatalogService catalogService,
                                PromotionRepository promotionRepository,
                                AccountSessionService accountSessionService) {
        this.catalogService = catalogService;
        this.promotionRepository = promotionRepository;
        this.accountSessionService = accountSessionService;
    }

    @GetMapping({"/", "/store"})
    public String storefront(@RequestParam(name = "category", defaultValue = "all") String category,
                             Model model,
                             HttpSession session) {
        List<StoreCategoryView> categories = catalogService.getCategories();
        model.addAttribute("activeCategory", category);
        model.addAttribute("categories", categories);
        model.addAttribute("packages", catalogService.getVisiblePackages(category));
        model.addAttribute("featuredPackages", catalogService.getFeaturedPackages());
        model.addAttribute("heroCategory", categories.stream()
                .filter(item -> item.slug().equalsIgnoreCase(category))
                .findFirst()
                .orElse(null));
        model.addAttribute("promotions", promotionRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .filter(promotion -> promotion.getStartsAt() == null || !promotion.getStartsAt().isAfter(Instant.now()))
                .filter(promotion -> promotion.getEndsAt() == null || !promotion.getEndsAt().isBefore(Instant.now()))
                .limit(3)
                .toList());
        model.addAttribute("playerSession", accountSessionService.current(session).orElse(null));
        return "store/index";
    }

    @GetMapping("/support")
    public String support(Model model, HttpSession session) {
        model.addAttribute("playerSession", accountSessionService.current(session).orElse(null));
        return "store/support";
    }

    @GetMapping("/checkout/success")
    public String checkoutSuccess(@RequestParam(name = "order", required = false) String orderNumber,
                                  Model model,
                                  HttpSession session) {
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("playerSession", accountSessionService.current(session).orElse(null));
        return "store/checkout-success";
    }

    @GetMapping("/checkout/cancel")
    public String checkoutCancel(@RequestParam(name = "order", required = false) String orderNumber,
                                 Model model,
                                 HttpSession session) {
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("playerSession", accountSessionService.current(session).orElse(null));
        return "store/checkout-cancel";
    }
}
