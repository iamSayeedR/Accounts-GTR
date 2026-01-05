package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for individual line items in cash flow statement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowLineItemDTO {

    private String itemCode;
    private String itemDescription;
    private String flowType; // "INFLOW" or "OUTFLOW"
    private String category; // "OPERATING", "INVESTING", "FINANCING"
    private BigDecimal amount;
    private Integer displayOrder;
}
