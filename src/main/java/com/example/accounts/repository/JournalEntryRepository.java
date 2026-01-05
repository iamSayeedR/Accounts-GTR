package com.example.accounts.repository;

import com.example.accounts.entity.JournalEntry;
import com.example.accounts.entity.enums.JournalEntryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    Optional<JournalEntry> findByEntryNumber(String entryNumber);

    List<JournalEntry> findByStatus(JournalEntryStatus status);

    List<JournalEntry> findByEntryDateBetween(LocalDate startDate, LocalDate endDate);

    List<JournalEntry> findByCompanyCompanyId(Long companyId);

    boolean existsByEntryNumber(String entryNumber);
}
