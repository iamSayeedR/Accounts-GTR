package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for Cash Flow Transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowTransactionRequest {

    private String transactionNumber;
    private LocalDate transactionDate;
    private Long cashFlowItemId;
    private String flowType; // "INFLOW" or "OUTFLOW"
    private String category; // "OPERATING", "INVESTING", "FINANCING"
    private BigDecimal amount;
    private String description;
    private String entity;
    private String currency;
    private String referenceNumber;
    private String notes;
}
