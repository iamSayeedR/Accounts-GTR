package com.example.accounts.dto;

import com.example.accounts.entity.enums.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {

    @NotBlank(message = "Company code is required")
    private String code;

    @NotBlank(message = "Company name is required")
    private String name;

    private String fullDescription;

    @NotNull(message = "Company type is required")
    private CompanyType companyType;

    private String taxId;
    private String legalAddress;
    private String deliveryAddress;
    private String contactPerson;
    private String bankAccount;

    @Builder.Default
    private Boolean isVATTaxpayer = false;

    @Builder.Default
    private Boolean pricesIncludeVAT = false;

    private CompanyGLAccountRequest glAccounts;
}
