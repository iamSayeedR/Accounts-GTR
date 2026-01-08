package com.example.accounts.controller;

import com.example.accounts.dto.ExpenseItemRequest;
import com.example.accounts.dto.ExpenseItemResponse;
import com.example.accounts.service.ExpenseItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense-items")
@CrossOrigin(origins = "http://localhost:5174")
@RequiredArgsConstructor
@Tag(name = "Expense Items", description = "APIs for managing expense items")
public class ExpenseItemController {

    private final ExpenseItemService expenseItemService;

    @PostMapping
    @Operation(summary = "Create a new expense item")
    public ResponseEntity<ExpenseItemResponse> createExpenseItem(@Valid @RequestBody ExpenseItemRequest request) {
        ExpenseItemResponse response = expenseItemService.createExpenseItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense item by ID")
    public ResponseEntity<ExpenseItemResponse> getExpenseItemById(@PathVariable Long id) {
        ExpenseItemResponse response = expenseItemService.getExpenseItemById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all expense items")
    public ResponseEntity<List<ExpenseItemResponse>> getAllExpenseItems() {
        List<ExpenseItemResponse> responses = expenseItemService.getAllExpenseItems();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{expenseItemTypeId}")
    @Operation(summary = "Get expense items by type")
    public ResponseEntity<List<ExpenseItemResponse>> getExpenseItemsByType(@PathVariable Long expenseItemTypeId) {
        List<ExpenseItemResponse> responses = expenseItemService.getExpenseItemsByType(expenseItemTypeId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update expense item")
    public ResponseEntity<ExpenseItemResponse> updateExpenseItem(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseItemRequest request) {
        ExpenseItemResponse response = expenseItemService.updateExpenseItem(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense item")
    public ResponseEntity<Void> deleteExpenseItem(@PathVariable Long id) {
        expenseItemService.deleteExpenseItem(id);
        return ResponseEntity.noContent().build();
    }
}
