package com.tjxjnoobie.website.admin;

import com.tjxjnoobie.store.domain.service.EntitlementService;
import com.tjxjnoobie.store.domain.service.OrderService;
import com.tjxjnoobie.store.persistence.entity.PackageBenefitEntity;
import com.tjxjnoobie.store.persistence.entity.PackageCategoryEntity;
import com.tjxjnoobie.store.persistence.entity.PromotionEntity;
import com.tjxjnoobie.store.persistence.entity.StoreOrderEntity;
import com.tjxjnoobie.store.persistence.entity.StorePackageEntity;
import com.tjxjnoobie.store.persistence.repository.PackageCategoryRepository;
import com.tjxjnoobie.store.persistence.repository.PromotionRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderItemRepository;
import com.tjxjnoobie.store.persistence.repository.StorePackageRepository;
import com.tjxjnoobie.website.api.dto.AdminCategoryUpsertRequest;
import com.tjxjnoobie.website.api.dto.AdminPackageUpsertRequest;
import com.tjxjnoobie.website.api.dto.AdminPromotionUpsertRequest;
import com.tjxjnoobie.website.api.dto.ApiStatusResponse;
import com.tjxjnoobie.website.api.dto.RevokeEntitlementRequest;
import com.tjxjnoobie.website.security.StoreAdminPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminApiController {

    private final AdminStoreService adminStoreService;
    private final OrderService orderService;
    private final EntitlementService entitlementService;
    private final StoreOrderItemRepository storeOrderItemRepository;
    private final StorePackageRepository storePackageRepository;
    private final PackageCategoryRepository packageCategoryRepository;
    private final PromotionRepository promotionRepository;

    public AdminApiController(AdminStoreService adminStoreService,
                              OrderService orderService,
                              EntitlementService entitlementService,
                              StoreOrderItemRepository storeOrderItemRepository,
                              StorePackageRepository storePackageRepository,
                              PackageCategoryRepository packageCategoryRepository,
                              PromotionRepository promotionRepository) {
        this.adminStoreService = adminStoreService;
        this.orderService = orderService;
        this.entitlementService = entitlementService;
        this.storeOrderItemRepository = storeOrderItemRepository;
        this.storePackageRepository = storePackageRepository;
        this.packageCategoryRepository = packageCategoryRepository;
        this.promotionRepository = promotionRepository;
    }

    @GetMapping("/orders")
    public Object getOrders() {
        return adminStoreService.getRecentOrders().stream().limit(100).map(this::toOrderSummary).toList();
    }

    @GetMapping("/orders/{id}")
    public Object getOrder(@PathVariable Long id) {
        StoreOrderEntity order = adminStoreService.getOrder(id);
        Map<String, Object> response = toOrderSummary(order);
        response.put("items", storeOrderItemRepository.findByOrder_Id(order.getId()).stream()
                .map(item -> Map.of(
                        "id", item.getId(),
                        "packageSlug", item.getPackageSlugSnapshot(),
                        "packageName", item.getPackageNameSnapshot(),
                        "quantity", item.getQuantity(),
                        "unitPrice", item.getUnitPrice(),
                        "totalPrice", item.getTotalPrice()
                ))
                .toList());
        return response;
    }

    @GetMapping("/players/{uuid}")
    public Object getPlayer(@PathVariable UUID uuid) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("player", adminStoreService.getPlayer(uuid));
        response.put("orders", orderService.getOrdersForPlayer(uuid));
        response.put("entitlements", entitlementService.getActiveEntitlements(uuid));
        return response;
    }

    @GetMapping("/audit")
    public Object getAudit() {
        return adminStoreService.getRecentAuditEntries(100);
    }

    @PostMapping("/fulfillment/{jobId}/retry")
    public ApiStatusResponse retryFulfillment(@PathVariable Long jobId,
                                              @AuthenticationPrincipal StoreAdminPrincipal principal) {
        adminStoreService.retryFulfillment(jobId, principal.getUsername());
        return new ApiStatusResponse(true, "Fulfillment retry queued");
    }

    @PostMapping("/entitlements/{id}/revoke")
    public ApiStatusResponse revokeEntitlement(@PathVariable Long id,
                                               @RequestBody(required = false) RevokeEntitlementRequest request,
                                               @AuthenticationPrincipal StoreAdminPrincipal principal) {
        adminStoreService.revokeEntitlement(id, request == null ? null : request.reason(), principal.getUsername());
        return new ApiStatusResponse(true, "Entitlement revoked and queued for fulfillment");
    }

    @GetMapping("/packages")
    public Object getPackages() {
        return adminStoreService.getPackages();
    }

    @PostMapping("/packages")
    @ResponseStatus(HttpStatus.CREATED)
    public Object createPackage(@Valid @RequestBody AdminPackageUpsertRequest request) {
        return savePackage(null, request);
    }

    @PutMapping("/packages/{id}")
    public Object updatePackage(@PathVariable Long id, @Valid @RequestBody AdminPackageUpsertRequest request) {
        return savePackage(id, request);
    }

    @DeleteMapping("/packages/{id}")
    public ApiStatusResponse deletePackage(@PathVariable Long id) {
        adminStoreService.deletePackage(id);
        return new ApiStatusResponse(true, "Package deleted");
    }

    @GetMapping("/categories")
    public Object getCategories() {
        return adminStoreService.getCategories();
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public Object createCategory(@Valid @RequestBody AdminCategoryUpsertRequest request) {
        return saveCategory(null, request);
    }

    @PutMapping("/categories/{id}")
    public Object updateCategory(@PathVariable Long id, @Valid @RequestBody AdminCategoryUpsertRequest request) {
        return saveCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    public ApiStatusResponse deleteCategory(@PathVariable Long id) {
        adminStoreService.deleteCategory(id);
        return new ApiStatusResponse(true, "Category deleted");
    }

    @GetMapping("/promotions")
    public Object getPromotions() {
        return adminStoreService.getPromotions();
    }

    @PostMapping("/promotions")
    @ResponseStatus(HttpStatus.CREATED)
    public Object createPromotion(@Valid @RequestBody AdminPromotionUpsertRequest request) {
        return savePromotion(null, request);
    }

    @PutMapping("/promotions/{id}")
    public Object updatePromotion(@PathVariable Long id, @Valid @RequestBody AdminPromotionUpsertRequest request) {
        return savePromotion(id, request);
    }

    @DeleteMapping("/promotions/{id}")
    public ApiStatusResponse deletePromotion(@PathVariable Long id) {
        adminStoreService.deletePromotion(id);
        return new ApiStatusResponse(true, "Promotion deleted");
    }

    private Object savePackage(Long id, AdminPackageUpsertRequest request) {
        StorePackageEntity entity = id == null
                ? new StorePackageEntity()
                : storePackageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found"));
        PackageCategoryEntity category = packageCategoryRepository.findBySlug(request.categorySlug())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown category slug"));

        entity.setCategory(category);
        entity.setSlug(request.slug());
        entity.setName(request.name());
        entity.setShortDescription(request.shortDescription());
        entity.setDescriptionHtml(request.descriptionHtml());
        entity.setPrice(request.price());
        entity.setCurrency("USD");
        entity.setVisible(request.visible());
        entity.setFeatured(request.featured());
        entity.setGiftable(request.giftable());
        entity.setPackageType(request.packageType());
        entity.setBillingInterval(request.billingInterval());
        entity.setAccentKey(adminStoreService.normalizeAccent(request.accentKey()));
        entity.setMetadataJson("{}");
        StorePackageEntity saved = adminStoreService.savePackage(entity);

        List<PackageBenefitEntity> benefits = request.benefits().stream()
                .map(benefitRequest -> {
                    PackageBenefitEntity benefit = new PackageBenefitEntity();
                    benefit.setBenefitType(benefitRequest.benefitType());
                    benefit.setTargetSystem(benefitRequest.targetSystem());
                    benefit.setTargetKey(benefitRequest.targetKey());
                    benefit.setTargetValue(benefitRequest.targetValue());
                    benefit.setDurationDays(benefitRequest.durationDays());
                    benefit.setStackingPolicy(benefitRequest.stackingPolicy());
                    benefit.setMetadataJson("{}");
                    return benefit;
                })
                .toList();
        adminStoreService.replaceBenefits(saved, benefits);
        return saved;
    }

    private Object saveCategory(Long id, AdminCategoryUpsertRequest request) {
        PackageCategoryEntity entity = id == null
                ? new PackageCategoryEntity()
                : packageCategoryRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        entity.setSlug(request.slug());
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setHeroTitle(request.heroTitle());
        entity.setHeroSubtitle(request.heroSubtitle());
        entity.setIconKey(request.iconKey());
        entity.setDisplayOrder(request.displayOrder());
        entity.setActive(request.active());
        return adminStoreService.saveCategory(entity);
    }

    private Object savePromotion(Long id, AdminPromotionUpsertRequest request) {
        PromotionEntity entity = id == null
                ? new PromotionEntity()
                : promotionRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion not found"));
        entity.setName(request.name());
        entity.setSlug(request.slug());
        entity.setStatus(request.status());
        entity.setTargetType(request.targetType());
        entity.setDiscountType(request.discountType());
        entity.setDiscountValue(request.discountValue());
        entity.setCategorySlug(request.categorySlug());
        entity.setPackageSlug(request.packageSlug());
        entity.setStartsAt(request.startsAt());
        entity.setEndsAt(request.endsAt());
        return adminStoreService.savePromotion(entity);
    }

    private Map<String, Object> toOrderSummary(StoreOrderEntity order) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", order.getId());
        map.put("orderNumber", order.getOrderNumber());
        map.put("purchaser", order.getPurchaserUsernameSnapshot());
        map.put("recipient", order.getRecipientUsernameSnapshot());
        map.put("state", order.getState());
        map.put("total", order.getTotal());
        map.put("paidAt", order.getPaidAt());
        map.put("createdAt", order.getCreatedAt());
        return map;
    }
}
