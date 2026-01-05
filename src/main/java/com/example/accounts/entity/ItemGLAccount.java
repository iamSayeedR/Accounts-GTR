package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_gl_accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemGLAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_gl_account_id")
    private Long itemGLAccountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // Main GL Account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gl_account_id")
    private ChartOfAccount glAccount;

    // For Inventory Items
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_on_consignment_account_id")
    private ChartOfAccount goodsOnConsignmentAccount;

    // Revenue
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_revenue_account_id")
    private ChartOfAccount salesRevenueAccount;

    // Discounts
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_discounts_account_id")
    private ChartOfAccount tradeDiscountsAccount;

    // Cost of Goods Sold
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cost_of_goods_sold_account_id")
    private ChartOfAccount costOfGoodsSoldAccount;

    // Deferred Expenses
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deferred_expenses_account_id")
    private ChartOfAccount deferredExpensesAccount;

    // Output VAT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "output_vat_account_id")
    private ChartOfAccount outputVATAccount;

    // Input VAT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "input_vat_account_id")
    private ChartOfAccount inputVATAccount;
}
