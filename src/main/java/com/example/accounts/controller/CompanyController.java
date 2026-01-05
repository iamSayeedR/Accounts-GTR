package com.example.accounts.controller;

import com.example.accounts.dto.CompanyRequest;
import com.example.accounts.dto.CompanyResponse;
import com.example.accounts.entity.enums.CompanyType;
import com.example.accounts.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "APIs for managing customers and suppliers")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @Operation(summary = "Create a new company")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.createCompany(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{companyId}")
    @Operation(summary = "Get company by ID")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable Long companyId) {
        CompanyResponse response = companyService.getCompanyById(companyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get company by code")
    public ResponseEntity<CompanyResponse> getCompanyByCode(@PathVariable String code) {
        CompanyResponse response = companyService.getCompanyByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{companyType}")
    @Operation(summary = "Get all companies by type")
    public ResponseEntity<List<CompanyResponse>> getCompaniesByType(@PathVariable CompanyType companyType) {
        List<CompanyResponse> responses = companyService.getCompaniesByType(companyType);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Get all companies")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        List<CompanyResponse> responses = companyService.getAllCompanies();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active companies")
    public ResponseEntity<List<CompanyResponse>> getActiveCompanies() {
        List<CompanyResponse> responses = companyService.getActiveCompanies();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{companyId}")
    @Operation(summary = "Update company")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable Long companyId,
            @Valid @RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.updateCompany(companyId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{companyId}/activate")
    @Operation(summary = "Activate company")
    public ResponseEntity<CompanyResponse> activateCompany(@PathVariable Long companyId) {
        CompanyResponse response = companyService.activateCompany(companyId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{companyId}/deactivate")
    @Operation(summary = "Deactivate company")
    public ResponseEntity<CompanyResponse> deactivateCompany(@PathVariable Long companyId) {
        CompanyResponse response = companyService.deactivateCompany(companyId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{companyId}")
    @Operation(summary = "Delete company")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();
    }
}
