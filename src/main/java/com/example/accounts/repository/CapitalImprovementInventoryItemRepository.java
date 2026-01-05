package com.example.accounts.repository;

import com.example.accounts.entity.CapitalImprovementInventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Capital Improvement Inventory Items
 */
@Repository
public interface CapitalImprovementInventoryItemRepository
        extends JpaRepository<CapitalImprovementInventoryItem, Long> {

    /**
     * Find all inventory items for a specific improvement
     */
    @Query("SELECT i FROM CapitalImprovementInventoryItem i WHERE i.improvement.improvementId = :improvementId")
    List<CapitalImprovementInventoryItem> findByImprovementId(@Param("improvementId") Long improvementId);

    /**
     * Find inventory items by warehouse
     */
    @Query("SELECT i FROM CapitalImprovementInventoryItem i WHERE i.warehouse = :warehouse")
    List<CapitalImprovementInventoryItem> findByWarehouse(@Param("warehouse") String warehouse);

    /**
     * Find inventory items by item code
     */
    @Query("SELECT i FROM CapitalImprovementInventoryItem i WHERE i.itemCode = :itemCode")
    List<CapitalImprovementInventoryItem> findByItemCode(@Param("itemCode") String itemCode);

    /**
     * Get total extracted inventory amount for an improvement
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM CapitalImprovementInventoryItem i WHERE i.improvement.improvementId = :improvementId")
    java.math.BigDecimal getTotalExtractedAmount(@Param("improvementId") Long improvementId);
}
