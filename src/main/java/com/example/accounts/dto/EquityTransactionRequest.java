package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for equity transactions
 * All fields are dynamic - no hard-coded values
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquityTransactionRequest {

    private String transactionNumber;
    private LocalDate transactionDate;
    private Long equityAccountId;
    private String transactionType;
    private BigDecimal amount;
    private String description;

    // DYNAMIC fiscal year - can be any year
    private Integer fiscalYear;

    private String fiscalPeriod;
    private String companyName;
    private String referenceNumber;
    private String notes;
}
