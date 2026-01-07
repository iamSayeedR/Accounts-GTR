package com.example.accounts.controller;

import com.example.accounts.dto.CashFlowItemRequest;
import com.example.accounts.dto.CashFlowItemResponse;
import com.example.accounts.service.CashFlowItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cash-flow-items")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Tag(name = "Cash Flow Items", description = "APIs for managing cash flow items")
public class CashFlowItemController {

    private final CashFlowItemService cashFlowItemService;

    @PostMapping
    @Operation(summary = "Create a new cash flow item")
    public ResponseEntity<CashFlowItemResponse> createCashFlowItem(@Valid @RequestBody CashFlowItemRequest request) {
        CashFlowItemResponse response = cashFlowItemService.createCashFlowItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cash flow item by ID")
    public ResponseEntity<CashFlowItemResponse> getCashFlowItemById(@PathVariable Long id) {
        CashFlowItemResponse response = cashFlowItemService.getCashFlowItemById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all cash flow items")
    public ResponseEntity<List<CashFlowItemResponse>> getAllCashFlowItems() {
        List<CashFlowItemResponse> responses = cashFlowItemService.getAllCashFlowItems();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update cash flow item")
    public ResponseEntity<CashFlowItemResponse> updateCashFlowItem(
            @PathVariable Long id,
            @Valid @RequestBody CashFlowItemRequest request) {
        CashFlowItemResponse response = cashFlowItemService.updateCashFlowItem(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cash flow item")
    public ResponseEntity<Void> deleteCashFlowItem(@PathVariable Long id) {
        cashFlowItemService.deleteCashFlowItem(id);
        return ResponseEntity.noContent().build();
    }
}
