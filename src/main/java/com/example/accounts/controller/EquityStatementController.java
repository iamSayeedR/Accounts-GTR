package com.example.accounts.controller;

import com.example.accounts.dto.EquityStatementDTO;
import com.example.accounts.dto.EquityTransactionRequest;
import com.example.accounts.dto.EquityTransactionResponse;
import com.example.accounts.service.EquityStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Statement of Changes in Equity
 * All endpoints support DYNAMIC year parameters
 */
@RestController
@RequestMapping("/api/equity")
@CrossOrigin(origins = "http://localhost:5174")
@RequiredArgsConstructor
@Tag(name = "Statement of Changes in Equity", description = "Manage equity transactions and generate statements")
public class EquityStatementController {

    private final EquityStatementService equityStatementService;

    @PostMapping("/transactions")
    @Operation(summary = "Create equity transaction", description = "Record equity transaction for ANY fiscal year (completely dynamic)")
    public ResponseEntity<EquityTransactionResponse> createTransaction(@RequestBody EquityTransactionRequest request) {
        EquityTransactionResponse response = equityStatementService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/transactions/{id}/post")
    @Operation(summary = "Post equity transaction", description = "Post an equity transaction")
    public ResponseEntity<Void> postTransaction(@PathVariable Long id) {
        equityStatementService.postTransaction(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transactions/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieve equity transaction details")
    public ResponseEntity<EquityTransactionResponse> getById(@PathVariable Long id) {
        EquityTransactionResponse response = equityStatementService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/year/{year}")
    @Operation(summary = "Get transactions by year", description = "Retrieve all transactions for ANY fiscal year (dynamic)")
    public ResponseEntity<List<EquityTransactionResponse>> getByYear(
            @Parameter(description = "Fiscal year (e.g., 2024, 2025, 2026, etc.)") @PathVariable Integer year) {
        List<EquityTransactionResponse> responses = equityStatementService.getByYear(year);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/transactions/years")
    @Operation(summary = "Get all fiscal years", description = "Get list of all fiscal years with transactions (dynamic)")
    public ResponseEntity<List<Integer>> getAllFiscalYears() {
        List<Integer> years = equityStatementService.getAllFiscalYears();
        return ResponseEntity.ok(years);
    }

    @GetMapping("/statement")
    @Operation(summary = "Generate statement of changes in equity", description = "Generate statement for ANY year range (e.g., 2024-2025, 2026-2027, etc.) - completely dynamic")
    public ResponseEntity<EquityStatementDTO> generateStatement(
            @Parameter(description = "Start year (e.g., 2024, 2026, etc.)") @RequestParam Integer startYear,
            @Parameter(description = "End year (e.g., 2025, 2027, etc.)") @RequestParam Integer endYear,
            @Parameter(description = "Company name (optional)") @RequestParam(required = false) String companyName) {
        EquityStatementDTO statement = equityStatementService.generateStatement(startYear, endYear, companyName);
        return ResponseEntity.ok(statement);
    }

    @GetMapping("/statement/year/{year}")
    @Operation(summary = "Generate statement for single year", description = "Generate statement for ANY single year (dynamic)")
    public ResponseEntity<EquityStatementDTO> generateStatementForYear(
            @Parameter(description = "Fiscal year (e.g., 2024, 2025, 2026, etc.)") @PathVariable Integer year,
            @Parameter(description = "Company name (optional)") @RequestParam(required = false) String companyName) {
        EquityStatementDTO statement = equityStatementService.generateStatement(year, year, companyName);
        return ResponseEntity.ok(statement);
    }
}
