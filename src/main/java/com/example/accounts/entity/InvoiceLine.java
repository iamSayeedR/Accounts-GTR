package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_lines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long lineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(length = 500)
    private String description;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "line_amount", precision = 19, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal lineAmount = BigDecimal.ZERO;

    @Column(name = "vat_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal vatRate = BigDecimal.ZERO;

    @Column(name = "vat_amount", precision = 19, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal vatAmount = BigDecimal.ZERO;

    @Column(name = "discount_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountRate = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 19, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "net_amount", precision = 19, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal netAmount = BigDecimal.ZERO;

    // Calculate amounts
    public void calculateAmounts() {
        this.lineAmount = quantity.multiply(unitPrice);
        this.discountAmount = lineAmount.multiply(discountRate).divide(BigDecimal.valueOf(100));
        BigDecimal amountAfterDiscount = lineAmount.subtract(discountAmount);
        this.vatAmount = amountAfterDiscount.multiply(vatRate).divide(BigDecimal.valueOf(100));
        this.netAmount = amountAfterDiscount.add(vatAmount);
    }
}
