package com.example.accounts.controller;

import com.example.accounts.dto.ItemGLAccountRequest;
import com.example.accounts.dto.ItemGLAccountResponse;
import com.example.accounts.service.ItemGLAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-gl-accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5174")
public class ItemGLAccountController {

    private final ItemGLAccountService service;

    @PostMapping
    public ResponseEntity<ItemGLAccountResponse> create(@RequestBody ItemGLAccountRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemGLAccountResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ItemGLAccountResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemGLAccountResponse> update(@PathVariable Long id,
            @RequestBody ItemGLAccountRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
