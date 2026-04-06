package com.tjxjnoobie.store.persistence.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "store_order_item")
public class StoreOrderItemEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private StoreOrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_package_id")
    private StorePackageEntity storePackage;

    @Column(nullable = false)
    private String packageSlugSnapshot;

    @Column(nullable = false)
    private String packageNameSnapshot;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String benefitSnapshotJson;

    public Long getId() {
        return id;
    }

    public StoreOrderEntity getOrder() {
        return order;
    }

    public void setOrder(StoreOrderEntity order) {
        this.order = order;
    }

    public StorePackageEntity getStorePackage() {
        return storePackage;
    }

    public void setStorePackage(StorePackageEntity storePackage) {
        this.storePackage = storePackage;
    }

    public String getPackageSlugSnapshot() {
        return packageSlugSnapshot;
    }

    public void setPackageSlugSnapshot(String packageSlugSnapshot) {
        this.packageSlugSnapshot = packageSlugSnapshot;
    }

    public String getPackageNameSnapshot() {
        return packageNameSnapshot;
    }

    public void setPackageNameSnapshot(String packageNameSnapshot) {
        this.packageNameSnapshot = packageNameSnapshot;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getBenefitSnapshotJson() {
        return benefitSnapshotJson;
    }

    public void setBenefitSnapshotJson(String benefitSnapshotJson) {
        this.benefitSnapshotJson = benefitSnapshotJson;
    }
}
