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
public class JournalEntryLineResponse {
    private Long lineId;
    private Integer lineNumber;
    private Long accountId;
    private String accountCode;
    private String accountDescription;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private String description;
    private Long itemId;
    private String itemCode;
    private Long companyId;
    private String companyCode;
    private String warehouse;
    private String contract;
    private BigDecimal quantity;
}
