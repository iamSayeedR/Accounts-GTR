package com.example.accounts.repository;

import com.example.accounts.entity.CashFlowItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashFlowItemRepository extends JpaRepository<CashFlowItem, Long> {

    Optional<CashFlowItem> findByCode(String code);

    List<CashFlowItem> findByIsActive(Boolean isActive);

    boolean existsByCode(String code);
}
