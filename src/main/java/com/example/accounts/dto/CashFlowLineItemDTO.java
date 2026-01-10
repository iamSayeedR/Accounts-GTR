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
public class CashFlowLineItemDTO {
    private String itemCode;
    private String itemDescription;
    private String flowType;
    private String category;
    private BigDecimal amount;
    private Integer displayOrder;
}
