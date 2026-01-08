package com.example.accounts.controller;

import com.example.accounts.dto.ExpenseItemTypeRequest;
import com.example.accounts.dto.ExpenseItemTypeResponse;
import com.example.accounts.service.ExpenseItemTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense-item-types")
@CrossOrigin(origins = "http://localhost:5174")
@RequiredArgsConstructor
@Tag(name = "Expense Item Types", description = "APIs for managing expense item types")
public class ExpenseItemTypeController {

    private final ExpenseItemTypeService expenseItemTypeService;

    @PostMapping
    @Operation(summary = "Create a new expense item type")
    public ResponseEntity<ExpenseItemTypeResponse> createExpenseItemType(
            @Valid @RequestBody ExpenseItemTypeRequest request) {
        ExpenseItemTypeResponse response = expenseItemTypeService.createExpenseItemType(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense item type by ID")
    public ResponseEntity<ExpenseItemTypeResponse> getExpenseItemTypeById(@PathVariable Long id) {
        ExpenseItemTypeResponse response = expenseItemTypeService.getExpenseItemTypeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all expense item types")
    public ResponseEntity<List<ExpenseItemTypeResponse>> getAllExpenseItemTypes() {
        List<ExpenseItemTypeResponse> responses = expenseItemTypeService.getAllExpenseItemTypes();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active expense item types")
    public ResponseEntity<List<ExpenseItemTypeResponse>> getActiveExpenseItemTypes() {
        List<ExpenseItemTypeResponse> responses = expenseItemTypeService.getActiveExpenseItemTypes();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update expense item type")
    public ResponseEntity<ExpenseItemTypeResponse> updateExpenseItemType(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseItemTypeRequest request) {
        ExpenseItemTypeResponse response = expenseItemTypeService.updateExpenseItemType(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense item type")
    public ResponseEntity<Void> deleteExpenseItemType(@PathVariable Long id) {
        expenseItemTypeService.deleteExpenseItemType(id);
        return ResponseEntity.noContent().build();
    }
}
