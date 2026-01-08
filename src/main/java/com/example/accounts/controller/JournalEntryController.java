package com.example.accounts.controller;

import com.example.accounts.dto.JournalEntryRequest;
import com.example.accounts.dto.JournalEntryResponse;
import com.example.accounts.entity.enums.JournalEntryStatus;
import com.example.accounts.service.JournalEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journal-entries")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Journal Entries", description = "APIs for managing journal entries and double-entry accounting")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @PostMapping
    @Operation(summary = "Create a new journal entry")
    public ResponseEntity<JournalEntryResponse> createJournalEntry(@Valid @RequestBody JournalEntryRequest request) {
        JournalEntryResponse response = journalEntryService.createJournalEntry(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get journal entry by ID")
    public ResponseEntity<JournalEntryResponse> getJournalEntryById(@PathVariable Long id) {
        JournalEntryResponse response = journalEntryService.getJournalEntryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all journal entries")
    public ResponseEntity<List<JournalEntryResponse>> getAllJournalEntries() {
        List<JournalEntryResponse> responses = journalEntryService.getAllJournalEntries();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get journal entries by status")
    public ResponseEntity<List<JournalEntryResponse>> getJournalEntriesByStatus(
            @PathVariable JournalEntryStatus status) {
        List<JournalEntryResponse> responses = journalEntryService.getJournalEntriesByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/post")
    @Operation(summary = "Post a journal entry")
    public ResponseEntity<JournalEntryResponse> postJournalEntry(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "system") String postedBy) {
        JournalEntryResponse response = journalEntryService.postJournalEntry(id, postedBy);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reverse")
    @Operation(summary = "Reverse a posted journal entry")
    public ResponseEntity<JournalEntryResponse> reverseJournalEntry(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "system") String reversedBy) {
        JournalEntryResponse response = journalEntryService.reverseJournalEntry(id, reversedBy);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a draft journal entry")
    public ResponseEntity<Void> deleteJournalEntry(@PathVariable Long id) {
        journalEntryService.deleteJournalEntry(id);
        return ResponseEntity.noContent().build();
    }
}
