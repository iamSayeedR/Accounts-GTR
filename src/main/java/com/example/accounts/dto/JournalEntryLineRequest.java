package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryLineRequest {
    private Integer lineNumber;
    private Long accountId;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private String description;
    private Long itemId;
    private Long companyId;
    private String warehouse;
    private String contract;
    private BigDecimal quantity;
}
