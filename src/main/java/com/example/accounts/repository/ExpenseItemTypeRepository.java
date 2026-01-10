package com.example.accounts.repository;

import com.example.accounts.entity.ExpenseItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseItemTypeRepository extends JpaRepository<ExpenseItemType, Long> {
}
