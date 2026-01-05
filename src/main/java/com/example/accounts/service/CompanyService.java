package com.example.accounts.service;

import com.example.accounts.dto.CompanyGLAccountResponse;
import com.example.accounts.dto.CompanyRequest;
import com.example.accounts.dto.CompanyResponse;
import com.example.accounts.entity.ChartOfAccount;
import com.example.accounts.entity.Company;
import com.example.accounts.entity.CompanyGLAccount;
import com.example.accounts.entity.enums.CompanyType;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.ChartOfAccountRepository;
import com.example.accounts.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;

    public CompanyResponse createCompany(CompanyRequest request) {
        if (companyRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Company code already exists: " + request.getCode());
        }

        Company company = Company.builder()
                .code(request.getCode())
                .name(request.getName())
                .fullDescription(request.getFullDescription())
                .companyType(request.getCompanyType())
                .taxId(request.getTaxId())
                .legalAddress(request.getLegalAddress())
                .deliveryAddress(request.getDeliveryAddress())
                .contactPerson(request.getContactPerson())
                .bankAccount(request.getBankAccount())
                .isVATTaxpayer(request.getIsVATTaxpayer())
                .pricesIncludeVAT(request.getPricesIncludeVAT())
                .isActive(true)
                .build();

        if (request.getGlAccounts() != null) {
            CompanyGLAccount glAccount = createCompanyGLAccount(request.getGlAccounts());
            glAccount.setCompany(company);
            company.setCompanyGLAccount(glAccount);
        }

        Company saved = companyRepository.save(company);
        return mapToResponse(saved);
    }

    public CompanyResponse getCompanyById(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        return mapToResponse(company);
    }

    public CompanyResponse getCompanyByCode(String code) {
        Company company = companyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with code: " + code));
        return mapToResponse(company);
    }

    public List<CompanyResponse> getCompaniesByType(CompanyType companyType) {
        return companyRepository.findByCompanyType(companyType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CompanyResponse> getActiveCompanies() {
        return companyRepository.findByIsActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CompanyResponse updateCompany(Long companyId, CompanyRequest request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        if (!company.getCode().equals(request.getCode()) && companyRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Company code already exists: " + request.getCode());
        }

        company.setCode(request.getCode());
        company.setName(request.getName());
        company.setFullDescription(request.getFullDescription());
        company.setCompanyType(request.getCompanyType());
        company.setTaxId(request.getTaxId());
        company.setLegalAddress(request.getLegalAddress());
        company.setDeliveryAddress(request.getDeliveryAddress());
        company.setContactPerson(request.getContactPerson());
        company.setBankAccount(request.getBankAccount());
        company.setIsVATTaxpayer(request.getIsVATTaxpayer());
        company.setPricesIncludeVAT(request.getPricesIncludeVAT());

        if (request.getGlAccounts() != null) {
            if (company.getCompanyGLAccount() == null) {
                CompanyGLAccount glAccount = createCompanyGLAccount(request.getGlAccounts());
                glAccount.setCompany(company);
                company.setCompanyGLAccount(glAccount);
            } else {
                updateCompanyGLAccount(company.getCompanyGLAccount(), request.getGlAccounts());
            }
        }

        Company updated = companyRepository.save(company);
        return mapToResponse(updated);
    }

    public CompanyResponse activateCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        company.setIsActive(true);
        return mapToResponse(companyRepository.save(company));
    }

    public CompanyResponse deactivateCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        company.setIsActive(false);
        return mapToResponse(companyRepository.save(company));
    }

    public void deleteCompany(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with id: " + companyId);
        }
        companyRepository.deleteById(companyId);
    }

    private CompanyGLAccount createCompanyGLAccount(com.example.accounts.dto.CompanyGLAccountRequest request) {
        return CompanyGLAccount.builder()
                .accountsReceivable(getAccountById(request.getAccountsReceivableId()))
                .advancesReceived(getAccountById(request.getAdvancesReceivedId()))
                .pdcsReceived(getAccountById(request.getPdcsReceivedId()))
                .contractAssets(getAccountById(request.getContractAssetsId()))
                .retentionReceivables(getAccountById(request.getRetentionReceivablesId()))
                .retentionOutputVAT(getAccountById(request.getRetentionOutputVATId()))
                .accountsPayable(getAccountById(request.getAccountsPayableId()))
                .advancesPaid(getAccountById(request.getAdvancesPaidId()))
                .pdcsIssued(getAccountById(request.getPdcsIssuedId()))
                .retentionPayables(getAccountById(request.getRetentionPayablesId()))
                .retentionInputVAT(getAccountById(request.getRetentionInputVATId()))
                .unbilledPurchases(getAccountById(request.getUnbilledPurchasesId()))
                .advanceToReceive(getAccountById(request.getAdvanceToReceiveId()))
                .build();
    }

    private void updateCompanyGLAccount(CompanyGLAccount glAccount,
            com.example.accounts.dto.CompanyGLAccountRequest request) {
        glAccount.setAccountsReceivable(getAccountById(request.getAccountsReceivableId()));
        glAccount.setAdvancesReceived(getAccountById(request.getAdvancesReceivedId()));
        glAccount.setPdcsReceived(getAccountById(request.getPdcsReceivedId()));
        glAccount.setContractAssets(getAccountById(request.getContractAssetsId()));
        glAccount.setRetentionReceivables(getAccountById(request.getRetentionReceivablesId()));
        glAccount.setRetentionOutputVAT(getAccountById(request.getRetentionOutputVATId()));
        glAccount.setAccountsPayable(getAccountById(request.getAccountsPayableId()));
        glAccount.setAdvancesPaid(getAccountById(request.getAdvancesPaidId()));
        glAccount.setPdcsIssued(getAccountById(request.getPdcsIssuedId()));
        glAccount.setRetentionPayables(getAccountById(request.getRetentionPayablesId()));
        glAccount.setRetentionInputVAT(getAccountById(request.getRetentionInputVATId()));
        glAccount.setUnbilledPurchases(getAccountById(request.getUnbilledPurchasesId()));
        glAccount.setAdvanceToReceive(getAccountById(request.getAdvanceToReceiveId()));
    }

    private ChartOfAccount getAccountById(Long accountId) {
        if (accountId == null)
            return null;
        return chartOfAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Chart of Account not found with id: " + accountId));
    }

    private CompanyResponse mapToResponse(Company company) {
        CompanyGLAccountResponse glAccountResponse = null;
        if (company.getCompanyGLAccount() != null) {
            CompanyGLAccount gla = company.getCompanyGLAccount();
            glAccountResponse = CompanyGLAccountResponse.builder()
                    .accountsReceivableId(
                            gla.getAccountsReceivable() != null ? gla.getAccountsReceivable().getAccountId() : null)
                    .accountsReceivableCode(
                            gla.getAccountsReceivable() != null ? gla.getAccountsReceivable().getAccountCode() : null)
                    .advancesReceivedId(
                            gla.getAdvancesReceived() != null ? gla.getAdvancesReceived().getAccountId() : null)
                    .advancesReceivedCode(
                            gla.getAdvancesReceived() != null ? gla.getAdvancesReceived().getAccountCode() : null)
                    .pdcsReceivedId(gla.getPdcsReceived() != null ? gla.getPdcsReceived().getAccountId() : null)
                    .pdcsReceivedCode(gla.getPdcsReceived() != null ? gla.getPdcsReceived().getAccountCode() : null)
                    .contractAssetsId(gla.getContractAssets() != null ? gla.getContractAssets().getAccountId() : null)
                    .contractAssetsCode(
                            gla.getContractAssets() != null ? gla.getContractAssets().getAccountCode() : null)
                    .retentionReceivablesId(
                            gla.getRetentionReceivables() != null ? gla.getRetentionReceivables().getAccountId() : null)
                    .retentionReceivablesCode(
                            gla.getRetentionReceivables() != null ? gla.getRetentionReceivables().getAccountCode()
                                    : null)
                    .retentionOutputVATId(
                            gla.getRetentionOutputVAT() != null ? gla.getRetentionOutputVAT().getAccountId() : null)
                    .retentionOutputVATCode(
                            gla.getRetentionOutputVAT() != null ? gla.getRetentionOutputVAT().getAccountCode() : null)
                    .accountsPayableId(
                            gla.getAccountsPayable() != null ? gla.getAccountsPayable().getAccountId() : null)
                    .accountsPayableCode(
                            gla.getAccountsPayable() != null ? gla.getAccountsPayable().getAccountCode() : null)
                    .advancesPaidId(gla.getAdvancesPaid() != null ? gla.getAdvancesPaid().getAccountId() : null)
                    .advancesPaidCode(gla.getAdvancesPaid() != null ? gla.getAdvancesPaid().getAccountCode() : null)
                    .pdcsIssuedId(gla.getPdcsIssued() != null ? gla.getPdcsIssued().getAccountId() : null)
                    .pdcsIssuedCode(gla.getPdcsIssued() != null ? gla.getPdcsIssued().getAccountCode() : null)
                    .retentionPayablesId(
                            gla.getRetentionPayables() != null ? gla.getRetentionPayables().getAccountId() : null)
                    .retentionPayablesCode(
                            gla.getRetentionPayables() != null ? gla.getRetentionPayables().getAccountCode() : null)
                    .retentionInputVATId(
                            gla.getRetentionInputVAT() != null ? gla.getRetentionInputVAT().getAccountId() : null)
                    .retentionInputVATCode(
                            gla.getRetentionInputVAT() != null ? gla.getRetentionInputVAT().getAccountCode() : null)
                    .unbilledPurchasesId(
                            gla.getUnbilledPurchases() != null ? gla.getUnbilledPurchases().getAccountId() : null)
                    .unbilledPurchasesCode(
                            gla.getUnbilledPurchases() != null ? gla.getUnbilledPurchases().getAccountCode() : null)
                    .advanceToReceiveId(
                            gla.getAdvanceToReceive() != null ? gla.getAdvanceToReceive().getAccountId() : null)
                    .advanceToReceiveCode(
                            gla.getAdvanceToReceive() != null ? gla.getAdvanceToReceive().getAccountCode() : null)
                    .build();
        }

        return CompanyResponse.builder()
                .companyId(company.getCompanyId())
                .code(company.getCode())
                .name(company.getName())
                .fullDescription(company.getFullDescription())
                .companyType(company.getCompanyType())
                .taxId(company.getTaxId())
                .legalAddress(company.getLegalAddress())
                .deliveryAddress(company.getDeliveryAddress())
                .contactPerson(company.getContactPerson())
                .bankAccount(company.getBankAccount())
                .isVATTaxpayer(company.getIsVATTaxpayer())
                .pricesIncludeVAT(company.getPricesIncludeVAT())
                .isActive(company.getIsActive())
                .glAccounts(glAccountResponse)
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }
}
