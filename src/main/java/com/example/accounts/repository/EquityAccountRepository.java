package com.example.accounts.repository;

import com.example.accounts.entity.EquityAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Equity Accounts
 */
@Repository
public interface EquityAccountRepository extends JpaRepository<EquityAccount, Long> {

    /**
     * Find by code
     */
    Optional<EquityAccount> findByCode(String code);

    /**
     * Find by account type
     */
    List<EquityAccount> findByAccountType(String accountType);

    /**
     * Find all active accounts ordered by display order
     */
    @Query("SELECT ea FROM EquityAccount ea WHERE ea.isActive = true ORDER BY ea.displayOrder, ea.code")
    List<EquityAccount> findAllActiveOrdered();

    /**
     * Find by account type and active status
     */
    @Query("SELECT ea FROM EquityAccount ea WHERE ea.accountType = :accountType AND ea.isActive = true")
    List<EquityAccount> findActiveByAccountType(@Param("accountType") String accountType);
}
