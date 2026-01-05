package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemGLAccountResponse {
    private Long glAccountId;
    private String glAccountCode;
    private String glAccountDescription;

    private Long goodsOnConsignmentAccountId;
    private String goodsOnConsignmentAccountCode;

    private Long salesRevenueAccountId;
    private String salesRevenueAccountCode;

    private Long tradeDiscountsAccountId;
    private String tradeDiscountsAccountCode;

    private Long costOfGoodsSoldAccountId;
    private String costOfGoodsSoldAccountCode;

    private Long deferredExpensesAccountId;
    private String deferredExpensesAccountCode;

    private Long outputVATAccountId;
    private String outputVATAccountCode;

    private Long inputVATAccountId;
    private String inputVATAccountCode;
}
