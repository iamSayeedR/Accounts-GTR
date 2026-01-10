package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemGLAccountRequest {
    private Long itemId;
    private Long glAccountId;
    private Long goodsOnConsignmentAccountId;
    private Long salesRevenueAccountId;
    private Long tradeDiscountsAccountId;
    private Long costOfGoodsSoldAccountId;
    private Long deferredExpensesAccountId;
    private Long outputVATAccountId;
    private Long inputVATAccountId;
}
