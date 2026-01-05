package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing individual fixed assets assigned to employees
 * Tracks laptops, phones, tools, and other assets for personal use
 */
@Entity
@Table(name = "individual_fixed_assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualFixedAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @Column(name = "assignment_number", unique = true, nullable = false, length = 50)
    private String assignmentNumber;

    @Column(name = "assignment_date", nullable = false)
    private LocalDate assignmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_asset_id", nullable = false)
    private FixedAsset fixedAsset;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType; // "Transfer to employee" or "Return from employee"

    // Employee details
    @Column(name = "employee_id", length = 50)
    private String employeeId;

    @Column(name = "employee_name", length = 200)
    private String employeeName;

    @Column(name = "employee_department", length = 200)
    private String employeeDepartment;

    @Column(name = "employee_position", length = 200)
    private String employeePosition;

    @Column(name = "employee_email", length = 200)
    private String employeeEmail;

    // Assignment details
    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "expected_return_date")
    private LocalDate expectedReturnDate;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @Column(name = "status", length = 50)
    private String status; // "ASSIGNED", "RETURNED", "LOST", "DAMAGED"

    @Column(name = "condition_at_assignment", length = 100)
    private String conditionAtAssignment;

    @Column(name = "condition_at_return", length = 100)
    private String conditionAtReturn;

    // Responsibility
    @Column(name = "responsibility_agreement_signed")
    private Boolean responsibilityAgreementSigned = false;

    @Column(name = "agreement_date")
    private LocalDate agreementDate;

    @Column(name = "witness_name", length = 200)
    private String witnessName;

    // Return details
    @Column(name = "return_notes", columnDefinition = "TEXT")
    private String returnNotes;

    @Column(name = "damage_reported")
    private Boolean damageReported = false;

    @Column(name = "damage_description", columnDefinition = "TEXT")
    private String damageDescription;

    // Posting
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id")
    private JournalEntry journalEntry;

    @Column(name = "is_posted")
    private Boolean isPosted = false;

    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Column(name = "posted_by", length = 100)
    private String postedBy;

    // Comments
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Audit fields
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "ASSIGNED";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
