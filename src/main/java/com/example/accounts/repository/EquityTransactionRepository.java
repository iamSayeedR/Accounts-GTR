package com.example.accounts.repository;

import com.example.accounts.entity.EquityTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Equity Transactions
 * All queries are dynamic based on parameters
 */
@Repository
public interface EquityTransactionRepository extends JpaRepository<EquityTransaction, Long> {

    /**
     * Find by transaction number
     */
    Optional<EquityTransaction> findByTransactionNumber(String transactionNumber);

    /**
     * Find by fiscal year (DYNAMIC - any year)
     */
    @Query("SELECT et FROM EquityTransaction et WHERE et.fiscalYear = :year ORDER BY et.transactionDate")
    List<EquityTransaction> findByFiscalYear(@Param("year") Integer year);

    /**
     * Find by fiscal year range (DYNAMIC - any year range)
     */
    @Query("SELECT et FROM EquityTransaction et WHERE et.fiscalYear BETWEEN :startYear AND :endYear ORDER BY et.fiscalYear, et.transactionDate")
    List<EquityTransaction> findByFiscalYearRange(@Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear);

    /**
     * Find by equity account and fiscal year (DYNAMIC)
     */
    @Query("SELECT et FROM EquityTransaction et WHERE et.equityAccount.equityAccountId = :accountId AND et.fiscalYear = :year ORDER BY et.transactionDate")
    List<EquityTransaction> findByEquityAccountAndFiscalYear(
            @Param("accountId") Long accountId,
            @Param("year") Integer year);

    /**
     * Find by transaction type and fiscal year (DYNAMIC)
     */
    @Query("SELECT et FROM EquityTransaction et WHERE et.transactionType = :type AND et.fiscalYear = :year ORDER BY et.transactionDate")
    List<EquityTransaction> findByTransactionTypeAndFiscalYear(
            @Param("type") String type,
            @Param("year") Integer year);

    /**
     * Get total by account, type, and year (DYNAMIC CALCULATION)
     */
    @Query("SELECT COALESCE(SUM(et.amount), 0) FROM EquityTransaction et WHERE et.equityAccount.equityAccountId = :accountId AND et.transactionType = :type AND et.fiscalYear = :year")
    BigDecimal getTotalByAccountTypeAndYear(
            @Param("accountId") Long accountId,
            @Param("type") String type,
            @Param("year") Integer year);

    /**
     * Get total by account and year (DYNAMIC CALCULATION)
     */
    @Query("SELECT COALESCE(SUM(et.amount), 0) FROM EquityTransaction et WHERE et.equityAccount.equityAccountId = :accountId AND et.fiscalYear = :year")
    BigDecimal getTotalByAccountAndYear(
            @Param("accountId") Long accountId,
            @Param("year") Integer year);

    /**
     * Get opening balance for a year (DYNAMIC - closing balance of previous year)
     */
    @Query("SELECT COALESCE(SUM(et.amount), 0) FROM EquityTransaction et WHERE et.equityAccount.equityAccountId = :accountId AND et.fiscalYear < :year")
    BigDecimal getOpeningBalance(
            @Param("accountId") Long accountId,
            @Param("year") Integer year);

    /**
     * Get closing balance for a year (DYNAMIC CALCULATION)
     */
    @Query("SELECT COALESCE(SUM(et.amount), 0) FROM EquityTransaction et WHERE et.equityAccount.equityAccountId = :accountId AND et.fiscalYear <= :year")
    BigDecimal getClosingBalance(
            @Param("accountId") Long accountId,
            @Param("year") Integer year);

    /**
     * Find unposted transactions
     */
    @Query("SELECT et FROM EquityTransaction et WHERE et.isPosted = false ORDER BY et.transactionDate")
    List<EquityTransaction> findUnposted();

    /**
     * Find by company name and fiscal year (DYNAMIC)
     */
    @Query("SELECT et FROM EquityTransaction et WHERE et.companyName = :companyName AND et.fiscalYear = :year ORDER BY et.transactionDate")
    List<EquityTransaction> findByCompanyAndYear(
            @Param("companyName") String companyName,
            @Param("year") Integer year);

    /**
     * Get all distinct fiscal years (DYNAMIC - returns all years in database)
     */
    @Query("SELECT DISTINCT et.fiscalYear FROM EquityTransaction et ORDER BY et.fiscalYear DESC")
    List<Integer> findAllFiscalYears();
}
