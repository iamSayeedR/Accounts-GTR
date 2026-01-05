package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for equity column headers
 * Dynamically loaded from equity accounts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquityColumnDTO {

    private Long equityAccountId;
    private String columnName;
    private String accountType;
    private Integer displayOrder;
}
