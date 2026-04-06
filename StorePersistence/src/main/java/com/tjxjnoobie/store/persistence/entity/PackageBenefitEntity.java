package com.tjxjnoobie.store.persistence.entity;

import com.tjxjnoobie.store.domain.model.BenefitType;
import com.tjxjnoobie.store.domain.model.StackingPolicy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "package_benefit")
public class PackageBenefitEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_package_id")
    private StorePackageEntity storePackage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BenefitType benefitType;

    @Column(nullable = false)
    private String targetSystem;

    @Column(nullable = false)
    private String targetKey;

    @Column(nullable = false)
    private String targetValue;

    private Integer durationDays;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StackingPolicy stackingPolicy = StackingPolicy.REPLACE;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String metadataJson = "{}";

    public Long getId() {
        return id;
    }

    public StorePackageEntity getStorePackage() {
        return storePackage;
    }

    public void setStorePackage(StorePackageEntity storePackage) {
        this.storePackage = storePackage;
    }

    public BenefitType getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(BenefitType benefitType) {
        this.benefitType = benefitType;
    }

    public String getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(String targetSystem) {
        this.targetSystem = targetSystem;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public StackingPolicy getStackingPolicy() {
        return stackingPolicy;
    }

    public void setStackingPolicy(StackingPolicy stackingPolicy) {
        this.stackingPolicy = stackingPolicy;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }
}
