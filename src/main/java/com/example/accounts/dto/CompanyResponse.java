package com.example.accounts.dto;

import com.example.accounts.entity.enums.CompanyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
    private Long companyId;
    private String code;
    private String name;
    private String fullDescription;
    private CompanyType companyType;
    private String taxId;
    private String legalAddress;
    private String deliveryAddress;
    private String contactPerson;
    private String bankAccount;
    private Boolean isVATTaxpayer;
    private Boolean pricesIncludeVAT;
    private Boolean isActive;
    private CompanyGLAccountResponse glAccounts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
