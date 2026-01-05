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
public class InvoiceLineResponse {
    private Long lineId;
    private Integer lineNumber;
    private Long itemId;
    private String itemCode;
    private String itemDescription;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineAmount;
    private BigDecimal vatRate;
    private BigDecimal vatAmount;
    private BigDecimal discountRate;
    private BigDecimal discountAmount;
    private BigDecimal netAmount;
}
