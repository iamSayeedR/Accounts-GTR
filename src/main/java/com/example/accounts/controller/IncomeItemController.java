package com.example.accounts.controller;

import com.example.accounts.dto.IncomeItemRequest;
import com.example.accounts.dto.IncomeItemResponse;
import com.example.accounts.service.IncomeItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income-items")
@RequiredArgsConstructor
@Tag(name = "Income Items", description = "APIs for managing income items")
public class IncomeItemController {

    private final IncomeItemService incomeItemService;

    @PostMapping
    @Operation(summary = "Create a new income item")
    public ResponseEntity<IncomeItemResponse> createIncomeItem(@Valid @RequestBody IncomeItemRequest request) {
        IncomeItemResponse response = incomeItemService.createIncomeItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get income item by ID")
    public ResponseEntity<IncomeItemResponse> getIncomeItemById(@PathVariable Long id) {
        IncomeItemResponse response = incomeItemService.getIncomeItemById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all income items")
    public ResponseEntity<List<IncomeItemResponse>> getAllIncomeItems() {
        List<IncomeItemResponse> responses = incomeItemService.getAllIncomeItems();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update income item")
    public ResponseEntity<IncomeItemResponse> updateIncomeItem(
            @PathVariable Long id,
            @Valid @RequestBody IncomeItemRequest request) {
        IncomeItemResponse response = incomeItemService.updateIncomeItem(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete income item")
    public ResponseEntity<Void> deleteIncomeItem(@PathVariable Long id) {
        incomeItemService.deleteIncomeItem(id);
        return ResponseEntity.noContent().build();
    }
}
