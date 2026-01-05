package com.example.accounts.service;

import com.example.accounts.dto.ChartOfAccountRequest;
import com.example.accounts.dto.ChartOfAccountResponse;
import com.example.accounts.entity.ChartOfAccount;
import com.example.accounts.entity.enums.AccountBalanceType;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.AccountDimensionRepository;
import com.example.accounts.repository.ChartOfAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChartOfAccountServiceTest {

    @Mock
    private ChartOfAccountRepository chartOfAccountRepository;

    @Mock
    private AccountDimensionRepository accountDimensionRepository;

    @InjectMocks
    private ChartOfAccountService chartOfAccountService;

    private ChartOfAccount testAccount;
    private ChartOfAccountRequest testRequest;

    @BeforeEach
    void setUp() {
        testAccount = ChartOfAccount.builder()
                .accountId(1L)
                .accountCode("1000")
                .description("Cash")
                .accountType(AccountBalanceType.DEBIT)
                .section("BS Cash")
                .currency("USD")
                .isActive(true)
                .build();

        testRequest = ChartOfAccountRequest.builder()
                .accountCode("1000")
                .description("Cash")
                .accountType(AccountBalanceType.DEBIT)
                .section("BS Cash")
                .currency("USD")
                .build();
    }

    @Test
    void createChartOfAccount_Success() {
        when(chartOfAccountRepository.existsByAccountCode("1000")).thenReturn(false);
        when(chartOfAccountRepository.save(any(ChartOfAccount.class))).thenReturn(testAccount);

        ChartOfAccountResponse response = chartOfAccountService.createAccount(testRequest);

        assertNotNull(response);
        assertEquals("1000", response.getAccountCode());
        assertEquals("Cash", response.getDescription());
        verify(chartOfAccountRepository, times(1)).save(any(ChartOfAccount.class));
    }

    @Test
    void createChartOfAccount_DuplicateCode_ThrowsException() {
        when(chartOfAccountRepository.existsByAccountCode("1000")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            chartOfAccountService.createAccount(testRequest);
        });

        verify(chartOfAccountRepository, never()).save(any(ChartOfAccount.class));
    }

    @Test
    void getChartOfAccountById_Success() {
        when(chartOfAccountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        ChartOfAccountResponse response = chartOfAccountService.getAccountById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getAccountId());
        assertEquals("1000", response.getAccountCode());
    }

    @Test
    void getChartOfAccountById_NotFound_ThrowsException() {
        when(chartOfAccountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            chartOfAccountService.getAccountById(999L);
        });
    }

    @Test
    void getAllChartOfAccounts_Success() {
        List<ChartOfAccount> accounts = Arrays.asList(testAccount);
        when(chartOfAccountRepository.findAll()).thenReturn(accounts);

        List<ChartOfAccountResponse> responses = chartOfAccountService.getAllAccounts();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("1000", responses.get(0).getAccountCode());
    }

    @Test
    void activateChartOfAccount_Success() {
        testAccount.setIsActive(false);
        when(chartOfAccountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(chartOfAccountRepository.save(any(ChartOfAccount.class))).thenReturn(testAccount);

        ChartOfAccountResponse response = chartOfAccountService.activateAccount(1L);

        assertTrue(response.getIsActive());
        verify(chartOfAccountRepository, times(1)).save(testAccount);
    }

    @Test
    void deactivateChartOfAccount_Success() {
        when(chartOfAccountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(chartOfAccountRepository.save(any(ChartOfAccount.class))).thenReturn(testAccount);

        ChartOfAccountResponse response = chartOfAccountService.deactivateAccount(1L);

        assertFalse(response.getIsActive());
        verify(chartOfAccountRepository, times(1)).save(testAccount);
    }
}
