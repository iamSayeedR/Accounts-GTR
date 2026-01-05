package com.example.accounts.dto;

import com.example.accounts.entity.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {

    @NotBlank(message = "Invoice number is required")
    private String invoiceNumber;

    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;

    private LocalDate dueDate;

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotNull(message = "Company is required")
    private Long companyId;

    private String contract;
    private String entity;
    private String warehouse;
    private String basis;
    private String notes;

    // GL Accounts for Invoice Processing
    private Long accountsPayableGLAccountId;
    private Long prepaymentsGLAccountId;

    @NotEmpty(message = "At least one line is required")
    private List<InvoiceLineRequest> lines;
}
