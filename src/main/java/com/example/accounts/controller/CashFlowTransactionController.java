package com.example.accounts.controller;

import com.example.accounts.dto.CashFlowTransactionRequest;
import com.example.accounts.dto.CashFlowTransactionResponse;
import com.example.accounts.service.CashFlowStatementService;
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
public class CashFlowTransactionController {

    private final CashFlowStatementService service;

    @PostMapping
    public ResponseEntity<CashFlowTransactionResponse> create(@RequestBody CashFlowTransactionRequest request) {
        return new ResponseEntity<>(service.createTransaction(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashFlowTransactionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<CashFlowTransactionResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<CashFlowTransactionResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(service.getByDateRange(startDate, endDate));
    }

    @GetMapping("/unposted")
    public ResponseEntity<List<CashFlowTransactionResponse>> getUnposted() {
        return ResponseEntity.ok(service.getUnposted());
    }

    @PostMapping("/{id}/post")
    public ResponseEntity<Void> post(@PathVariable Long id) {
        service.postTransaction(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CashFlowTransactionResponse> update(@PathVariable Long id,
            @RequestBody CashFlowTransactionRequest request) {
        return ResponseEntity.ok(service.updateTransaction(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
