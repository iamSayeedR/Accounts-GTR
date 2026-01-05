package com.example.accounts.dto;

import com.example.accounts.entity.enums.DocumentType;
import com.example.accounts.entity.enums.JournalEntryStatus;
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
public class JournalEntryResponse {
    private Long journalEntryId;
    private String entryNumber;
    private LocalDate entryDate;
    private DocumentType documentType;
    private String description;
    private Long companyId;
    private String companyCode;
    private String companyName;
    private String referenceNumber;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private JournalEntryStatus status;
    private LocalDateTime postedDate;
    private String postedBy;
    private List<JournalEntryLineResponse> lines;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
