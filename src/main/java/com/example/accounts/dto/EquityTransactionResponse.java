package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for equity transactions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquityTransactionResponse {

    private Long transactionId;
    private String transactionNumber;
    private LocalDate transactionDate;

    // Equity account details
    private Long equityAccountId;
    private String equityAccountCode;
    private String equityAccountName;
    private String accountType;

    private String transactionType;
    private BigDecimal amount;
    private String description;
    private Integer fiscalYear;
    private String fiscalPeriod;
    private String companyName;

    // Posting details
    private Boolean isPosted;
    private LocalDateTime postedDate;

    private String referenceNumber;
    private String notes;

    // Audit
    private LocalDateTime createdAt;
    private String createdBy;
}
