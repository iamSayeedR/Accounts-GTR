package com.example.accounts.repository;

import com.example.accounts.entity.ExpenseItem;
import com.example.accounts.entity.enums.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseItemRepository extends JpaRepository<ExpenseItem, Long> {

    List<ExpenseItem> findByExpenseItemTypeExpenseItemTypeId(Long expenseItemTypeId);

    List<ExpenseItem> findByResourceType(ResourceType resourceType);

    List<ExpenseItem> findByIsActive(Boolean isActive);
}
