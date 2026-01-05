package com.example.accounts.repository;

import com.example.accounts.entity.IndividualFixedAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Individual Fixed Assets
 */
@Repository
public interface IndividualFixedAssetRepository extends JpaRepository<IndividualFixedAsset, Long> {

    /**
     * Find by assignment number
     */
    Optional<IndividualFixedAsset> findByAssignmentNumber(String assignmentNumber);

    /**
     * Find all assignments for a specific asset
     */
    @Query("SELECT i FROM IndividualFixedAsset i WHERE i.fixedAsset.fixedAssetId = :assetId ORDER BY i.assignmentDate DESC")
    List<IndividualFixedAsset> findByFixedAssetId(@Param("assetId") Long assetId);

    /**
     * Find current assignment for an asset (assigned and not returned)
     */
    @Query("SELECT i FROM IndividualFixedAsset i WHERE i.fixedAsset.fixedAssetId = :assetId AND i.status = 'ASSIGNED' AND i.actualReturnDate IS NULL")
    Optional<IndividualFixedAsset> findCurrentAssignmentByAssetId(@Param("assetId") Long assetId);

    /**
     * Find all assignments for a specific employee
     */
    @Query("SELECT i FROM IndividualFixedAsset i WHERE i.employeeId = :employeeId ORDER BY i.assignmentDate DESC")
    List<IndividualFixedAsset> findByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * Find current assignments for an employee (assigned and not returned)
     */
    @Query("SELECT i FROM IndividualFixedAsset i WHERE i.employeeId = :employeeId AND i.status = 'ASSIGNED' AND i.actualReturnDate IS NULL")
    List<IndividualFixedAsset> findCurrentAssignmentsByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * Find all unposted assignments
     */
    @Query("SELECT i FROM IndividualFixedAsset i WHERE i.isPosted = false ORDER BY i.assignmentDate DESC")
    List<IndividualFixedAsset> findUnposted();

    /**
     * Find assignments by status
     */
    @Query("SELECT i FROM IndividualFixedAsset i WHERE i.status = :status ORDER BY i.assignmentDate DESC")
    List<IndividualFixedAsset> findByStatus(@Param("status") String status);

    /**
     * Find assignments by transaction type
     */
    @Query("SELECT i FROM IndividualFixedAsset i WHERE i.transactionType = :transactionType ORDER BY i.assignmentDate DESC")
    List<IndividualFixedAsset> findByTransactionType(@Param("transactionType") String transactionType);

    /**
     * Find assignments by date range
     */
    @Query("SELECT i FROM IndividualFixedAsset i WHERE i.assignmentDate BETWEEN :startDate AND :endDate ORDER BY i.assignmentDate DESC")
    List<IndividualFixedAsset> findByDateRange(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find overdue returns (expected return date passed but not returned)
     */
    @Query("SELECT i FROM IndividualFixedAsset i WHERE i.expectedReturnDate < :currentDate AND i.actualReturnDate IS NULL AND i.status = 'ASSIGNED'")
    List<IndividualFixedAsset> findOverdueReturns(@Param("currentDate") LocalDate currentDate);

    /**
     * Find assignments by department
     */
    @Query("SELECT i FROM IndividualFixedAsset i WHERE i.employeeDepartment = :department ORDER BY i.assignmentDate DESC")
    List<IndividualFixedAsset> findByDepartment(@Param("department") String department);
}
