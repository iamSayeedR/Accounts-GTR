package com.example.accounts.service;

import com.example.accounts.dto.*;
import com.example.accounts.entity.EquityAccount;
import com.example.accounts.entity.EquityTransaction;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.EquityAccountRepository;
import com.example.accounts.repository.EquityTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Equity Statement
 * ALL CALCULATIONS ARE 100% DYNAMIC - NO HARD-CODED VALUES
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EquityStatementService {

    private final EquityTransactionRepository transactionRepository;
    private final EquityAccountRepository accountRepository;

    /**
     * Generate statement for ANY year range - completely dynamic
     */
    @Transactional(readOnly = true)
    public EquityStatementDTO generateStatement(Integer startYear, Integer endYear, String companyName) {
        log.info("Generating equity statement for years {} to {} for company: {}", startYear, endYear, companyName);

        EquityStatementDTO statement = new EquityStatementDTO();
        statement.setStartYear(startYear);
        statement.setEndYear(endYear);
        statement.setCompanyName(companyName != null ? companyName : "All Companies");
        statement.setCurrency("Dirham (UAE)");
        statement.generatePeriodDescription();

        // Get all active equity accounts DYNAMICALLY from database
        List<EquityAccount> accounts = accountRepository.findAllActiveOrdered();

        // Build column headers DYNAMICALLY
        List<EquityColumnDTO> columns = accounts.stream()
                .map(this::toColumnDTO)
                .collect(Collectors.toList());

        // Add Total Equity column
        EquityColumnDTO totalColumn = new EquityColumnDTO();
        totalColumn.setColumnName("Total Equity");
        totalColumn.setAccountType("TOTAL");
        totalColumn.setDisplayOrder(999);
        columns.add(totalColumn);

        statement.setEquityColumns(columns);

        // Build rows DYNAMICALLY for each year
        List<EquityRowDTO> allRows = new ArrayList<>();

        // Process each year in range
        for (int year = startYear; year <= endYear; year++) {
            allRows.addAll(generateRowsForYear(year, accounts));
        }

        statement.setRows(allRows);

        log.info("Generated equity statement with {} rows", allRows.size());
        return statement;
    }

    /**
     * Generate rows for a specific year - COMPLETELY DYNAMIC
     */
    private List<EquityRowDTO> generateRowsForYear(Integer year, List<EquityAccount> accounts) {
        List<EquityRowDTO> rows = new ArrayList<>();

        // 1. Opening Balance Row - CALCULATED DYNAMICALLY
        EquityRowDTO openingRow = new EquityRowDTO();
        openingRow.setRowId("opening_" + year);
        openingRow.setRowDescription("Balance at 1 January " + year);
        openingRow.setRowType("OPENING");
        openingRow.setIsBold(true);

        BigDecimal totalOpening = BigDecimal.ZERO;
        for (EquityAccount account : accounts) {
            // DYNAMIC CALCULATION: Get opening balance from previous year's closing
            BigDecimal opening = transactionRepository.getOpeningBalance(account.getEquityAccountId(), year);
            openingRow.addValue(account.getAccountType(), opening);
            totalOpening = totalOpening.add(opening);
        }
        openingRow.addValue("TOTAL", totalOpening);
        rows.add(openingRow);

        // 2. Comprehensive Profit or Loss Section Header
        EquityRowDTO profitHeader = new EquityRowDTO();
        profitHeader.setRowId("profit_header_" + year);
        profitHeader.setRowDescription("Comprehensive Profit or Loss");
        profitHeader.setRowType("SECTION_HEADER");
        profitHeader.setIsBold(true);
        rows.add(profitHeader);

        // 3. Profit for the Year - CALCULATED DYNAMICALLY
        rows.add(createTransactionRow("profit_year_" + year, "Profit for the Year",
                "PROFIT_FOR_YEAR", year, accounts));

        // 4. Other Comprehensive Income Header
        EquityRowDTO ociHeader = new EquityRowDTO();
        ociHeader.setRowId("oci_header_" + year);
        ociHeader.setRowDescription("Other Comprehensive Profit or Loss");
        ociHeader.setRowType("SECTION_HEADER");
        ociHeader.setIsItalic(true);
        ociHeader.setIndentLevel(1);
        rows.add(ociHeader);

        // 5. OCI Items - CALCULATED DYNAMICALLY
        rows.add(createTransactionRow("oci_benefit_" + year, "Remeasurements of Defined Benefit Liability",
                "OCI_DEFINED_BENEFIT", year, accounts, 1));
        rows.add(createTransactionRow("oci_fair_value_" + year,
                "Revaluation of Non-Current Assets Held at Fair Value Model",
                "OCI_FAIR_VALUE", year, accounts, 1));

        // 6. Total Other Comprehensive Income - CALCULATED DYNAMICALLY
        rows.add(createTotalRow("total_oci_" + year, "Total Other Comprehensive Profit or Loss",
                List.of("OCI_DEFINED_BENEFIT", "OCI_FAIR_VALUE"), year, accounts, true, 1));

        // 7. Total Comprehensive Profit or Loss - CALCULATED DYNAMICALLY
        rows.add(createTotalRow("total_comprehensive_" + year, "Total Comprehensive Profit or Loss",
                List.of("PROFIT_FOR_YEAR", "OCI_DEFINED_BENEFIT", "OCI_FAIR_VALUE"), year, accounts, true, 0));

        // 8. Equity Transactions - CALCULATED DYNAMICALLY
        rows.add(createTransactionRow("issue_shares_" + year, "Issue of Ordinary Shares",
                "ISSUE_ORDINARY_SHARES", year, accounts));
        rows.add(createTransactionRow("treasury_" + year, "Sale or Purchase of Treasury Shares",
                "TREASURY_SHARES_TRANSACTION", year, accounts));
        rows.add(createTransactionRow("ownership_" + year, "Changes in Ownership Interests",
                "OWNERSHIP_CHANGES", year, accounts));
        rows.add(createTransactionRow("dividends_" + year, "Dividends",
                "DIVIDENDS", year, accounts));
        rows.add(createTransactionRow("contributions_" + year, "Contributions from Shareholders",
                "SHAREHOLDER_CONTRIBUTIONS", year, accounts));
        rows.add(createTransactionRow("other_" + year, "Other",
                "OTHER_EQUITY", year, accounts));

        // 9. Closing Balance Row - CALCULATED DYNAMICALLY
        EquityRowDTO closingRow = new EquityRowDTO();
        closingRow.setRowId("closing_" + year);
        closingRow.setRowDescription("Balance at 31 December " + year);
        closingRow.setRowType("CLOSING");
        closingRow.setIsBold(true);

        BigDecimal totalClosing = BigDecimal.ZERO;
        for (EquityAccount account : accounts) {
            // DYNAMIC CALCULATION: Sum all transactions up to this year
            BigDecimal closing = transactionRepository.getClosingBalance(account.getEquityAccountId(), year);
            closingRow.addValue(account.getAccountType(), closing);
            totalClosing = totalClosing.add(closing);
        }
        closingRow.addValue("TOTAL", totalClosing);
        rows.add(closingRow);

        return rows;
    }

    /**
     * Create transaction row - DYNAMIC CALCULATION
     */
    private EquityRowDTO createTransactionRow(String rowId, String description, String transactionType,
            Integer year, List<EquityAccount> accounts) {
        return createTransactionRow(rowId, description, transactionType, year, accounts, 0);
    }

    /**
     * Create transaction row with indent - DYNAMIC CALCULATION
     */
    private EquityRowDTO createTransactionRow(String rowId, String description, String transactionType,
            Integer year, List<EquityAccount> accounts, Integer indentLevel) {
        EquityRowDTO row = new EquityRowDTO();
        row.setRowId(rowId);
        row.setRowDescription(description);
        row.setRowType("TRANSACTION");
        row.setCategory(transactionType);
        row.setIndentLevel(indentLevel);

        BigDecimal total = BigDecimal.ZERO;
        for (EquityAccount account : accounts) {
            // DYNAMIC CALCULATION: Get sum of all transactions of this type for this
            // account and year
            BigDecimal amount = transactionRepository.getTotalByAccountTypeAndYear(
                    account.getEquityAccountId(), transactionType, year);
            row.addValue(account.getAccountType(), amount);
            total = total.add(amount);
        }
        row.addValue("TOTAL", total);

        return row;
    }

    /**
     * Create total row - DYNAMIC CALCULATION
     */
    private EquityRowDTO createTotalRow(String rowId, String description, List<String> transactionTypes,
            Integer year, List<EquityAccount> accounts, Boolean isBold, Integer indentLevel) {
        EquityRowDTO row = new EquityRowDTO();
        row.setRowId(rowId);
        row.setRowDescription(description);
        row.setRowType("TOTAL");
        row.setIsTotal(true);
        row.setIsBold(isBold);
        row.setIndentLevel(indentLevel);

        BigDecimal total = BigDecimal.ZERO;
        for (EquityAccount account : accounts) {
            BigDecimal accountTotal = BigDecimal.ZERO;
            for (String type : transactionTypes) {
                // DYNAMIC CALCULATION: Sum multiple transaction types
                BigDecimal amount = transactionRepository.getTotalByAccountTypeAndYear(
                        account.getEquityAccountId(), type, year);
                accountTotal = accountTotal.add(amount);
            }
            row.addValue(account.getAccountType(), accountTotal);
            total = total.add(accountTotal);
        }
        row.addValue("TOTAL", total);

        return row;
    }

    /**
     * Create equity transaction - DYNAMIC
     */
    public EquityTransactionResponse createTransaction(EquityTransactionRequest request) {
        log.info("Creating equity transaction for year: {}", request.getFiscalYear());

        EquityAccount account = accountRepository.findById(request.getEquityAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equity account not found with id: " + request.getEquityAccountId()));

        EquityTransaction transaction = new EquityTransaction();
        transaction.setTransactionNumber(request.getTransactionNumber());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setEquityAccount(account);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setFiscalYear(request.getFiscalYear());
        transaction.setFiscalPeriod(request.getFiscalPeriod());
        transaction.setCompanyName(request.getCompanyName());
        transaction.setReferenceNumber(request.getReferenceNumber());
        transaction.setNotes(request.getNotes());

        EquityTransaction saved = transactionRepository.save(transaction);
        log.info("Created equity transaction with id: {}", saved.getTransactionId());

        return toTransactionResponse(saved);
    }

    /**
     * Post transaction
     */
    public void postTransaction(Long transactionId) {
        log.info("Posting equity transaction: {}", transactionId);

        EquityTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equity transaction not found with id: " + transactionId));

        transaction.setIsPosted(true);
        transaction.setPostedDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        log.info("Posted equity transaction: {}", transactionId);
    }

    /**
     * Get transaction by ID
     */
    @Transactional(readOnly = true)
    public EquityTransactionResponse getById(Long id) {
        EquityTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equity transaction not found with id: " + id));
        return toTransactionResponse(transaction);
    }

    /**
     * Get transactions by year - DYNAMIC
     */
    @Transactional(readOnly = true)
    public List<EquityTransactionResponse> getByYear(Integer year) {
        return transactionRepository.findByFiscalYear(year).stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all fiscal years - DYNAMIC
     */
    @Transactional(readOnly = true)
    public List<Integer> getAllFiscalYears() {
        return transactionRepository.findAllFiscalYears();
    }

    /**
     * Convert to column DTO
     */
    private EquityColumnDTO toColumnDTO(EquityAccount account) {
        EquityColumnDTO dto = new EquityColumnDTO();
        dto.setEquityAccountId(account.getEquityAccountId());
        dto.setColumnName(account.getName());
        dto.setAccountType(account.getAccountType());
        dto.setDisplayOrder(account.getDisplayOrder());
        return dto;
    }

    /**
     * Convert to transaction response
     */
    private EquityTransactionResponse toTransactionResponse(EquityTransaction transaction) {
        EquityTransactionResponse response = new EquityTransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setTransactionNumber(transaction.getTransactionNumber());
        response.setTransactionDate(transaction.getTransactionDate());

        if (transaction.getEquityAccount() != null) {
            response.setEquityAccountId(transaction.getEquityAccount().getEquityAccountId());
            response.setEquityAccountCode(transaction.getEquityAccount().getCode());
            response.setEquityAccountName(transaction.getEquityAccount().getName());
            response.setAccountType(transaction.getEquityAccount().getAccountType());
        }

        response.setTransactionType(transaction.getTransactionType());
        response.setAmount(transaction.getAmount());
        response.setDescription(transaction.getDescription());
        response.setFiscalYear(transaction.getFiscalYear());
        response.setFiscalPeriod(transaction.getFiscalPeriod());
        response.setCompanyName(transaction.getCompanyName());
        response.setIsPosted(transaction.getIsPosted());
        response.setPostedDate(transaction.getPostedDate());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setNotes(transaction.getNotes());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setCreatedBy(transaction.getCreatedBy());

        return response;
    }
}
