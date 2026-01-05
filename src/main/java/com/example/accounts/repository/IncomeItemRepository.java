package com.example.accounts.repository;

import com.example.accounts.entity.IncomeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeItemRepository extends JpaRepository<IncomeItem, Long> {

    Optional<IncomeItem> findByCode(String code);

    List<IncomeItem> findByIsActive(Boolean isActive);

    boolean existsByCode(String code);
}
