package com.example.accounts.controller;

import com.example.accounts.dto.ItemGLAccountRequest;
import com.example.accounts.dto.ItemGLAccountResponse;
import com.example.accounts.service.ItemGLAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-gl-accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5174")
@Tag(name = "Item GL Accounts", description = "APIs for managing GL account mappings for items")
public class ItemGLAccountController {

    private final ItemGLAccountService service;

    @PostMapping
    @Operation(summary = "Create a new item GL account mapping")
    public ResponseEntity<ItemGLAccountResponse> create(@RequestBody ItemGLAccountRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item GL account mapping by ID")
    public ResponseEntity<ItemGLAccountResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all item GL account mappings")
    public ResponseEntity<List<ItemGLAccountResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item GL account mapping")
    public ResponseEntity<ItemGLAccountResponse> update(@PathVariable Long id,
            @RequestBody ItemGLAccountRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item GL account mapping")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
