package com.example.accounts.service;

import com.example.accounts.dto.*;
import com.example.accounts.entity.*;
import com.example.accounts.entity.enums.JournalEntryStatus;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.InvalidTransactionException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;
    private final CompanyRepository companyRepository;
    private final ItemRepository itemRepository;

    public JournalEntryResponse createJournalEntry(JournalEntryRequest request) {
        if (journalEntryRepository.existsByEntryNumber(request.getEntryNumber())) {
            throw new DuplicateResourceException("Journal entry number already exists: " + request.getEntryNumber());
        }

        JournalEntry journalEntry = JournalEntry.builder()
                .entryNumber(request.getEntryNumber())
                .entryDate(request.getEntryDate())
                .documentType(request.getDocumentType())
                .description(request.getDescription())
                .referenceNumber(request.getReferenceNumber())
                .status(JournalEntryStatus.DRAFT)
                .build();

        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Company not found with id: " + request.getCompanyId()));
            journalEntry.setCompany(company);
        }

        // Add lines
        for (JournalEntryLineRequest lineReq : request.getLines()) {
            JournalEntryLine line = createJournalEntryLine(lineReq);
            journalEntry.addLine(line);
        }

        // Calculate totals
        journalEntry.calculateTotals();

        // Validate balance
        if (!journalEntry.isBalanced()) {
            throw new InvalidTransactionException("Journal entry is not balanced. Debit: " +
                    journalEntry.getTotalDebit() + ", Credit: " + journalEntry.getTotalCredit());
        }

        JournalEntry saved = journalEntryRepository.save(journalEntry);
        return mapToResponse(saved);
    }

    public JournalEntryResponse getJournalEntryById(Long id) {
        JournalEntry journalEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));
        return mapToResponse(journalEntry);
    }

    public List<JournalEntryResponse> getAllJournalEntries() {
        return journalEntryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<JournalEntryResponse> getJournalEntriesByStatus(JournalEntryStatus status) {
        return journalEntryRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public JournalEntryResponse postJournalEntry(Long id, String postedBy) {
        JournalEntry journalEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));

        if (journalEntry.getStatus() == JournalEntryStatus.POSTED) {
            throw new InvalidTransactionException("Journal entry is already posted");
        }

        if (!journalEntry.isBalanced()) {
            throw new InvalidTransactionException("Cannot post unbalanced journal entry");
        }

        journalEntry.setStatus(JournalEntryStatus.POSTED);
        journalEntry.setPostedDate(LocalDateTime.now());
        journalEntry.setPostedBy(postedBy);

        JournalEntry saved = journalEntryRepository.save(journalEntry);
        return mapToResponse(saved);
    }

    public JournalEntryResponse reverseJournalEntry(Long id, String reversedBy) {
        JournalEntry journalEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));

        if (journalEntry.getStatus() != JournalEntryStatus.POSTED) {
            throw new InvalidTransactionException("Only posted journal entries can be reversed");
        }

        journalEntry.setStatus(JournalEntryStatus.REVERSED);

        JournalEntry saved = journalEntryRepository.save(journalEntry);
        return mapToResponse(saved);
    }

    public void deleteJournalEntry(Long id) {
        JournalEntry journalEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));

        if (journalEntry.getStatus() == JournalEntryStatus.POSTED) {
            throw new InvalidTransactionException("Cannot delete posted journal entry. Please reverse it first.");
        }

        journalEntryRepository.deleteById(id);
    }

    private JournalEntryLine createJournalEntryLine(JournalEntryLineRequest request) {
        ChartOfAccount account = chartOfAccountRepository.findById(request.getAccountId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Account not found with id: " + request.getAccountId()));

        JournalEntryLine line = JournalEntryLine.builder()
                .lineNumber(request.getLineNumber())
                .account(account)
                .debitAmount(request.getDebitAmount() != null ? request.getDebitAmount() : BigDecimal.ZERO)
                .creditAmount(request.getCreditAmount() != null ? request.getCreditAmount() : BigDecimal.ZERO)
                .description(request.getDescription())
                .warehouse(request.getWarehouse())
                .contract(request.getContract())
                .quantity(request.getQuantity())
                .build();

        if (request.getItemId() != null) {
            Item item = itemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + request.getItemId()));
            line.setItem(item);
        }

        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Company not found with id: " + request.getCompanyId()));
            line.setCompany(company);
        }

        return line;
    }

    private JournalEntryResponse mapToResponse(JournalEntry journalEntry) {
        List<JournalEntryLineResponse> lineResponses = journalEntry.getLines().stream()
                .map(this::mapLineToResponse)
                .collect(Collectors.toList());

        return JournalEntryResponse.builder()
                .journalEntryId(journalEntry.getJournalEntryId())
                .entryNumber(journalEntry.getEntryNumber())
                .entryDate(journalEntry.getEntryDate())
                .documentType(journalEntry.getDocumentType())
                .description(journalEntry.getDescription())
                .companyId(journalEntry.getCompany() != null ? journalEntry.getCompany().getCompanyId() : null)
                .companyCode(journalEntry.getCompany() != null ? journalEntry.getCompany().getCode() : null)
                .companyName(journalEntry.getCompany() != null ? journalEntry.getCompany().getName() : null)
                .referenceNumber(journalEntry.getReferenceNumber())
                .totalDebit(journalEntry.getTotalDebit())
                .totalCredit(journalEntry.getTotalCredit())
                .status(journalEntry.getStatus())
                .postedDate(journalEntry.getPostedDate())
                .postedBy(journalEntry.getPostedBy())
                .lines(lineResponses)
                .createdAt(journalEntry.getCreatedAt())
                .updatedAt(journalEntry.getUpdatedAt())
                .createdBy(journalEntry.getCreatedBy())
                .build();
    }

    private JournalEntryLineResponse mapLineToResponse(JournalEntryLine line) {
        return JournalEntryLineResponse.builder()
                .lineId(line.getLineId())
                .lineNumber(line.getLineNumber())
                .accountId(line.getAccount().getAccountId())
                .accountCode(line.getAccount().getAccountCode())
                .accountDescription(line.getAccount().getDescription())
                .debitAmount(line.getDebitAmount())
                .creditAmount(line.getCreditAmount())
                .description(line.getDescription())
                .itemId(line.getItem() != null ? line.getItem().getItemId() : null)
                .itemCode(line.getItem() != null ? line.getItem().getCode() : null)
                .companyId(line.getCompany() != null ? line.getCompany().getCompanyId() : null)
                .companyCode(line.getCompany() != null ? line.getCompany().getCode() : null)
                .warehouse(line.getWarehouse())
                .contract(line.getContract())
                .quantity(line.getQuantity())
                .build();
    }
}
