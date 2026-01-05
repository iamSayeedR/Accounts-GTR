package com.example.accounts.repository;

import com.example.accounts.entity.Company;
import com.example.accounts.entity.enums.CompanyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByCode(String code);

    List<Company> findByCompanyType(CompanyType companyType);

    List<Company> findByIsActive(Boolean isActive);

    boolean existsByCode(String code);
}
