package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO for statement rows
 * All values are calculated dynamically
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquityRowDTO {

    private String rowId;
    private String rowDescription;
    private String rowType; // OPENING, PROFIT, TRANSACTION, CLOSING, SECTION_HEADER
    private String category;

    // Map of accountType -> amount (DYNAMIC - calculated from transactions)
    private Map<String, BigDecimal> values = new HashMap<>();

    private Boolean isTotal = false;
    private Boolean isBold = false;
    private Boolean isItalic = false;
    private Integer indentLevel = 0;

    /**
     * Add value for a specific account type
     */
    public void addValue(String accountType, BigDecimal amount) {
        values.put(accountType, amount);
    }

    /**
     * Get value for a specific account type
     */
    public BigDecimal getValue(String accountType) {
        return values.getOrDefault(accountType, BigDecimal.ZERO);
    }
}
