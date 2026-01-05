package com.example.accounts.repository;

import com.example.accounts.entity.AccountDimension;
import com.example.accounts.entity.enums.DimensionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountDimensionRepository extends JpaRepository<AccountDimension, Long> {

    List<AccountDimension> findByChartOfAccountAccountId(Long accountId);

    List<AccountDimension> findByDimensionType(DimensionType dimensionType);

    List<AccountDimension> findByChartOfAccountAccountIdAndDimensionType(Long accountId, DimensionType dimensionType);
}
