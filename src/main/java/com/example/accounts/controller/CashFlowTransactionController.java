package com.example.accounts.controller;

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

@RestController
@RequestMapping("/api/cash-flow-transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5174")
@Tag(name = "Cash Flow Transactions", description = "APIs for managing cash flow statement transactions")
public class CashFlowTransactionController {

    private final CashFlowStatementService service;

    @PostMapping
    @Operation(summary = "Create a new cash flow transaction")
    public ResponseEntity<CashFlowTransactionResponse> create(@RequestBody CashFlowTransactionRequest request) {
        return new ResponseEntity<>(service.createTransaction(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cash flow transaction by ID")
    public ResponseEntity<CashFlowTransactionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all cash flow transactions")
    public ResponseEntity<List<CashFlowTransactionResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get cash flow transactions by date range")
    public ResponseEntity<List<CashFlowTransactionResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(service.getByDateRange(startDate, endDate));
    }

    @GetMapping("/unposted")
    @Operation(summary = "Get unposted cash flow transactions")
    public ResponseEntity<List<CashFlowTransactionResponse>> getUnposted() {
        return ResponseEntity.ok(service.getUnposted());
    }

    @PostMapping("/{id}/post")
    @Operation(summary = "Post cash flow transaction")
    public ResponseEntity<Void> post(@PathVariable Long id) {
        service.postTransaction(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update cash flow transaction")
    public ResponseEntity<CashFlowTransactionResponse> update(@PathVariable Long id,
            @RequestBody CashFlowTransactionRequest request) {
        return ResponseEntity.ok(service.updateTransaction(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cash flow transaction")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
