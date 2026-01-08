package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Statement of Changes in Equity
 * All values are calculated dynamically from transactions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquityStatementDTO {

    private String title = "STATEMENT OF CHANGES IN EQUITY";
    private String companyName;
    private String currency;

    // DYNAMIC years - can be any years
    private Integer startYear;
    private Integer endYear;
    private String period; // Generated dynamically from years

    // Equity columns (DYNAMIC - from database)
    private List<EquityColumnDTO> equityColumns = new ArrayList<>();

    // Statement rows (DYNAMIC - calculated from transactions)
    private List<EquityRowDTO> rows = new ArrayList<>();

    /**
     * Generate period description dynamically
     */
    public void generatePeriodDescription() {
        if (startYear != null && endYear != null) {
            this.period = String.format("For the Period Ended 31 December, %d and %d", endYear, startYear);
        } else if (endYear != null) {
            this.period = String.format("For the Period Ended 31 December, %d", endYear);
        } else {
            this.period = "For the Period Ended 31 December";
        }
    }
}
