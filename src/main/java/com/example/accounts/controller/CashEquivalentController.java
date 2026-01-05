package com.example.accounts.controller;

import com.example.accounts.dto.CashEquivalentRequest;
import com.example.accounts.dto.CashEquivalentResponse;
import com.example.accounts.service.CashEquivalentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cash-equivalents")
@RequiredArgsConstructor
@Tag(name = "Cash Equivalents", description = "APIs for managing Cash Equivalents")
public class CashEquivalentController {

    private final CashEquivalentService cashEquivalentService;

    @PostMapping
    @Operation(summary = "Create a new cash equivalent")
    public ResponseEntity<CashEquivalentResponse> createCashEquivalent(
            @Valid @RequestBody CashEquivalentRequest request) {
        CashEquivalentResponse response = cashEquivalentService.createCashEquivalent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cash equivalent by ID")
    public ResponseEntity<CashEquivalentResponse> getCashEquivalentById(@PathVariable Long id) {
        CashEquivalentResponse response = cashEquivalentService.getCashEquivalentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all cash equivalents")
    public ResponseEntity<List<CashEquivalentResponse>> getAllCashEquivalents() {
        List<CashEquivalentResponse> responses = cashEquivalentService.getAllCashEquivalents();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active cash equivalents")
    public ResponseEntity<List<CashEquivalentResponse>> getActiveCashEquivalents() {
        List<CashEquivalentResponse> responses = cashEquivalentService.getActiveCashEquivalents();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/group/{groupName}")
    @Operation(summary = "Get cash equivalents by group")
    public ResponseEntity<List<CashEquivalentResponse>> getCashEquivalentsByGroup(@PathVariable String groupName) {
        List<CashEquivalentResponse> responses = cashEquivalentService.getCashEquivalentsByGroup(groupName);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update cash equivalent")
    public ResponseEntity<CashEquivalentResponse> updateCashEquivalent(
            @PathVariable Long id,
            @Valid @RequestBody CashEquivalentRequest request) {
        CashEquivalentResponse response = cashEquivalentService.updateCashEquivalent(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate cash equivalent")
    public ResponseEntity<CashEquivalentResponse> activateCashEquivalent(@PathVariable Long id) {
        CashEquivalentResponse response = cashEquivalentService.activateCashEquivalent(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate cash equivalent")
    public ResponseEntity<CashEquivalentResponse> deactivateCashEquivalent(@PathVariable Long id) {
        CashEquivalentResponse response = cashEquivalentService.deactivateCashEquivalent(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cash equivalent")
    public ResponseEntity<Void> deleteCashEquivalent(@PathVariable Long id) {
        cashEquivalentService.deleteCashEquivalent(id);
        return ResponseEntity.noContent().build();
    }
}
