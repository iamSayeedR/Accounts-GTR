package com.example.accounts.controller;

import com.example.accounts.dto.ExpenseItemTypeRequest;
import com.example.accounts.dto.ExpenseItemTypeResponse;
import com.example.accounts.service.ExpenseItemTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense-item-types")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5174")
@Tag(name = "Expense Item Types", description = "APIs for managing expense item categories")
public class ExpenseItemTypeController {

    private final ExpenseItemTypeService service;

    @PostMapping
    @Operation(summary = "Create a new expense item type")
    public ResponseEntity<ExpenseItemTypeResponse> create(@RequestBody ExpenseItemTypeRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense item type by ID")
    public ResponseEntity<ExpenseItemTypeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all expense item types")
    public ResponseEntity<List<ExpenseItemTypeResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update expense item type")
    public ResponseEntity<ExpenseItemTypeResponse> update(@PathVariable Long id,
            @RequestBody ExpenseItemTypeRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense item type")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
