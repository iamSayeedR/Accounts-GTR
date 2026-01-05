package com.example.accounts.service;

import com.example.accounts.dto.CompanyGLAccountRequest;
import com.example.accounts.dto.CompanyRequest;
import com.example.accounts.dto.CompanyResponse;
import com.example.accounts.entity.ChartOfAccount;
import com.example.accounts.entity.Company;
import com.example.accounts.entity.CompanyGLAccount;
import com.example.accounts.entity.enums.CompanyType;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.repository.ChartOfAccountRepository;
import com.example.accounts.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ChartOfAccountRepository chartOfAccountRepository;

    @InjectMocks
    private CompanyService companyService;

    private Company testCompany;
    private CompanyRequest testRequest;
    private ChartOfAccount arAccount;

    @BeforeEach
    void setUp() {
        arAccount = ChartOfAccount.builder()
                .accountId(1L)
                .accountCode("1100")
                .description("Accounts Receivable")
                .build();

        CompanyGLAccount glAccount = CompanyGLAccount.builder()
                .accountsReceivable(arAccount)
                .build();

        testCompany = Company.builder()
                .companyId(1L)
                .code("CUST001")
                .name("Customer ABC")
                .companyType(CompanyType.CUSTOMER)
                .isActive(true)
                .companyGLAccount(glAccount)
                .build();

        CompanyGLAccountRequest glRequest = CompanyGLAccountRequest.builder()
                .accountsReceivableId(1L)
                .build();

        testRequest = CompanyRequest.builder()
                .code("CUST001")
                .name("Customer ABC")
                .companyType(CompanyType.CUSTOMER)
                .glAccounts(glRequest)
                .build();
    }

    @Test
    void createCompany_Success() {
        when(companyRepository.existsByCode("CUST001")).thenReturn(false);
        when(chartOfAccountRepository.findById(1L)).thenReturn(Optional.of(arAccount));
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

        CompanyResponse response = companyService.createCompany(testRequest);

        assertNotNull(response);
        assertEquals("CUST001", response.getCode());
        assertEquals("Customer ABC", response.getName());
        assertEquals(CompanyType.CUSTOMER, response.getCompanyType());
        assertNotNull(response.getGlAccounts());
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void createCompany_DuplicateCode_ThrowsException() {
        when(companyRepository.existsByCode("CUST001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            companyService.createCompany(testRequest);
        });

        verify(companyRepository, never()).save(any(Company.class));
    }
}
