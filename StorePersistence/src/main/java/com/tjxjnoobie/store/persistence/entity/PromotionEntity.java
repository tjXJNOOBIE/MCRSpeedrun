package com.tjxjnoobie.store.persistence.entity;

import com.tjxjnoobie.store.domain.model.CouponType;
import com.tjxjnoobie.store.domain.model.PromotionStatus;
import com.tjxjnoobie.store.domain.model.PromotionTargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "promotion")
public class PromotionEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PromotionStatus status = PromotionStatus.DRAFT;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PromotionTargetType targetType = PromotionTargetType.GLOBAL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType discountType = CouponType.PERCENTAGE;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    private String categorySlug;

    private String packageSlug;

    private Instant startsAt;

    private Instant endsAt;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public PromotionTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(PromotionTargetType targetType) {
        this.targetType = targetType;
    }

    public CouponType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(CouponType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getPackageSlug() {
        return packageSlug;
    }

    public void setPackageSlug(String packageSlug) {
        this.packageSlug = packageSlug;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }
}
