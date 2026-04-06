package com.tjxjnoobie.website.admin;

import com.tjxjnoobie.store.domain.model.ActorType;
import com.tjxjnoobie.store.domain.model.AuditRecord;
import com.tjxjnoobie.store.domain.model.BenefitType;
import com.tjxjnoobie.store.domain.model.EntitlementState;
import com.tjxjnoobie.store.domain.model.FulfillmentStatus;
import com.tjxjnoobie.store.domain.model.OrderState;
import com.tjxjnoobie.store.domain.model.PaymentStatus;
import com.tjxjnoobie.store.domain.service.AuditService;
import com.tjxjnoobie.store.domain.service.FulfillmentOrchestrator;
import com.tjxjnoobie.store.persistence.entity.AdminRoleEntity;
import com.tjxjnoobie.store.persistence.entity.AuditLogEntity;
import com.tjxjnoobie.store.persistence.entity.CouponEntity;
import com.tjxjnoobie.store.persistence.entity.EntitlementEntity;
import com.tjxjnoobie.store.persistence.entity.FulfillmentJobEntity;
import com.tjxjnoobie.store.persistence.entity.PackageBenefitEntity;
import com.tjxjnoobie.store.persistence.entity.PackageCategoryEntity;
import com.tjxjnoobie.store.persistence.entity.PlayerAccountEntity;
import com.tjxjnoobie.store.persistence.entity.PromotionEntity;
import com.tjxjnoobie.store.persistence.entity.StoreOrderEntity;
import com.tjxjnoobie.store.persistence.entity.StorePackageEntity;
import com.tjxjnoobie.store.persistence.repository.AdminRoleRepository;
import com.tjxjnoobie.store.persistence.repository.AuditLogRepository;
import com.tjxjnoobie.store.persistence.repository.CouponRepository;
import com.tjxjnoobie.store.persistence.repository.EntitlementRepository;
import com.tjxjnoobie.store.persistence.repository.FulfillmentJobRepository;
import com.tjxjnoobie.store.persistence.repository.PackageBenefitRepository;
import com.tjxjnoobie.store.persistence.repository.PackageCategoryRepository;
import com.tjxjnoobie.store.persistence.repository.PaymentTransactionRepository;
import com.tjxjnoobie.store.persistence.repository.PlayerAccountRepository;
import com.tjxjnoobie.store.persistence.repository.PromotionRepository;
import com.tjxjnoobie.store.persistence.repository.StoreOrderRepository;
import com.tjxjnoobie.store.persistence.repository.StorePackageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminStoreService {

    private final StoreOrderRepository storeOrderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final EntitlementRepository entitlementRepository;
    private final FulfillmentJobRepository fulfillmentJobRepository;
    private final PlayerAccountRepository playerAccountRepository;
    private final StorePackageRepository storePackageRepository;
    private final PackageCategoryRepository packageCategoryRepository;
    private final PackageBenefitRepository packageBenefitRepository;
    private final PromotionRepository promotionRepository;
    private final CouponRepository couponRepository;
    private final AuditLogRepository auditLogRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final FulfillmentOrchestrator fulfillmentOrchestrator;
    private final AuditService auditService;

    public AdminStoreService(StoreOrderRepository storeOrderRepository,
                             PaymentTransactionRepository paymentTransactionRepository,
                             EntitlementRepository entitlementRepository,
                             FulfillmentJobRepository fulfillmentJobRepository,
                             PlayerAccountRepository playerAccountRepository,
                             StorePackageRepository storePackageRepository,
                             PackageCategoryRepository packageCategoryRepository,
                             PackageBenefitRepository packageBenefitRepository,
                             PromotionRepository promotionRepository,
                             CouponRepository couponRepository,
                             AuditLogRepository auditLogRepository,
                             AdminRoleRepository adminRoleRepository,
                             FulfillmentOrchestrator fulfillmentOrchestrator,
                             AuditService auditService) {
        this.storeOrderRepository = storeOrderRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.entitlementRepository = entitlementRepository;
        this.fulfillmentJobRepository = fulfillmentJobRepository;
        this.playerAccountRepository = playerAccountRepository;
        this.storePackageRepository = storePackageRepository;
        this.packageCategoryRepository = packageCategoryRepository;
        this.packageBenefitRepository = packageBenefitRepository;
        this.promotionRepository = promotionRepository;
        this.couponRepository = couponRepository;
        this.auditLogRepository = auditLogRepository;
        this.adminRoleRepository = adminRoleRepository;
        this.fulfillmentOrchestrator = fulfillmentOrchestrator;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public AdminOverviewMetrics getOverviewMetrics() {
        List<StoreOrderEntity> orders = storeOrderRepository.findAll();
        BigDecimal revenue = orders.stream()
                .filter(order -> order.getState() == OrderState.PAID || order.getState() == OrderState.FULFILLED || order.getState() == OrderState.PARTIALLY_FULFILLED)
                .map(StoreOrderEntity::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long activeRanks = entitlementRepository.findAll().stream()
                .filter(entitlement -> entitlement.getState() == EntitlementState.ACTIVE)
                .filter(entitlement -> entitlement.getBenefitType() == BenefitType.RANK || entitlement.getBenefitType() == BenefitType.PERMISSION_GROUP)
                .count();
        BigDecimal chargebackExposure = paymentTransactionRepository.findAll().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.DISPUTED)
                .map(payment -> payment.getAmount() == null ? BigDecimal.ZERO : payment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long completedOrders = orders.stream()
                .filter(order -> order.getState() == OrderState.PAID || order.getState() == OrderState.FULFILLED || order.getState() == OrderState.PARTIALLY_FULFILLED)
                .count();
        BigDecimal conversionRate = orders.isEmpty()
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(completedOrders)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(orders.size()), 2, java.math.RoundingMode.HALF_UP);
        return new AdminOverviewMetrics(revenue, activeRanks, chargebackExposure, conversionRate);
    }

    @Transactional(readOnly = true)
    public List<AdminPlayerSpendView> getTopPlayers() {
        Map<String, AdminPlayerSpendView> rows = new LinkedHashMap<>();
        for (StoreOrderEntity order : storeOrderRepository.findAll()) {
            PlayerAccountEntity player = order.getRecipientPlayerAccount();
            if (player == null) {
                continue;
            }
            AdminPlayerSpendView existing = rows.get(player.getMinecraftUuid());
            BigDecimal totalSpent = (existing == null ? BigDecimal.ZERO : existing.totalSpent()).add(order.getTotal());
            long orderCount = existing == null ? 1 : existing.orderCount() + 1;
            boolean flagged = existing != null && existing.flagged();
            rows.put(player.getMinecraftUuid(), new AdminPlayerSpendView(
                    player.getMinecraftUuid(),
                    player.getCurrentUsername(),
                    totalSpent,
                    orderCount,
                    flagged
            ));
        }
        return rows.values().stream()
                .sorted(Comparator.comparing(AdminPlayerSpendView::totalSpent).reversed())
                .limit(25)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StoreOrderEntity> getRecentOrders() {
        return storeOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Transactional(readOnly = true)
    public StoreOrderEntity getOrder(Long orderId) {
        return storeOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Transactional(readOnly = true)
    public PlayerAccountEntity getPlayer(UUID uuid) {
        return playerAccountRepository.findByMinecraftUuid(uuid.toString())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
    }

    @Transactional(readOnly = true)
    public List<AuditLogEntity> getRecentAuditEntries(int limit) {
        return auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .limit(limit)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FulfillmentJobEntity> getRecentFulfillmentJobs() {
        return fulfillmentJobRepository.findRecentForAdmin(PageRequest.of(0, 50));
    }

    @Transactional(readOnly = true)
    public List<StorePackageEntity> getPackages() {
        return storePackageRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Transactional(readOnly = true)
    public List<PackageCategoryEntity> getCategories() {
        return packageCategoryRepository.findAll(Sort.by(Sort.Direction.ASC, "displayOrder"));
    }

    @Transactional(readOnly = true)
    public List<PackageBenefitEntity> getBenefits() {
        return packageBenefitRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Transactional(readOnly = true)
    public List<PromotionEntity> getPromotions() {
        return promotionRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Transactional(readOnly = true)
    public List<CouponEntity> getCoupons() {
        return couponRepository.findAll(Sort.by(Sort.Direction.ASC, "code"));
    }

    @Transactional(readOnly = true)
    public List<AdminRoleEntity> getAdminRoles() {
        return adminRoleRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Transactional
    public void retryFulfillment(Long jobId, String actorId) {
        fulfillmentOrchestrator.retry(jobId, actorId);
    }

    @Transactional
    public EntitlementEntity revokeEntitlement(Long entitlementId, String reason, String actorId) {
        EntitlementEntity entitlement = entitlementRepository.findById(entitlementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entitlement not found"));
        if (entitlement.getState() == EntitlementState.REVOKED) {
            return entitlement;
        }
        entitlement.setState(EntitlementState.REVOKED);
        entitlement.setRevokedAt(Instant.now());
        entitlement.setRevocationReason(reason == null || reason.isBlank() ? "REVOKED_BY_ADMIN" : reason);
        entitlementRepository.save(entitlement);

        FulfillmentJobEntity job = new FulfillmentJobEntity();
        job.setEntitlement(entitlement);
        job.setTargetSystem(entitlement.getTargetSystem());
        job.setStatus(FulfillmentStatus.PENDING);
        job.setRetryCount(0);
        job.setIdempotencyKey(UUID.randomUUID().toString());
        job.setCorrelationId(UUID.randomUUID().toString());
        job.setNextAttemptAt(Instant.now());
        fulfillmentJobRepository.save(job);

        auditService.record(new AuditRecord(
                ActorType.ADMIN,
                actorId,
                "ENTITLEMENT_REVOKED",
                "entitlement",
                String.valueOf(entitlementId),
                job.getCorrelationId(),
                "{\"reason\":\"" + entitlement.getRevocationReason() + "\"}"
        ));
        return entitlement;
    }

    @Transactional
    public StorePackageEntity savePackage(StorePackageEntity entity) {
        return storePackageRepository.save(entity);
    }

    @Transactional
    public void replaceBenefits(StorePackageEntity storePackage, List<PackageBenefitEntity> benefits) {
        List<PackageBenefitEntity> existing = packageBenefitRepository.findByStorePackage_Id(storePackage.getId());
        packageBenefitRepository.deleteAll(existing);
        for (PackageBenefitEntity benefit : benefits) {
            benefit.setStorePackage(storePackage);
            packageBenefitRepository.save(benefit);
        }
    }

    @Transactional
    public PackageCategoryEntity saveCategory(PackageCategoryEntity entity) {
        return packageCategoryRepository.save(entity);
    }

    @Transactional
    public PromotionEntity savePromotion(PromotionEntity entity) {
        return promotionRepository.save(entity);
    }

    @Transactional
    public void deletePackage(Long packageId) {
        storePackageRepository.deleteById(packageId);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        packageCategoryRepository.deleteById(categoryId);
    }

    @Transactional
    public void deletePromotion(Long promotionId) {
        promotionRepository.deleteById(promotionId);
    }

    public String normalizeAccent(String accent) {
        if (accent == null || accent.isBlank()) {
            return "amber";
        }
        return accent.trim().toLowerCase(Locale.ROOT);
    }
}
