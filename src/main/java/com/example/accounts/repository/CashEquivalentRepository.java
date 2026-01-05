package com.example.accounts.repository;

import com.example.accounts.entity.CashEquivalent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashEquivalentRepository extends JpaRepository<CashEquivalent, Long> {

    List<CashEquivalent> findByIsActive(Boolean isActive);

    List<CashEquivalent> findByGroupName(String groupName);
}
