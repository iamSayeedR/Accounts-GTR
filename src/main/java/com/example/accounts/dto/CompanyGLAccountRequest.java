package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyGLAccountRequest {
    // Customer Accounts
    private Long accountsReceivableId;
    private Long advancesReceivedId;
    private Long pdcsReceivedId;
    private Long contractAssetsId;
    private Long retentionReceivablesId;
    private Long retentionOutputVATId;

    // Supplier Accounts
    private Long accountsPayableId;
    private Long advancesPaidId;
    private Long pdcsIssuedId;
    private Long retentionPayablesId;
    private Long retentionInputVATId;
    private Long unbilledPurchasesId;
    private Long advanceToReceiveId;
}
