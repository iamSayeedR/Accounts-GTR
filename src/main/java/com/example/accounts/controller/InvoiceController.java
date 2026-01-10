package com.example.accounts.controller;

import com.example.accounts.dto.InvoiceRequest;
import com.example.accounts.dto.InvoiceResponse;
import com.example.accounts.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:5174")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "APIs for managing customer and supplier invoices with automatic GL posting")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @Operation(summary = "Create a new invoice")
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse response = invoiceService.createInvoice(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        InvoiceResponse response = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all invoices")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        List<InvoiceResponse> responses = invoiceService.getAllInvoices();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get invoices by company")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByCompany(@PathVariable Long companyId) {
        List<InvoiceResponse> responses = invoiceService.getInvoicesByCompany(companyId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/post")
    @Operation(summary = "Post an invoice (creates automatic journal entry)")
    public ResponseEntity<InvoiceResponse> postInvoice(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "system") String postedBy) {
        InvoiceResponse response = invoiceService.postInvoice(id, postedBy);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update invoice")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse response = invoiceService.updateInvoice(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete invoice")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
