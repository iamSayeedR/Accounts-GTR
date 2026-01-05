package com.example.accounts.dto;

import com.example.accounts.entity.enums.DocumentType;
import com.example.accounts.entity.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long invoiceId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private DocumentType documentType;
    private Long companyId;
    private String companyCode;
    private String companyName;
    private String contract;
    private String entity;
    private String warehouse;
    private String basis;
    private BigDecimal subtotalAmount;
    private BigDecimal vatAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private InvoiceStatus status;
    private Boolean isPosted;
    private LocalDateTime postedDate;
    private Long journalEntryId;
    private String journalEntryNumber;
    private List<InvoiceLineResponse> lines;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
