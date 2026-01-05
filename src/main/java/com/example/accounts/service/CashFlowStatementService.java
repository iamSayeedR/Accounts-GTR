package com.example.accounts.service;
import com.example.accounts.dto.*;
import com.example.accounts.entity.CashFlowItem;
import com.example.accounts.entity.CashFlowTransaction;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.CashFlowItemRepository;
import com.example.accounts.repository.CashFlowTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for Cash Flow Statement operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CashFlowStatementService {

    private final CashFlowTransactionRepository transactionRepository;
    private final CashFlowItemRepository cashFlowItemRepository;

    /**
     * Create cash flow transaction
     */
    public CashFlowTransactionResponse createTransaction(CashFlowTransactionRequest request) {
        log.info("Creating cash flow transaction: {}", request.getTransactionNumber());

        CashFlowItem item = cashFlowItemRepository.findById(request.getCashFlowItemId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cash flow item not found with id: " + request.getCashFlowItemId()));

        CashFlowTransaction transaction = new CashFlowTransaction();
        transaction.setTransactionNumber(request.getTransactionNumber());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setCashFlowItem(item);
        transaction.setFlowType(request.getFlowType());
        transaction.setCategory(request.getCategory());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setEntity(request.getEntity());
        transaction.setCurrency(request.getCurrency());
        transaction.setReferenceNumber(request.getReferenceNumber());
        transaction.setNotes(request.getNotes());

        CashFlowTransaction saved = transactionRepository.save(transaction);
        log.info("Created cash flow transaction with id: {}", saved.getTransactionId());

        return toResponse(saved);
    }

    /**
     * Post transaction
     */
    public void postTransaction(Long transactionId) {
        log.info("Posting cash flow transaction: {}", transactionId);

        CashFlowTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cash flow transaction not found with id: " + transactionId));

        transaction.setIsPosted(true);
        transaction.setPostedDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        log.info("Posted cash flow transaction: {}", transactionId);
    }

    /**
     * Generate cash flow statement
     */
    @Transactional(readOnly = true)
    public CashFlowStatementDTO generateStatement(LocalDate startDate, LocalDate endDate, String entity) {
        log.info("Generating cash flow statement from {} to {} for entity: {}", startDate, endDate, entity);

        CashFlowStatementDTO statement = new CashFlowStatementDTO();
        statement.setStartDate(startDate);
        statement.setEndDate(endDate);
        statement.setEntity(entity != null ? entity : "All entities");
        statement.setCurrency("Dirham (UAE)");

        // Set period description
        if (startDate == null && endDate == null) {
            statement.setPeriod("Data for all time");
        } else {
            statement.setPeriod(String.format("Data from %s to %s", startDate, endDate));
        }

        // Get transactions for the period
        List<CashFlowTransaction> transactions;
        if (entity != null && !entity.isEmpty()) {
            if (startDate != null && endDate != null) {
                transactions = transactionRepository.findByEntityAndDateRange(entity, startDate, endDate);
            } else {
                transactions = transactionRepository.findByEntity(entity);
            }
        } else {
            if (startDate != null && endDate != null) {
                transactions = transactionRepository.findByDateRange(startDate, endDate);
            } else {
                transactions = transactionRepository.findAll();
            }
        }

        // Group transactions by cash flow item
        Map<Long, List<CashFlowTransaction>> groupedByItem = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getCashFlowItem().getCashFlowItemId()));

        // Process inflow items
        List<CashFlowLineItemDTO> inflowItems = new ArrayList<>();
        for (Map.Entry<Long, List<CashFlowTransaction>> entry : groupedByItem.entrySet()) {
            List<CashFlowTransaction> itemTransactions = entry.getValue();
            CashFlowTransaction firstTransaction = itemTransactions.get(0);

            if ("INFLOW".equals(firstTransaction.getFlowType())) {
                BigDecimal totalAmount = itemTransactions.stream()
                        .map(CashFlowTransaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                CashFlowLineItemDTO lineItem = new CashFlowLineItemDTO();
                lineItem.setItemCode(firstTransaction.getCashFlowItem().getCode());
                lineItem.setItemDescription(firstTransaction.getCashFlowItem().getDescription());
                lineItem.setFlowType("INFLOW");
                lineItem.setCategory(firstTransaction.getCategory());
                lineItem.setAmount(totalAmount);
                lineItem.setDisplayOrder(firstTransaction.getCashFlowItem().getDisplayOrder());

                inflowItems.add(lineItem);
            }
        }

        // Sort inflow items by display order
        inflowItems.sort(Comparator.comparing(
                item -> item.getDisplayOrder() != null ? item.getDisplayOrder() : Integer.MAX_VALUE));
        statement.setInflowItems(inflowItems);

        // Process outflow items
        List<CashFlowLineItemDTO> outflowItems = new ArrayList<>();
        for (Map.Entry<Long, List<CashFlowTransaction>> entry : groupedByItem.entrySet()) {
            List<CashFlowTransaction> itemTransactions = entry.getValue();
            CashFlowTransaction firstTransaction = itemTransactions.get(0);

            if ("OUTFLOW".equals(firstTransaction.getFlowType())) {
                BigDecimal totalAmount = itemTransactions.stream()
                        .map(CashFlowTransaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                CashFlowLineItemDTO lineItem = new CashFlowLineItemDTO();
                lineItem.setItemCode(firstTransaction.getCashFlowItem().getCode());
                lineItem.setItemDescription(firstTransaction.getCashFlowItem().getDescription());
                lineItem.setFlowType("OUTFLOW");
                lineItem.setCategory(firstTransaction.getCategory());
                lineItem.setAmount(totalAmount);
                lineItem.setDisplayOrder(firstTransaction.getCashFlowItem().getDisplayOrder());

                outflowItems.add(lineItem);
            }
        }

        // Sort outflow items by display order
        outflowItems.sort(Comparator.comparing(
                item -> item.getDisplayOrder() != null ? item.getDisplayOrder() : Integer.MAX_VALUE));
        statement.setOutflowItems(outflowItems);

        // Calculate totals
        statement.calculateTotals();

        log.info("Generated cash flow statement with {} inflow items and {} outflow items",
                inflowItems.size(), outflowItems.size());

        return statement;
    }

    /**
     * Get transaction by ID
     */
    @Transactional(readOnly = true)
    public CashFlowTransactionResponse getById(Long id) {
        CashFlowTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cash flow transaction not found with id: " + id));
        return toResponse(transaction);
    }

    /**
     * Get all transactions
     */
    @Transactional(readOnly = true)
    public List<CashFlowTransactionResponse> getAll() {
        return transactionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get transactions by date range
     */
    @Transactional(readOnly = true)
    public List<CashFlowTransactionResponse> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByDateRange(startDate, endDate).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get unposted transactions
     */
    @Transactional(readOnly = true)
    public List<CashFlowTransactionResponse> getUnposted() {
        return transactionRepository.findUnposted().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to response DTO
     */
    private CashFlowTransactionResponse toResponse(CashFlowTransaction transaction) {
        CashFlowTransactionResponse response = new CashFlowTransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setTransactionNumber(transaction.getTransactionNumber());
        response.setTransactionDate(transaction.getTransactionDate());

        if (transaction.getCashFlowItem() != null) {
            response.setCashFlowItemId(transaction.getCashFlowItem().getCashFlowItemId());
            response.setCashFlowItemCode(transaction.getCashFlowItem().getCode());
            response.setCashFlowItemDescription(transaction.getCashFlowItem().getDescription());
        }

        response.setFlowType(transaction.getFlowType());
        response.setCategory(transaction.getCategory());
        response.setAmount(transaction.getAmount());
        response.setDescription(transaction.getDescription());
        response.setEntity(transaction.getEntity());
        response.setCurrency(transaction.getCurrency());
        response.setIsPosted(transaction.getIsPosted());
        response.setPostedDate(transaction.getPostedDate());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setNotes(transaction.getNotes());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setCreatedBy(transaction.getCreatedBy());

        return response;
    }
}
