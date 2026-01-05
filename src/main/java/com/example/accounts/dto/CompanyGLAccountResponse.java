package com.example.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyGLAccountResponse {
    // Customer Accounts
    private Long accountsReceivableId;
    private String accountsReceivableCode;

    private Long advancesReceivedId;
    private String advancesReceivedCode;

    private Long pdcsReceivedId;
    private String pdcsReceivedCode;

    private Long contractAssetsId;
    private String contractAssetsCode;

    private Long retentionReceivablesId;
    private String retentionReceivablesCode;

    private Long retentionOutputVATId;
    private String retentionOutputVATCode;

    // Supplier Accounts
    private Long accountsPayableId;
    private String accountsPayableCode;

    private Long advancesPaidId;
    private String advancesPaidCode;

    private Long pdcsIssuedId;
    private String pdcsIssuedCode;

    private Long retentionPayablesId;
    private String retentionPayablesCode;

    private Long retentionInputVATId;
    private String retentionInputVATCode;

    private Long unbilledPurchasesId;
    private String unbilledPurchasesCode;

    private Long advanceToReceiveId;
    private String advanceToReceiveCode;
}
