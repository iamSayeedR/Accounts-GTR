package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company_gl_accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyGLAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_gl_account_id")
    private Long companyGLAccountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Customer Accounts
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounts_receivable_id")
    private ChartOfAccount accountsReceivable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advances_received_id")
    private ChartOfAccount advancesReceived;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdcs_received_id")
    private ChartOfAccount pdcsReceived;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_assets_id")
    private ChartOfAccount contractAssets;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retention_receivables_id")
    private ChartOfAccount retentionReceivables;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retention_output_vat_id")
    private ChartOfAccount retentionOutputVAT;

    // Supplier Accounts
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounts_payable_id")
    private ChartOfAccount accountsPayable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advances_paid_id")
    private ChartOfAccount advancesPaid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdcs_issued_id")
    private ChartOfAccount pdcsIssued;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retention_payables_id")
    private ChartOfAccount retentionPayables;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retention_input_vat_id")
    private ChartOfAccount retentionInputVAT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unbilled_purchases_id")
    private ChartOfAccount unbilledPurchases;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advance_to_receive_id")
    private ChartOfAccount advanceToReceive;
}
