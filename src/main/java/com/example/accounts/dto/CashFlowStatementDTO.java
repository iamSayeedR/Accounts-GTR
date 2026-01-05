package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Cash Flow Statement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowStatementDTO {

    private String title = "CASH FLOW STATEMENT";
    private String entity;
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String period; // "Data for all time" or specific period

    // Opening balance
    private BigDecimal openingBalance = BigDecimal.ZERO;

    // Cash Inflow section
    private List<CashFlowLineItemDTO> inflowItems = new ArrayList<>();
    private BigDecimal totalInflow = BigDecimal.ZERO;

    // Cash Outflow section
    private List<CashFlowLineItemDTO> outflowItems = new ArrayList<>();
    private BigDecimal totalOutflow = BigDecimal.ZERO;

    // Net flow
    private BigDecimal netFlow = BigDecimal.ZERO;

    // Closing balance
    private BigDecimal closingBalance = BigDecimal.ZERO;

    /**
     * Calculate totals
     */
    public void calculateTotals() {
        // Calculate total inflow
        totalInflow = inflowItems.stream()
                .map(CashFlowLineItemDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total outflow
        totalOutflow = outflowItems.stream()
                .map(CashFlowLineItemDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate net flow
        netFlow = totalInflow.subtract(totalOutflow);

        // Calculate closing balance
        closingBalance = openingBalance.add(netFlow);
    }
}
