package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for equity accounts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquityAccountRequest {

    private String code;
    private String name;
    private String accountType;
    private Integer displayOrder;
    private Boolean isActive;
    private String description;
}
