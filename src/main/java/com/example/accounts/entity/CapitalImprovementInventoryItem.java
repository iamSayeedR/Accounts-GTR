package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing inventory items extracted/consumed during capital
 * improvements
 */
@Entity
@Table(name = "capital_improvement_inventory_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapitalImprovementInventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "improvement_id", nullable = false)
    private FixedAssetCapitalImprovement improvement;

    @Column(name = "source_fixed_asset_id")
    private Long sourceFixedAssetId;

    @Column(name = "warehouse", length = 200)
    private String warehouse;

    @Column(name = "item_code", length = 100)
    private String itemCode;

    @Column(name = "item_name", length = 200)
    private String itemName;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "uom", length = 50)
    private String uom;

    @Column(name = "unit_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitCost;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gl_account_id")
    private ChartOfAccount glAccount;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Audit fields
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Calculate amount if not set
        if (amount == null && quantity != null && unitCost != null) {
            amount = quantity.multiply(unitCost);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Recalculate amount
        if (quantity != null && unitCost != null) {
            amount = quantity.multiply(unitCost);
        }
    }
}
