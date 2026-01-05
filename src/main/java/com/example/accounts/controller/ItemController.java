package com.example.accounts.controller;

import com.example.accounts.dto.ItemRequest;
import com.example.accounts.dto.ItemResponse;
import com.example.accounts.entity.enums.ItemType;
import com.example.accounts.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Items", description = "APIs for managing inventory, service, fixed asset, and manpower items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Operation(summary = "Create a new item")
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest request) {
        ItemResponse response = itemService.createItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Get item by ID")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long itemId) {
        ItemResponse response = itemService.getItemById(itemId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get item by code")
    public ResponseEntity<ItemResponse> getItemByCode(@PathVariable String code) {
        ItemResponse response = itemService.getItemByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{itemType}")
    @Operation(summary = "Get all items by type")
    public ResponseEntity<List<ItemResponse>> getItemsByType(@PathVariable ItemType itemType) {
        List<ItemResponse> responses = itemService.getItemsByType(itemType);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Get all items")
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        List<ItemResponse> responses = itemService.getAllItems();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active items")
    public ResponseEntity<List<ItemResponse>> getActiveItems() {
        List<ItemResponse> responses = itemService.getActiveItems();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "Update item")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody ItemRequest request) {
        ItemResponse response = itemService.updateItem(itemId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{itemId}/activate")
    @Operation(summary = "Activate item")
    public ResponseEntity<ItemResponse> activateItem(@PathVariable Long itemId) {
        ItemResponse response = itemService.activateItem(itemId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{itemId}/deactivate")
    @Operation(summary = "Deactivate item")
    public ResponseEntity<ItemResponse> deactivateItem(@PathVariable Long itemId) {
        ItemResponse response = itemService.deactivateItem(itemId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Delete item")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
