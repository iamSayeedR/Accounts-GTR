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
public class JournalEntryRequest {

    @NotBlank(message = "Entry number is required")
    private String entryNumber;

    @NotNull(message = "Entry date is required")
    private LocalDate entryDate;

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "Description is required")
    private String description;

    private Long companyId;
    private String referenceNumber;

    @NotEmpty(message = "At least one line is required")
    private List<JournalEntryLineRequest> lines;
}
