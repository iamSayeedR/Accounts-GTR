package com.example.accounts.controller;

import com.example.accounts.dto.CashFlowStatementDTO;
import com.example.accounts.dto.CashFlowTransactionRequest;
import com.example.accounts.dto.CashFlowTransactionResponse;
import com.example.accounts.service.CashFlowStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Cash Flow Statement
 */
@RestController
@RequestMapping("/api/cash-flow")
@CrossOrigin(origins = "http://localhost:5174")
@RequiredArgsConstructor
@Tag(name = "Cash Flow Statement", description = "Manage cash flow transactions and generate statements")
public class CashFlowStatementController {

    private final CashFlowStatementService cashFlowStatementService;

    @PostMapping("/transactions")
    @Operation(summary = "Create cash flow transaction", description = "Record a new cash inflow or outflow transaction")
    public ResponseEntity<CashFlowTransactionResponse> createTransaction(
            @RequestBody CashFlowTransactionRequest request) {
        CashFlowTransactionResponse response = cashFlowStatementService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/transactions/{id}/post")
    @Operation(summary = "Post transaction", description = "Post a cash flow transaction")
    public ResponseEntity<Void> postTransaction(@PathVariable Long id) {
        cashFlowStatementService.postTransaction(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transactions/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieve cash flow transaction details")
    public ResponseEntity<CashFlowTransactionResponse> getById(@PathVariable Long id) {
        CashFlowTransactionResponse response = cashFlowStatementService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get all transactions", description = "Retrieve all cash flow transactions")
    public ResponseEntity<List<CashFlowTransactionResponse>> getAll() {
        List<CashFlowTransactionResponse> responses = cashFlowStatementService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/transactions/date-range")
    @Operation(summary = "Get transactions by date range", description = "Retrieve transactions within a date range")
    public ResponseEntity<List<CashFlowTransactionResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CashFlowTransactionResponse> responses = cashFlowStatementService.getByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/transactions/unposted")
    @Operation(summary = "Get unposted transactions", description = "Retrieve all unposted cash flow transactions")
    public ResponseEntity<List<CashFlowTransactionResponse>> getUnposted() {
        List<CashFlowTransactionResponse> responses = cashFlowStatementService.getUnposted();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/statement")
    @Operation(summary = "Generate cash flow statement", description = "Generate cash flow statement for a period")
    public ResponseEntity<CashFlowStatementDTO> generateStatement(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String entity) {
        CashFlowStatementDTO statement = cashFlowStatementService.generateStatement(startDate, endDate, entity);
        return ResponseEntity.ok(statement);
    }
}
