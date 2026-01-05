package com.example.accounts.controller;

import com.example.accounts.dto.AuditLogResponse;
import com.example.accounts.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Log Management", description = "APIs for querying audit logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/{auditId}")
    @Operation(summary = "Get audit log by ID")
    public ResponseEntity<AuditLogResponse> getAuditLogById(@PathVariable Long auditId) {
        AuditLogResponse response = auditLogService.getAuditLogById(auditId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all audit logs for a user")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByUserId(@PathVariable Long userId) {
        List<AuditLogResponse> responses = auditLogService.getAuditLogsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/entity")
    @Operation(summary = "Get audit logs by entity type and ID")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEntity(
            @RequestParam String entityType,
            @RequestParam Long entityId) {
        List<AuditLogResponse> responses = auditLogService.getAuditLogsByEntity(entityType, entityId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get audit logs by date range")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLogResponse> responses = auditLogService.getAuditLogsByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Get all audit logs")
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {
        List<AuditLogResponse> responses = auditLogService.getAllAuditLogs();
        return ResponseEntity.ok(responses);
    }
}
