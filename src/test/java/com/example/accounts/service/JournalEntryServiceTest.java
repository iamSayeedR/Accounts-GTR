package com.example.accounts.service;

import com.example.accounts.dto.JournalEntryLineRequest;
import com.example.accounts.dto.JournalEntryRequest;
import com.example.accounts.dto.JournalEntryResponse;
import com.example.accounts.entity.*;
import com.example.accounts.entity.enums.*;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.InvalidTransactionException;
import com.example.accounts.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalEntryServiceTest {

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @Mock
    private ChartOfAccountRepository chartOfAccountRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private JournalEntryService journalEntryService;

    private ChartOfAccount debitAccount;
    private ChartOfAccount creditAccount;
    private JournalEntryRequest balancedRequest;
    private JournalEntryRequest unbalancedRequest;

    @BeforeEach
    void setUp() {
        debitAccount = ChartOfAccount.builder()
                .accountId(1L)
                .accountCode("1000")
                .description("Cash")
                .accountType(AccountBalanceType.DEBIT)
                .build();

        creditAccount = ChartOfAccount.builder()
                .accountId(2L)
                .accountCode("4000")
                .description("Sales Revenue")
                .accountType(AccountBalanceType.CREDIT)
                .build();

        // Balanced journal entry
        JournalEntryLineRequest debitLine = JournalEntryLineRequest.builder()
                .lineNumber(1)
                .accountId(1L)
                .debitAmount(new BigDecimal("1000.00"))
                .creditAmount(BigDecimal.ZERO)
                .description("Cash received")
                .build();

        JournalEntryLineRequest creditLine = JournalEntryLineRequest.builder()
                .lineNumber(2)
                .accountId(2L)
                .debitAmount(BigDecimal.ZERO)
                .creditAmount(new BigDecimal("1000.00"))
                .description("Sales revenue")
                .build();

        balancedRequest = JournalEntryRequest.builder()
                .entryNumber("JE-001")
                .entryDate(LocalDate.now())
                .documentType(DocumentType.JOURNAL_ENTRY)
                .description("Test journal entry")
                .lines(Arrays.asList(debitLine, creditLine))
                .build();

        // Unbalanced journal entry
        JournalEntryLineRequest unbalancedDebitLine = JournalEntryLineRequest.builder()
                .lineNumber(1)
                .accountId(1L)
                .debitAmount(new BigDecimal("1000.00"))
                .creditAmount(BigDecimal.ZERO)
                .build();

        JournalEntryLineRequest unbalancedCreditLine = JournalEntryLineRequest.builder()
                .lineNumber(2)
                .accountId(2L)
                .debitAmount(BigDecimal.ZERO)
                .creditAmount(new BigDecimal("500.00"))
                .build();

        unbalancedRequest = JournalEntryRequest.builder()
                .entryNumber("JE-002")
                .entryDate(LocalDate.now())
                .documentType(DocumentType.JOURNAL_ENTRY)
                .description("Unbalanced entry")
                .lines(Arrays.asList(unbalancedDebitLine, unbalancedCreditLine))
                .build();
    }

    @Test
    void createJournalEntry_Balanced_Success() {
        when(journalEntryRepository.existsByEntryNumber("JE-001")).thenReturn(false);
        when(chartOfAccountRepository.findById(1L)).thenReturn(Optional.of(debitAccount));
        when(chartOfAccountRepository.findById(2L)).thenReturn(Optional.of(creditAccount));
        when(journalEntryRepository.save(any(JournalEntry.class))).thenAnswer(invocation -> {
            JournalEntry je = invocation.getArgument(0);
            je.setJournalEntryId(1L);
            return je;
        });

        JournalEntryResponse response = journalEntryService.createJournalEntry(balancedRequest);

        assertNotNull(response);
        assertEquals("JE-001", response.getEntryNumber());
        assertEquals(new BigDecimal("1000.00"), response.getTotalDebit());
        assertEquals(new BigDecimal("1000.00"), response.getTotalCredit());
        assertEquals(2, response.getLines().size());
        verify(journalEntryRepository, times(1)).save(any(JournalEntry.class));
    }

    @Test
    void createJournalEntry_Unbalanced_ThrowsException() {
        when(journalEntryRepository.existsByEntryNumber("JE-002")).thenReturn(false);
        when(chartOfAccountRepository.findById(1L)).thenReturn(Optional.of(debitAccount));
        when(chartOfAccountRepository.findById(2L)).thenReturn(Optional.of(creditAccount));

        assertThrows(InvalidTransactionException.class, () -> {
            journalEntryService.createJournalEntry(unbalancedRequest);
        });

        verify(journalEntryRepository, never()).save(any(JournalEntry.class));
    }

    @Test
    void createJournalEntry_DuplicateNumber_ThrowsException() {
        when(journalEntryRepository.existsByEntryNumber("JE-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            journalEntryService.createJournalEntry(balancedRequest);
        });

        verify(journalEntryRepository, never()).save(any(JournalEntry.class));
    }

    @Test
    void postJournalEntry_Success() {
        JournalEntry draftEntry = JournalEntry.builder()
                .journalEntryId(1L)
                .entryNumber("JE-001")
                .status(JournalEntryStatus.DRAFT)
                .totalDebit(new BigDecimal("1000.00"))
                .totalCredit(new BigDecimal("1000.00"))
                .build();

        when(journalEntryRepository.findById(1L)).thenReturn(Optional.of(draftEntry));
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(draftEntry);

        JournalEntryResponse response = journalEntryService.postJournalEntry(1L, "admin");

        assertNotNull(response);
        assertEquals(JournalEntryStatus.POSTED, response.getStatus());
        assertNotNull(response.getPostedDate());
        assertEquals("admin", response.getPostedBy());
    }

    @Test
    void postJournalEntry_AlreadyPosted_ThrowsException() {
        JournalEntry postedEntry = JournalEntry.builder()
                .journalEntryId(1L)
                .status(JournalEntryStatus.POSTED)
                .build();

        when(journalEntryRepository.findById(1L)).thenReturn(Optional.of(postedEntry));

        assertThrows(InvalidTransactionException.class, () -> {
            journalEntryService.postJournalEntry(1L, "admin");
        });
    }

    @Test
    void reverseJournalEntry_Success() {
        JournalEntry postedEntry = JournalEntry.builder()
                .journalEntryId(1L)
                .status(JournalEntryStatus.POSTED)
                .build();

        when(journalEntryRepository.findById(1L)).thenReturn(Optional.of(postedEntry));
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(postedEntry);

        JournalEntryResponse response = journalEntryService.reverseJournalEntry(1L, "admin");

        assertEquals(JournalEntryStatus.REVERSED, response.getStatus());
    }

    @Test
    void deleteJournalEntry_Draft_Success() {
        JournalEntry draftEntry = JournalEntry.builder()
                .journalEntryId(1L)
                .status(JournalEntryStatus.DRAFT)
                .build();

        when(journalEntryRepository.findById(1L)).thenReturn(Optional.of(draftEntry));
        doNothing().when(journalEntryRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            journalEntryService.deleteJournalEntry(1L);
        });

        verify(journalEntryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteJournalEntry_Posted_ThrowsException() {
        JournalEntry postedEntry = JournalEntry.builder()
                .journalEntryId(1L)
                .status(JournalEntryStatus.POSTED)
                .build();

        when(journalEntryRepository.findById(1L)).thenReturn(Optional.of(postedEntry));

        assertThrows(InvalidTransactionException.class, () -> {
            journalEntryService.deleteJournalEntry(1L);
        });

        verify(journalEntryRepository, never()).deleteById(any());
    }
}
