package com.example.accounts.repository;

import com.example.accounts.entity.ExpenseItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseItemTypeRepository extends JpaRepository<ExpenseItemType, Long> {

    Optional<ExpenseItemType> findByCode(String code);

    List<ExpenseItemType> findByIsActive(Boolean isActive);

    boolean existsByCode(String code);
}
