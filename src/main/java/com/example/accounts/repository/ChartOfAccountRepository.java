package com.example.accounts.repository;

import com.example.accounts.entity.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, Long> {

    Optional<ChartOfAccount> findByAccountCode(String accountCode);

    List<ChartOfAccount> findBySection(String section);

    List<ChartOfAccount> findByParentGroup(String parentGroup);

    List<ChartOfAccount> findByIsActive(Boolean isActive);

    List<ChartOfAccount> findByIsOffBalance(Boolean isOffBalance);

    List<ChartOfAccount> findByIsQuantitative(Boolean isQuantitative);

    boolean existsByAccountCode(String accountCode);

    @org.springframework.data.jpa.repository.Query("SELECT c.description FROM ChartOfAccount c WHERE c.parentGroup = :parentGroup")
    List<String> findDescriptionsByParentGroup(
            @org.springframework.data.repository.query.Param("parentGroup") String parentGroup);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.parentGroup FROM ChartOfAccount c WHERE c.parentGroup IS NOT NULL AND c.parentGroup != '' ORDER BY c.parentGroup")
    List<String> findDistinctParentGroups();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.section FROM ChartOfAccount c WHERE c.section IS NOT NULL AND c.section != '' ORDER BY c.section")
    List<String> findDistinctSections();

    @org.springframework.data.jpa.repository.Query("SELECT c.description FROM ChartOfAccount c WHERE c.section = :section")
    List<String> findDescriptionsBySection(
            @org.springframework.data.repository.query.Param("section") String section);

    @org.springframework.data.jpa.repository.Query("SELECT c.description FROM ChartOfAccount c WHERE c.section = :section OR c.parentGroup = :parentGroup")
    List<String> findDescriptionsBySectionOrParentGroup(
            @org.springframework.data.repository.query.Param("section") String section,
            @org.springframework.data.repository.query.Param("parentGroup") String parentGroup);
}
