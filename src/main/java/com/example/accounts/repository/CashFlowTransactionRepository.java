package com.example.accounts.repository;

import com.example.accounts.entity.CashFlowTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Cash Flow Transactions
 */
@Repository
public interface CashFlowTransactionRepository extends JpaRepository<CashFlowTransaction, Long> {

    /**
     * Find by transaction number
     */
    Optional<CashFlowTransaction> findByTransactionNumber(String transactionNumber);

    /**
     * Find by flow type
     */
    @Query("SELECT t FROM CashFlowTransaction t WHERE t.flowType = :flowType ORDER BY t.transactionDate DESC")
    List<CashFlowTransaction> findByFlowType(@Param("flowType") String flowType);

    /**
     * Find by category
     */
    @Query("SELECT t FROM CashFlowTransaction t WHERE t.category = :category ORDER BY t.transactionDate DESC")
    List<CashFlowTransaction> findByCategory(@Param("category") String category);

    /**
     * Find by date range
     */
    @Query("SELECT t FROM CashFlowTransaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<CashFlowTransaction> findByDateRange(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find by flow type and date range
     */
    @Query("SELECT t FROM CashFlowTransaction t WHERE t.flowType = :flowType AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<CashFlowTransaction> findByFlowTypeAndDateRange(
            @Param("flowType") String flowType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find by category and date range
     */
    @Query("SELECT t FROM CashFlowTransaction t WHERE t.category = :category AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<CashFlowTransaction> findByCategoryAndDateRange(
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find by entity
     */
    @Query("SELECT t FROM CashFlowTransaction t WHERE t.entity = :entity ORDER BY t.transactionDate DESC")
    List<CashFlowTransaction> findByEntity(@Param("entity") String entity);

    /**
     * Find by entity and date range
     */
    @Query("SELECT t FROM CashFlowTransaction t WHERE t.entity = :entity AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<CashFlowTransaction> findByEntityAndDateRange(
            @Param("entity") String entity,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find unposted transactions
     */
    @Query("SELECT t FROM CashFlowTransaction t WHERE t.isPosted = false ORDER BY t.transactionDate DESC")
    List<CashFlowTransaction> findUnposted();

    /**
     * Get total inflow for period
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM CashFlowTransaction t WHERE t.flowType = 'INFLOW' AND t.transactionDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalInflow(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Get total outflow for period
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM CashFlowTransaction t WHERE t.flowType = 'OUTFLOW' AND t.transactionDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalOutflow(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Get total by cash flow item for period
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM CashFlowTransaction t WHERE t.cashFlowItem.cashFlowItemId = :itemId AND t.transactionDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalByItem(
            @Param("itemId") Long itemId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
