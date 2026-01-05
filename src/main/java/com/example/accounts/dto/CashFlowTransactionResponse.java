package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Cash Flow Transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowTransactionResponse {

    private Long transactionId;
    private String transactionNumber;
    private LocalDate transactionDate;

    // Cash Flow Item details
    private Long cashFlowItemId;
    private String cashFlowItemCode;
    private String cashFlowItemDescription;

    private String flowType;
    private String category;
    private BigDecimal amount;
    private String description;
    private String entity;
    private String currency;

    // Posting details
    private Boolean isPosted;
    private LocalDateTime postedDate;

    private String referenceNumber;
    private String notes;

    // Audit
    private LocalDateTime createdAt;
    private String createdBy;
}
