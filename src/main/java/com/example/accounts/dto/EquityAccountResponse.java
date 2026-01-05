package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for equity accounts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquityAccountResponse {

    private Long equityAccountId;
    private String code;
    private String name;
    private String accountType;
    private Integer displayOrder;
    private Boolean isActive;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
